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
