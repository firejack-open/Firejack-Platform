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

package net.firejack.platform.core.store.lookup;

import net.firejack.platform.core.model.ITreeStore;
import net.firejack.platform.core.model.SearchModel;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.utils.Paging;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface ILookupStore<E extends LookupModel, ID extends Serializable> extends ITreeStore<E, ID> {

    /**
     * @param registryNodeId
     * @param filter
     * @return
     */
    List<E> findAllByParentIdWithFilter(Long registryNodeId, SpecifiedIdsFilter<Long> filter);

    List<E> findAllByParentIdWithFilter(Long registryNodeId, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param id
     * @param filter
     * @param version
     * @return
     */
    List<E> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter, Integer version);

    /**
     * @param id
     * @param filter
     * @param isSortable
     * @return
     */
    List<E> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter, Boolean isSortable);

    /**
     * @param id
     * @param filter
     * @param version
     * @param isSortable
     * @return
     */
    public List<E> findAllByParentIdWithFilter(Long id, SpecifiedIdsFilter<Long> filter, Integer version, Boolean isSortable);

    /**
     * @param lookup
     * @return
     */
    E findByLookup( String lookup);

    /**
     * @param lookup
     * @param withParent
     * @return
     */
    E findByLookup( String lookup,  boolean withParent);

    /**
     * @param lookupList
     * @return
     */
    List<E> findAllByLookupList(Collection<String> lookupList);

    /**
     * @param lookupPrefix
     * @return
     */
    List<E> findAllByLikeLookupPrefix( String lookupPrefix);

    /**
     *
     * @param lookup
     * @param filter
     * @return
     */
    List findPlaceHolderEntity(String lookup, String filter);

	/**
	 *
	 *
	 * @param term
	 * @param prefix
	 *@param type
	 * @param paging  @return
	 */
	List<SearchModel> search(String term, String prefix, RegistryNodeType type, Paging paging);

	/**
	 *
	 *
	 * @param term
	 * @param prefix
	 * @param type
	 * @return
	 */
	Integer searchCount(String term, String prefix, RegistryNodeType type);
}
