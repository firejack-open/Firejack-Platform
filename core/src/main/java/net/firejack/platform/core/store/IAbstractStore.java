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
import net.firejack.platform.core.utils.SearchQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IAbstractStore<E,ID> extends  HibernateSupport {

	E instantiate();
    Class<E> getClazz();

	void saveOrUpdate(E entity);
	void saveOrUpdateAll(List<E> entities);

	void update(E entity);

	List<E> search(LinkedList<Criterion> criterionList, Paging paging);
	List<E> search(LinkedList<Criterion> criterionList, Map<String, String> aliases, Paging paging);
    List<E> search(LinkedList<Criterion> criterionList, Map<String, String> aliases, Paging paging, boolean isOr);

	<T> List<T> searchWithProjection(LinkedList<Criterion> criterionList, Projection projection, Paging paging);
    <T> List<T> searchWithProjection(
            LinkedList<Criterion> criterionList, Projection projection, Map<String, String> aliases, Paging paging);
    <T> List<T> searchWithProjection(
            LinkedList<Criterion> criterionList, Projection projection, Map<String, String> aliases, Paging paging, boolean isOr);
    <T> List<T> searchWithProjection(
            LinkedList<Criterion> criterionList, Projection projection, Map<String, String> aliases, Paging paging, boolean isOr, boolean isLeft);

	<T> T searchUniqueWithProjection(LinkedList<Criterion> criterionList, Projection projection, Paging paging);
    <T> T searchUniqueWithProjection(
            LinkedList<Criterion> criterionList, Projection projection, Map<String, String> aliases, Paging paging);
    <T> T searchUniqueWithProjection(
            LinkedList<Criterion> criterionList, Projection projection, Map<String, String> aliases, Paging paging, boolean isOr);

	Integer searchCount(LinkedList<Criterion> criterionList);
	Integer searchCount(LinkedList<Criterion> criterionList, Map<String, String> aliases);
    Integer searchCount(LinkedList<Criterion> criterionList, Map<String, String> aliases, boolean isOr);

    int count(final List<Criterion> criterions, final Map<String, String> aliases, final SpecifiedIdsFilter filter);
    <ES> List<ES> findWithFilter(List<Criterion> criterionList, Map<String, String> aliases, SpecifiedIdsFilter filter, Paging paging);
    <ES> List<ES> findWithFilter(
            List<Criterion> criterions, Map<String, String> aliases, Projection projection,
            List<String> fetchPaths, Class<ES> projectionClazz, SpecifiedIdsFilter filter, Paging paging);

	E findById(ID id);

    void deleteById(ID id);
    void delete(E entity);

    List<E> simpleSearch(String term, List<String> fields, Paging paging, SpecifiedIdsFilter filter);
    Integer simpleSearchCount(String term, List<String> fields, SpecifiedIdsFilter filter);

    List<E> advancedSearch(List<List<SearchQuery>> searchQueries, Paging paging);
    Integer advancedSearchCount(List<List<SearchQuery>> searchQueries);

    List<E> advancedSearchWithIdsFilter(List<List<SearchQuery>> searchQueries, Paging paging, SpecifiedIdsFilter filter);
    Integer advancedSearchCountWithIdsFilter(List<List<SearchQuery>> searchQueries, SpecifiedIdsFilter filter);

    List<E> advancedSearchWithProjection(List<List<SearchQuery>> searchQueries, Projection projection, Map<String, String> aliases, Paging paging);

	<I extends Serializable> Map<I, ID> searchKeys(String key, Collection<I> values);
}