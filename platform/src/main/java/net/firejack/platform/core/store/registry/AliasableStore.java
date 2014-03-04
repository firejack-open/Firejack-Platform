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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.utils.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class AliasableStore<R extends RegistryNodeModel> extends RegistryNodeStore<R> implements IAliasableStore<R> {

    @Autowired
    @Qualifier("systemStore")
    private ISystemStore systemStore;

    @Override
	@Transactional
	public void save(R model) {
        super.save(model);

        RegistryNodeModel mainSystemModel = model.getParent();
        List<SystemModel> aliasSystemModels = systemStore.findAliasesById(mainSystemModel.getId(), null);
        for (SystemModel aliasSystemModel : aliasSystemModels) {
            synchronize(mainSystemModel, aliasSystemModel);
        }
    }

    @Override
    @Transactional
    public void delete(R model) {
        List<R> aliasModels = findAliasesById(model.getId(), null);
        for (R aliasModel : aliasModels) {
            deleteRecursively(aliasModel);
        }
        super.delete(model);
    }

    public void synchronize(RegistryNodeModel mainSystemModel, SystemModel aliasSystemModel) {
        List<R> mainServerModels = findAllByParentIdWithFilter(mainSystemModel.getId(), null);
        for (R mainServerModel : mainServerModels) {
            R serverModel = findByParentIdAndMainId(aliasSystemModel.getId(), mainServerModel.getId());
            if (serverModel == null) {
                serverModel = instantiate();
            }
            ClassUtils.copyProperties(mainServerModel, serverModel, new String[]{"id", "path", "lookup", "parent", "uid", "hash"});
            serverModel.setParent(aliasSystemModel);
            serverModel.setMain(mainServerModel);
            save(serverModel);
        }
    }

}
