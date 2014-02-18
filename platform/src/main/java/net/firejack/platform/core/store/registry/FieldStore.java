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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.store.lookup.LookupStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;


@Component("fieldStore")
public class FieldStore extends LookupStore<FieldModel, Long> implements IFieldStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(FieldModel.class);
    }

    @Override
    @Transactional
    public void saveOrUpdate(FieldModel field) {
        field.setChildCount(0);
        super.saveOrUpdate(field);
    }

    @Override
    @Transactional
    public void saveOrUpdateAll(List<FieldModel> fields) {
        for (FieldModel field : fields) {
            field.setChildCount(0);
        }
        super.saveOrUpdateAll(fields);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldModel> findFieldsByRegistryNodeId(Long registryNodeId) {
        return find(null, null, "FieldStore.findFieldsByRegistryNodeId",
                "registryNodeId", registryNodeId);
    }

    @Override
    @Transactional(readOnly = true)
    public FieldModel findByParentLookupAndName(String parentLookup, String fieldName) {
        return findSingle("FieldStore.findFieldByParentLookupAndName",
                "parentLookup", parentLookup, "name", fieldName);
    }

    @Override
    @Transactional
    public void deleteByParentLookupAndName(String parentLookup, String fieldName) {
        FieldModel field = findByParentLookupAndName(parentLookup, fieldName);
        if (field != null) {
            delete(field);
        }
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
        List<FieldModel> fields = findFieldsByRegistryNodeId(registryNodeId);
        for (FieldModel field : fields) {
            delete(field);
        }
    }

}
