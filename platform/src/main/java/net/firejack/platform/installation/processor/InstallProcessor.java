/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.installation.processor;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.cache.CacheProcessor;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.InstallUtils;
import net.firejack.platform.installation.processor.event.*;
import net.firejack.platform.model.event.CompleteInitEvent;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import net.firejack.platform.web.cache.ICacheManagerInitializer;
import net.firejack.platform.web.cache.ICachedEntryProducer;
import net.firejack.platform.web.security.extension.cache.ConsoleCachedEntryProducer;
import net.firejack.platform.web.security.model.principal.GuestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class InstallProcessor implements ApplicationListener<ContextRefreshedEvent>, ApplicationEventPublisherAware, ICacheManagerInitializer {

	private static final String MSG_ERROR_FAILED_TO_RETRIEVE_GUEST_ROLE = "Failed to retrieve guest role.";

	@Autowired
	private SetupSystemProcessor setupSystemProcessor;
	@Autowired
	private CacheProcessor cacheProcessor;
	@Autowired
	private IRoleStore roleStore;
	@Value("${debug.mode}")
	private boolean debugMode;

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	@Async
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!ConfigContainer.isAppInstalled()) {
			File xmlEnvFile = InstallUtils.getXmlEnv();
			File propEnvFile = InstallUtils.getPropEnv();

			if (xmlEnvFile.exists() && propEnvFile.exists()) {
				publisher.publishEvent(new InstallMasterEvent(this));
			} else if (OpenFlameConfig.MASTER_URL.exist()) {
				publisher.publishEvent(new InstallSlaveEvent(this));
			} else {
				throw new IllegalStateException("Application  has not installed because config file not found by path xml: " +
						xmlEnvFile.getAbsolutePath() + " and properties: " + propEnvFile.getAbsolutePath());
			}

			OpenFlameConfig.APP_ENV_XML.setValue(FileUtils.md5(xmlEnvFile));
			OpenFlameConfig.APP_ENV_PROPERTIES.setValue(FileUtils.md5(propEnvFile));

			OpenFlameConfig.save();
		} else {
			publisher.publishEvent(new ReloadEnvironmentEvent(this));
			OPFEngine.initialize();
		}

		if (!CacheManager.isInitialized()) {
			CacheManager.initializeIfFirstTime(this);
		}

		setupSystemProcessor.loadRepositories();
		setupSystemProcessor.setupSystemURL();
		publisher.publishEvent(new OpenflameUpgradeEvent(this));
		publisher.publishEvent(new BuildTemplateEvent(this));
		publisher.publishEvent(new CompleteInitEvent(this));
	}

	@Override
	public ICachedEntryProducer populateCachedEntryProducer() {
		return new ConsoleCachedEntryProducer();
	}

	@Override
	public ICacheDataProcessor getCachedDataProcessor() {
		return this.cacheProcessor;
	}

    @Override
    public Set<Long> getGuestRoleIds() {
        List<RoleModel> guestRoles = roleStore.findByName(GuestUser.GUEST_ROLE_NAME);
        if (guestRoles == null || guestRoles.isEmpty()) {
            throw new IllegalStateException(MSG_ERROR_FAILED_TO_RETRIEVE_GUEST_ROLE);
        }
        Set<Long> roleIds = new HashSet<Long>();
        for (RoleModel guestRole : guestRoles) {
            roleIds.add(guestRole.getId());
        }
        return roleIds;
    }

	public boolean isDebugMode() {
		return debugMode;
	}
}
