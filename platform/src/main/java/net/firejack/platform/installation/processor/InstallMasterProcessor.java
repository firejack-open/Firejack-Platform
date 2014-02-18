/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.installation.processor;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.installation.processor.event.InstallMasterEvent;
import net.firejack.platform.installation.processor.event.OpenflameUpgradeEvent;
import net.firejack.platform.installation.processor.event.PrepareTemplateEvent;
import net.firejack.platform.utils.OpenFlameConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class InstallMasterProcessor implements ApplicationListener<InstallMasterEvent>, ApplicationEventPublisherAware {
	@Autowired
	private SetupDatabaseProcessor setupDatabaseProcessor;
	@Autowired
	private InitializationMetadataProcessor initializationMetadataProcessor;
	@Autowired
	private SetupSystemProcessor setupSystemProcessor;

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	public void onApplicationEvent(InstallMasterEvent event) {
		String user = OpenFlameConfig.DB_USER_NAME.getValue();
		String password = OpenFlameConfig.DB_USER_PASSWORD.getValue();
		String host = OpenFlameConfig.DB_HOST.getValue();
		String port = OpenFlameConfig.DB_PORT.getValue();

		OPFEngine.initialize();

		if (OpenFlameDataSource.ping(DatabaseName.MySQL, host, port, user, password, null, null)) {
			boolean created = setupDatabaseProcessor.createDatabase();
			if (created) {
				setupDatabaseProcessor.restoreDatabase();
			}


			setupSystemProcessor.setupSystem();
			setupSystemProcessor.setupSystemAlias();

			if (created) {
				initializationMetadataProcessor.initializePlatform();
			} else {
				publisher.publishEvent(new OpenflameUpgradeEvent(this));
			}

			setupSystemProcessor.associatePackage();
			setupSystemProcessor.setupSystemAccount();
			publisher.publishEvent(new PrepareTemplateEvent(this));
		}
	}
}
