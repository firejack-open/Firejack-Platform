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

package net.firejack.platform.service.registry.broker.package_;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createPackageBroker")
public class CreatePackageBroker extends OPFSaveBroker<PackageModel, Package, RegistryNodeTree> {

	@Autowired
	private IPackageStore store;

	@Autowired
	protected FileHelper helper;

    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Package has created successfully";
	}

	@Override
	protected PackageModel convertToEntity(Package model) {
		return factory.convertFrom(PackageModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(PackageModel entity) {
		return treeNodeFactory.convertTo(entity);
	}

	@Override
	protected void save(PackageModel model) throws Exception {
		store.save(model);
		OPFEngine.FileStoreService.createDirectory(OpenFlame.FILESTORE_BASE,helper.getVersion(), model.getId().toString(), model.getVersion().toString());
	}

}