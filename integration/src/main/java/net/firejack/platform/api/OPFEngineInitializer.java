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

import net.firejack.platform.api.authority.AuthorityServiceProxy;
import net.firejack.platform.api.authority.IAuthorityService;
import net.firejack.platform.api.config.ConfigServiceProxy;
import net.firejack.platform.api.config.IConfigService;
import net.firejack.platform.api.content.ContentServiceProxy;
import net.firejack.platform.api.content.IContentService;
import net.firejack.platform.api.deployment.DeploymentServiceProxy;
import net.firejack.platform.api.deployment.IDeploymentService;
import net.firejack.platform.api.directory.DirectoryServiceProxy;
import net.firejack.platform.api.directory.IDirectoryService;
import net.firejack.platform.api.filestore.FileStoreServiceProxy;
import net.firejack.platform.api.filestore.IFileStoreService;
import net.firejack.platform.api.mail.IMailService;
import net.firejack.platform.api.mail.MailServiceProxy;
import net.firejack.platform.api.mobile.IMobileService;
import net.firejack.platform.api.mobile.MobileServiceProxy;
import net.firejack.platform.api.process.IProcessService;
import net.firejack.platform.api.process.ProcessServiceProxy;
import net.firejack.platform.api.registry.IRegistryService;
import net.firejack.platform.api.registry.RegistryServiceProxy;
import net.firejack.platform.api.schedule.IScheduleService;
import net.firejack.platform.api.schedule.ScheduleServiceProxy;
import net.firejack.platform.api.securitymanager.ISecurityManagerService;
import net.firejack.platform.api.securitymanager.SecurityManagerServiceProxy;
import net.firejack.platform.api.server.IRemoteDeploymentService;
import net.firejack.platform.api.server.RemoteDeploymentServiceProxy;
import net.firejack.platform.api.site.ISiteService;
import net.firejack.platform.api.site.SiteServiceProxy;
import net.firejack.platform.api.statistics.IStatisticsService;
import net.firejack.platform.api.statistics.StatisticsServiceProxy;
import net.firejack.platform.core.domain.AbstractDTO;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Map;


class OPFEngineInitializer {
	private static final Logger logger = Logger.getLogger(OPFEngineInitializer.class);

	private static OPFEngineInitializer instance;
	private ApplicationContext context;

	public static OPFEngineInitializer getInstance(ApplicationContext context) {
		if (instance == null && context != null) {
			instance = new OPFEngineInitializer(context);
		}
		return instance;
	}

	private OPFEngineInitializer(ApplicationContext context) {
		this.context=context;
		Class[] classes = getBeans();

		logger.info("Initializing OPFEngine services...");
        //----------------------------
        try {
            IRegistryService registryService = context.getBean(APIConstants.BEAN_NAME_REGISTRY_SERVICE, IRegistryService.class);
            logger.info("Local implementation for OPFEngine.RegistryService was found. Using class - [" + registryService.getClass() + "].");
            OPFEngine.RegistryService = registryService;
        } catch (NoSuchBeanDefinitionException e) {
            logger.info("Local implementation for OPFEngine.RegistryService was not found, - using Proxy implementation.");
            OPFEngine.RegistryService = new RegistryServiceProxy(classes);
        }

        //----------------------------
        try {
            IProcessService processService = context.getBean(APIConstants.BEAN_NAME_PROCESS_SERVICE, IProcessService.class);
            logger.info("Local implementation for OPFEngine.ProcessService was found. Using class - [" + processService.getClass() + "].");
            OPFEngine.ProcessService = processService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.ProcessService was not found, - using Proxy implementation.");
            OPFEngine.ProcessService = new ProcessServiceProxy(classes);
        }

        //----------------------------
        try {
            IContentService contentService = context.getBean(APIConstants.BEAN_NAME_CONTENT_SERVICE, IContentService.class);
            logger.info("Local implementation for OPFEngine.ContentService was found. Using class - [" + contentService.getClass() + "].");
            OPFEngine.ContentService = contentService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.ContentService was not found, - using Proxy implementation.");
            OPFEngine.ContentService = new ContentServiceProxy(classes);
        }

        //----------------------------
        try {
            ISecurityManagerService securityManagerService = context.getBean(
                    APIConstants.BEAN_NAME_SECURITY_MANAGER_SERVICE, ISecurityManagerService.class);
            logger.info("Local implementation for OPFEngine.SecurityManagerService was found. Using class - [" +
                    securityManagerService.getClass() + "].");
            OPFEngine.SecurityManagerService = securityManagerService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.SecurityManagerService was not found, - using Proxy implementation.");
            OPFEngine.SecurityManagerService = new SecurityManagerServiceProxy(classes);
        }

        //----------------------------
        try {
            IAuthorityService authorityService = context.getBean(
                APIConstants.BEAN_NAME_AUTHORITY_SERVICE, IAuthorityService.class);
            logger.info("Local implementation for OPFEngine.AuthorityService was found. Using class - [" +
                    authorityService.getClass() + "].");
            OPFEngine.AuthorityService = authorityService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.AuthorityService was not found, - using Proxy implementation.");
            OPFEngine.AuthorityService = new AuthorityServiceProxy(classes);
        }

        //----------------------------
        try {
            ISiteService siteService = context.getBean(APIConstants.BEAN_NAME_SITE_SERVICE, ISiteService.class);
            logger.info("Local implementation for OPFEngine.SiteService was found. Using class - [" + siteService.getClass() + "].");
            OPFEngine.SiteService = siteService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.SiteService was not found, - using Proxy implementation.");
            OPFEngine.SiteService = new SiteServiceProxy(classes);
        }

        //----------------------------
        try {
            IDirectoryService directoryService = context.getBean(APIConstants.BEAN_NAME_DIRECTORY_SERVICE, IDirectoryService.class);
            logger.info("Local implementation for OPFEngine.DirectoryService was found. Using class - [" + directoryService.getClass() + "].");
            OPFEngine.DirectoryService = directoryService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.DirectoryService was not found, - using Proxy implementation.");
            OPFEngine.DirectoryService = new DirectoryServiceProxy(classes);
        }
        
        //----------------------------
        try {
            IStatisticsService statisticsService = context.getBean(
                    APIConstants.BEAN_NAME_STATISTICS_SERVICE, IStatisticsService.class);
            logger.info("Local implementation for OPFEngine.StatisticsService was found. Using class - [" +
                    statisticsService.getClass() + "].");
            OPFEngine.StatisticsService = statisticsService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.DirectoryService was not found, - using Proxy implementation.");
            OPFEngine.StatisticsService = new StatisticsServiceProxy(classes);
        }

        //----------------------------
	    try {
		    IConfigService configService = context.getBean(APIConstants.BEAN_NAME_CONFIG_SERVICE, IConfigService.class);
		    logger.info("Local implementation for OPFEngine.ConfigService was found. Using class - [" + configService.getClass() + "].");
		    OPFEngine.ConfigService = configService;
	    } catch (BeansException e) {
		    logger.info("Local implementation for OPFEngine.ConfigService was not found, - using Proxy implementation.");
		    OPFEngine.ConfigService = new ConfigServiceProxy(classes);
	    }

        //----------------------------
	    try {
//            throw new BeanCreationException("Need to init proxy filestore implementation");               //TODO [CLUSTER]
		    IFileStoreService fileStoreService = context.getBean(APIConstants.BEAN_NAME_FILESTORE_SERVICE, IFileStoreService.class);
		    fileStoreService.valid();
		    logger.info("Local implementation for OPFEngine.FileStoreService was found. Using class - [" + fileStoreService.getClass() + "].");
		    OPFEngine.FileStoreService = fileStoreService;
	    } catch (BeansException e) {
		    logger.info("Local implementation for OPFEngine.FileStoreService was not found, - using Proxy implementation.");
		    OPFEngine.FileStoreService = new FileStoreServiceProxy(classes);
	    }

        //----------------------------
        try {
            IRemoteDeploymentService remoteDeploymentService = context.getBean(APIConstants.BEAN_NAME_REMOTE_DEPLOYMENT_SERVICE, IRemoteDeploymentService.class);
		    logger.info("Local implementation for OPFEngine.RemoteDeploymentService was found. Using class - [" + remoteDeploymentService.getClass() + "].");
		    OPFEngine.RemoteDeploymentService = remoteDeploymentService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.RemoteDeploymentService was not found, - using Proxy implementation.");
		    OPFEngine.RemoteDeploymentService = new RemoteDeploymentServiceProxy(classes);
        }

        //----------------------------
        try {
            IDeploymentService deploymentService = context.getBean(APIConstants.BEAN_NAME_DEPLOYMENT_SERVICE, IDeploymentService.class);
		    logger.info("Local implementation for OPFEngine.DeploymentService was found. Using class - [" + deploymentService.getClass() + "].");
		    OPFEngine.DeploymentService = deploymentService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.RemoteDeploymentService was not found, - using Proxy implementation.");
		    OPFEngine.DeploymentService = new DeploymentServiceProxy(classes);
        }

        //----------------------------
        try {
            IMailService mailService = context.getBean(APIConstants.BEAN_NAME_MAIL_SERVICE, IMailService.class);
            logger.info("Local implementation for OPFEngine.MailService was found. Using class - [" + mailService.getClass() + "].");
            OPFEngine.MailService = mailService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.MailService was not found, - using Proxy implementation.");
            OPFEngine.MailService = new MailServiceProxy(classes);
        }

        //----------------------------
        try {
            IScheduleService scheduleService = context.getBean(APIConstants.BEAN_NAME_SCHEDULE_SERVICE, IScheduleService.class);
            logger.info("Local implementation for OPFEngine.ScheduleService was found. Using class - [" + scheduleService.getClass() + "].");
            OPFEngine.ScheduleService = scheduleService;
        } catch (BeansException e) {
            logger.info("Local implementation for OPFEngine.ScheduleService was not found, - using Proxy implementation.");
            OPFEngine.ScheduleService = new ScheduleServiceProxy(classes);
        }

		try {
			IMobileService mobileService = context.getBean(APIConstants.BEAN_NAME_MOBILE_SERVICE, IMobileService.class);
			logger.info("Local implementation for OPFEngine.MobileService was found. Using class - [" + mobileService.getClass() + "].");
			OPFEngine.MobileService = mobileService;
		} catch (BeansException e) {
			logger.info("Local implementation for OPFEngine.MobileService was not found, - using Proxy implementation.");
			OPFEngine.MobileService = new MobileServiceProxy(classes);
		}
	}

	private Class[] getBeans() {
		Map<String, AbstractDTO> map = context.getBeansOfType(AbstractDTO.class);
		Class[] classes = new Class[map.size() + 1];
		int i = 0;
		for (AbstractDTO dto : map.values()) {
			classes[i++] = dto.getClass();
		}
        classes[i] = ArrayList.class;
		return classes;
	}

	public void release() {
		instance = null;
	}

	@SuppressWarnings("unchecked")
    public <T> T populateProxy(Class<T> interfaceClass) {
		Class[] classes = getBeans();
        T proxy;
        if (IRegistryService.class.equals(interfaceClass)) {
            proxy = (T) new RegistryServiceProxy(classes);
        } else if (IProcessService.class.equals(interfaceClass)) {
            proxy = (T) new ProcessServiceProxy(classes);
        } else if (IContentService.class.equals(interfaceClass)) {
            proxy = (T) new ContentServiceProxy(classes);
        } else if (ISecurityManagerService.class.equals(interfaceClass)) {
            proxy = (T) new SecurityManagerServiceProxy(classes);
        } else if (IAuthorityService.class.equals(interfaceClass)) {
            proxy = (T) new AuthorityServiceProxy(classes);
        } else if (ISiteService.class.equals(interfaceClass)) {
            proxy = (T) new SiteServiceProxy(classes);
        } else if (IDirectoryService.class.equals(interfaceClass)) {
            proxy = (T) new DirectoryServiceProxy(classes);
        } else if (IStatisticsService.class.equals(interfaceClass)) {
            proxy = (T) new StatisticsServiceProxy(classes);
        } else if (IConfigService.class.equals(interfaceClass)) {
            proxy = (T) new ConfigServiceProxy(classes);
        } else if (IFileStoreService.class.equals(interfaceClass)) {
            proxy = (T) new FileStoreServiceProxy(classes);
        } else if (IRemoteDeploymentService.class.equals(interfaceClass)) {
            proxy = (T) new RemoteDeploymentServiceProxy(classes);
        } else if (IDeploymentService.class.equals(interfaceClass)) {
            proxy = (T) new DeploymentServiceProxy(classes);
        } else if (IMailService.class.equals(interfaceClass)) {
            proxy = (T) new MailServiceProxy(classes);
        } else {
            proxy = null;
        }
        return proxy;
    }

}
