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

package net.firejack.platform.web.security.model;


import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class OpenFlameSecurityConstants {

    private static final Logger logger = Logger.getLogger(OpenFlameSecurityConstants.class);

    public static final Long EMPTY_SECURED_RECORD_ID = -1L;
    public static final String OPF_CERTIFICATE_HEADER = "opf-certificate";
    public static final String USER_NAME_REQUEST_HEADER = "user-name";
    public static final String EMAIL_REQUEST_HEADER = "email";
    public static final String USER_PASSWORD_REQUEST_HEADER = "password";
    public static final String IP_ADDRESS_HEADER = "browser.ip.address";
    public static final String CLIENT_INFO_HEADER = "client.info";
    public static final String TOKEN_REQUEST_HEADER = "session.token";
    public static final String RESET_PASSWORD_PAGE_URL_HEADER = "reset.password.page.url";
    public static final String STANDARD_ID_HEADER = "standard.id";
    public static final String DEFAULT_SM_ROLE_CONFIG_LOOKUP = "net.firejack.platform.sm-default-role-name";
    public static final String SM_CONFIGS_PATH_ENV_VARIABLE = "SM_CONFIGS";

    public static final String PARENT_ID_REQUEST_HEADER = "parent_id";
    public static final String PARENT_TYPE_REQUEST_HEADER = "parent_type";
    public static final String OBJECT_ID_REQUEST_HEADER = "objectId";

    /**
     * OpenID related constants
     */
    public static final String OPENID_PARAMETER_NAME = "openid_identifier";
    public static final String OPENID_HEADER_PREFIX = "openid-header";
    public static final String FACEBOOK_HEADER_PREFIX = "facebook-header";
    /**
     * OAuth related constants
     */
    public static final String OAUTH_ACTIVE_REQUEST_ATTRIBUTE = "OAUTH_ACTIVE_REQUEST_ATTRIBUTE";
    public static final String OAUTH_OPF_TOKEN_ATTRIBUTE = "openFlameToken";
    //    public static final String OAUTH_ACCOUNT_PARAMETER = "oauthAccount";
//    public static final String OAUTH_PASSWORD_PARAMETER = "oauthPassword";
    public static final String OAUTH_CALLBACK_PARAMETER = "oauth_callback";
    public static final String OAUTH_TOKEN_PARAMETER = "oauth_token";
    public static final String OAUTH_TOKEN_SECRET_PARAMETER = "oauth_token_secret";
    public static final String OAUTH_ACCESSOR_SECRET_PARAMETER = "oauth_accessor_secret";
    public static final String OAUTH_NAME_PROPERTY = "name";

    public static final String AUTHENTICATION_TOKEN_ATTRIBUTE = "opf.authentication.token";
    public static final String AUTHENTICATION_TOKEN_EXPIRED_ATTRIBUTE = "opf.authentication.token.expired";
    public static final String BUSINESS_CONTEXT_ATTRIBUTE = "BUSINESS_CONTEXT_ATTRIBUTE";
    public static final String CLIENT_IP_ADDRESS_ATTRIBUTE = "client.ip.address";

    public static final String PASSWORD_ENCRYPTED_PRIVATE_KEY = "PASSWORD_ENCRYPTED_PRIVATE_KEY";

    public static final String GOOGLE_ANALYTICS_CONFIG_LOOKUP = "net.firejack.platform.defaults.google-analytics-id";

    /**
     * API calls related constants
     */
    public static final String MARKER_HEADER = "OPF_API_CALL";
    public static final MessageFormat API_SECURITY_ERROR_RESPONSE =
            new MessageFormat("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<model>" +
                    "<message>{0}</message>" +
                    "<success>false</success>" +
                    "</model>"
            );
    public static final String API_401_ERROR_RESPONSE =
            API_SECURITY_ERROR_RESPONSE.format(new String[]{"Access Denied."});
    public static final String API_403_ERROR_RESPONSE =
            API_SECURITY_ERROR_RESPONSE.format(new String[]{"Access denied for guest user."});
    public static final String API_406_ERROR_RESPONSE =
            API_SECURITY_ERROR_RESPONSE.format(new String[]{"Session has expired."});
    public static final String API_403_DIFFERENT_IP_ERROR_RESPONSE =
            API_SECURITY_ERROR_RESPONSE.format(new String[]{"Attempt to use session token from IP address that is not permitted."});

    /**
     * Static property fields
     */
    private static String baseUrl;

    private static String packageLookup;

    private static boolean clientContext;

    private static OPFContext SYSTEM_USER_CTX;
    private static boolean siteMinderDebugMode;
    private static boolean siteMinderAuthSupported;
    private static String siteMinderAuthIdHeader;
    private static String opfDirectUrl;

    //private static ThreadLocal<String> baseUrlHolder = new InheritableThreadLocal<String>();

    /**
     * @return
     */
    public static String getBaseUrl() {
        //uncomment if it would be necessary to calculate incoming request base url instead of using configured config value
        //this might be useful for productivity - request coming from the same server will be handled faster
        //than requests coming from outside the server
//        return baseUrlHolder.get();
        return OpenFlameSecurityConstants.baseUrl;
    }

    /**
     * @param baseUrl
     */
    public static void setBaseUrl(String baseUrl) {
        //uncomment if it would be necessary to calculate incoming request base url instead of using configured config value
        //this might be useful for productivity - request coming from the same server will be handled faster
        //than requests coming from outside the server
        /*if (baseUrl == null) {
                      baseUrlHolder.remove();
                  } else {
                      baseUrlHolder.set(baseUrl);
                  }
                  //OpenFlameSecurityConstants.baseUrl = baseUrl;*/
        OpenFlameSecurityConstants.baseUrl = baseUrl;
    }

    public static Map<String, String> getBaseUrlInfo() {
        Map<String, String> urlInfo = new HashMap<String, String>();
        try {
            URI uri = new URI(OpenFlameSecurityConstants.baseUrl);
            urlInfo.put("scheme", uri.getScheme());
            urlInfo.put("host", uri.getHost());
            urlInfo.put("port", String.valueOf(uri.getPort()));
            urlInfo.put("path", uri.getPath());
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return urlInfo;
    }

//    /**
//     * @return
//     */
//    public static String getSTSBaseUrl() {
//        return OpenFlameSecurityConstants.stsBaseUrl;
//    }
//
//    /**
//     * @param stsBaseUrl
//     */
//    public static void setSTSBaseUrl(String stsBaseUrl) {
//        OpenFlameSecurityConstants.stsBaseUrl = stsBaseUrl;
//    }

    /**
     * @return
     */
    public static String getPackageLookup() {
        return packageLookup;
    }

    /**
     * @param lookup
     */
    public static void setPackageLookup(String lookup) {
	    if (StringUtils.isBlank(packageLookup)) {
		    packageLookup = lookup;
	    }
    }

	public static boolean isClientContext() {
        return clientContext;
    }

    public static void setClientContext(boolean clientContext) {
        OpenFlameSecurityConstants.clientContext = clientContext;
    }

    public static void printXmlErrorToResponse(HttpServletResponse httpResponse, String error)
            throws IOException {
        if (!httpResponse.isCommitted()) {
            httpResponse.setContentType(MediaType.APPLICATION_XML);
            httpResponse.getOutputStream().println(error);
            httpResponse.getOutputStream().close();
        }
    }

    public static void setSystemUserContext(OPFContext systemUserContext) {
        OpenFlameSecurityConstants.SYSTEM_USER_CTX = systemUserContext;
    }

    public static OPFContext getSystemUserContext() {
        return OpenFlameSecurityConstants.SYSTEM_USER_CTX;
    }

    public static boolean isSiteMinderAuthSupported() {
        return siteMinderAuthSupported;
    }

    public static void setSiteMinderAuthSupported(boolean siteMinderAuthSupported) {
        OpenFlameSecurityConstants.siteMinderAuthSupported = siteMinderAuthSupported;
    }

    public static String getSiteMinderAuthIdHeader() {
        return siteMinderAuthIdHeader;
    }

    public static void setSiteMinderAuthIdHeader(String siteMinderAuthIdHeader) {
        OpenFlameSecurityConstants.siteMinderAuthIdHeader = siteMinderAuthIdHeader;
    }

    public static boolean isSiteMinderDebugMode() {
        return siteMinderDebugMode;
    }

    public static void setSiteMinderDebugMode(boolean siteMinderDebugMode) {
        OpenFlameSecurityConstants.siteMinderDebugMode = siteMinderDebugMode;
    }

    public static String getOpfDirectUrl() {
        return OpenFlameSecurityConstants.opfDirectUrl;
    }

    public static void setOpfDirectUrl(String opfDirectUrl) {
        OpenFlameSecurityConstants.opfDirectUrl = opfDirectUrl;
    }

}