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

import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.IndexEntityReferenceModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.store.lookup.LookupStore;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component("indexStore")
public class IndexStore extends LookupStore<IndexModel, Long> implements IIndexStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(IndexModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndexModel> findIndexesByEntityId(Long entityId) {
        List<IndexModel> indexModels = find(null, null, "IndexStore.findIndexesByEntityId",
                "entityId", entityId);
        for (IndexModel indexModel : indexModels) {
            Hibernate.initialize(indexModel.getFields());
            Hibernate.initialize(indexModel.getReferences());
        }
        return indexModels;

    }

    @Override
    @Transactional
    public void deleteAllByEntityId(Long entityId) {
        List<IndexModel> indexes = findIndexesByEntityId(entityId);
        for (IndexModel index : indexes) {
            delete(index);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public IndexModel findIndexByRelationship(Long relationshipId) {
        return findSingle("IndexStore.findIndexByRelationshipId", "relationshipId", relationshipId);
    }

    @Override
    @Transactional
    public void createOrUpdateIndex(RelationshipModel relationshipModel) {
        if (!RelationshipType.ASSOCIATION.equals(relationshipModel.getRelationshipType()) &&
                !RelationshipType.WEIGHTED_ASSOCIATION.equals(relationshipModel.getRelationshipType())) {
            IndexModel indexModel = findIndexByRelationship(relationshipModel.getId());
            if (indexModel == null) {
                indexModel = new IndexModel();
                indexModel.setName(relationshipModel.getSourceConstraintName());
                indexModel.setIndexType(IndexType.INDEX);
                indexModel.setParent(relationshipModel.getSourceEntity());
                indexModel.setRelationship(relationshipModel);
            }

            IndexEntityReferenceModel referenceModel;
            List<IndexEntityReferenceModel> referenceModels = indexModel.getReferences();
            if (referenceModels != null && !referenceModels.isEmpty()) {
                referenceModel = referenceModels.get(0);
            } else {
                referenceModel = new IndexEntityReferenceModel();
                if (referenceModels == null) {
                    referenceModels = new ArrayList<IndexEntityReferenceModel>();
                    indexModel.setReferences(referenceModels);
                }
                referenceModels.add(referenceModel);
            }

            referenceModel.setColumnName(relationshipModel.getTargetEntityRefName());
            referenceModel.setEntityModel(relationshipModel.getTargetEntity());
            referenceModel.setIndex(indexModel);

            save(indexModel);
        }
    }

}
