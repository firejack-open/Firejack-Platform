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

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.utils.Tuple;

import java.util.List;

public interface ISecuredRecordStore extends IStore<SecuredRecordModel, Long> {

    /**
     * @param externalNumberId
     * @param registryNode
     * @return
     */
    SecuredRecordModel findByExternalIdAndRegistryNode(Long externalNumberId, RegistryNodeModel registryNode);

    SecuredRecordModel findByExternalStringIdAndRegistryNode(String externalStringId, RegistryNodeModel registryNode);

    /**
     * @param parentId
     * @return
     */
    List<SecuredRecordModel> findChildrenByParentId(Long parentId);

    /**
     * @param securedRecord
     */
    void saveOrUpdateRecursive(SecuredRecordModel securedRecord);

    /**
     * Save list of secured records
     * @param securedRecordList list of secured record list
     */
    void saveOrUpdateRecursive(List<SecuredRecordModel> securedRecordList);

    /**
     * @return
     */
    List<SecuredRecordModel> findAllWithLoadedRegistryNode();

    void delete(SecuredRecordModel securedRecord);

    List<SecuredRecordModel> deleteAllByIdAndType(List<Tuple<Long, String>> idTypeData);

    SecuredRecordModel findByIdAndType(final Long id, final String typeLookup);

}
