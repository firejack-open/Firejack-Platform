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

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.utils.Paging;

import java.util.List;


public interface IRegistryNodeStore<R extends LookupModel> extends ILookupStore<R, Long> {

    /**
     * Method retrieves EntityModel inherited from RegistryNodeModel by id with initialized parent
     *
     * @param id of entity
     * @return entity by id with parent
     */
    R findByIdWithParent(Long id);

    /**
     * @param ids
     * @param filter
     * @return
     */
    List<R> findByIdsWithFilter(List<Long> ids, SpecifiedIdsFilter<Long> filter);

    /**
     * @param ids
     * @param filter
     * @return
     */
    Integer findCountByParentIdsWithFilter(List<Long> ids, SpecifiedIdsFilter<Long> filter);

    /**
     * @param registryNodeIds
     * @param filter
     * @param paging
     * @return
     */
    List<R> findAllByParentIdsWithFilter(List<Long> registryNodeIds, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param lookup
     * @param filter
     * @return
     */
    Integer findCountByLikeLookupWithFilter(String lookup, SpecifiedIdsFilter<Long> filter);

    /**
     * @param lookup
     * @param filter
     * @param paging
     * @return
     */
    List<R> findAllByLikeLookupWithFilter(String lookup, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * Find all RegistryNode parents for child element
     * @param lookup lookup of child entity
     * @return
     */
    List<R> findAllParentsForEntityLookup(String lookup);

	<T extends RegistryNodeModel> List<T> findAllParentsForLookup(String lookup, Class<?>... classes);

	<T extends RegistryNodeModel> T findAllParentsById(Long id);

	<T extends LookupModel> void findAllParents(T entity);

	List<String> findAllDuplicateNamesByType(String _package, Class<?>... _class);
	List<R> findAllDuplicateEntityByType(String _package, String name,Class<?>... _class);

    /**
     * @param registryNodeId
     * @param filter
     * @return
     */
    List<R> findChildrenByParentId(Long registryNodeId, SpecifiedIdsFilter<Long> filter);

    /**
     * @param registryNodeId
     * @param registryNodeClasses
     * @param filter
     * @return
     */
    List<R> findChildrenByParentIdAndTypes(Long registryNodeId, SpecifiedIdsFilter<Long> filter, Class<?>... registryNodeClasses);


    /**
     * @param lookup
     * @param registryNodeClasses
     * @param filter
     * @return
     */
    List<R> findAllByPrefixLookupAndTypes(String lookup, SpecifiedIdsFilter<Long> filter, Class<?>... registryNodeClasses);

	/**
	 *
	 * @param registryNodeId
	 * @param discriminatorValues
	 * @param filter
	 * @return
	 */
	List<R> findChildrenByParentIdAndTypes(Long registryNodeId, List<String> discriminatorValues, SpecifiedIdsFilter<Long> filter);

    /**
     * @param lookup
     * @param filter
     * @return
     */
    List<R> findAllByLikeLookupWithFilter(String lookup, SpecifiedIdsFilter<Long> filter);

    List<R> findAliasesById(final Long mainId, final SpecifiedIdsFilter<Long> filter);

    R findByParentIdAndMainId(final Long parentId, final Long mainId);

    List<R> findAllByMainId(final Long mainId);

    void saveForGenerator(R registryNode);

    /**
     * @param registryNodeId
     * @return
     */
    String findRegistryNodeRefPath(Long registryNodeId);

    /**
     * @param registryNodeId
     * @return
     */
    String findRegistryNodeRef(Long registryNodeId);

    void save(R registryNode);

    /**
     * @param registryNode
     * @param isCreateAutoDescription
     */
    void save(R registryNode, boolean isCreateAutoDescription);

    /**
     * @param parentId
     * @return
     */
    Integer findMaxOrderPosition(final Long parentId);

    /**
     * @param registryNode
     */
    void saveWithoutUpdateChildren(R registryNode);

    /**
     * @param registryNodeId
     */
    void deleteRecursiveById(Long registryNodeId);

    /**
     * @param registryNode
     */
    void deleteRecursively(R registryNode);

    void delete(R registryNode);

    /**
     * @param registryNodeId
     * @param newRegistryNodeParentId
     * @param oldRegistryNodeParentId
     * @param position
     */
    void movePosition(Long registryNodeId, Long newRegistryNodeParentId, Long oldRegistryNodeParentId, Integer position);

    Integer findExternalCountByLookup(String lookup);

	List<R> findAllByName(String _package, String name);
}
