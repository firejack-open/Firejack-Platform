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

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
