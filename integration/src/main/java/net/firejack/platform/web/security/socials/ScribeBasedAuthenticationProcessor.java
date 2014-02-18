/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.web.security.socials;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.IAuthenticationProcessor;
import net.firejack.platform.web.security.filter.ISignInProcessor;
import net.firejack.platform.web.security.filter.message.FilterMessageStock;
import net.firejack.platform.web.security.filter.message.FilterMessageType;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public abstract class ScribeBasedAuthenticationProcessor
        implements IAuthenticationProcessor/*, ApplicationListener<ChangeConfigEvent>*/ {

    private static final Logger logger = Logger.getLogger(ScribeBasedAuthenticationProcessor.class);
    private String initialRequestMarkerAttribute = this.getClass() + "_initial_request_marker";
    private String requestTokenAttribute = this.getClass() + "_request_token";
//    private String consumerKey;
//    private String consumerSecret;
    private OAuthService service;
    private ISignInProcessor signInProcessor;
    private String authenticationEntryPointUrl;
    private String defaultPageUrl;

    public String getDefaultPageUrl() {
        return OpenFlameSecurityConstants.getBaseUrl() + defaultPageUrl;
    }

    public void setDefaultPageUrl(String defaultPageUrl) {
        this.defaultPageUrl = defaultPageUrl;
    }

    public String getAuthenticationEntryPointUrl() {
        return authenticationEntryPointUrl;
    }

    public void setAuthenticationEntryPointUrl(String authenticationEntryPointUrl) {
        this.authenticationEntryPointUrl = authenticationEntryPointUrl;
    }

    public ISignInProcessor getSignInProcessor() {
        return signInProcessor;
    }

    public void setSignInProcessor(ISignInProcessor signInProcessor) {
        this.signInProcessor = signInProcessor;
    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
        logger.info("initialization of " + this.getClass().getName());
    }

    @Override
    public boolean isAuthenticationCase(HttpServletRequest request) {
         String requestPath = WebUtils.getRequestPath(request);
        return requestPath.equalsIgnoreCase(getAuthenticationEntryPointUrl());
    }

    @Override
    public void processAuthentication(HttpServletRequest request, HttpServletResponse response,
                                      FilterChain filterChain) throws IOException, ServletException {
        if (isInitialRequest(request)) {
            markInitialRequest(request);
            Token requestToken = getRequestToken(request);
            saveRequestToken(requestToken, request);
            String authUrl = getService().getAuthorizationUrl(requestToken);
            response.sendRedirect(authUrl);
        } else {
            String pinCode = getPinCode(request);
            if (StringUtils.isBlank(pinCode)) {
                response.sendRedirect(getDefaultPageUrl());
            } else {
                String browserIpAddress = getBrowserIpAddress(request);
                Token accessToken;
                try {
                    accessToken = getAccessToken(pinCode, request);
                    AuthenticationToken authToken = onAccessTokenRetrieved(
                            accessToken.getToken(), accessToken.getSecret(),
                            browserIpAddress, request, response);
                    getSignInProcessor().processSignInInternal(request, response, authToken);
                } catch (OAuthException e) {
                    logger.error(e.getMessage(), e);
                    FilterMessageStock.getInstance().addFilterMessage(FilterMessageType.ERROR, e.getMessage());
                    response.sendRedirect(getDefaultPageUrl());
                }
            }
        }
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    public void release() {
    }

    protected OAuthService getService() {
        if (!isEnable())
            throw new IllegalStateException("Service disabled.");

        if (service == null) {
            service = provideService();
            if (service == null) {
                logger.warn("OAuth Authentication Provider method returns empty OAuthService reference.");
            }
        }
        return service;
    }

    private boolean isEnable() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(getEnableConfigLookup());
        return config == null ? false : Boolean.valueOf(config.getValue());
    }

    protected String getConsumerKey() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(getConsumerKeyConfigLookup());
        return config != null ? config.getValue() : null;
    }

    protected String getConsumerSecret() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(getConsumerSecretConfigLookup());
        return config != null ? config.getValue() : null;
    }

    protected Token getRequestToken(HttpServletRequest request) {
        return getService().getRequestToken();
    }

    protected Token getAccessToken(String pinCode, HttpServletRequest request) throws OAuthException {
        Verifier v = new Verifier(pinCode);
        Token requestToken = getSavedRequestToken(request);
        clearSavedRequestToken(request);
        Token accessToken = getService().getAccessToken(requestToken, v);
        logger.info("Access Token: " + accessToken.getToken());
        logger.info("Raw Response: " + accessToken.getRawResponse());
        return accessToken;
    }

    protected String getPinCode(HttpServletRequest request) {
        return request.getParameter("code");
    }

    protected boolean isInitialRequest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object initialRequestMarker = session.getAttribute(this.initialRequestMarkerAttribute);
        boolean result = initialRequestMarker == null;
        session.removeAttribute(this.initialRequestMarkerAttribute);
        return result;
    }

    protected void markInitialRequest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(this.initialRequestMarkerAttribute, Boolean.TRUE);
        session.setAttribute(OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE, request.getRemoteAddr());
    }

    protected String getBrowserIpAddress(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String browserIpAddress = (String) session.getAttribute(OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE);
        session.removeAttribute(OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE);
        return browserIpAddress;
    }

    protected void saveRequestToken(Token requestToken, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(this.requestTokenAttribute, requestToken);
    }

    protected Token getSavedRequestToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Token) session.getAttribute(this.requestTokenAttribute);
    }

    protected void clearSavedRequestToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(this.requestTokenAttribute);
    }

    protected abstract AuthenticationToken onAccessTokenRetrieved(
            String accessToken, String tokenSecret, String browserIpAddress,
            HttpServletRequest request, HttpServletResponse response);

    protected abstract String getConsumerKeyConfigLookup();

    protected abstract String getConsumerSecretConfigLookup();

    protected abstract String getEnableConfigLookup();

    protected abstract OAuthService provideService();

}