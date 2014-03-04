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

package net.firejack.platform.core.validation.condition;

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class AbstractPackageMethodCondition extends AbstractCondition {

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Override
    public List<Constraint> generateConstraints(Map<String, String> params) {
        return null;
    }

    @Override
    public String defaultValue(Map<String, String> params) {
        String defaultValue = null;
        if (params != null) {
            String sParentId = params.get("parentId");
            if (StringUtils.isNumeric(sParentId)) {
                Long registryNodeId = Long.parseLong(sParentId);
                List<Long> registryNodeIds = new ArrayList<Long>();
                List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
                registryNodeStore.findCollectionParentIds(registryNodeIds, registryNodeId, collectionArrayIds);
                PackageModel packageRN = null;
                for (Long parentId : registryNodeIds) {
                    RegistryNodeModel registryNode = registryNodeStore.findById(parentId);
                    if (registryNode instanceof PackageModel) {
                        packageRN = (PackageModel) registryNode;
                        break;
                    }
                }
                if (packageRN != null) {
                    defaultValue = getDefaultValue(packageRN);
                }
            }
        }
        return defaultValue;
    }

    protected abstract String getDefaultValue(PackageModel packageRN);

}
