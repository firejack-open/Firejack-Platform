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
