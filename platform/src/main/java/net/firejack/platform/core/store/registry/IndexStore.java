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
