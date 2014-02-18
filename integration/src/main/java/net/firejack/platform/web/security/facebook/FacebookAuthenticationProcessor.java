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

package net.firejack.platform.web.security.facebook;

import com.google.code.facebookapi.*;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.IAuthenticationProcessor;
import net.firejack.platform.web.security.filter.ISignInProcessor;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;


@Component
@Deprecated
public class FacebookAuthenticationProcessor implements IAuthenticationProcessor {

    private static final Logger logger = Logger.getLogger(FacebookAuthenticationProcessor.class);

    public static String FACEBOOK_API_KEY;
    public static String FACEBOOK_SECRET;

    private static final String FACEBOOK_USER_CLIENT = "facebook.user.client";
    private static final String MOBILE_CLIENT = "mobile_client";

    private String facebookEntryPointUrl;
    private FacebookIDConsumer facebookIDConsumer;
    private ISignInProcessor signInProcessor;
    private String defaultPageUrl;

    public String getFacebookEntryPointUrl() {
        return facebookEntryPointUrl;
    }

    public void setFacebookEntryPointUrl(String facebookEntryPointUrl) {
        this.facebookEntryPointUrl = facebookEntryPointUrl;
    }

    private FacebookIDConsumer getFacebookIDConsumer() {
        if (facebookIDConsumer == null) {
            facebookIDConsumer = new FacebookIDConsumer();
        }
        return facebookIDConsumer;
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
        return requestPath.equalsIgnoreCase(getFacebookEntryPointUrl()) ||
                requestPath.equalsIgnoreCase(getMobileClientAuthenticationInfoPage());
    }

    @Override
    public void processAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String facebookUserId = "id";
        String ipAddress = "ip";
        
        if (StringUtils.isNotBlank(FACEBOOK_API_KEY) && StringUtils.isNotBlank(FACEBOOK_SECRET)) {
            try {
                MDC.put(ipAddress, request.getRemoteAddr());
    
                HttpSession session = request.getSession(true);
                IFacebookRestClient<Document> userClient = getUserClient(session);
                if (userClient == null) {
                    logger.debug("User session doesn't have a Facebook API client setup yet. Creating one and storing it in the user's session.");
                    userClient = new FacebookXmlRestClient(FACEBOOK_API_KEY, FACEBOOK_SECRET);
                    session.setAttribute(FACEBOOK_USER_CLIENT, userClient);
                }
    
                logger.trace("Creating a FacebookWebappHelper, which copies fb_ request param data into the userClient");
                FacebookWebappHelper<Document> facebook = new FacebookWebappHelper<Document>(request, response, FACEBOOK_API_KEY, FACEBOOK_SECRET, userClient);
    
                if ( facebook.isLogin() ) {
                    if (WebUtils.getRequestPath(request).equalsIgnoreCase(getMobileClientAuthenticationInfoPage())) {
                        Object authInfo;
                        try {
                            OPFContext context = OPFContext.getContext();
                            if (context == null) {
                                authInfo = new ServiceResponse("Failed to get context information", false);
                            } else if (context.getPrincipal().isGuestPrincipal()) {
                                authInfo = new ServiceResponse("Guest user tries to execute forbidden operations", false);
                            } else {
                                authInfo = new AuthenticationToken(context.getSessionToken(), null);
                            }
                        } catch (ContextLookupException e) {
                            authInfo = new ServiceResponse(e.getMessage(), false);
                        }
                        processMobilePhoneResponse(authInfo, request, response);
                    } else {

                        long facebookUserID;
                        try {
                            facebookUserID = userClient.users_getLoggedInUser();
                        } catch (FacebookException ex) {
                            response.sendError(
                                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    "Error while fetching user's facebook ID");
                            logger.error("Error while getting cached (supplied by request params) value "
                                    + "of the user's facebook ID or while fetching it from the Facebook service "
                                    + "if the cached value was not present for some reason. Cached value = {}"
                                    + userClient.getCacheUserId());
                            return;
                        }

                        try {
                            Document document = facebook.getFacebookRestClient().users_getInfo(Arrays.asList(facebookUserID),
                                    Arrays.asList(ProfileField.NAME, ProfileField.FIRST_NAME, ProfileField.LAST_NAME,
                                            ProfileField.ABOUT_ME, ProfileField.CURRENT_LOCATION, ProfileField.LOCALE,
                                            ProfileField.PIC_SQUARE, ProfileField.PIC_BIG));
                            String browserIpAddress = (String) session.getAttribute(
                                    OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE);
                            AuthenticationToken authenticationToken =
                                    getFacebookIDConsumer().processResponse(facebookUserID, document, browserIpAddress);
                            boolean isMobileClient = isMobileClient(request);
                            if (isMobileClient && authenticationToken != null) {
                                String token = authenticationToken.getToken();
                                Cookie authenticationTokenCookie = new Cookie(
                                        OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE,
                                        URLEncoder.encode(token, "utf8"));
                                authenticationTokenCookie.setPath("/");
                                //authenticationTokenCookie.setDomain(this.baseUrl);
                                response.addCookie(authenticationTokenCookie);

                                OpenFlamePrincipal principal = new UserPrincipal(authenticationToken.getUser());
                                OPFContext.initContext(principal, token);
                                response.sendRedirect(OpenFlameSecurityConstants.getBaseUrl() + getMobileClientAuthenticationInfoPage());
                            } else {
                                getSignInProcessor().processSignInInternal(request, response, authenticationToken);
                            }
                        } catch (FacebookException e) {
                            logger.error(e.getMessage(), e);
                        }
                        MDC.put(facebookUserId, String.valueOf(facebookUserID));
                    }
                } else {
                    session.setAttribute(OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE, request.getRemoteAddr());
//                    String nextPage = request.getRequestURI();
                    /* cut out the first /, the context path and the 2nd / */
//                    nextPage = nextPage.substring(nextPage.indexOf("/", 1));
//                    nextPage = OpenFlameSecurityConstants.getBaseUrl() + nextPage;
//                    nextPage = URLEncoder.encode(nextPage, "UTF8");
//                    String url = facebook.getLoginUrl(nextPage, facebook.inFrame());
                    boolean isMobileClient = isMobileClient(request);
                    /*String nextPage;
                    if (isMobileClient) {
                        nextPage = request.getRequestURI();
                        StringBuilder sb = new StringBuilder(OpenFlameSecurityConstants.getBaseUrl());
                        sb.append(nextPage.substring(nextPage.indexOf("/", 1)))
                                .append('?').append(MOBILE_CLIENT).append('=')
                                .append(Boolean.TRUE);
                        nextPage = URLEncoder.encode(sb.toString(), "UTF8");
                    } else {
                        nextPage = null;
                    }*/

                    /*String url;
                    if (isMobileClient) {
                        url = "https://www.facebook.com/dialog/oauth?client_id=" + FACEBOOK_API_KEY;
                        url += "&redirect_uri=";
                        url += nextPage;
                        url += "&response_type=token";
                        url += "&display=touch";
                    } else {
                        url = getLoginUrl(null, null, facebook.inFrame());
                    }*/
                    String url = getLoginUrl(null, null, facebook.inFrame());
                    if (isMobileClient) {
                        url += "&display=popup";
                        session.setAttribute(MOBILE_CLIENT, true);
                    }
                    try {
                        response.sendRedirect( url );
                    } catch (IOException e) {
                        throw new RuntimeException( e );
                    }
                }
            } finally {
                MDC.remove(ipAddress);
                MDC.remove(facebookUserId);
            }
        } else {
            response.sendRedirect(getDefaultPageUrl());
        }
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        session.removeAttribute(FACEBOOK_USER_CLIENT);
        session.removeAttribute(OpenFlameSecurityConstants.CLIENT_IP_ADDRESS_ATTRIBUTE);//do we have to remove this attribute at all?
        session.removeAttribute(MOBILE_CLIENT);

        Cookie facebookCookie = WebUtils.getRequestCookie(request, FACEBOOK_API_KEY);
        if (facebookCookie != null) {
            facebookCookie.setMaxAge(0);
            facebookCookie.setPath( request.getContextPath() + "/" );
            response.addCookie(facebookCookie);
        }
        Cookie sessionKeyCookie = WebUtils.getRequestCookie(request, FACEBOOK_API_KEY + "_session_key");
        if (sessionKeyCookie != null) {
            sessionKeyCookie.setMaxAge(0);
            sessionKeyCookie.setPath( request.getContextPath() + "/" );
            response.addCookie(sessionKeyCookie);
        }
        Cookie userCookie = WebUtils.getRequestCookie(request, FACEBOOK_API_KEY + "_user");
        if (userCookie != null) {
            userCookie.setMaxAge(0);
            userCookie.setPath( request.getContextPath() + "/" );
            response.addCookie(userCookie);
        }
    }

    private String getFacebookApiKeyConfigLookup() {
        return OpenFlameSecurityConstants.getPackageLookup() + ".facebook-api-key";
    }

    private String getFacebookSecretConfigLookup() {
        return OpenFlameSecurityConstants.getPackageLookup() + ".facebook-secret";
    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
//        logger.info("initialize");
//        ServiceResponse<Config> facebookApiKeyResponse = OPFEngine.ConfigService.findByLookup(getFacebookApiKeyConfigLookup(), ConfigType.STRING);
//        Config facebookApiKeyConfig = facebookApiKeyResponse.getItem();
//        if (facebookApiKeyConfig != null) {
//            FACEBOOK_API_KEY = facebookApiKeyConfig.getValue();
//        }
//        ServiceResponse<Config> facebookSecretResponse = OPFEngine.ConfigService.findByLookup(getFacebookSecretConfigLookup(), ConfigType.STRING);
//        Config facebookSecretConfig = facebookSecretResponse.getItem();
//        if (facebookSecretConfig != null) {
//            FACEBOOK_SECRET = facebookSecretConfig.getValue();
//        }
    }

    public static FacebookXmlRestClient getUserClient(HttpSession session) {
        return (FacebookXmlRestClient)session.getAttribute(FACEBOOK_USER_CLIENT);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused")
    public static String xmlToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void processMobilePhoneResponse(
            Object respObject, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String requestType = request.getContentType() == null ?
                request.getHeader("accept") : request.getContentType();
        if (MediaType.APPLICATION_XML.equalsIgnoreCase(requestType)) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance();
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                ServletOutputStream out = response.getOutputStream();
                marshaller.marshal(respObject, out);
                out.close();
            } catch (JAXBException e) {
                logger.error(e.getMessage(), e);
                OpenFlameSecurityConstants.printXmlErrorToResponse(
                        response, OpenFlameSecurityConstants.API_SECURITY_ERROR_RESPONSE.format(
                        new String[]{e.getMessage()}));

            }
        } else {
            String jsonData;
            try {
                jsonData = WebUtils.serializeObjectToJSON(respObject);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                jsonData = "{ success: false; message: '" + e.getMessage() + "' }";
            }
            ServletOutputStream out = response.getOutputStream();
            out.println(jsonData);
            out.close();
        }
    }

    /**
	 * Returns the url that facebook uses to prompt the user to login to this application.
	 *
     * @param domainPart domain part
	 * @param next indicates the page to which facebook should redirect the user has logged in.
     * @param canvas canvas
     *
     * @return login url
	 */
	public String getLoginUrl(String domainPart, String next, boolean canvas ) {
		String url = FacebookWebappHelper.getFacebookUrl(domainPart) + "/login.php?v=1.0&api_key=" + FACEBOOK_API_KEY;
		try {
			url += next != null ? "&next=" + URLEncoder.encode( next, "UTF-8" ) : "";
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
		url += canvas ? "&canvas=true" : "";
		return url;
	}

    @Override
    public void release() {
    }

    private String getMobileClientAuthenticationInfoPage() {
        return getFacebookEntryPointUrl() + "/mobile-token-info";
    }

    private boolean isMobileClient(HttpServletRequest request) {
        String mobileClientParam = request.getParameter(MOBILE_CLIENT);
        boolean result = mobileClientParam != null && Boolean.valueOf(mobileClientParam);
        HttpSession session = request.getSession(false);
        if (!result && session != null && session.getAttribute(MOBILE_CLIENT) != null) {
            result = (Boolean) session.getAttribute(MOBILE_CLIENT);
        }
        return result;
    }

}