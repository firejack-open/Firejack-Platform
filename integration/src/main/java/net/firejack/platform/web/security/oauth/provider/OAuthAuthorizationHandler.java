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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static net.firejack.platform.web.security.model.OpenFlameSecurityConstants.OAUTH_OPF_TOKEN_ATTRIBUTE;

public class OAuthAuthorizationHandler extends BasicOAuthRequestHandler {
    private static final Logger logger = Logger.getLogger(OAuthAuthorizationHandler.class);
    public static final String DEFAULT_AUTHORIZATION_HANDLER_URL = "/oauth-provider/authorize";
    public static final String DEFAULT_AUTHORIZE_PAGE_URL = "/oauth_authorize.jsp";
    private String authorizePage;
    private String userNameParameterName;
    private String passwordParameterName;

    /**
     * @param urlEntryPoint
     */
    public OAuthAuthorizationHandler(String urlEntryPoint) {
        super(urlEntryPoint);
    }

    /***/
    public OAuthAuthorizationHandler() {
        this(DEFAULT_AUTHORIZATION_HANDLER_URL);
    }

    /**
     * @return
     */
    public String getAuthorizePage() {
        if (authorizePage == null) {
            this.authorizePage = DEFAULT_AUTHORIZE_PAGE_URL;
        }
        return authorizePage;
    }

    /**
     * @param authorizePage
     */
    public void setAuthorizePage(String authorizePage) {
        this.authorizePage = authorizePage;
    }

    /**
     * @return
     */
    public String getUserNameParameterName() {
        return userNameParameterName;
    }

    /**
     * @param userNameParameterName
     */
    public void setUserNameParameterName(String userNameParameterName) {
        this.userNameParameterName = userNameParameterName;
    }

    /**
     * @return
     */
    public String getPasswordParameterName() {
        return passwordParameterName;
    }

    /**
     * @param passwordParameterName
     */
    public void setPasswordParameterName(String passwordParameterName) {
        this.passwordParameterName = passwordParameterName;
    }

    @Override
    public void processPOST(OAuthRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            OAuthAccessor accessor = OAuthProcessor.getAccessor(request.getMessage());
            String userName = request.getHttpParameter(getUserNameParameterName());
            String password = request.getHttpParameter(getPasswordParameterName());
            AuthenticationToken openFlameToken = null;
            if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
                openFlameToken = authenticate(userName, password, request.getHttpRequest().getRemoteAddr());
            }
            if (openFlameToken == null) {
                sendToAuthorizePage(request, response, accessor);
                return;
            }
            OAuthProcessor.markAsAuthorized(accessor, openFlameToken);

            returnToConsumer(request, response, accessor);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            OAuthProcessor.handleException(e, request.getHttpRequest(), response, true);
        }
    }

    @Override
    public void processGET(OAuthRequest request, HttpServletResponse httpResponse) throws IOException, ServletException {
        try {
            OAuthAccessor accessor = OAuthProcessor.getAccessor(request.getMessage());
            if (accessor.getProperty(OAUTH_OPF_TOKEN_ATTRIBUTE) != null) {
                // already authorized send the user back
                returnToConsumer(request, httpResponse, accessor);
            } else {
                sendToAuthorizePage(request, httpResponse, accessor);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            OAuthProcessor.handleException(e, request.getHttpRequest(), httpResponse, true);
        }
    }

    protected AuthenticationToken authenticate(String userName, String password, String browserIpAddress) {
        ServiceResponse<AuthenticationToken> response =
                OPFEngine.AuthorityService.processSTSSignIn(userName, password, browserIpAddress);
        AuthenticationToken token;
        if (response == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (response.isSuccess()) {
            token = response.getItem();
        } else {
            logger.error("AuthorityService API returned failure status. Reason: " + response.getMessage());
            token = null;
        }
        return token;
    }

    private void returnToConsumer(OAuthRequest request,
                                  HttpServletResponse response, OAuthAccessor accessor)
            throws IOException, ServletException {
        // send the user back to site's callBackUrl
        String callback = request.getHttpParameter(OpenFlameSecurityConstants.OAUTH_CALLBACK_PARAMETER);
        if ("none".equals(callback) && StringUtils.isNotBlank(accessor.consumer.callbackURL)) {
            // first check if we have something in our properties file
            callback = accessor.consumer.callbackURL;
        }

        if ("none".equals(callback)) {
            // no call back it must be a client
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            StringBuilder sb = new StringBuilder();
            sb.append("You have successfully authorized ");
            String description = (String) accessor.consumer.getProperty("description");
            if (StringUtils.isNotBlank(description)) {
                sb.append('\'').append(description).append('\'');
            }
            sb.append(". Please close this browser window and click continue in the client.");
            out.println(sb.toString());
            out.close();
        } else {
            // if callback is not passed in, use the callback from config
            callback = StringUtils.isBlank(callback) ? accessor.consumer.callbackURL : callback;
            if (accessor.requestToken != null) {
                callback = OAuth.addParameters(callback, OpenFlameSecurityConstants.OAUTH_TOKEN_PARAMETER, accessor.requestToken);
            }

            response.sendRedirect(callback);
        }
    }

    private void sendToAuthorizePage(
            OAuthRequest oAuthRequest, HttpServletResponse response, OAuthAccessor accessor)
            throws IOException, ServletException {
        String callback = oAuthRequest.getHttpParameter(OpenFlameSecurityConstants.OAUTH_CALLBACK_PARAMETER);
        if (StringUtils.isBlank(callback)) {
            callback = "none";
        }
        String consumer_description = (String) accessor.consumer.getProperty("description");
        HttpServletRequest request = oAuthRequest.getHttpRequest();
        request.setAttribute("CONS_DESC", consumer_description);
        request.setAttribute("CALLBACK", callback);
        request.setAttribute("TOKEN", accessor.requestToken);
        request.setAttribute("authorizationUrl", OpenFlameSecurityConstants.getBaseUrl() + getUrlEntryPoint());
        //request.getRequestDispatcher(OpenFlameSecurityConstants.getBaseUrl() + getAuthorizePage()).forward(request, response);
        request.getRequestDispatcher(getAuthorizePage()).forward(request, response);
    }
}