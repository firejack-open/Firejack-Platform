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

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import net.oauth.*;
import net.oauth.server.OAuthServlet;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static net.firejack.platform.web.security.model.OpenFlameSecurityConstants.OAUTH_NAME_PROPERTY;
import static net.firejack.platform.web.security.model.OpenFlameSecurityConstants.OAUTH_OPF_TOKEN_ATTRIBUTE;

@SuppressWarnings("unused")
public class OAuthProcessor {

    private static final Logger logger = Logger.getLogger(OAuthProcessor.class);
    private static final OAuthValidator VALIDATOR = new SimpleOAuthValidator();

    private static final Map<String, OAuthConsumer> ALL_CONSUMERS
            = Collections.synchronizedMap(new HashMap<String, OAuthConsumer>(10));

    private static final Collection<OAuthAccessor> ALL_TOKENS = new HashSet<OAuthAccessor>();

    /**
     * @param message
     * @param accessor
     * @throws net.oauth.OAuthException
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    public static void validateMessage(OAuthMessage message, OAuthAccessor accessor) throws OAuthException, IOException, URISyntaxException {
        VALIDATOR.validateMessage(message, accessor);
    }

    /**
     * @param requestMessage
     * @return
     * @throws net.oauth.OAuthProblemException
     *
     * @throws java.io.IOException
     */
    public static synchronized OAuthConsumer getConsumer(
            OAuthMessage requestMessage)
            throws IOException, OAuthProblemException {

        OAuthConsumer consumer;
        // try to load from local cache if not throw exception
        String consumerKey = requestMessage.getConsumerKey();
        if (StringUtils.isBlank(consumerKey)) {
            consumer = null;
            consumerKey = generateConsumerKey();
        } else {
            consumer = ALL_CONSUMERS.get(consumerKey);
        }
        if (consumer == null) {
            String callbackUrl = requestMessage.getParameter("callbackURL");
            consumer = populateConsumer(consumerKey, callbackUrl);
            ALL_CONSUMERS.put(consumerKey, consumer);
        }

        return consumer;
    }

    /**
     * @param consumerKey
     * @param callbackUrl
     * @return
     */
    public static OAuthConsumer populateConsumer(String consumerKey, String callbackUrl) {
        String consumerSecret = generateConsumerSecret(consumerKey);

        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey, consumerSecret, null);
        consumer.setProperty(OAUTH_NAME_PROPERTY, consumerKey);
        consumer.setProperty("description", "Generated OAuth Consumer.");

        return consumer;
    }

    /**
     * @param consumerKey
     * @param callbackUrl
     * @param oAuthServiceProvider
     * @return
     */
    public static OAuthConsumer populateConsumer(String consumerKey, String callbackUrl, OAuthServiceProvider oAuthServiceProvider) {
        String consumerSecret = generateConsumerSecret(consumerKey);

        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey, consumerSecret, oAuthServiceProvider);
        consumer.setProperty(OAUTH_NAME_PROPERTY, consumerKey);
        consumer.setProperty("description", "Generated OAuth Consumer.");

        return consumer;
    }

    /**
     * @return
     */
    public static String generateConsumerKey() {
        return "CONSUMER_" + UUID.randomUUID().toString();
    }

    /**
     * @param consumerKey
     * @return
     */
    public static String generateConsumerSecret(String consumerKey) {
        return consumerKey + "_consumerSecret";
    }

    /**
     * Get the access token and token secret for the given oauth_token.
     *
     * @param requestMessage request message
     * @return OAuth accessor
     * @throws IOException           IO Exception
     * @throws OAuthProblemException OAuth Problem Exception
     */
    public static synchronized OAuthAccessor getAccessor(OAuthMessage requestMessage)
            throws IOException, OAuthProblemException {

        // try to load from local cache if not throw exception
        String consumer_token = requestMessage.getToken();
        OAuthAccessor accessor = null;
        for (OAuthAccessor a : OAuthProcessor.ALL_TOKENS) {
            if (a.requestToken != null) {
                if (a.requestToken.equals(consumer_token)) {
                    accessor = a;
                    break;
                }
            } else if (a.accessToken != null) {
                if (a.accessToken.equals(consumer_token)) {
                    accessor = a;
                    break;
                }
            }
        }

        if (accessor == null) {
            throw new OAuthProblemException("token_expired");
        }

        return accessor;
    }

    /**
     * Set the access token
     *
     * @param accessor       oauth accessor
     * @param openFlameToken authentication token returned after authentication on OpenFlame
     * @throws OAuthException OAuth Exception
     */
    public static synchronized void markAsAuthorized(OAuthAccessor accessor, AuthenticationToken openFlameToken)
            throws OAuthException {
        // first remove the accessor from cache
        OpenFlamePrincipal principal = new UserPrincipal(openFlameToken.getUser());
        OPFContext.initContext(principal, openFlameToken.getToken());

        ALL_TOKENS.remove(accessor);

        accessor.setProperty(OAUTH_OPF_TOKEN_ATTRIBUTE, openFlameToken);

        // update token in local cache
        ALL_TOKENS.add(accessor);
    }

    /**
     * @param accessor
     */
    public static synchronized void signOut(OAuthAccessor accessor) {
        if (accessor != null) {
            ALL_TOKENS.remove(accessor);
        }
    }

    /**
     * @param httpRequest
     * @return
     */
    public static boolean isOAuthRequest(HttpServletRequest httpRequest) {
        String oAuthHeader = httpRequest.getHeader("authorization");
        return StringUtils.isNotBlank(oAuthHeader) && oAuthHeader.contains("oauth_nonce");
    }

    /**
     * @param httpRequest
     * @return
     */
    public static String getSessionToken(HttpServletRequest httpRequest) {
        OAuthMessage oAuthMessage = OAuthServlet.getMessage(httpRequest, null);
        String sessionToken;
        try {
            OAuthAccessor accessor = OAuthProcessor.getAccessor(oAuthMessage);
            if (accessor == null) {
                sessionToken = null;
            } else {
                AuthenticationToken openFlameToken = (AuthenticationToken) accessor.getProperty(OAUTH_OPF_TOKEN_ATTRIBUTE);
                sessionToken = openFlameToken == null ? null : openFlameToken.getToken();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            sessionToken = null;
        } catch (OAuthProblemException e) {
            logger.error(e.getMessage(), e);
            sessionToken = null;
        }
        return sessionToken;
    }

    /**
     * Generate a fresh request token and secret for a consumer.
     *
     * @param accessor accessor
     * @throws OAuthException throws OAuthException
     */
    public static synchronized void generateRequestToken(OAuthAccessor accessor) throws OAuthException {
        // generate oauth_token and oauth_secret
        String consumer_key = (String) accessor.consumer.getProperty(OAUTH_NAME_PROPERTY);
        // generate token and secret based on consumer_key

        // for now use md5 of name + current time as token
        String token_data = consumer_key + System.nanoTime();
        String token = DigestUtils.md5Hex(token_data);
        // for now use md5 of name + current time + token as secret
        String secret_data = consumer_key + System.nanoTime() + token;
        String secret = DigestUtils.md5Hex(secret_data);

        accessor.requestToken = token;
        accessor.tokenSecret = secret;
        accessor.accessToken = null;

        // add to the local cache
        ALL_TOKENS.add(accessor);
    }

    /**
     * Generate a fresh request token and secret for a consumer.
     *
     * @param accessor accessor
     * @throws OAuthException OAuth Exception
     */
    public static synchronized void generateAccessToken(OAuthAccessor accessor)
            throws OAuthException {
        AuthenticationToken openFlameToken =
                (AuthenticationToken) accessor.getProperty(OAUTH_OPF_TOKEN_ATTRIBUTE);
        // first remove the accessor from cache
        ALL_TOKENS.remove(accessor);

        accessor.requestToken = null;
        accessor.accessToken = openFlameToken.getToken();

        // update token in local cache
        ALL_TOKENS.add(accessor);
    }

    /**
     * @param e
     * @param request
     * @param response
     * @param sendBody
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public static void handleException(Exception e, HttpServletRequest request,
                                       HttpServletResponse response, boolean sendBody)
            throws IOException, ServletException {
        String realm = (request.isSecure()) ? "https://" : "http://";
        realm += request.getLocalName();
        OAuthServlet.handleException(response, e, realm, sendBody);
    }


}