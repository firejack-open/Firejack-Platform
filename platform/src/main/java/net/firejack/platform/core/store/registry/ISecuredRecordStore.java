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
