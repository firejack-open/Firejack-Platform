/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.web.security.filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchAuthenticationProcessor implements IAuthenticationProcessor {

    private List<IAuthenticationProcessor> processors;
    private final ThreadLocal<IAuthenticationProcessor> currentProcessorHolder = new InheritableThreadLocal<IAuthenticationProcessor>();

    /**
     * @param processor
     */
    public void addProcessor(IAuthenticationProcessor processor) {
        if (processor != null) {
            getProcessors().add(processor);
        }
    }

    @Override
    public boolean isAuthenticationCase(HttpServletRequest request) {
        for (IAuthenticationProcessor processor : getProcessors()) {
            if (processor.isAuthenticationCase(request)) {
                currentProcessorHolder.set(processor);
                return true;
            }
        }
        return false;
    }

    @Override
    public void processAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        IAuthenticationProcessor currentProcessor = currentProcessorHolder.get();
        if (currentProcessor != null) {
            currentProcessorHolder.remove();
            currentProcessor.processAuthentication(request, response, filterChain);
        }
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
        for (IAuthenticationProcessor processor : getProcessors()) {
            processor.processUnAuthentication(request, response);
        }
    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
        for (IAuthenticationProcessor processor : getProcessors()) {
            processor.initialize(config);
        }
    }

    @Override
    public void release() {
        if (processors != null) {
            for (IAuthenticationProcessor processor : processors) {
                processor.release();
            }
        }
    }

    private List<IAuthenticationProcessor> getProcessors() {
        if (processors == null) {
            processors = new ArrayList<IAuthenticationProcessor>();
        }
        return processors;
    }
}