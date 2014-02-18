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

package net.firejack.platform.web.security.socials.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.User;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.socials.ScribeBasedAuthenticationProcessor;
import org.apache.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


public class FacebookAuthenticationProcessor extends ScribeBasedAuthenticationProcessor {

    private static final Logger logger = Logger.getLogger(FacebookAuthenticationProcessor.class);

    @Override
    protected Token getRequestToken(HttpServletRequest request) {
        return null;
    }

    @Override
    protected void saveRequestToken(Token requestToken, HttpServletRequest request) {
    }

    @Override
    protected Token getSavedRequestToken(HttpServletRequest request) {
        return null;
    }

    @Override
    protected void clearSavedRequestToken(HttpServletRequest request) {
    }

    @Override
    protected AuthenticationToken onAccessTokenRetrieved(
            String accessToken, String tokenSecret, String browserIpAddress,
            HttpServletRequest request, HttpServletResponse response) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
        User user = facebookClient.fetchObject("me", User.class);
        Long facebookUserID = Long.parseLong(user.getId());
        Map<String, String> mappedAttributes = new HashMap<String, String>();
        mappedAttributes.put(FacebookConstants.FB_ATTR_USERNAME, user.getUsername());
        mappedAttributes.put(FacebookConstants.FB_ATTR_FIRST_NAME, user.getFirstName());
        mappedAttributes.put(FacebookConstants.FB_ATTR_LAST_NAME, user.getLastName());
        mappedAttributes.put(FacebookConstants.FB_ATTR_ABOUT_ME, user.getAbout());
        NamedFacebookType location = user.getLocation();
        mappedAttributes.put(FacebookConstants.FB_ATTR_CURRENT_LOCATION, location == null ? null : location.getName());
        mappedAttributes.put(FacebookConstants.FB_ATTR_LOCALE, user.getLocale());
        mappedAttributes.put(FacebookConstants.FB_ATTR_PIC_SQUARE,
                "http://graph.facebook.com/" + user.getUsername() + "/picture?type=square");
        mappedAttributes.put(FacebookConstants.FB_ATTR_PIC_BIG,
                "http://graph.facebook.com/" + user.getUsername() + "/picture?type=large");
        ServiceResponse<AuthenticationToken> authResponse =
                OPFEngine.AuthorityService.processFacebookIdSignIn(facebookUserID, mappedAttributes, browserIpAddress);
        AuthenticationToken authToken;
        if (authResponse == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (authResponse.isSuccess()) {
            authToken = authResponse.getItem();
        } else {
            logger.error("API Service response has failure status. Reason: " + authResponse.getMessage());
            authToken = null;
        }

        return authToken;
    }

    @Override
    protected OAuthService provideService() {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        serviceBuilder.provider(FacebookApi.class)
                .apiKey(getConsumerKey())
                .apiSecret(getConsumerSecret())
                .callback(getCallbackUrl());
        return serviceBuilder.build();
    }

    @Override
    protected String getConsumerKeyConfigLookup() {
        return OpenFlameSecurityConstants.getPackageLookup() + ".facebook-api-key";
    }

    @Override
    protected String getConsumerSecretConfigLookup() {
        return OpenFlameSecurityConstants.getPackageLookup() + ".facebook-secret";
    }

    @Override
    protected String getEnableConfigLookup() {
        return OpenFlameSecurityConstants.getPackageLookup() + ".facebook-enable";
    }

    protected String getCallbackUrl() {
        return OpenFlameSecurityConstants.getBaseUrl() + getAuthenticationEntryPointUrl();
    }
}