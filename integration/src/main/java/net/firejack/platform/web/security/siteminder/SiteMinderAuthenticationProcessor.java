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

package net.firejack.platform.web.security.siteminder;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.filter.IAuthenticationProcessor;
import net.firejack.platform.web.security.filter.ISecurityFilter;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import org.apache.log4j.Logger;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SiteMinderAuthenticationProcessor implements IAuthenticationProcessor {

    protected static final String SM_CONFIGS_FILENAME = "sm.properties";

    public static final String DEFAULT_STANDARD_ID_HEADER = "HTTP_UID";
    public static final String DEFAULT_OPF_DIRECT_URL = "http://localhost:8080/platform";
    public static final String PROP_STANDARD_ID_HEADER = "standard.id.header";
    public static final String PROP_USER_ACL_URL = "user.acl.service.url";
    public static final String PROP_USER_ROLE_ACL_URL = "user.role.acl.service.url";
    public static final String PROP_SUPER_USERS_LIST= "user.admin.list";
    public static final String PROP_DIRECT_OPF_URL = "opf.direct.url";
    public static final String PROP_DEBUG_MODE = "sm.debug.mode";

    private static String smConfigsFileMD5;

    private static final Logger logger = Logger.getLogger(SiteMinderAuthenticationProcessor.class);
    private UserAccessControlHandler accessControlHandler;
    private ScheduledExecutorService executor;
    private ISecurityFilter securityFilter;
    private Boolean debugMode;

    public SiteMinderAuthenticationProcessor(ISecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    public static Tuple<File, Map<String,String>> loadSMProperties() {
        Tuple<File, Map<String,String>> result;
        String smConfigsPath = System.getenv(OpenFlameSecurityConstants.SM_CONFIGS_PATH_ENV_VARIABLE);
        if (StringUtils.isBlank(smConfigsPath)) {
            result = null;
        } else {
            File smConfigs = new File(smConfigsPath, SM_CONFIGS_FILENAME);
            if (smConfigs.exists()) {
                Map<String,String> properties = MiscUtils.getProperties(smConfigs);
                result = new Tuple<File, Map<String, String>>(smConfigs, properties);
            } else {
                result = null;
            }
        }
        return result;
    }

    public UserAccessControlHandler getAccessControlHandler() {
        if (this.accessControlHandler == null) {
            this.accessControlHandler = new DefaultUserAccessControlHandler();
        }
        return this.accessControlHandler;
    }

    public void setAccessControlHandler(UserAccessControlHandler accessControlHandler) {
        this.accessControlHandler = accessControlHandler;
    }

    @Override
    public boolean isAuthenticationCase(HttpServletRequest request) {
        return OpenFlameSecurityConstants.isSiteMinderAuthSupported() && StringUtils.isNotBlank(getSMHeader(request));
    }

    @Override
    public void processAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        String standardIdHeader = getSMHeader(request);
        if (standardIdHeader == null) {
            throw new IllegalStateException(
                    "StandardIdHeader values is not passed to the authentication handler as expected.");
        } else if (getAccessControlHandler().userIsActive(standardIdHeader)) {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            String currentSessionToken = context == null || context.getPrincipal().isGuestPrincipal() ?
                    null : context.getSessionToken();

            if (StringUtils.isNotBlank(currentSessionToken)) {
                Set<String> siteMinderLevelSessions = CacheManager.getInstance().getSiteMinderLevelSessions();
                if (siteMinderLevelSessions == null || !siteMinderLevelSessions.contains(currentSessionToken)) {
                    currentSessionToken = null;
                }
            }
            processSiteMinderAuthentication(standardIdHeader, currentSessionToken, request, response, filterChain);
        } else {
            informUserIsNotActive(request, response);
        }
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
        String smConfigsPath = System.getenv(OpenFlameSecurityConstants.SM_CONFIGS_PATH_ENV_VARIABLE);
        if (StringUtils.isBlank(smConfigsPath)) {
            logger.info(OpenFlameSecurityConstants.SM_CONFIGS_PATH_ENV_VARIABLE +
                    " is not configured for SiteMinder authentication. OpenFlame runs in normal mode.");
        } else {
            try {
                Tuple<File, Map<String, String>> fileMapTuple = loadSMProperties();
                if (fileMapTuple != null) {
                    OpenFlameSecurityConstants.setSiteMinderAuthSupported(true);
                    Map<String,String> properties = fileMapTuple.getValue();
                    String standardIdHeader = null, opfDirectUrl = null;
                    if (properties != null) {
                        String debugModeValue = properties.get(PROP_DEBUG_MODE);
                        if (StringUtils.isNotBlank(debugModeValue)) {
                            debugMode = Boolean.valueOf(debugModeValue);
                        }
                        standardIdHeader = properties.get(PROP_STANDARD_ID_HEADER);
                        opfDirectUrl = properties.get(PROP_DIRECT_OPF_URL);
                    }
                    setAccessControlHandler(populateAccessControlHandler(properties));

                    if (StringUtils.isBlank(standardIdHeader)) {
                        standardIdHeader = DEFAULT_STANDARD_ID_HEADER;
                    }
                    if (StringUtils.isBlank(opfDirectUrl)) {
                        opfDirectUrl = DEFAULT_OPF_DIRECT_URL;
                    }

                    OpenFlameSecurityConstants.setSiteMinderAuthIdHeader(standardIdHeader);
                    OpenFlameSecurityConstants.setOpfDirectUrl(opfDirectUrl);
                    OpenFlameSecurityConstants.setSiteMinderDebugMode(isDebugMode());

                    logger.info("SM-metrics: [standardIdHeader = " + standardIdHeader + "] [opfDirectUrl = " + opfDirectUrl);

                    File configsFile = fileMapTuple.getKey();
                    scheduleSiteMinderDataSynchronization(configsFile, FileUtils.md5(configsFile));
                }
            } catch (Throwable th) {
                logger.error("Failed to process site minder properties.");
                logger.error(th.getMessage(), th);
            }
        }
    }

    public void release() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    protected void scheduleSiteMinderDataSynchronization(final File configsFile, final String initialMD5Value) {
        if ("net.firejack.platform".equals(OpenFlameSecurityConstants.getPackageLookup()) &&
                ConfigContainer.isAppInstalled()) {//only http filter that runs under OpenFlame-console itself should run the scheduler
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    synchronizeSuperUsers(configsFile, initialMD5Value);
                }
            }, 0, 2, TimeUnit.MINUTES);
        }
    }

    protected void processSiteMinderAuthentication(
            String standardIdHeader, String currentSessionToken,
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (StringUtils.isBlank(currentSessionToken)) {
            List<String> roleLookupList = getAccessControlHandler().roleLookupList(standardIdHeader);
            User user = prepareUserByStandardId(standardIdHeader);
            if (roleLookupList != null) {
                List<UserRole> userRoles = new ArrayList<UserRole>();
                for (String roleLookup : roleLookupList) {
                    Role role = new Role();
                    role.setLookup(roleLookup);
                    UserRole userRole = new UserRole();
                    userRole.setRole(role);
                    userRoles.add(userRole);
                }
                user.setUserRoles(userRoles);
            }
            ServiceResponse<AuthenticationToken> authResponse =
                    OPFEngine.AuthorityService.processSiteMinderSignIn(user, request.getRemoteAddr());
            if (authResponse.isSuccess()) {
                AuthenticationToken authenticationToken = authResponse.getItem();
                String token = authenticationToken.getToken();
                Cookie authenticationTokenCookie = new Cookie(
                        OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE,
                        URLEncoder.encode(token, "utf8"));
                authenticationTokenCookie.setPath("/");
                //authenticationTokenCookie.setDomain(this.baseUrl);
                response.addCookie(authenticationTokenCookie);

                OpenFlamePrincipal principal = new UserPrincipal(authenticationToken.getUser());
                OPFContext context = OPFContext.initContext(principal, token);
                onContextAuthenticated(standardIdHeader, context);

                doFilterSpecial(request, response, filterChain);
            } else {
                String errorMessage = "Failed to obtain authentication token. Reason : " + authResponse.getMessage();
                logger.error(errorMessage);
                showErrorPage(errorMessage, request, response);
            }
        } else {
            doFilterSpecial(request, response, filterChain);
        }
    }

    protected void doFilterSpecial(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        boolean authorizedBySecurityRule = getSecurityFilter().isAuthorizedByRule(request, response, EntityProtocol.HTTP);
        if (!authorizedBySecurityRule) {
            logger.warn("OPF NOTE: Request is not authorized by opf security rule.");
        }
        Throwable throwable = null;
        try {
            filterChain.doFilter(request, response);
        } catch(Throwable th) {
            getSecurityFilter().afterDoFilter(request, response, th);
            throwable = th;
        }
        getSecurityFilter().afterDoFilter(request, response, throwable);
    }

    protected User prepareUserByStandardId(String standardId) {
        User user = new User();
        user.setUsername(standardId.toUpperCase());
        return user;
    }

    @SuppressWarnings("unused")
    protected void onContextAuthenticated(String standardId, OPFContext context) {
        //do custom logic here if needed.
    }

    protected void informUserIsNotActive(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        showErrorPage("Current user is not active.", request, response);
    }

    protected void showErrorPage(String errorMessage, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ServletOutputStream out = response.getOutputStream();
        String requestType = request.getContentType() == null ?
                request.getHeader("accept") : request.getContentType();
        if (MediaType.APPLICATION_XML.equalsIgnoreCase(requestType)) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance();
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(new ServiceResponse(errorMessage, false), out);
                out.close();
            } catch (JAXBException e) {
                logger.error(e.getMessage(), e);
                OpenFlameSecurityConstants.printXmlErrorToResponse(
                        response, OpenFlameSecurityConstants.API_SECURITY_ERROR_RESPONSE.format(
                        new String[]{e.getMessage()}));

            }
        } else if (MediaType.APPLICATION_JSON.equalsIgnoreCase(requestType)) {
            String jsonData;
            try {
                jsonData = WebUtils.serializeObjectToJSON(new ServiceResponse(errorMessage, false));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                jsonData = "{ success: false; message: '" + e.getMessage() + "' }";
            }
            out.println(jsonData);
            out.close();
        } else {
            out.println("<html><title>Error</title><body><h3>");
            out.println(errorMessage);
            out.println("</h3></body></html>");
        }

        out.flush();
        out.close();
    }

    protected UserAccessControlHandler populateAccessControlHandler(Map<String,String> properties) {
        String aclHandlerUrl = null, roleAclHandlerUrl = null;
        if (properties != null) {
            aclHandlerUrl = properties.get(PROP_USER_ACL_URL);
            roleAclHandlerUrl = properties.get(PROP_USER_ROLE_ACL_URL);
        }
        DefaultUserAccessControlHandler aclHandler = new DefaultUserAccessControlHandler();
        aclHandler.setHandlerUrl(aclHandlerUrl);
        aclHandler.setRoleLookupHandlerUrl(roleAclHandlerUrl);
        return aclHandler;
    }

    protected String getSMHeader(HttpServletRequest request) {
        return request == null ? null : request.getHeader(OpenFlameSecurityConstants.getSiteMinderAuthIdHeader());
    }

    @SuppressWarnings("unused")
    protected boolean isDebugMode() {
        return debugMode != null && debugMode;
    }

    @SuppressWarnings("unused")
    protected ISecurityFilter getSecurityFilter() {
        return securityFilter;
    }

    private void synchronizeSuperUsers(File configsFile, String initialMD5Value) {
        if (configsFile != null && configsFile.exists()) {
            SiteMinderAuthenticationProcessor.smConfigsFileMD5 =
                    SiteMinderAuthenticationProcessor.smConfigsFileMD5 == null ?
                            initialMD5Value : SiteMinderAuthenticationProcessor.smConfigsFileMD5;
            String currentSmConfigsFileMD5 = FileUtils.md5(configsFile);
            //Check if properties file has changed
            if (!currentSmConfigsFileMD5.equals(SiteMinderAuthenticationProcessor.smConfigsFileMD5)) {
                logger.info("Reloading " + SM_CONFIGS_FILENAME + " file...");
                Map<String,String> properties = MiscUtils.getProperties(configsFile);
                String superUserList = properties.get(PROP_SUPER_USERS_LIST);
                ServiceResponse<SimpleIdentifier<String>> resp =
                        OPFEngine.AuthorityService.synchronizeSMAdmins(superUserList);
                if (resp.isSuccess()) {
                    SiteMinderAuthenticationProcessor.smConfigsFileMD5 = currentSmConfigsFileMD5;
                    logger.info("SiteMinder Admin list synchronized successfully. Message from server: " + resp.getMessage());
                } else {
                    logger.error("Failed to synchronize smAdmins. Reason: " + resp.getMessage());
                }
            }
        }
    }

}