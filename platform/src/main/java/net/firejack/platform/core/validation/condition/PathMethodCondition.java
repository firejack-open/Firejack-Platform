/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.core.validation.condition;

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component("pathMethodCondition")
public class PathMethodCondition extends AbstractCondition {

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
                String path = "";
                boolean isNeedPath = false;
                for (Long parentId : registryNodeIds) {
                    RegistryNodeModel registryNode = registryNodeStore.findById(parentId);
                    if (registryNode instanceof PackageModel) {
                        isNeedPath = true;
                        break;
                    }
                    path = "/" + StringUtils.normalize(registryNode.getName()) + path;
                }
                if (isNeedPath && StringUtils.isNotBlank(path)) {
                    defaultValue = path;
                }
            }
        }
        return defaultValue;
    }

}