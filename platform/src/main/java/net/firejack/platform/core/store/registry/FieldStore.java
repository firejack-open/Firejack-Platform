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
