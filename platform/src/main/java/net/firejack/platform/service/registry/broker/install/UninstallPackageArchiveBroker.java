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

package net.firejack.platform.service.registry.broker.install;

import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.api.registry.domain.DatabaseAction;
import net.firejack.platform.api.registry.domain.PackageAction;
import net.firejack.platform.api.registry.model.DatabaseActionType;
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.installer.IDeployService;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TrackDetails
@Component("uninstallPackageArchiveBroker")
@ProgressComponent(weight = 80, showLogs = true)
public class UninstallPackageArchiveBroker extends ServiceBroker<ServiceRequest<PackageAction>, ServiceResponse> {

    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private IDatabaseStore databaseStore;
    @Autowired
    private IDeployService deployService;
    @Autowired
    private ManuallyProgress progress;

    @Override
    protected ServiceResponse perform(ServiceRequest<PackageAction> request) throws Exception {
        PackageAction packageAction = request.getData();
        Long packageId = packageAction.getId();

        PackageModel packageRN = packageStore.findWithSystemById(packageId);

        Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels = packageStore.findAllWithDatabaseById(packageId);
        Map<String, DatabaseAction> associatedDatabases = new HashMap<String, DatabaseAction>();
        List<DatabaseAction> databaseActions = packageAction.getDatabaseActions();
        if (databaseActions != null) {
            for (DatabaseAction databaseAction : databaseActions) {
                if (databaseAction.getAction() == DatabaseActionType.DROP) {
                    DatabaseModel databaseModel = databaseStore.findById(databaseAction.getId());
                    Database database = factory.convertTo(Database.class, databaseModel);
                    databaseAction.setDatabase(database);
                    String domainLookup = getDomainLookupByDatabase(associatedDatabaseModels, database.getId());
                    associatedDatabases.put(domainLookup, databaseAction);
                }
            }
        }

        String token = OPFContext.getContext().getSessionToken();

        progress.status("Undeployment process is running...", 1, LogLevel.INFO);
        deployService.undeployPackage(packageRN, token, associatedDatabases);

        return new ServiceResponse("Uninstall successfully", true);
    }

    private String getDomainLookupByDatabase(Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels, Long id) {
        for (Map.Entry<RegistryNodeModel, DatabaseModel> associatedDatabaseModel : associatedDatabaseModels.entrySet()) {
            if (associatedDatabaseModel.getValue().getId().equals(id)) {
                return associatedDatabaseModel.getKey().getLookup();
            }
        }
        return null;
    }
}
