/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
