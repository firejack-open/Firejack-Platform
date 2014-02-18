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

package net.firejack.platform.web.security.filter;

import com.sun.jersey.core.util.Base64;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.action.ActionDetectorFactory;
import net.firejack.platform.web.security.action.IActionDetector;
import net.firejack.platform.web.security.action.container.CachedActionContainerFactory;
import net.firejack.platform.web.security.action.container.IActionContainerFactory;
import net.firejack.platform.web.security.exception.SecurityConfigurationException;
import net.firejack.platform.web.security.exception.SecurityRuntimeException;
import net.firejack.platform.web.security.facebook.MobileFacebookAuthenticationProcessor;
import net.firejack.platform.web.security.filter.handlers.*;
import net.firejack.platform.web.security.filter.message.FilterMessageStock;
import net.firejack.platform.web.security.filter.message.FilterMessageType;
import net.firejack.platform.web.security.filter.rules.*;
import net.firejack.platform.web.security.linkedin.LinkedInAuthenticationProcessor;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.attribute.CachedContextFacade;
import net.firejack.platform.web.security.model.context.ContextContainerDelegate;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import net.firejack.platform.web.security.navigation.CachedNavElementContainerFactory;
import net.firejack.platform.web.security.navigation.INavElementContainerFactory;
import net.firejack.platform.web.security.oauth.provider.OAuthAuthenticationProvider;
import net.firejack.platform.web.security.oauth.provider.OAuthProcessor;
import net.firejack.platform.web.security.openid.OpenIDAuthenticationProcessor;
import net.firejack.platform.web.security.openid.OpenIDConsumer;
import net.firejack.platform.web.security.permission.CachedPermissionContainerFactory;
import net.firejack.platform.web.security.permission.IPermissionContainerFactory;
import net.firejack.platform.web.security.permission.IPermissionContainerRule;
import net.firejack.platform.web.security.protocol.ProtocolMapping;
import net.firejack.platform.web.security.protocol.ProtocolMappings;
import net.firejack.platform.web.security.resource.CachedResourceLocationContainerFactory;
import net.firejack.platform.web.security.resource.IResourceLocationContainerFactory;
import net.firejack.platform.web.security.resource.IResourceLocationContainerRule;
import net.firejack.platform.web.security.session.LicenseSession;
import net.firejack.platform.web.security.siteminder.SiteMinderAuthenticationProcessor;
import net.firejack.platform.web.security.socials.facebook.FacebookAuthenticationProcessor;
import net.firejack.platform.web.security.sr.CachedSecuredRecordInfoContainerFactory;
import net.firejack.platform.web.security.sr.ISecuredRecordInfoContainerFactory;
import net.firejack.platform.web.security.sr.ISpecifiedIdsFilterContainerFactory;
import net.firejack.platform.web.security.sr.SpecifiedIdsFilterContainerFactory;
import net.firejack.platform.web.security.twitter.MobileTwitterAuthenticationProcessor;
import net.firejack.platform.web.security.twitter.TwitterAuthenticationProcessor;
import net.firejack.platform.web.security.x509.KeyUtils;
import net.firejack.platform.web.statistics.manager.DefaultStatisticsManager;
import net.firejack.platform.web.statistics.manager.IStatisticsManager;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;
import org.apache.log4j.Logger;
import org.openid4java.consumer.ConsumerException;
import sun.misc.BASE64Decoder;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.util.*;

/**
 * Base Security Filter class. This class contains base logic for OpenFlame security processing.
 * This class is standalone and does not require any additional servlets or listeners declared in web.xml.
 * Security filter requires configuration of several parameters. They are :
 * <p/>
 * sign-in-page-url                   - relative url to page that contains sign-n form
 * sign-in-handler-url                - relative url configured to receive and handle posted sign in form data
 * sign-out-handler-url               - relative url configured for sign out processing
 * username-form-parameter            - parameter name that aimed to provide username info in sign in data post
 * password-form-parameter            - parameter name that aimed to provide password info in sign in data post
 * access-denied-page                 - relative url for access denied page
 * default-page                       - relative url for default page. If authentication processed successfully, then application redirects user browser to this page.
 * protocol-prefixes                  - this parameter's value contains mapping of protocol entry points. For instance, filter may have following configuration of this parameter:
 * <p/>
 * <init-param>
 * <param-name>protocol-prefixes</param-name>
 * <param-value>
 * <![CDATA[<protocol-mappings>
 * <protocol name="HTTP" prefix="/platform"/>
 * <protocol name="HTTP" prefix="/rest"/>
 * <protocol name="SOAP" prefix="/ws"/>
 * </protocol-mappings>]]>
 * </param-value>
 * </init-param>
 * <p/>
 * package-lookup                     - this parameter specifies lookup of package associated with hosted application. This lookup narrowing range of elements retrieved from DB.
 * <p/>
 * To configure OpenId login hosted application should specify following parameters:
 * <p/>
 * openid-sign-in-handler-url         - relative url aimed to process openId sign in
 * openid-parameter                   - openId identifier request parameter
 * <p/>
 * Also, filters derived from this class may have own required parameters.
 *
 * @see net.firejack.platform.core.model.registry.EntityProtocol
 */
public class OpenFlameFilter implements Filter, ISecurityFilter, ISignInProcessor {

    public static final String PARAM_SIGN_IN_PAGE_URL = "sign-in-page-url";
    public static final String PARAM_SIGN_IN_HANDLER = "sign-in-handler-url";
    public static final String PARAM_RESET_PASSWORD_HANDLER = "reset-password-handler-url";
    public static final String PARAM_SIGN_OUT_HANDLER_URL = "sign-out-handler-url";
    public static final String PARAM_OPEN_ID_SIGN_IN_HANDLER_URL = "openid-sign-in-handler-url";
    public static final String PARAM_OPEN_ID_IDENTIFIER = "openid-parameter";
    public static final String PARAM_FACEBOOK_SIGN_IN_HANDLER_URL = "facebook-sign-in-handler-url";
    public static final String PARAM_TWITTER_SIGN_IN_HANDLER_URL = "twitter-sign-in-handler-url";
    public static final String PARAM_LINKEDIN_SIGN_IN_HANDLER_URL = "linkedin-sign-in-handler-url";
    public static final String PARAM_OAUTH_PROVIDER_SUPPORTED = "oauth-provider-supported";
    public static final String PARAM_OAUTH_PROVIDER_AUTHORIZE_PAGE = "oauth-provider-authorize-page";
    public static final String PARAM_USERNAME = "username-form-parameter";
    public static final String PARAM_PASSWORD = "password-form-parameter";
    public static final String PARAM_ENCRYPTED = "encrypted-form-parameter";
    public static final String PARAM_EMAIL = "email-form-parameter";
    public static final String PARAM_ACCESS_DENIED_PAGE = "access-denied-page";
    public static final String PARAM_DEFAULT_TARGET_PAGE = "default-page";
    public static final String PARAM_PROTOCOL_PREFIXES = "protocol-prefixes";
    public static final String PARAM_PACKAGE_LOOKUP = "package-lookup";
    public static final String PARAM_STATS_BUFFER_VOLUME = "stats-buffer-volume";
    public static final String PARAM_STATS_BUFFER_PEAK = "stats-buffer-peak";
    public static final String PARAM_STATS_BUFFER_INSERT_SIZE = "stats-buffer-insert-size";
    public static final String PARAM_REDIRECT = "redirect";
    public static final String STATS_CHECK = "/status/war";

    public static final String MSG_ERROR_FAILED_TO_INSTANTIATE_OPENID_CONSUMER = "Failed to instantiate " + OpenIDConsumer.class;
    private static final String MSG_INAPPROPRIATE_IP_ADDRESS = "Trying to use token corresponded to different IP address.";

    private static final Logger logger = Logger.getLogger(OpenFlameFilter.class);
    protected Map<String, Object> parametersMap = new HashMap<String, Object>();
    protected ContextContainerDelegate contextContainerDelegate;
    private IActionDetector actionDetector;
    private OpenIDConsumer openIDConsumer;
    private IAuthorizationRule authorizationRule;
    private IAuthenticationSuccessHandler authenticationSuccessHandler;
    private IAuthenticationFailureHandler authenticationFailureHandler;
    private IAccessDeniedHandler accessDeniedHandler;
    private IStatisticsManager statisticsManager;
    private List<IFilterFlowInterceptor> filterFlowInterceptorList;
    private BatchAuthenticationProcessor authenticationProcessor;
    private LicenseSession licenseSession;
	private static ThreadLocal<String> pageUID = new InheritableThreadLocal<String>();
    private boolean isFilterInitialized = false;
    private List<String> notRedirectUrls;

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        OpenFlameSecurityConstants.setClientContext(true);
        checkParameters(filterConfig);
        ServletContext servletContext = filterConfig.getServletContext();
        initializeContextContainerDelegate(servletContext);
        OPFContext.initContextContainerDelegate(this.contextContainerDelegate);
        OpenFlameSecurityConstants.setBaseUrl(ConfigContainer.get("base.url"));
        OpenFlameSecurityConstants.setPackageLookup(getSParameter(PARAM_PACKAGE_LOOKUP));
        OPFContext.initPermissionContainerFactory(populatePermissionContainerFactory());
        OPFContext.initActionContainerFactory(populateActionContainerFactory());
        OPFContext.initResourceLocationContainerFactory(populateResourceLocationContainerFactory());
        OPFContext.initNavElementContainerFactory(populateNavElementContainerFactory());
        OPFContext.initSecuredRecordContainerFactory(populateSecuredRecordContainerFactory());
        OPFContext.initSpecifiedIdsFilterContainerFactory(populateSpecifiedIdsFilterContainerFactory());
        getAuthenticationProcessor().initialize(filterConfig);

        initNotRedirectUrls();

//        String openFlameUrl = Env.FIREJACK_URL.getValue();
//        OpenFlameSecurityConstants.setSTSBaseUrl(openFlameUrl);

        //initializeSessionExpirationCheckStrategy();
        this.licenseSession = LicenseSession.getInstance();
        this.getStatisticsTracker().scheduleTracking();
        this.isFilterInitialized = true;
    }

    protected void initNotRedirectUrls() {
        notRedirectUrls = new ArrayList<String>();
        notRedirectUrls.add(getSParameter(PARAM_ACCESS_DENIED_PAGE));
        notRedirectUrls.add(getSParameter(PARAM_SIGN_OUT_HANDLER_URL));
        notRedirectUrls.add(getSParameter(PARAM_SIGN_IN_HANDLER));
//        notRedirectUrls.add(OpenFlameSecurityConstants.getBaseUrl() + getSParameter(PARAM_SIGN_IN_HANDLER));
    }

    /**
     * Method responsible for filter flow
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if (!this.isFilterInitialized) {
            httpResponse.sendError(HttpServletResponse.SC_CONFLICT, "Application is not initialized yet. Please wait a few seconds...");
            return;
        } else if (STATS_CHECK.equals(httpRequest.getServletPath())) {
	        httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

	    if (!ConfigContainer.isAppInstalled()) {
		    RequestDispatcher rd = httpRequest.getRequestDispatcher("/maintenance.jsp");
		    rd.forward(httpRequest, httpResponse);
		    return;
	    }

        //uncomment if it would be necessary to calculate incoming request base url instead of using configured config value
        //this might be useful for productivity - request coming from the same server will be handled faster
        //than requests coming from outside the server
        //onBeforeFilterRequest(httpRequest);

        EntityProtocol protocol = findProtocol(httpRequest);
        if (protocol == EntityProtocol.SOAP) {
            //-> Don't do anything meaningful - Log ws request and then delegate security handling to ws handlers.
            logger.info("Delegating SOAP-request [" + WebUtils.getRequestPath(httpRequest) + "] to ws handlers...");
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
	        String pageUID = httpRequest.getHeader("Page-UID");
            if (StringUtils.isBlank(pageUID)) {
                pageUID = WebUtils.getQueryParam(httpRequest, "Page-UID");
            }
	        OpenFlameFilter.setPageUID(pageUID);

//	        processSystemAccess(httpRequest);
            boolean tokenIsValid = true;
            try {
                this.contextContainerDelegate.initializeOpenFlameContext(httpRequest);
            } catch (SecurityRuntimeException e) {
                tokenIsValid = false;
                processTokenSecurityViolation(httpRequest, httpResponse);
            }
            if (tokenIsValid) {
                //process authentication and authorization
                processGuestAccess();

                OPFContext context = OPFContext.getContext();
                context.analyzeRequest(httpRequest);
                context.reset();

                if (!licenseSession.verify(context.getPrincipal())){
                    FilterMessageStock.getInstance().addFilterMessage(FilterMessageType.ERROR, "Too many active sessions");
                    processSignOut(httpRequest, httpResponse);
                    return;
                }

                updateContextInternal(httpRequest);

                if (context.getCurrentRequestPath().equalsIgnoreCase(getSignInHandlerUrl()) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
                    processSignIn(httpRequest, httpResponse);
                } else if (context.getCurrentRequestPath().equalsIgnoreCase(getResetPasswordHandlerUrl()) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
                    processResetPassword(httpRequest, httpResponse);
                } else if (context.getCurrentRequestPath().equalsIgnoreCase(getResetPasswordHandlerUrl()) && "GET".equalsIgnoreCase(httpRequest.getMethod())) {
                    processConfirmResetPassword(httpRequest, httpResponse);
                } else if (context.getCurrentRequestPath().equalsIgnoreCase(getSignOutUrl())) {
                    licenseSession.remove(context.getPrincipal());
                    processSignOut(httpRequest, httpResponse);
                } else if (getAuthenticationProcessor().isAuthenticationCase(httpRequest)) {
                    getAuthenticationProcessor().processAuthentication(httpRequest, httpResponse, filterChain);
                } else {
                    Throwable th = null;
                    if (OAuthProcessor.isOAuthRequest(httpRequest)) {
                        try {
                            OAuthMessage oAuthMessage = OAuthServlet.getMessage(httpRequest, null);
                            OAuthAccessor accessor = OAuthProcessor.getAccessor(oAuthMessage);
                            OAuthProcessor.validateMessage(oAuthMessage, accessor);

                            if (isAuthorizedByRule(httpRequest, httpResponse, protocol)) {
                                filterChain.doFilter(servletRequest, servletResponse);
                            } else {
                                throw new OAuthProblemException("permission_denied");
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            afterDoFilter(httpRequest, httpResponse, th);
                            OAuthProcessor.handleException(e, httpRequest, httpResponse, true);
                            return;
                        }
                    } else if (isBasicAuthRequest(httpRequest, httpResponse)) {
	                    filterChain.doFilter(servletRequest, servletResponse);
                    } else {
                        if (isAccessAuthorized(protocol, httpRequest, httpResponse)) {
                            try {
                                filterChain.doFilter(servletRequest, servletResponse);
                            } catch (Throwable e) {
                                logger.error(e.getMessage(), e);
                                th = e;
                            }
                        } else if (!httpResponse.isCommitted()) {
                            showAccessDeniedPage(httpRequest, httpResponse);
                        }
                    }
                    afterDoFilter(httpRequest, httpResponse, th);
                }
                this.contextContainerDelegate.releaseOpenFlameContextResources();
                context.reset();
            }
        }
        //uncomment if it would be necessary to calculate incoming request base url instead of using configured config value
        //this might be useful for productivity - request coming from the same server will be handled faster
        //than requests coming from outside the server
        //onAfterFilterRequest();
	    pageUID.remove();
    }

    //uncomment if it would be necessary to calculate incoming request base url instead of using configured config value
    //this might be useful for productivity - request coming from the same server will be handled faster
    //than requests coming from outside the server
    /*protected void onBeforeFilterRequest(HttpServletRequest httpRequest) {
             OpenFlameSecurityConstants.setBaseUrl(WebUtils.getApplicationFullPath(httpRequest));
         }

         protected void onAfterFilterRequest() {
              OpenFlameSecurityConstants.setBaseUrl(null);
         }*/

	private boolean isBasicAuthRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		String authentication = httpRequest.getHeader("authorization");
		if (StringUtils.isNotBlank(authentication) && authentication.startsWith("Basic ")) {
			authentication = authentication.substring("Basic ".length());
			String[] values = Base64.base64Decode(authentication).split(":");
			if (values.length < 2) {
				return false;
			}
			String username = values[0];
			String password = values[1];
			if (username == null || password == null) {
				return false;
			}

			AuthenticationToken authenticationToken = requestAuthenticationToken(username, password, httpRequest.getRemoteAddr());
			processAuthInternal(httpResponse, authenticationToken);
			return true;
		}
		return false;
	}

	public void afterDoFilter(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Throwable th) throws IOException {
        List<IFilterFlowInterceptor> filterFlowInterceptors = getFilterFlowInterceptorList();
        if (filterFlowInterceptors != null) {
            for (IFilterFlowInterceptor interceptor : filterFlowInterceptors) {
                interceptor.afterExecution(httpRequest, httpResponse, th);
            }
        }
    }

    protected IAuthenticationProcessor getAuthenticationProcessor() {
        if (authenticationProcessor == null) {
            authenticationProcessor = new BatchAuthenticationProcessor();
            if (StringUtils.isNotBlank(getSParameter(PARAM_OPEN_ID_SIGN_IN_HANDLER_URL))) {
                OpenIDAuthenticationProcessor openIDAuthenticationProcessor = new OpenIDAuthenticationProcessor();
                openIDAuthenticationProcessor.setOpenIdEntryPointUrl(getSParameter(PARAM_OPEN_ID_SIGN_IN_HANDLER_URL));
                openIDAuthenticationProcessor.setOpenIDConsumer(getOpenIDConsumer());
                openIDAuthenticationProcessor.setSignInProcessor(this);
                authenticationProcessor.addProcessor(openIDAuthenticationProcessor);
            }
            String facebookAuthEntryPoint = getSParameter(PARAM_FACEBOOK_SIGN_IN_HANDLER_URL);
            if (StringUtils.isNotBlank(facebookAuthEntryPoint)) {
                FacebookAuthenticationProcessor facebookAuthenticationProcessor = new FacebookAuthenticationProcessor();
                facebookAuthenticationProcessor.setAuthenticationEntryPointUrl(facebookAuthEntryPoint);
                facebookAuthenticationProcessor.setDefaultPageUrl(getSParameter(PARAM_DEFAULT_TARGET_PAGE));
                facebookAuthenticationProcessor.setSignInProcessor(this);
                authenticationProcessor.addProcessor(facebookAuthenticationProcessor);

                MobileFacebookAuthenticationProcessor mobileFBAuthenticationProcessor =
                        new MobileFacebookAuthenticationProcessor();
                mobileFBAuthenticationProcessor.setMobileFacebookEntryPointUrl(
                        facebookAuthEntryPoint + "/mobile");
                authenticationProcessor.addProcessor(mobileFBAuthenticationProcessor);
            }
            String twitterAuthEntryPoint = getSParameter(PARAM_TWITTER_SIGN_IN_HANDLER_URL);
            if (StringUtils.isNotBlank(twitterAuthEntryPoint)) {
                TwitterAuthenticationProcessor twitterAuthenticationProcessor = new TwitterAuthenticationProcessor();
                twitterAuthenticationProcessor.setTwitterAuthenticationEntryPointUrl(twitterAuthEntryPoint);
                twitterAuthenticationProcessor.setDefaultPageUrl(getSParameter(PARAM_DEFAULT_TARGET_PAGE));
                twitterAuthenticationProcessor.setSignInProcessor(this);
                authenticationProcessor.addProcessor(twitterAuthenticationProcessor);

                MobileTwitterAuthenticationProcessor mobileTwitterAuthenticationProcessor =
                        new MobileTwitterAuthenticationProcessor();
                mobileTwitterAuthenticationProcessor.setTwitterAuthenticationEntryPointUrl(
                        twitterAuthEntryPoint + "/mobile");
                authenticationProcessor.addProcessor(mobileTwitterAuthenticationProcessor);
            }
            String linkedinAuthEntryPoint = getSParameter(PARAM_LINKEDIN_SIGN_IN_HANDLER_URL);
            if (StringUtils.isNotBlank(linkedinAuthEntryPoint)) {
                LinkedInAuthenticationProcessor linkedInAuthenticationProcessor = new LinkedInAuthenticationProcessor();
                linkedInAuthenticationProcessor.setLinkedInEntryPointUrl(linkedinAuthEntryPoint);
                linkedInAuthenticationProcessor.setDefaultPageUrl(getSParameter(PARAM_DEFAULT_TARGET_PAGE));
                linkedInAuthenticationProcessor.setSignInProcessor(this);
                authenticationProcessor.addProcessor(linkedInAuthenticationProcessor);

//                MobileFacebookAuthenticationProcessor mobileFBAuthenticationProcessor =
//                        new MobileFacebookAuthenticationProcessor();
//                mobileFBAuthenticationProcessor.setMobileFacebookEntryPointUrl(
//                        linkedinAuthEntryPoint + "/mobile");
//                authenticationProcessor.addProcessor(mobileFBAuthenticationProcessor);
            }
            if (getParameter(PARAM_OAUTH_PROVIDER_SUPPORTED) != null) {
                authenticationProcessor.addProcessor(populateOAuthAuthenticationProvider());
            }

            IAuthenticationProcessor smAuthenticationProcessor = getSiteMinderAuthenticationProcessor();
            if (smAuthenticationProcessor != null) {
                authenticationProcessor.addProcessor(smAuthenticationProcessor);
            }
        }
        return authenticationProcessor;
    }

    protected IAuthenticationProcessor getSiteMinderAuthenticationProcessor() {
        return new SiteMinderAuthenticationProcessor(this);
    }

    protected OAuthAuthenticationProvider populateOAuthAuthenticationProvider() {
        OAuthAuthenticationProvider oauthAuthenticationProvider = new OAuthAuthenticationProvider();
        oauthAuthenticationProvider.setAuthorizePage(getSParameter(PARAM_OAUTH_PROVIDER_AUTHORIZE_PAGE));
        oauthAuthenticationProvider.setAccountParameterName(getSParameter(PARAM_USERNAME));
        oauthAuthenticationProvider.setPasswordParameterName(getSParameter(PARAM_PASSWORD));
        return oauthAuthenticationProvider;
    }

	public static String getPageUID(){
		return pageUID.get();
	}

	public static void setPageUID(String uid){
		pageUID.set(uid);
	}

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        logger.info("Finalize usage of security filter [" + this.getClass() + "].");
        this.getStatisticsTracker().destroy();
        CacheManager.getInstance().close();
	    parametersMap.clear();
        getAuthenticationProcessor().release();
    }

    /**
     * Implementation of showSignInPage() method
     *
     * @see net.firejack.platform.web.security.filter.ISecurityFilter#showSignInPage(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void showSignInPage(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        getAuthenticationFailureHandler().onFailure(httpRequest, httpResponse, notRedirectUrls);
    }

    /**
     * Implementation of showAccessDeniedPage() method
     *
     * @see net.firejack.platform.web.security.filter.ISecurityFilter#showAccessDeniedPage(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void showAccessDeniedPage(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        getAccessDeniedHandler().onAccessDenied(httpRequest, httpResponse);
    }

    /**
     * Implementation of getStatisticsTracker()
     *
     * @see net.firejack.platform.web.security.filter.ISecurityFilter#getStatisticsTracker()
     */
    public IStatisticsManager getStatisticsTracker() {
        if (statisticsManager == null) {
            statisticsManager = populateStatisticsManager();
        }
        return statisticsManager;
    }

    /**
     * Implementation of getFilterFlowInterceptorList() method
     *
     * @see net.firejack.platform.web.security.filter.ISecurityFilter#getFilterFlowInterceptorList()
     */
    public List<IFilterFlowInterceptor> getFilterFlowInterceptorList() {
        if (filterFlowInterceptorList == null) {
            filterFlowInterceptorList = populateFilterFlowInterceptors();
        }
        return filterFlowInterceptorList;
    }

    /**
     * Populate filter flow interceptor list
     *
     * @return list of populated filter flow interceptors
     * @see IFilterFlowInterceptor
     */
    protected List<IFilterFlowInterceptor> populateFilterFlowInterceptors() {
        List<IFilterFlowInterceptor> interceptors = new ArrayList<IFilterFlowInterceptor>();
        interceptors.add(getStatisticsTracker());
        return interceptors;
    }

    /**
     * Handle guest access
     *
     * @throws IOException method can throw IOException
     */
    protected void processGuestAccess() throws IOException {
        //SecurityUtils.processGuestAccess(this.contextContainerDelegate);
        this.contextContainerDelegate.initBusinessContextForGuest();
    }

//    protected void processSystemAccess(HttpServletRequest httpRequest) {
//        this.contextContainerDelegate.initBusinessContextForSystem(httpRequest);
//    }

    /**
     * Process sign in request
     *
     * @param httpRequest  http request
     * @param httpResponse http response
     * @throws IOException method can throw IOException
     */
    @SuppressWarnings("unchecked")
    protected void processSignIn(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String username = httpRequest.getParameter(getSParameter(PARAM_USERNAME));
        String password = httpRequest.getParameter(getSParameter(PARAM_PASSWORD));
        if (getSParameter(PARAM_ENCRYPTED) != null) {
            String encrypted = httpRequest.getParameter(getSParameter(PARAM_ENCRYPTED));
            if (StringUtils.isNotBlank(encrypted) && encrypted.matches("true|false")) {
                boolean isEncrypted = Boolean.parseBoolean(encrypted);
                if (isEncrypted) {
                    HttpSession session = httpRequest.getSession();
                    PrivateKey privateKey = (PrivateKey) session.getAttribute(OpenFlameSecurityConstants.PASSWORD_ENCRYPTED_PRIVATE_KEY);
                    if (privateKey != null) {
                        try {
                            byte[] decryptedPassword = KeyUtils.decrypt(privateKey, new BASE64Decoder().decodeBuffer(password));
                            password = new String(decryptedPassword);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    session.removeAttribute(OpenFlameSecurityConstants.PASSWORD_ENCRYPTED_PRIVATE_KEY);
                }
            }
        }
        AuthenticationToken authenticationToken = requestAuthenticationToken(username, password, httpRequest.getRemoteAddr());
        processSignInInternal(httpRequest, httpResponse, authenticationToken);
    }

    /**
     * Internal handling of sign in process
     *
     * @param httpResponse        http response
     * @param authenticationToken authentication token
     * @throws IOException method can throw IOException
     */
    public void processSignInInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationToken authenticationToken) throws IOException {
        if (authenticationToken == null) {
            showSignInPage(httpRequest, httpResponse);
        } else {
	        processAuthInternal(httpResponse, authenticationToken);
            getAuthenticationSuccessHandler().onSuccessAuthentication(httpRequest, httpResponse, notRedirectUrls);
        }
    }

    protected void processAuthInternal(HttpServletResponse httpResponse, AuthenticationToken authenticationToken) throws UnsupportedEncodingException {
		String token = authenticationToken.getToken();
		Cookie authenticationTokenCookie = new Cookie(
		        OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE,
		        URLEncoder.encode(token, "utf8"));
		authenticationTokenCookie.setPath("/");

//        Map<String, String> urlInfo = OpenFlameSecurityConstants.getBaseUrlInfo();
//        String host = urlInfo.get("host");
//        String domain = host.startsWith("www.") ? host.substring(4) : host;
//        authenticationTokenCookie.setDomain("." + domain);

        httpResponse.addCookie(authenticationTokenCookie);

		OpenFlamePrincipal principal = new UserPrincipal(authenticationToken.getUser());
		OPFContext.initContext(principal, token);
	}

	/**
     * Process reset password request
     *
     * @param httpRequest  http request
     * @param httpResponse http response
     * @throws IOException method can throw IOException
     */
    protected void processResetPassword(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String usernameOrEmail = httpRequest.getParameter(getSParameter(PARAM_EMAIL));
        requestForgotPassword(usernameOrEmail);
        processForgotPasswordInternal(httpRequest, httpResponse);
    }

    /**
     * Internal handling of sign in process
     *
     * @param httpResponse        http response
     * @throws IOException method can throw IOException
     */
    public void processForgotPasswordInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        showSignInPage(httpRequest, httpResponse);
    }

    /**
     * Process confirm reset password request
     *
     * @param httpRequest  http request
     * @param httpResponse http response
     * @throws IOException method can throw IOException
     */
    protected void processConfirmResetPassword(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String resetPasswordToken = WebUtils.getQueryParam(httpRequest, "token");
        requestConfirmResetPassword(resetPasswordToken);
        processConfirmResetPasswordInternal(httpRequest, httpResponse);
    }

    /**
     * Internal handling of sign in process
     *
     * @param httpResponse        http response
     * @throws IOException method can throw IOException
     */
    public void processConfirmResetPasswordInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        showSignInPage(httpRequest, httpResponse);
    }

    /**
     * Sign out request handling
     *
     * @param httpRequest  http request
     * @param httpResponse http response
     * @throws IOException method can throw IOException
     */
    protected void processSignOut(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        if (OAuthProcessor.isOAuthRequest(httpRequest)) {
            String sessionToken = OAuthProcessor.getSessionToken(httpRequest);
            httpResponse.setContentType("text/plain");
            PrintWriter writer = httpResponse.getWriter();
            if (processSTSSignOut(sessionToken)) {
                this.contextContainerDelegate.invalidateBusinessContextInStore();
                logger.warn("Sign out for OAuth token = [" + sessionToken + "] was processed successfully.");
                writer.println("Successfully sign out session for token = " + sessionToken);
                OAuthMessage oAuthMessage = OAuthServlet.getMessage(httpRequest, null);
                OAuthAccessor accessor;
                try {
                    accessor = OAuthProcessor.getAccessor(oAuthMessage);
                } catch (OAuthProblemException e) {
                    logger.error(e.getMessage(), e);
                    accessor = null;
                }
                OAuthProcessor.signOut(accessor);
            } else {
                logger.warn("Failed to process sign out for OAuth token = [" + sessionToken + "].");
                writer.println("Filed to sign out session for token = " + sessionToken);
            }
            writer.flush();
            writer.close();
        } else {
            processSignOutInternal(httpRequest, httpResponse, true);
            logger.info("Sign out processed successfully. Redirecting to sign in form...");
            showSignInPage(httpRequest, httpResponse);
        }
    }

    /**
     * Internal handling of sign out
     *
     * @param httpRequest       http response
     * @param httpResponse      http response
     * @param processSTSSignOut This parameter indicates whether sign out process should involve sign-out on STS
     */
    protected void processSignOutInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, boolean processSTSSignOut) {
        Cookie cookie = WebUtils.getRequestCookie(
                httpRequest, OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE);
        if (cookie != null) {
            if (!processSTSSignOut || processSTSSignOut(cookie.getValue())) {
                cookie.setMaxAge(0);
                if (cookie.getPath() == null) {
                    cookie.setPath("/");
                }
                httpResponse.addCookie(cookie);
                this.contextContainerDelegate.invalidateBusinessContextInStore();
            }
        }
        getAuthenticationProcessor().processUnAuthentication(httpRequest, httpResponse);
    }

    /**
     * This method checks whether access for requested resource is authorized
     *
     * @param protocol     entity protocol
     * @param httpRequest  http request
     * @param httpResponse http response
     * @return whether access authorized or not
     * @throws IOException method can throw IOException
     */
    protected boolean isAccessAuthorized(EntityProtocol protocol,
                                         HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException {
        boolean accessGranted = true;
        if (protocol != null) {
            if (protocol == EntityProtocol.HTTP) {
                OpenFlamePrincipal principal = ContextManager.getPrincipal();
                if (!principal.isGuestPrincipal()) {
                    String userSessionToken = getTokenFromRequest(protocol, httpRequest);
                    if (userSessionToken == null) {
                        showSignInPage(httpRequest, httpResponse);
                        accessGranted = false;
                    } else if (processSTSExpiration(userSessionToken)) {
                        processExpiration(httpRequest, httpResponse);
                        accessGranted = false;
                    }
                }
                if (accessGranted) {
                    accessGranted = isAuthorizedByRule(httpRequest, httpResponse, protocol);
                }
            }
        }
        return accessGranted;
    }

    public boolean isAuthorizedByRule(
            HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            EntityProtocol protocol) throws IOException {
        boolean authorized = true;
        getAuthorizationRule().initialize();
        if (getAuthorizationRule().isRuleCase(this, httpRequest, protocol)) {
            Boolean authorizeAccess = getAuthorizationRule().authorizeAccess(this, httpRequest, httpResponse);
            authorized = authorizeAccess != null && authorizeAccess;
        }
        getAuthorizationRule().release();
        return authorized;
    }

    /**
     * Get authorization logic delegate
     *
     * @return get authorization rule
     */
    protected IAuthorizationRule getAuthorizationRule() {
        if (authorizationRule == null) {
            CompositeAuthorizationRule compositeRule = new CompositeAuthorizationRule();
            List<IAuthorizationRule> rules = getFilterAuthorizationRules();
            if (rules != null) {
                for (IAuthorizationRule authRule : rules) {
                    compositeRule.addRule(authRule);
                }
            }
            authorizationRule = compositeRule;
        }
        return authorizationRule;
    }

    /**
     * Get predefined set of authorization rules that will be used by composite authorization rule
     *
     * @return list of predefined authorization rules
     * @link CompositeAuthorizationRule
     */
    protected List<IAuthorizationRule> getFilterAuthorizationRules() {
        List<IAuthorizationRule> rules = new ArrayList<IAuthorizationRule>();
        Collections.addAll(rules,
                new ActionDetectionRule(getActionDetector()),
                new NavigationElementsRule(),
                new ResourceLocationRule());
        if (!OpenFlameSecurityConstants.isClientContext()) {
            rules.add(new InstallationAuthorizationRule());
        }
        return rules;
    }

    /**
     * Get action detector
     *
     * @return action detector specific implementation
     */
    protected IActionDetector getActionDetector() {
        if (actionDetector == null) {
            actionDetector = populateActionDetector();
        }
        return actionDetector;
    }

    /**
     * Get sign in page url
     *
     * @return sign in page url
     */
    protected String getSignInPageUrl() {
        return getSParameter(PARAM_SIGN_IN_PAGE_URL);
    }

    /**
     * Get sign in handler url
     *
     * @return sign in handler url
     */
    protected String getSignInHandlerUrl() {
        return getSParameter(PARAM_SIGN_IN_HANDLER);
    }

    /**
     * Get sign in handler url
     *
     * @return sign in handler url
     */
    protected String getResetPasswordHandlerUrl() {
        return getSParameter(PARAM_RESET_PASSWORD_HANDLER);
    }

    /**
     * Get sign out url
     *
     * @return sign out url
     */
    protected String getSignOutUrl() {
        return getSParameter(PARAM_SIGN_OUT_HANDLER_URL);
    }

    /**
     * Get default target url
     *
     * @return default target url
     */
    protected String getDefaultTargetUrl() {
        return getSParameter(PARAM_DEFAULT_TARGET_PAGE);
    }

    protected String getAccessDeniedUrl() {
        return getSParameter(PARAM_ACCESS_DENIED_PAGE);
    }

    protected IAuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        if (authenticationSuccessHandler == null) {
            authenticationSuccessHandler = new ShowDefaultPageHandler(getDefaultTargetUrl());
        }
        return authenticationSuccessHandler;
    }

    public void setAuthenticationSuccessHandler(IAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    protected IAuthenticationFailureHandler getAuthenticationFailureHandler() {
        if (authenticationFailureHandler == null) {
            authenticationFailureHandler = populateAuthenticationFailureHandler();
        }
        return authenticationFailureHandler;
    }

    protected IAuthenticationFailureHandler populateAuthenticationFailureHandler() {
        return new ShowSignInPageHandler(getSignInPageUrl());
    }

    public IAccessDeniedHandler getAccessDeniedHandler() {
        if (accessDeniedHandler == null) {
            accessDeniedHandler = populateAccessDeniedHandler();
        }
        return accessDeniedHandler;
    }

    protected IAccessDeniedHandler populateAccessDeniedHandler() {
        return new ShowAccessDeniedPageHandler(getAccessDeniedUrl());
    }

    protected void checkParameters(FilterConfig filterConfig) {
        Set<String> customParameters = lowerCaseCollection(getCustomParameterNames());
        @SuppressWarnings("unchecked")
        Enumeration<String> parameterNames = (Enumeration<String>) filterConfig.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String lowerCaseParameterName = parameterName.toLowerCase();
            Object parameterValue = filterConfig.getInitParameter(parameterName);
            if (customParameters.contains(lowerCaseParameterName)) {
                parameterValue = processCustomParameter(
                        lowerCaseParameterName, (String) parameterValue);
            }
            parametersMap.put(lowerCaseParameterName, parameterValue);
        }
        Set<String> requiredParameters = lowerCaseCollection(getRequiredParameterNames());
        Set<String> keySet = parametersMap.keySet();
        for (String requiredParameter : requiredParameters) {
            if (!keySet.contains(requiredParameter)) {
                throw new SecurityConfigurationException("Missed required parameter [" + requiredParameter + "]");
            }
        }
    }

    private EntityProtocol findProtocol(HttpServletRequest request) {
        String requestUri = WebUtils.getRequestPath(request);
        if (requestUri != null) {
            for (ProtocolMapping protocolMapping : getProtocolMappings()) {
                if (requestUri.startsWith(protocolMapping.getUrlPrefix())) {
                    return protocolMapping.getProtocol();
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected Set<ProtocolMapping> getProtocolMappings() {
        return (Set<ProtocolMapping>) getParameter(PARAM_PROTOCOL_PREFIXES);
    }

    protected String[] getRequiredParameterNames() {
        String[] requiredParameterNames = new String[]{
                PARAM_SIGN_IN_HANDLER, PARAM_SIGN_OUT_HANDLER_URL,
                PARAM_USERNAME, PARAM_PASSWORD, PARAM_SIGN_IN_PAGE_URL,
                PARAM_ACCESS_DENIED_PAGE, PARAM_DEFAULT_TARGET_PAGE,
                PARAM_PROTOCOL_PREFIXES
        };
        if (StringUtils.isNotBlank(getSParameter(PARAM_OPEN_ID_SIGN_IN_HANDLER_URL))) {
            requiredParameterNames = Arrays.copyOf(requiredParameterNames, requiredParameterNames.length + 2);
            requiredParameterNames[requiredParameterNames.length - 2] = PARAM_OPEN_ID_IDENTIFIER;
            requiredParameterNames[requiredParameterNames.length - 1] = PARAM_OPEN_ID_SIGN_IN_HANDLER_URL;
        }
        return requiredParameterNames;
    }

    protected String[] append(String[] source, String... elements) {
        List<String> items = new ArrayList<String>();
        if (source != null) {
            Collections.addAll(items, source);
        }
        Collections.addAll(items, elements);
        return items.toArray(new String[items.size()]);
    }

    /**
     * Get names of parameters that require custom retrieval
     * @return names of custom parameters
     */
    protected String[] getCustomParameterNames() {
        return new String[]{PARAM_PROTOCOL_PREFIXES};
    }

    /**
     * Process custom parameter. By default it supposed that all parameters have string value.
     * But for particular parameters we can override this behaviour in this method. See also
     * @see OpenFlameFilter#getCustomParameterNames()
     *
     * @param parameterName name of custom parameter
     * @param parameterValue String representation of custom parameter
     * @return processed custom parameter
     */
    @SuppressWarnings("unused")
    protected Object processCustomParameter(String parameterName, String parameterValue) {
        if (PARAM_PROTOCOL_PREFIXES.equals(parameterName)) {
            if (StringUtils.isNotBlank(parameterValue)) {
                Set<ProtocolMapping> protocolMappings = new TreeSet<ProtocolMapping>();
                try {
                    protocolMappings.addAll(
                            ProtocolMappings.processMappings(parameterValue));
                    if (protocolMappings.isEmpty()) {
                        throw new SecurityConfigurationException(
                                "Wrong definition of parameter [" + PARAM_PROTOCOL_PREFIXES + "].");
                    } else {
                        return protocolMappings;
                    }
                } catch (Throwable e) {
                    throw new SecurityConfigurationException(e);
                }
            }
        }

        return parameterValue;
    }

    protected ContextContainerDelegate populateContextContainerDelegate() {
        return ContextContainerDelegate.getInstance();
    }

    protected void initializeContextContainerDelegate(ServletContext servletContext) {
        this.contextContainerDelegate = populateContextContainerDelegate();
        this.contextContainerDelegate.initialize(servletContext, populateCachedContextFacade());
    }

    protected CachedContextFacade populateCachedContextFacade() {
        return new CachedContextFacade();
    }

    protected String getSParameter(String parameterName) {
        return (String) parametersMap.get(parameterName);
    }

    protected Object getParameter(String parameterName) {
        return parametersMap.get(parameterName);
    }

    protected String getTokenFromRequest(EntityProtocol protocol, HttpServletRequest request) {
        String token = null;
        if (protocol == EntityProtocol.HTTP) {
            Cookie authenticationTokenCookie = WebUtils.getRequestCookie(
		            request, OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE);
            if (authenticationTokenCookie != null) {
                try {
                    token = URLDecoder.decode(authenticationTokenCookie.getValue(), "utf8");
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } else {
            //process SOAP here
        }
        return token;
    }

    protected OpenIDConsumer getOpenIDConsumer() {
        if (openIDConsumer == null) {
            openIDConsumer = populateOpenIDConsumer();
        }
        return openIDConsumer;
    }

    protected void processExpiration(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        logger.info("User session for " + ContextManager.getPrincipal().getName() + " has expired.");
        String requestType = httpRequest.getContentType() == null ?
                httpRequest.getHeader("accept") : httpRequest.getContentType();
        if (MediaType.APPLICATION_JSON.equalsIgnoreCase(requestType)) {
            processSignOutInternal(httpRequest, httpResponse, false);
            httpResponse.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Session has expired.");
        } else {
            if (MediaType.APPLICATION_XML.equalsIgnoreCase(requestType)) {
                String apiCallMarkerHeaderValue = httpRequest.getHeader(OpenFlameSecurityConstants.MARKER_HEADER);
                if (StringUtils.isNotBlank(apiCallMarkerHeaderValue)) {
                    logger.warn("Session expired for API method call made by [" + apiCallMarkerHeaderValue + "].");
                    this.contextContainerDelegate.invalidateBusinessContextInStore();
                    OpenFlameSecurityConstants.printXmlErrorToResponse(
                        httpResponse, OpenFlameSecurityConstants.API_406_ERROR_RESPONSE);
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Session has expired.");
                }
            } else {
                processSignOut(httpRequest, httpResponse);
            }
        }
    }

    protected void processTokenSecurityViolation(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String requestType = httpRequest.getContentType() == null ?
                httpRequest.getHeader("accept") : httpRequest.getContentType();
        if (MediaType.APPLICATION_JSON.equalsIgnoreCase(requestType)) {
            httpResponse.setContentType(requestType);
            ServletOutputStream out = httpResponse.getOutputStream();
            out.println("{message:'" + MSG_INAPPROPRIATE_IP_ADDRESS + "'; success: false}");
            out.close();
        } else if (MediaType.APPLICATION_XML.equalsIgnoreCase(requestType)) {
            logger.warn("Trying to use token corresponded to different IP address in API call.");
            OpenFlameSecurityConstants.printXmlErrorToResponse(
                    httpResponse, OpenFlameSecurityConstants.API_403_DIFFERENT_IP_ERROR_RESPONSE);
        } else {
            logger.warn(MSG_INAPPROPRIATE_IP_ADDRESS);
            Cookie cookie = WebUtils.getRequestCookie(
                    httpRequest, OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE);
            if (cookie != null) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                httpResponse.addCookie(cookie);
            }
            showSignInPage(httpRequest, httpResponse);
        }
    }

    /**
     * Populate Permission Container Factory
     * @return permission container factory
     */
    protected IPermissionContainerFactory populatePermissionContainerFactory() {
        CachedPermissionContainerFactory permissionContainerFactory = new CachedPermissionContainerFactory();
        permissionContainerFactory.setRules(populatePermissionContainerRules());
        return permissionContainerFactory;
    }

    protected List<IPermissionContainerRule> populatePermissionContainerRules() {
        return null;
    }

    /**
     * Populate action container factory
     * @return action container factory
     */
    protected IActionContainerFactory populateActionContainerFactory() {
        return new CachedActionContainerFactory();
    }

    /**
     * Populate masked resource container factory
     * @return masked resource container factory
     */
    protected IResourceLocationContainerFactory populateResourceLocationContainerFactory() {
        CachedResourceLocationContainerFactory resourceLocationContainerFactory =
                new CachedResourceLocationContainerFactory(OpenFlameSecurityConstants.getPackageLookup());
        resourceLocationContainerFactory.setRules(populateResourceLocationRules());
        return resourceLocationContainerFactory;
    }

    protected List<IResourceLocationContainerRule> populateResourceLocationRules() {
        return null;
    }

    /**
     * Populate Navigation Element Container Factory
     * @return Navigation Element Container Factory
     */
    protected INavElementContainerFactory populateNavElementContainerFactory() {
        return new CachedNavElementContainerFactory(OpenFlameSecurityConstants.getPackageLookup());
    }

    /**
     * Populate Secured Record Container factory
     * @return Secured Record Container factory
     */
    protected ISecuredRecordInfoContainerFactory populateSecuredRecordContainerFactory() {
        return new CachedSecuredRecordInfoContainerFactory();
    }

    protected ISpecifiedIdsFilterContainerFactory populateSpecifiedIdsFilterContainerFactory() {
        return new SpecifiedIdsFilterContainerFactory();
    }

    /**
     * Check whether token associated with user session was expired or not
     *
     * @param token token associated with user session
     * @return true if session was expired and false otherwise
     */
    protected boolean processSTSExpiration(String token) {
        return ContextManager.isSessionExpired(token);
    }

    /**
     * Process authentication on STS using specified username and password
     * @param username username to use for authentication
     * @param password password to use for authentication
     * @param clientIpAddress client IP address
     * @return Authentication Token associated with User Session, if authentication succeeds. Otherwise return null.
     */
    protected AuthenticationToken requestAuthenticationToken(String username, String password, String clientIpAddress) {
        ServiceResponse<AuthenticationToken> response =
                OPFEngine.AuthorityService.processSTSSignIn(username, password, clientIpAddress);
        AuthenticationToken authenticationToken;
        if (!response.isSuccess()) {
            authenticationToken = null;
            FilterMessageStock.getInstance().addFilterMessage(FilterMessageType.ERROR, response.getMessage());
        } else {
            authenticationToken = response.getItem();
        }
        return authenticationToken;
    }

    /**
     * Process forgot password on STS using specified email
     * @param email username to use for authentication
     * @return true is password has been sent
     */
    protected boolean requestForgotPassword(String email) {
        String resetPasswordPageUrl = OpenFlameSecurityConstants.getBaseUrl() + getResetPasswordHandlerUrl();
        ServiceResponse<AuthenticationToken> response = OPFEngine.AuthorityService.processSTSForgotPassword(email, resetPasswordPageUrl);
        boolean isSuccess = response.isSuccess();
        if (isSuccess) {
            FilterMessageStock.getInstance().addFilterMessage(FilterMessageType.INFO, response.getMessage());
        } else {
            FilterMessageStock.getInstance().addFilterMessage(FilterMessageType.WARNING, response.getMessage());
        }
        return isSuccess;
    }

    /**
     * Process reset password on STS using specified token
     * @param token to use for reset password
     * @return true is password has been reset
     */
    protected boolean requestConfirmResetPassword(String token) {
        ServiceResponse<AuthenticationToken> response = OPFEngine.AuthorityService.processSTSResetPassword(token);
        boolean isSuccess = response.isSuccess();
        if (isSuccess) {
            FilterMessageStock.getInstance().addFilterMessage(FilterMessageType.INFO, response.getMessage());
        } else {
            FilterMessageStock.getInstance().addFilterMessage(FilterMessageType.WARNING, response.getMessage());
        }
        return isSuccess;
    }

    /**
     * Sign out from STS
     * @param token token associated with user session
     * @return true if sign out processed successfully. Otherwise returns false.
     */
    protected boolean processSTSSignOut(String token) {
        ServiceResponse response = OPFEngine.AuthorityService.processSTSSignOut(token);
        if (!response.isSuccess() && StringUtils.isNotBlank(response.getMessage())) {
            logger.error(response.getMessage());
        }
        return response.isSuccess() || "Session has expired.".equals(response.getMessage());
    }

    /**
     * This method contains configuration of custom action detector
     * @return configured action detector
     */
    protected IActionDetector populateActionDetector() {
        ActionDetectorFactory actionDetectorFactory = ActionDetectorFactory.getInstance();
        return actionDetectorFactory.produceDefaultActionDetector();
    }

    /**
     * Populate OpenID consumer
     * @return instance of OpenId Consumer
     */
    protected OpenIDConsumer populateOpenIDConsumer() {
        try {
            return new OpenIDConsumer();
        } catch (ConsumerException e) {
            logger.error(e.getMessage(),e);
            throw new OpenFlameRuntimeException(MSG_ERROR_FAILED_TO_INSTANTIATE_OPENID_CONSUMER);
        }
    }

    protected void updateContextInternal(HttpServletRequest request) {
        if (!OpenFlameSecurityConstants.isClientContext() && !ConfigContainer.isAppInstalled()) {
            String sessionToken = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(
                            OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE)) {
                        sessionToken = cookie.getValue();
                    }
                }
            }
            OPFContext.getContext().setSessionToken(sessionToken);
        }
    }

    /**
     * Populate statistics manager
     * @return console statistics manager
     */
    protected IStatisticsManager populateStatisticsManager() {
	    DefaultStatisticsManager statisticsManager = new DefaultStatisticsManager();
        if (StringUtils.isNumeric(getSParameter(PARAM_STATS_BUFFER_VOLUME))) {
            int bufferVolume = Integer.parseInt(getSParameter(PARAM_STATS_BUFFER_VOLUME));
            statisticsManager.setBufferVolume(bufferVolume);
        }
        if (StringUtils.isNumeric(getSParameter(PARAM_STATS_BUFFER_PEAK))) {
            int bufferPeak = Integer.parseInt(getSParameter(PARAM_STATS_BUFFER_PEAK));
            statisticsManager.setBufferPeak(bufferPeak);
        }
        if (StringUtils.isNumeric(getSParameter(PARAM_STATS_BUFFER_INSERT_SIZE))) {
            int bufferInsertSize = Integer.parseInt(getSParameter(PARAM_STATS_BUFFER_INSERT_SIZE));
            statisticsManager.setBufferInsertSize(bufferInsertSize);
        }
        return statisticsManager;
    }

    private Set<String> lowerCaseCollection(String[] items) {
        Set<String> itemSet;
        if (items == null) {
            itemSet = Collections.emptySet();
        } else {
            itemSet = new TreeSet<String>();
            for (String item : items) {
                if (StringUtils.isNotBlank(item)) {
                    itemSet.add(item.toLowerCase());
                }
            }
        }
        return itemSet;
    }
}
