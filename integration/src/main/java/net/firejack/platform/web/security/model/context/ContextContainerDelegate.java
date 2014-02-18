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

package net.firejack.platform.web.security.model.context;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.SecurityUtils;
import net.firejack.platform.web.security.exception.AuthenticationException;
import net.firejack.platform.web.security.exception.SecurityRuntimeException;
import net.firejack.platform.web.security.filter.message.FilterMessageStock;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.attribute.*;
import net.firejack.platform.web.security.oauth.provider.OAuthProcessor;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class ContextContainerDelegate implements IContextContainerDelegate {

    private static final Logger logger = Logger.getLogger(ContextContainerDelegate.class);

    private HttpSessionFacade httpSessionFacade;
    private ServletContextFacade servletContextFacade;
    private CachedContextFacade cachedContextFacade;

    private static ThreadLocal<OPFContext> contextContainer = new InheritableThreadLocal<OPFContext>();
    private static ThreadLocal<OPFContextPersistenceStrategy> strategyHolder = new InheritableThreadLocal<OPFContextPersistenceStrategy>();

    private static ContextContainerDelegate instance;

    protected ContextContainerDelegate() {
    }

    /**
     * @return
     */
    public static ContextContainerDelegate getInstance() {
        if (instance == null) {
            instance = new ContextContainerDelegate();
        }
        return instance;
    }

    /**
     * @param servletContext
     */
    public void initialize(ServletContext servletContext) {
        this.initialize(servletContext, new CachedContextFacade());
    }

    /**
     * @param servletContext
     * @param cachedContextFacade
     */
    public void initialize(ServletContext servletContext, CachedContextFacade cachedContextFacade) {
        this.httpSessionFacade = new HttpSessionFacade();
        this.servletContextFacade = new ServletContextFacade(servletContext);
        this.cachedContextFacade = cachedContextFacade;
    }

    /***/
    public void setServletContextPersistenceStrategy() {
        strategyHolder.set(OPFContextPersistenceStrategy.SERVLET_CONTEXT);
    }

    /***/
    public void setHttpSessionPersistenceStrategy() {
        strategyHolder.set(OPFContextPersistenceStrategy.HTTP_SESSION);
    }

    /***/
    public void setCachePersistenceStrategy() {
        strategyHolder.set(OPFContextPersistenceStrategy.CACHE);
    }

    /**
     * @param session
     */
    public void setCurrentSession(HttpSession session) {
        setHttpSessionPersistenceStrategy();
        this.httpSessionFacade.initHttpSession(session);
        initContextFromStore();
    }

    /**
     * @param httpRequest http servlet request
     */
    public void initializeOpenFlameContext(HttpServletRequest httpRequest) {
        String sessionToken;
        if (OAuthProcessor.isOAuthRequest(httpRequest)) {
            sessionToken = OAuthProcessor.getSessionToken(httpRequest);
        } else {
            Cookie cookie = WebUtils.getRequestCookie(
                    httpRequest, OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE);
            setCachePersistenceStrategy();
            sessionToken = cookie == null ? null : cookie.getValue();
        }

        if (StringUtils.isNotBlank(sessionToken)) {
            String browserIpAddress = SecurityUtils.getBrowserIpAddress(sessionToken, httpRequest);
            if (SecurityUtils.tokenIsValid(sessionToken, browserIpAddress)) {
                this.cachedContextFacade.initSessionToken(sessionToken, browserIpAddress);
                initContextFromStore();
                OPFContext opfContext = contextContainer.get();
                if (opfContext != null && opfContext.getBrowserIpAddress() == null) {
                    opfContext.setBrowserIpAddress(browserIpAddress);
                }
            } else {//handle exception outside
                logger.error("Usage of token is forbidden because request originator provides wrong ip address.");
                throw new SecurityRuntimeException("Originator of the request is not permitted to use this token.");
            }
        }
        FilterMessageStock.getInstance().setHttpSession(httpRequest.getSession());
    }

    /***/
    public void releaseOpenFlameContextResources() {
        ContextContainerDelegate.contextContainer.remove();
        ContextContainerDelegate.strategyHolder.remove();
        this.cachedContextFacade.releaseAttributeResources();
    }

    /**
     * @param sessionToken
     */
    public void setCurrentSessionToken(String sessionToken) {
        setServletContextPersistenceStrategy();
        this.servletContextFacade.initSessionToken(sessionToken);
        initContextFromStore();
    }

    @Override
    public void saveContext(OPFContext context) throws AuthenticationException {
        getContextStore().setAttribute(context);
        contextContainer.set(context);
    }

    @Override
    public OPFContext retrieveContext() throws AuthenticationException {
        OPFContext context = contextContainer.get();
        if (context == null) {
            throw new AuthenticationException();
        }
        return context;
    }

    @Override
    public boolean contextInitialized() {
        return getContextStore().getAttribute() != null;
    }

    /***/
    public void initBusinessContextForGuest() {
        getContextStore().initGuestVisit();
    }

//    /**
//     * @param httpRequest
//     */
//    public void initBusinessContextForSystem(HttpServletRequest httpRequest) {
//        Cookie cookie = WebUtils.getRequestCookie(httpRequest, OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE);
//        String sessionToken = cookie == null ? null : cookie.getValue();
//        if (UserSessionManager.getInstance().isSystemToken(sessionToken)) {
//            getContextStore().initSystemVisit(sessionToken);
//        }
//    }

    /***/
    public void invalidateBusinessContextInStore() {
        getContextStore().invalidateBusinessContext();
    }

    protected SecurityAttributeFacade getContextStore() {
        SecurityAttributeFacade attributeContainerFacade;

        OPFContextPersistenceStrategy strategy = strategyHolder.get();
        if (strategy == OPFContextPersistenceStrategy.HTTP_SESSION) {
            attributeContainerFacade = httpSessionFacade;
        } else if (strategy == OPFContextPersistenceStrategy.SERVLET_CONTEXT) {
            attributeContainerFacade = servletContextFacade;
        } else {
            attributeContainerFacade = cachedContextFacade;
        }

        return attributeContainerFacade;
    }

    protected void initContextFromStore() {
        IAttributeContainerFacade<OPFContext> attributeContainerFacade = getContextStore();
        OPFContext context = attributeContainerFacade.getAttribute();
        contextContainer.set(context);
    }

}