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