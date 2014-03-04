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

import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.core.utils.db.DBUtils;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;


@Component
public class SetupDatabaseProcessor {

	private static final Logger logger = Logger.getLogger(SetupDatabaseProcessor.class);

	@Autowired
	@Qualifier("dataSource")
	private OpenFlameDataSource source;

	@Autowired
	private PackageInstallationService packageInstallationService;

	@ProgressStatus(description = "Create database schema.", weight = 5)
	public boolean createDatabase() {
		String schema = OpenFlameConfig.DB_SCHEMA.getValue();

		logger.info("Creating database schema: [" + schema + "]...");

		DataSource dataSource = source.database();

		if (DBUtils.dbExists(dataSource, schema)) {
			return false;
		}
		if (!DBUtils.createDatabase(dataSource, schema)) {
			throw new BusinessFunctionException("Can't create database schema: [" + schema + "]");
		}
		logger.info("... done!");
		return true;
	}

	@ProgressStatus(description = "Create Firejack Platform tables...", weight = 10)
	public void restoreDatabase() {
		DataSource dataSource = source.schema();
		if (DBUtils.executeStatement(dataSource, source.getValidationQuery())) {
			File packageXml = FileUtils.getResource("dbupdate", PackageFileType.PACKAGE_XML.getOfrFileName());

			packageInstallationService.install(packageXml, dataSource);
			source.refreshDBProperties();
		}
	}
}
