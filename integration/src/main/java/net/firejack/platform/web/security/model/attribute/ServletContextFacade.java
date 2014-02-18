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