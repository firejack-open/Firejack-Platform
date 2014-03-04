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
