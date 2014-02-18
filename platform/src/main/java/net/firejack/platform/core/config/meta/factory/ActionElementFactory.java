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

import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.utils.StringUtils;

public class ActionElementFactory extends NavigableElementFactory<ActionModel, ActionElement> {

    @Override
    protected void initEntitySpecific(ActionModel action, ActionElement actionElement) {
        super.initEntitySpecific(action, actionElement);
        if (action.getMethod() != null && !HTTPMethod.GET.equals(action.getMethod())) {
            if (StringUtils.isNotBlank(actionElement.getInputVOEntityLookup())) {
                EntityModel entity = (EntityModel) registryNodeStore.findByLookup(actionElement.getInputVOEntityLookup());
                action.setInputVOEntity(entity);
            }
            if (action.getInputVOEntity() == null) {
                action.setInputVOEntity((EntityModel) action.getParent());
            }
        }
        if (StringUtils.isNotBlank(actionElement.getOutputVOEntityLookup())) {
            RegistryNodeModel model = registryNodeStore.findByLookup(actionElement.getOutputVOEntityLookup());
            action.setOutputVOEntity(model);
        }
        if (action.getOutputVOEntity() == null) {
            action.setOutputVOEntity(action.getParent());
        }
    }

}