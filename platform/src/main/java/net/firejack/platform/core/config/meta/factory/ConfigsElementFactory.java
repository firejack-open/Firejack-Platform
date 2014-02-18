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
