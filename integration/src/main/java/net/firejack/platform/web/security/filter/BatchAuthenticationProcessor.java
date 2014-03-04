/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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