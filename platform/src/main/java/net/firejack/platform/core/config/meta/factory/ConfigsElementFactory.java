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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.conf.ConfigReference;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.config.ConfigModel;


public class ConfigsElementFactory extends PackageDescriptorConfigElementFactory<ConfigModel, ConfigReference> {

    /***/
    public ConfigsElementFactory() {
        setEntityClass(ConfigModel.class);
        setElementClass(ConfigReference.class);
    }

    @Override
    protected void initDescriptorElementSpecific(ConfigReference configElement, ConfigModel config) {
        super.initDescriptorElementSpecific(configElement, config);
        configElement.setPath(config.getPath());
        configElement.setName(config.getName());
        configElement.setValue(config.getValue());
        configElement.setDescription(config.getDescription());
    }

    public ConfigModel getEntity(ConfigReference configElement) {
        configElementFactory.checkElementName(configElement);
        ConfigModel config = populateEntity(configElement);
        if (config == null) {
            throw new OpenFlameRuntimeException("Failed to instantiate entity.");
        }
        config.setName(configElement.getName());
        config.setPath(DiffUtils.lookupByRefPath(configElement.getPath()));
        config.setValue(configElement.getValue());
        config.setLookup(DiffUtils.lookup(config.getPath(), config.getName()));
        config.setDescription(configElement.getDescription());
        RegistryNodeModel parent = registryNodeStore.findByLookup(config.getPath());
        config.setParent(parent);
        initializeModelUID(config, configElement);

        return config;
    }
}
