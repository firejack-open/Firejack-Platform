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

package net.firejack.platform.service.content.broker.collection;

import net.firejack.platform.api.content.domain.Collection;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.resource.CollectionMemberModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;


public abstract class SaveCollectionBroker
        extends OPFSaveBroker<CollectionModel, Collection, RegistryNodeTree> {

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Override
    protected CollectionModel convertToEntity(Collection collection) {
        return factory.convertFrom(CollectionModel.class, collection);
    }

    @Override
    protected RegistryNodeTree convertToModel(CollectionModel entity) {
        return factory.convertTo(RegistryNodeTree.class, entity);
    }

    @Override
    protected void save(CollectionModel collectionModel) throws Exception {
        List<CollectionMemberModel> collectionMemberModels = collectionModel.getCollectionMembers();
        if (collectionMemberModels != null) {
            int pos = 0;
            for (CollectionMemberModel collectionMemberModel : collectionMemberModels) {
                collectionMemberModel.setCollection(collectionModel);
                collectionMemberModel.setOrder(pos);
                pos++;
            }
        }
        collectionStore.save(collectionModel);
    }

}
