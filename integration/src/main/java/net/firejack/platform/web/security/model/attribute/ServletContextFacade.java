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

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.sf.ehcache.store.chm.ConcurrentHashMap;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.util.concurrent.ConcurrentMap;

public class ServletContextFacade extends SecurityAttributeFacade {

    private static final Logger logger = Logger.getLogger(ServletContextFacade.class);

    private ServletContext servletContext;
    private ThreadLocal<String> sessionTokenHolder = new InheritableThreadLocal<String>();

    /**
     * @param servletContext
     */
    public ServletContextFacade(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * @param sessionToken
     */
    public void initSessionToken(String sessionToken) {
        sessionTokenHolder.set(sessionToken);
    }

    @Override
    public void invalidateBusinessContext() {
        String currentSessionToken = sessionTokenHolder.get();
        if (currentSessionToken != null) {
            getContextMap().remove(currentSessionToken);
            sessionTokenHolder.remove();
        }
    }

    @Override
    public void setAttribute(OPFContext ctx) {
        String sessionToken = ctx.getSessionToken();
        if (StringUtils.isBlank(sessionToken)) {
            logger.warn("Business context session token is blank.");
        } else {
            getContextMap().putIfAbsent(sessionToken, ctx);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public OPFContext getAttribute() {
        OPFContext context;
        String currentSessionToken = sessionTokenHolder.get();
        if (currentSessionToken == null) {
            logger.warn("Current session token was not specified.");
            context = null;
        } else {
            context = getContextMap().get(currentSessionToken);
        }
        return context;
    }

    private ConcurrentMap<String, OPFContext> getContextMap() {
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, OPFContext> contextMap =
                (ConcurrentMap<String, OPFContext>) servletContext.getAttribute(
                        OpenFlameSecurityConstants.BUSINESS_CONTEXT_ATTRIBUTE);
        if (contextMap == null) {
            contextMap = new ConcurrentHashMap<String, OPFContext>();
            servletContext.setAttribute(
                    OpenFlameSecurityConstants.BUSINESS_CONTEXT_ATTRIBUTE, contextMap);
        }

        return contextMap;
    }

}