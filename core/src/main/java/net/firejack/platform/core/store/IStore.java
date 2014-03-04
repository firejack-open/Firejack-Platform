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

package net.firejack.platform.core.store;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.utils.Paging;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;


public interface IStore<E, ID> extends IAbstractStore<E, ID> {

    /**
     * @param id
     * @return
     */
    E load(ID id);

    /**
     * @return
     */
    List<E> findAll();

    /**
     * @param criterions
     * @param filter
     * @return
     */
    int count(List<Criterion> criterions, SpecifiedIdsFilter filter);

    /*int count(List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter);*/

    /**
     * @param idSpecifiedIdsFilter
     * @return
     */
    List<E> findAllWithFilter(SpecifiedIdsFilter idSpecifiedIdsFilter);

    /**
     * @param criterions
     * @param filter
     * @param orders
     * @return
     */
    List<E> findAllWithFilter(List<Criterion> criterions, SpecifiedIdsFilter filter, Order... orders);

    /**
     * @param criterions
     * @param filter
     * @param paging
     * @return
     */
    List<E> findAllWithFilter(List<Criterion> criterions, SpecifiedIdsFilter filter, Paging paging);

    /**
     * @param criterions
     * @param aliases
     * @param filter
     * @param orders
     * @return
     */
    List<E> findAllWithFilter(List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, Order... orders);

    /**
     * @param offset
     * @param limit
     * @param criterions
     * @param filter
     * @param orders
     * @return
     */
    List<E> findAllWithFilter(Integer offset, Integer limit, List<Criterion> criterions, SpecifiedIdsFilter filter, Order... orders);

    /**
     * @param offset
     * @param limit
     * @param criterions
     * @param aliases
     * @param filter
     * @param orders
     * @return
     */
    List<E> findAllWithFilter(Integer offset, Integer limit, List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, Order... orders);

    List<E> findAllWithFilter(List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, Paging paging);

    List<E> findAllWithFilter(List<Criterion> criterions, Map<String, String> aliases, SpecifiedIdsFilter filter, List<String> fetchPaths, Paging paging);

    /**
     * @param queryName
     * @param useScroll
     * @param params
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    int count(String queryName, boolean useScroll, Object... params) throws DataAccessException;

    /**
     * @param offset
     * @param limit
     * @param queryName
     * @param params
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    List<E> find(Integer offset, Integer limit, String queryName, Object... params) throws DataAccessException;

    /**
     * @param queryName
     * @param params
     * @return
     * @throws org.springframework.dao.DataAccessException
     *
     */
    E findSingle(String queryName, Object... params) throws DataAccessException;

    /**
     * @param example
     * @return
     */
    <T> int countByExample(T example);

    /**
     * @param offset
     * @param limit
     * @param example
     * @return
     */
    <T> List<T> findByExampleObject(Integer offset, Integer limit, T example);

    /**
     * @param example
     * @return
     */
    E findByExample(E example);

    /**
     * @param offset
     * @param limit
     * @param example
     * @return
     */
    <T> List<T> findByExample(Integer offset, Integer limit, T example);

    /**
     * @param entities
     */
    void deleteAll(List<E> entities);

    /**
     * @param entity
     */
    void lock(E entity);

    /**
     * @param entity
     */
    void refresh(E entity);

    /**
     * @param entity
     */
    void merge(E entity);

    /**
     * @param queryName
     * @param params
     * @return
     */
    int update(String queryName, Object... params);

	void evict (E model );
}
