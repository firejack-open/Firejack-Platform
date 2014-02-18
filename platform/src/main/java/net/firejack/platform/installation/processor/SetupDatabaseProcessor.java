/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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
