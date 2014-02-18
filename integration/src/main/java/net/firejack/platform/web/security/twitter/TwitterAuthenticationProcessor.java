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

package net.firejack.platform.web.security.twitter;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.filter.ISignInProcessor;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class TwitterAuthenticationProcessor extends BaseTwitterAuthenticationProcessor {

    private static final String ATTR_TWITTER_REQUEST_TOKEN = "twitter_auth_requestToken";
    private static final String ATTR_TWITTER_API = "twitter_auth";

    private static final Logger logger = Logger.getLogger(TwitterAuthenticationProcessor.class);

    private ISignInProcessor signInProcessor;
    private String defaultPageUrl;

    public ISignInProcessor getSignInProcessor() {
        return signInProcessor;
    }

    public void setSignInProcessor(ISignInProcessor signInProcessor) {
        this.signInProcessor = signInProcessor;
    }

    public String getDefaultPageUrl() {
        return OpenFlameSecurityConstants.getBaseUrl() + defaultPageUrl;
    }

    public void setDefaultPageUrl(String defaultPageUrl) {
        this.defaultPageUrl = defaultPageUrl;
    }

    @Override
    public void processAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (StringUtils.isBlank(getTwitterConsumerKey()) || StringUtils.isBlank(getTwitterConsumerSecret())) {
            logger.error("Twitter consumer key or consumer secret configs were not set.");
            response.sendRedirect(getDefaultPageUrl());
        } else {
            HttpSession httpSession = request.getSession(true);
            Twitter twitter = (Twitter) httpSession.getAttribute(ATTR_TWITTER_API);
            if (twitter == null) {
                twitter = populateTwitterService();
                httpSession.setAttribute(ATTR_TWITTER_API, twitter);
            }
            if (isRequestTokenCase(request)) {
                RequestToken requestToken;
                try {
                    requestToken = twitter.getOAuthRequestToken(getCallbackUrl());
                    httpSession.setAttribute(ATTR_TWITTER_REQUEST_TOKEN, requestToken);
                    response.sendRedirect(requestToken.getAuthenticationURL());//show twitter authentication form
                } catch (TwitterException e) {//service is unavailable
                    logger.error("Twitter service is unavailable.");
                    logger.error(e.getMessage(), e);
                    cleanSessionInformation(httpSession);
                    response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Twitter service is unavailable.");
                } catch (IllegalStateException e) {//access token is already available
                    logger.error("access token is already available");
                    cleanSessionInformation(httpSession);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Twitter processor already having information about access token.");
                }
            } else if (isAuthenticationCallback(request)) {
                RequestToken requestToken = (RequestToken) httpSession.getAttribute(ATTR_TWITTER_REQUEST_TOKEN);

                String verifier = request.getParameter("oauth_verifier");
                try {
                    twitter.getOAuthAccessToken(requestToken, verifier);
                    httpSession.removeAttribute(ATTR_TWITTER_REQUEST_TOKEN);

                    String browserIpAddress = request.getRemoteAddr();
                    AuthenticationToken authenticationToken = this.getOpenFlameToken(twitter, browserIpAddress);

                    if (authenticationToken == null) {
                        logger.error("Authentication token is null.");
                    }
                    getSignInProcessor().processSignInInternal(request, response, authenticationToken);
                } catch (TwitterException e) {
                    logger.error(e.getMessage(), e);
                    cleanSessionInformation(httpSession);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        }
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.removeAttribute(ATTR_TWITTER_API);
        }
    }

    private boolean isRequestTokenCase(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        return session.getAttribute(ATTR_TWITTER_REQUEST_TOKEN) == null;
    }

    private String getCallbackUrl() {
        return OpenFlameSecurityConstants.getBaseUrl() + getTwitterAuthenticationEntryPointUrl() + "?callback=true";
    }

    private boolean isAuthenticationCallback(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        return session.getAttribute(ATTR_TWITTER_REQUEST_TOKEN) != null && "true".equals(httpRequest.getParameter("callback"));
    }

    private void cleanSessionInformation(HttpSession session) {
        if (session != null) {
            session.removeAttribute(ATTR_TWITTER_REQUEST_TOKEN);
            session.removeAttribute(ATTR_TWITTER_API);
        }
    }

}