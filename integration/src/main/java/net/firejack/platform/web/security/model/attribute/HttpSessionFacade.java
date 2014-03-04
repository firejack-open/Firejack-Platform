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