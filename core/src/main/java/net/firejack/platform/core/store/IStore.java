/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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
