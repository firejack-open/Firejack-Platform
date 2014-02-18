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

package net.firejack.platform.api;


import net.firejack.platform.api.authority.IAuthorityService;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.config.IConfigService;
import net.firejack.platform.api.content.IContentService;
import net.firejack.platform.api.deployment.IDeploymentService;
import net.firejack.platform.api.directory.IDirectoryService;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.filestore.IFileStoreService;
import net.firejack.platform.api.mail.IMailService;
import net.firejack.platform.api.mobile.IMobileService;
import net.firejack.platform.api.process.IProcessService;
import net.firejack.platform.api.registry.IRegistryService;
import net.firejack.platform.api.schedule.IScheduleService;
import net.firejack.platform.api.securitymanager.ISecurityManagerService;
import net.firejack.platform.api.server.IRemoteDeploymentService;
import net.firejack.platform.api.site.ISiteService;
import net.firejack.platform.api.statistics.IStatisticsService;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.SecurityUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextContainerDelegate;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.SystemPrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class OPFEngine {

    static OPFEngineInitializer opfEngineInitializer;
    private static final Logger logger = Logger.getLogger(OPFEngine.class);

    /**
     * Make this class a singleton with static member variables that reference the core
     * API services. This class contains no actual methods of it's own, but allows access
     * to all the registered simple service interfaces from one static location. All instances
     * should be registered at OpenFlame or client application startup automatically based on
     * proper configuration of the configuration servlet.
     */
    private OPFEngine() {
    }


	/**
	 * Static block ensures that initialization occurs on first class access
	 */
	static
	{
//		initialize();
	}

	/**
	 * References the proper implementation of the RegistryService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
	public static IRegistryService RegistryService;

    /**
	 * References the proper implementation of the ProcessService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
	public static IProcessService ProcessService;

    /**
	 * References the proper implementation of the ContentService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IContentService ContentService;

    /**
	 * References the proper implementation of the SecurityManagerService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static ISecurityManagerService SecurityManagerService;

    /**
	 * References the proper implementation of the SecurityManagerService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IAuthorityService AuthorityService;

    /**
	 * References the proper implementation of the SiteService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static ISiteService SiteService;

    /**
	 * References the proper implementation of the DirectoryService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IDirectoryService DirectoryService;

    /**
	 * References the proper implementation of the StatisticsService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IStatisticsService StatisticsService;

	/**
	 * References the proper implementation of the ConfigService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IConfigService ConfigService;

	/**
	 * References the proper implementation of the FileStoreService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IFileStoreService FileStoreService;

	/**
	 * References the proper implementation of the RemoteDeploymentService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IRemoteDeploymentService RemoteDeploymentService;

	/**
	 * References the proper implementation of the DeploymentService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IDeploymentService DeploymentService;

	/**
	 * References the proper implementation of the DeploymentService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
    public static IMailService MailService;

    /**
     * References the proper implementation of the ScheduleService interface for
     * the current implementation and location (client / internal). The implementation
     * class is configured inside of the open flame configuration service and should be
     * set at startup time. Client applications will proxy their calls to the Open Flame
     * Services implementation with all required client-side caching enabled.
     */
    public static IScheduleService ScheduleService;

	/**
	 * References the proper implementation of the MobileService interface for
	 * the current implementation and location (client / internal). The implementation
	 * class is configured inside of the open flame configuration service and should be
	 * set at startup time. Client applications will proxy their calls to the Open Flame
	 * Services implementation with all required client-side caching enabled.
	 */
	public static IMobileService MobileService;

	/**
	 * Initializes the API based on the available OpenFlameContext. This initializer assumes
	 * that the Open Flame configuration service has been properly configured and initialized and
	 * that Open Flame configuration parameters for the appropriate service proxy or local implementations
	 * have been configured and are readable.
	 */
	public static void initialize()
	{
		opfEngineInitializer = OPFEngineInitializer.getInstance(OpenFlameSpringContext.getContext());
	}

	/**
	 * Initializes the API based on the available OpenFlameContext. This initializer assumes
	 * that the Open Flame configuration service has been properly configured and initialized and
	 * that Open Flame configuration parameters for the appropriate service proxy or local implementations
	 * have been configured and are readable.
	 * @param context Spring context
	 */
	public static void initialize(ApplicationContext context)
	{
		opfEngineInitializer = OPFEngineInitializer.getInstance(context);
	}

    /**
     * This method should be used outside of OpenFlameFilter context. Usually it's used as first method before set of
     * OpenFlame API Services methods calls. By invoking this method application requests authentication token information
     * from OpenFlame, and if authentication was successful application populates OPFContext using returned authentication
     * token info and associate it with current thread. This information allows to make calls to secured services.
     *
     * @param serverUrl OpeFLame server url
     * @param username username used for authentication request
     * @param password password used for authentication request
     * @throws BusinessFunctionException may throw BusinessFunctionException
     *
     * @see net.firejack.platform.api.OPFEngine#init(String, String)
     * @see net.firejack.platform.api.OPFEngine#release()
     */
    public static void init(String serverUrl, String username, String password)
            throws BusinessFunctionException {
        if (StringUtils.isBlank(serverUrl)) {
            throw new BusinessFunctionException("OpenFlame server url should not be blank.");
        } else if (StringUtils.isBlank(username)) {
            throw new BusinessFunctionException("OpenFlame username should not be blank.");
        } else if (StringUtils.isBlank(password)) {
            throw new BusinessFunctionException("OpenFlame password should not be blank.");
        }

	    initialize(new ClassPathXmlApplicationContext("/spring/api-client-config.xml"));

	    if (!OPFContext.isInitialized()) {
            OPFContext.initActionContainerFactory(SecurityUtils.emptyActionContainerFactory());
            OPFContext.initPermissionContainerFactory(SecurityUtils.emptyPermissionContainerFactory());
            OPFContext.initResourceLocationContainerFactory(SecurityUtils.emptyResourceLocationContainerFactory());
            OPFContext.initNavElementContainerFactory(SecurityUtils.emptyNavElementContainerFactory());
            OPFContext.initSecuredRecordContainerFactory(SecurityUtils.emptySecuredRecordInfoContainerFactory());
            OPFContext.initSpecifiedIdsFilterContainerFactory(SecurityUtils.emptySpecifiedIdsFilterContainerFactory());

            ContextContainerDelegate contextContainerDelegate = ContextContainerDelegate.getInstance();
            contextContainerDelegate.initialize(null);
            OPFContext.initContextContainerDelegate(contextContainerDelegate);
            if (!serverUrl.equals(Env.FIREJACK_URL.getValue())) {
                Env.FIREJACK_URL.setValue(serverUrl);
            }
        }

        ServiceResponse<AuthenticationToken> response =
                OPFEngine.AuthorityService.processSTSSignIn(username, password, null);
        if (response.isSuccess()) {
            AuthenticationToken authenticationToken = response.getItem();
            if (authenticationToken == null) {
                throw new BusinessFunctionException("Incorrect authentication response.");
            }
            String sessionToken = authenticationToken.getToken();
            User user = authenticationToken.getUser();
            OPFContext.initContext(new UserPrincipal(user), sessionToken);
        } else {
            throw new BusinessFunctionException(response.getMessage());
        }
    }

	public static void init(String serverUrl, String lookup, String name, String cert) throws BusinessFunctionException {
	    if (StringUtils.isBlank(serverUrl)) {
		    throw new BusinessFunctionException("OpenFlame server url should not be blank.");
	    } else if (StringUtils.isBlank(lookup)) {
		    throw new BusinessFunctionException("OpenFlame server lookup should not be blank.");
	    } else if (StringUtils.isBlank(cert)) {
		    throw new BusinessFunctionException("OpenFlame cert should not be blank.");
	    }

	    initialize(new ClassPathXmlApplicationContext("/spring/api-client-config.xml"));

	    if (!OPFContext.isInitialized()) {
            OPFContext.initActionContainerFactory(SecurityUtils.emptyActionContainerFactory());
            OPFContext.initPermissionContainerFactory(SecurityUtils.emptyPermissionContainerFactory());
            OPFContext.initResourceLocationContainerFactory(SecurityUtils.emptyResourceLocationContainerFactory());
            OPFContext.initNavElementContainerFactory(SecurityUtils.emptyNavElementContainerFactory());
            OPFContext.initSecuredRecordContainerFactory(SecurityUtils.emptySecuredRecordInfoContainerFactory());
            OPFContext.initSpecifiedIdsFilterContainerFactory(SecurityUtils.emptySpecifiedIdsFilterContainerFactory());

            ContextContainerDelegate contextContainerDelegate = ContextContainerDelegate.getInstance();
            contextContainerDelegate.initialize(null);
            OPFContext.initContextContainerDelegate(contextContainerDelegate);
            boolean needToOverrideBaseUrl = OpenFlameSecurityConstants.isSiteMinderAuthSupported() &&
                    StringUtils.isNotBlank(OpenFlameSecurityConstants.getOpfDirectUrl());
            if (!serverUrl.equals(Env.FIREJACK_URL.getValue()) && !needToOverrideBaseUrl) {
                Env.FIREJACK_URL.setValue(serverUrl);
            }
        }
	    ServiceResponse<AuthenticationToken> response = OPFEngine.AuthorityService.processSTSCertSignIn(lookup, name, cert);
        if (response.isSuccess()) {
            AuthenticationToken authenticationToken = response.getItem();
            if (authenticationToken == null) {
                throw new BusinessFunctionException("Incorrect authentication response.");
            }
            String sessionToken = authenticationToken.getToken();
            User user = authenticationToken.getUser();
            OPFContext systemUserContext = OPFContext.initContext(new SystemPrincipal(user), sessionToken);
            OpenFlameSecurityConstants.setSystemUserContext(systemUserContext);
        } else {
            throw new BusinessFunctionException(response.getMessage());
        }
    }

    /**
     * This method should be used outside of OpenFlameFilter context. Usually it's used as first method before set of
     * OpenFlame API Services methods calls. By invoking this method application requests authentication token information
     * from OpenFlame, and if authentication was successful application populates OPFContext using returned authentication
     * token info and associate it with current thread. This information allows to make calls to secured services.
     * This method uses <b><i>FIREJACK_URL</i><b/> environment variable value as OpenFlame server url.
     *
     * @param username username used for authentication request
     * @param password password used for authentication request
     * @throws BusinessFunctionException may throw BusinessFunctionException
     *
     * @see net.firejack.platform.api.OPFEngine#init(String, String, String)
     * @see net.firejack.platform.api.OPFEngine#release()
     */
    public static void init(String username, String password) throws BusinessFunctionException {
        String openFlameUrl = Env.FIREJACK_URL.getValue();
        init(openFlameUrl, username, password);
    }

    /**
     * This method should be used outside of OpenFlameFilter context. Usually it's used as last invocation right after
     * set of OpenFlame API Services methods calls. By invoking this method we sign out current session token and
     * releasing used OPFContext.
     */
    public static void release() {
        try {
            String sessionToken = OPFContext.getContext().getSessionToken();
            if (sessionToken != null) {
                ContextContainerDelegate.getInstance().invalidateBusinessContextInStore();
                ServiceResponse serviceResponse = OPFEngine.AuthorityService.processSTSSignOut(sessionToken);
                if (!serviceResponse.isSuccess()) {
                    throw new BusinessFunctionException(serviceResponse.getMessage());
                }
            }
	        opfEngineInitializer.release();
	        opfEngineInitializer = null;
        } catch (ContextLookupException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static <T> T getRemoteProxy(String baseUrl, Class<T> interfaceClass) {
        T proxy;
        if (opfEngineInitializer == null) {
            proxy = null;
        } else {
            proxy = opfEngineInitializer.populateProxy(interfaceClass);
            if (proxy != null) {
                ((AbstractServiceProxy) proxy).setBaseUrl(baseUrl);
            }
        }
        return proxy;
    }
}