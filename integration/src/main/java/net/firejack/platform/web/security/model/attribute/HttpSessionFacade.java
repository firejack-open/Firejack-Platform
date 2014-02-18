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

package net.firejack.platform.web.security.model.attribute;

import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;

public class HttpSessionFacade extends SecurityAttributeFacade {

    private static final Logger logger = Logger.getLogger(HttpSessionFacade.class);
    private ThreadLocal<HttpSession> httpSessionHolder = new InheritableThreadLocal<HttpSession>();

    /**
     * @param httpSession
     */
    public void initHttpSession(HttpSession httpSession) {
        httpSessionHolder.set(httpSession);
    }

    @Override
    public void invalidateBusinessContext() {
        HttpSession httpSession = httpSessionHolder.get();
        if (httpSession != null) {
            httpSession.invalidate();
            httpSessionHolder.remove();
        }
    }

    @Override
    public void setAttribute(OPFContext ctx) {
        HttpSession httpSession = httpSessionHolder.get();
        if (httpSession == null) {
            logger.warn("Failed to set Business Context to http session: HttpSession was not initialized for current thread.");
        } else {
            httpSession.setAttribute(OpenFlameSecurityConstants.BUSINESS_CONTEXT_ATTRIBUTE, ctx);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public OPFContext getAttribute() {
        OPFContext ctx;
        HttpSession httpSession = httpSessionHolder.get();
        if (httpSession == null) {
            logger.warn("Failed to get Business Context from http session: HttpSession was not initialized for current thread.");
            ctx = null;
        } else {
            ctx = (OPFContext) httpSession.getAttribute(OpenFlameSecurityConstants.BUSINESS_CONTEXT_ATTRIBUTE);
        }
        return ctx;
    }

}