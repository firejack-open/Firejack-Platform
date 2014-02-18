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

package net.firejack.platform.web.security.oauth.provider;

import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.IAuthenticationProcessor;
import net.oauth.server.OAuthServlet;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OAuthAuthenticationProvider implements IAuthenticationProcessor {

    private static final ThreadLocal<Tuple<HTTPMethod, IOAuthRequestHandler>> handlerInfo =
            new InheritableThreadLocal<Tuple<HTTPMethod, IOAuthRequestHandler>>();
    private List<IOAuthRequestHandler> handlers;
    private OAuthAuthorizationHandler authorizationHandler;

    /**
     * @param authorizePage
     */
    public void setAuthorizePage(String authorizePage) {
        getAuthorizationHandler().setAuthorizePage(authorizePage);
    }

    /**
     * @param accountParameterName
     */
    public void setAccountParameterName(String accountParameterName) {
        getAuthorizationHandler().setUserNameParameterName(accountParameterName);
    }

    /**
     * @param passwordParameterName
     */
    public void setPasswordParameterName(String passwordParameterName) {
        getAuthorizationHandler().setPasswordParameterName(passwordParameterName);
    }

    @Override
    public boolean isAuthenticationCase(HttpServletRequest request) {
        String requestPath = WebUtils.getRequestPath(request);
        HTTPMethod httpMethod = HTTPMethod.findByName(request.getMethod());
        if (httpMethod == HTTPMethod.GET || httpMethod == HTTPMethod.POST) {
            for (IOAuthRequestHandler handler : getHandlers()) {
                if (requestPath.equalsIgnoreCase(handler.getUrlEntryPoint())) {
                    handlerInfo.set(new Tuple<HTTPMethod, IOAuthRequestHandler>(httpMethod, handler));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void processAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Tuple<HTTPMethod, IOAuthRequestHandler> handlerInfoTuple = handlerInfo.get();
        if (handlerInfoTuple != null) {
            handlerInfo.remove();
            HTTPMethod method = handlerInfoTuple.getKey();
            IOAuthRequestHandler handler = handlerInfoTuple.getValue();
            OAuthRequest requestMessage = new OAuthRequest(request, OAuthServlet.getMessage(request, null));
            if (method == HTTPMethod.GET) {
                handler.processGET(requestMessage, response);
            } else {
                handler.processPOST(requestMessage, response);
            }
        }
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // not used
    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
        for (IOAuthRequestHandler handler : getHandlers()) {
            handler.init(config);
        }
    }

    @Override
    public void release() {
    }

    /**
     * @return
     */
    public List<IOAuthRequestHandler> getHandlers() {
        if (handlers == null) {
            handlers = new ArrayList<IOAuthRequestHandler>();
            handlers.add(getAuthorizationHandler());
            handlers.add(new OAuthAccessTokenHandler());
            handlers.add(new OAuthRequestTokenHandler());
            //handlers.add(new OAuthEchoHandler());
        }
        return handlers;
    }

    protected OAuthAuthorizationHandler populateAuthorizationHandler() {
        return new OAuthAuthorizationHandler();
    }

    private OAuthAuthorizationHandler getAuthorizationHandler() {
        if (authorizationHandler == null) {
            authorizationHandler = populateAuthorizationHandler();
        }
        return authorizationHandler;
    }
}