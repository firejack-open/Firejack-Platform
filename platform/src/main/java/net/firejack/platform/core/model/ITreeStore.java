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

package net.firejack.platform.core.model;

import net.firejack.platform.core.store.version.IUIDStore;

import java.io.Serializable;
import java.util.List;


public interface ITreeStore<O extends TreeEntityModel, ID extends Serializable> extends IUIDStore<O, ID> {

    /**
     * @param parent
     * @param offset
     * @param limit
     * @return
     */
    List<O> findEntriesByParentId(TreeEntityModel parent, Integer offset, Integer limit);

    /**
     * @param offset
     * @param limit
     * @return
     */
    List<O> findAllEntries(Integer offset, Integer limit);

    /**
     * @return
     */
    List<Object[]> findAllIdAndParentId();

    /**
     * @param id
     * @param newParent
     * @param oldParent
     */
    void changeParent(ID id, O newParent, O oldParent);

    /**
     * @param o
     */
    void save(O o);

    /**
     * @param parentId
     * @return
     */
    Integer countChildByParentId(Long parentId);

    /**
     * @param collectionIds
     * @param id
     * @param collectionArrayIds
     */
    void findCollectionParentIds(List<Long> collectionIds, Long id, List<Object[]> collectionArrayIds);

    /**
     * @param collectionIds
     * @param id
     * @param collectionArrayIds
     */
    void findCollectionChildrenIds(List<Long> collectionIds, Long id, List<Object[]> collectionArrayIds);

}
