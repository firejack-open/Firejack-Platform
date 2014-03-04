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
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.cache.CacheProcessor;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import net.firejack.platform.web.cache.ICacheManagerInitializer;
import net.firejack.platform.web.cache.ICachedEntryProducer;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import net.firejack.platform.web.security.extension.cache.ConsoleCachedEntryProducer;
import net.firejack.platform.web.security.model.principal.GuestUser;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component("initializationMetadataProcessor")
public class InitializationMetadataProcessor implements ICacheManagerInitializer {

    private static final String MSG_OPENFlAME_HAS_NOT_INSTALLED = "Firejack Platform Console has not installed.";
    private static final String MSG_ERROR_FAILED_TO_RETRIEVE_GUEST_ROLE = "Failed to retrieve guest role.";

    private static final Logger logger = Logger.getLogger(InitializationMetadataProcessor.class);

    @Autowired
    @Qualifier("packageInstallationService")
    private PackageInstallationService packageInstallationService;

    @Autowired
    private FileHelper helper;

    @Autowired
    @Qualifier("cacheProcessor")
    private CacheProcessor cacheProcessor;
    @Autowired
    private IRoleStore roleStore;

    @Value("${debug.mode}")
    private boolean debugMode;

    /***/
    @ProgressStatus(description = "Initialization of Firejack Platform...", weight = 10)
    public void initializePlatform() {
        try {
            File packageXmlInstallationFile = FileUtils.getResource("dbupdate", PackageFileType.PACKAGE_XML.getOfrFileName());
            File resourceZipInstallationFile = FileUtils.getResource("dbupdate", PackageFileType.RESOURCE_ZIP.getOfrFileName());
	        FileInputStream packageXml= new FileInputStream(packageXmlInstallationFile);
	        FileInputStream resourceZip = new FileInputStream(resourceZipInstallationFile);
	        StatusProviderTranslationResult statusResult = packageInstallationService.activatePackage(packageXml, resourceZip);
	        IOUtils.closeQuietly(packageXml);
	        IOUtils.closeQuietly(resourceZip);


            if (statusResult.getResult() && statusResult.getPackage() != null) {
                PackageModel packageRN = statusResult.getPackage();
                if (packageRN != null) {
	                packageXml= new FileInputStream(packageXmlInstallationFile);
                    String packageXmlName = packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension();
	                OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,packageXmlName,packageXml,helper.getVersion(), String.valueOf(packageRN.getId()),packageRN.getVersion().toString());
	                IOUtils.closeQuietly(packageXml);

	                resourceZip = new FileInputStream(resourceZipInstallationFile);
	                String resourceZipName = packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension();
	                OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, resourceZipName, resourceZip, helper.getVersion(), String.valueOf(packageRN.getId()), packageRN.getVersion().toString());
	                IOUtils.closeQuietly(resourceZip);
                }
            } else {
                throw new BusinessFunctionException(MSG_OPENFlAME_HAS_NOT_INSTALLED);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
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
