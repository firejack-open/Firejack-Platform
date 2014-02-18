/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.web.security.linkedin;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.IAuthenticationProcessor;
import net.firejack.platform.web.security.filter.ISignInProcessor;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedInAuthenticationProcessor implements IAuthenticationProcessor {

    private static final Logger logger = Logger.getLogger(LinkedInAuthenticationProcessor.class);

    public static final String ATTR_OAUTH_REQUEST_TOKEN = "oauthRequestToken";
	public static final String ATTR_OAUTH_ACCESS_TOKEN = "oauthAccessToken";

    private OAuthService service;
    private String linkedInEntryPointUrl;
    private ISignInProcessor signInProcessor;
    private String defaultPageUrl;

    public String getLinkedInEntryPointUrl() {
        return linkedInEntryPointUrl;
    }

    public void setLinkedInEntryPointUrl(String linkedInEntryPointUrl) {
        this.linkedInEntryPointUrl = linkedInEntryPointUrl;
    }

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
    public boolean isAuthenticationCase(HttpServletRequest request) {
        String requestPath = WebUtils.getRequestPath(request);
        return requestPath.equalsIgnoreCase(getLinkedInEntryPointUrl());
    }

    @Override
    public void processAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (StringUtils.isNotBlank(getLinkedInApiKey()) && StringUtils.isNotBlank(getLinkedInSecret())) {
            OAuthService service = getService();

            HttpSession httpSession = request.getSession(true);
            // getting request and access token from session
            Token requestToken = (Token) httpSession.getAttribute(ATTR_OAUTH_REQUEST_TOKEN);
            Token accessToken = (Token) httpSession.getAttribute(ATTR_OAUTH_ACCESS_TOKEN);
            if(requestToken == null && accessToken == null) {
                // generate new request token
                requestToken = service.getRequestToken();
                httpSession.setAttribute(ATTR_OAUTH_REQUEST_TOKEN, requestToken);

                // redirect to LinkedIn auth page
                response.sendRedirect(service.getAuthorizationUrl(requestToken));
            } else {
                String oauthVerifier = request.getParameter("oauth_verifier");
                if (StringUtils.isNotBlank(oauthVerifier)) {
                    // getting access token
                    Verifier verifier = new Verifier(oauthVerifier);
                    accessToken = service.getAccessToken(requestToken, verifier);

                    // store access token as a session attribute
                    httpSession.setAttribute(ATTR_OAUTH_ACCESS_TOKEN, accessToken);

                    // getting user profile
                    OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, "http://api.linkedin.com/v1/people/~:(id,first-name,last-name,industry,headline,email-address)");
                    service.signRequest(accessToken, oauthRequest);
                    Response oauthResponse = oauthRequest.send();
                    try {
                        httpSession.removeAttribute(ATTR_OAUTH_REQUEST_TOKEN);
                        httpSession.removeAttribute(ATTR_OAUTH_ACCESS_TOKEN);

                        String browserIpAddress = request.getRemoteAddr();
                        AuthenticationToken authenticationToken = getOpenFlameToken(oauthResponse, browserIpAddress);

                        if (authenticationToken == null) {
                            logger.error("Authentication token is null.");
                        }
                        getSignInProcessor().processSignInInternal(request, response, authenticationToken);
                        OPFContext.getContext().put(ATTR_OAUTH_ACCESS_TOKEN, accessToken);
                    } catch (BusinessFunctionException e) {
                        logger.error(e.getMessage(), e);
                        cleanSessionInformation(httpSession);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                } else {
                    httpSession.removeAttribute(ATTR_OAUTH_REQUEST_TOKEN);
                    httpSession.removeAttribute(ATTR_OAUTH_ACCESS_TOKEN);
                    response.sendRedirect(request.getRequestURL().toString());
                }
            }
        } else {
            logger.error("LinkedIn consumer key or consumer secret configs were not set.");
            response.sendRedirect(getDefaultPageUrl());
        }
    }

    private OAuthService getService() {
        if (!isEnable())
            throw new IllegalStateException("Service disabled.");

         if (service == null) {
             ServiceBuilder serviceBuilder = new ServiceBuilder().provider(LinkedInApi.class).apiKey(getLinkedInApiKey()).apiSecret(getLinkedInSecret());
             for (String permission : getLinkedInPermissions()) {
                 serviceBuilder.scope(permission);
             }
             service = serviceBuilder.callback(OpenFlameSecurityConstants.getBaseUrl() + "/linkedin-authentication").build();
         }
        return service;
    }

    private boolean isEnable() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(getEnableConfigLookup());
        return config == null ? false : Boolean.valueOf(config.getValue());
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        cleanSessionInformation(session);
    }

    private String getLinkedInApiKey() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(OpenFlameSecurityConstants.getPackageLookup() + ".linkedin-api-key");
        return config != null ? config.getValue() : null;
    }

    private String getLinkedInSecret() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(OpenFlameSecurityConstants.getPackageLookup() + ".linkedin-secret");
        return config != null ? config.getValue() : null;
    }

    protected String getEnableConfigLookup() {
        return OpenFlameSecurityConstants.getPackageLookup() + ".linkedin-enable";
    }

    private List<String> getLinkedInPermissions() {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(OpenFlameSecurityConstants.getPackageLookup() + ".linkedin-permissions");
        List<String> permissions = Arrays.asList("r_basicprofile", "r_emailaddress"); //default linkedin permissions
        if (config != null && StringUtils.isNotBlank(config.getValue())) {
            permissions = Arrays.asList(config.getValue().split("|"));
        }
        return permissions;
    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
        logger.info("initialization of " + this.getClass().getName());
    }

    @Override
    public void release() {
    }

    protected AuthenticationToken getOpenFlameToken(Response oauthResponse, String browserIpAddress) throws BusinessFunctionException {
        Map<String, String> mappedAttributes = getUserInformation(oauthResponse);

        ServiceResponse<AuthenticationToken> response = OPFEngine.AuthorityService.processLinkedInSignIn(mappedAttributes, browserIpAddress);
        AuthenticationToken token;
        if (response == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (response.isSuccess()) {
            token = response.getItem();
        } else {
            logger.error("API Service response has failure status. Reason: " + response.getMessage());
            token = null;
        }
        return token;
    }

    private Map<String, String> getUserInformation(Response oauthResponse) throws BusinessFunctionException {
        Map<String, String> userData = new HashMap<String, String>();
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(oauthResponse.getStream());

            XPath xpath = XPathFactory.newInstance().newXPath();
            // XPath Query for showing all nodes value
            XPathExpression expr = xpath.compile("//person/*");

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList tags = (NodeList) result;
            for (int i = 0; i < tags.getLength(); i++) {
                Element tag = (Element) tags.item(i);
                String name = tag.getTagName();
                String value = tag.getTextContent();
                userData.put(name, value);
            }
        } catch (ParserConfigurationException e) {
            logger.error("Failed to obtain opf authentication token. Precondition failed, reason: " + e.getMessage());
            throw new BusinessFunctionException(e);
        } catch (SAXException e) {
            logger.error("Failed to obtain opf authentication token. Precondition failed, reason: " + e.getMessage());
            throw new BusinessFunctionException(e);
        } catch (IOException e) {
            logger.error("Failed to obtain opf authentication token. Precondition failed, reason: " + e.getMessage());
            throw new BusinessFunctionException(e);
        } catch (XPathExpressionException e) {
            logger.error("Failed to obtain opf authentication token. Precondition failed, reason: " + e.getMessage());
            throw new BusinessFunctionException(e);
        }
        return userData;
    }

    private void cleanSessionInformation(HttpSession session) {
        if (session != null) {
            session.removeAttribute(ATTR_OAUTH_REQUEST_TOKEN);
            session.removeAttribute(ATTR_OAUTH_ACCESS_TOKEN);
        }
    }

}
