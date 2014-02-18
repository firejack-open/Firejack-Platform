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

package net.firejack.platform.generate.service;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unused")
@Component
public class DatabaseSQLGeneratorService implements IDatabaseSQLGeneratorService {

    @Autowired
    @Qualifier("packageInstallationService")
    private PackageInstallationService packageInstallationService;

    @Override
    @ProgressStatus(weight = 2, description = "Generate database sql script")
    public void generateSqlScript(IPackageDescriptor descriptor, Structure structure) throws IOException {
        Map<String, String> sqlScriptsMap = packageInstallationService.generateSQL(descriptor);
        for (Map.Entry<String, String> scriptEntry : sqlScriptsMap.entrySet()) {
            String filenamePrefix = scriptEntry.getKey();
            String sqlScriptForLookup = scriptEntry.getValue();
            FileUtils.writeStringToFile(new File(structure.getDbSql(), filenamePrefix + "-db-script.sql"), sqlScriptForLookup);
        }
    }

}
