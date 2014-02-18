/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.api.securitymanager;


import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.TreeNodeSecuredRecord;
import net.firejack.platform.core.domain.IdLookup;
import net.firejack.platform.core.model.CustomPK;
import net.firejack.platform.core.response.ServiceResponse;

import java.util.List;


public interface ISecurityManagerService {

    /**
     * Creates or updates a secured record with the appropriate parent and path. If no
     * secured record exists for the given id and type, one will be created. Otherwise,
     * the existing record will be updated with the existing path.
     *
     * @param id         long the id of the actual entity being secured
     * @param name       String the name of the actual entity being secured
     * @param type       String lookup of the registry node representing the entity type
     * @param parentId   long the id of the actual parent entity
     * @param parentType String lookup of the registry node representing the parent entity type
     * @return StatusResponse status response
     */
    ServiceResponse<SecuredRecord> createSecuredRecord(Long id, String name, String type, Long parentId, String parentType);

    /**
     * Creates or updates a secured record with the appropriate parent and path. If no
     * secured record exists for the given id and type, one will be created. Otherwise,
     * the existing record will be updated with the existing path.
     *
     * @param id         String the id of the actual entity being secured
     * @param name       String the name of the actual entity being secured
     * @param type       String lookup of the registry node representing the entity type
     * @param parentId   long the id of the actual parent entity
     * @param parentType String lookup of the registry node representing the parent entity type
     * @return StatusResponse status response
     */
    ServiceResponse<SecuredRecord> createSecuredRecord(String id, String name, String type, Long parentId, String parentType);

    /**
     * Creates or updates a secured record with the appropriate parent and path. If no
     * secured record exists for the given id and type, one will be created. Otherwise,
     * the existing record will be updated with the existing path.
     *
     * @param id         CustomPK the id of the actual entity being secured
     * @param name       String the name of the actual entity being secured
     * @param type       String lookup of the registry node representing the entity type
     * @param parentId   long the id of the actual parent entity
     * @param parentType String lookup of the registry node representing the parent entity type
     * @return StatusResponse status response
     */
    ServiceResponse<SecuredRecord> createSecuredRecord(CustomPK id, String name, String type, Long parentId, String parentType);

    /**
     * Create bunch of secured records
     * @param securedRecords secured records to create
     * @return service response that returns success or failure status along with appropriate message
     */
    ServiceResponse createSecuredRecords(List<TreeNodeSecuredRecord> securedRecords);

    /**
     * @param id entity id
     * @param type entity type lookup
     * @param nameToUpdate name to use in update operation
     * @return StatusResponse status response
     */
    ServiceResponse updateSecuredRecord(Long id, String type, String nameToUpdate);

//    /**
//     * Removes a path and parent from an existing secured record by finding the appropriate
//     * parent.
//     *
//     * @param id long the id of the actual entity being secured
//     * @param type String lookup of the registry node representing the entity type
//     */

    /**
     * @param id entity id
     * @param type lookup of entity type
     * @return StatusResponse status response
     */
    ServiceResponse removeSecuredRecordPath(long id, String type);

//    /**
//     * Removes a secured record entirely an either releases the entity in question from the
//     * hierarchy or allows it to be deleted from the system.
//     *
//     * @param id long the id of the actual entity being secured
//     * @param type String lookup of the registry node representing the entity type
//     */

    /**
     * @param id entity id
     * @param type entity type lookup
     * @return StatusResponse status response
     */
    ServiceResponse removeSecuredRecord(long id, String type);

    /**
     * @param stringId entity id
     * @param type entity type lookup
     * @return StatusResponse status response
     */
    ServiceResponse removeSecuredRecord(String stringId, String type);

    /**
     * @param srIdLookupList list of secured record IdLookup information.
     * Each IdLookup contains information about secured record's entity id and entity lookup
     *
     * @return StatusResponse status response
     */
    ServiceResponse removeSecuredRecords(List<IdLookup> srIdLookupList);

    /**
     * @param id entity id
     * @param lookup entity type lookup
     * @param parent information about parent secured record
     * @param oldParents information about old secured record parents
     * @return response containing updates secured record
     */
    ServiceResponse<SecuredRecord> moveSecuredRecord(Long id, String lookup, IdLookup parent, IdLookup... oldParents);

    /**
     * Returns all the security information from the secured record hierarchy for a given
     * entity based on its id and type (registry node lookup).
     *
     * @param id   long the id of the actual entity being secured
     * @param type String lookup of the registry node representing the entity type
     * @return SecuredRecord all relevant paths and parents required to do security checks
     */
    ServiceResponse<SecuredRecord> getSecuredRecordInfo(long id, String type);

    /*boolean checkPermission(String permissionLookup);

    boolean checkPermission(String permissionLookup, String entityType, Long entityId);

    List<Long> getAllowedIdFilter(String permissionLookup, String entityTypeLookup);

    List<Long> getAllowedIdList(String permissionName, String entityTypeLookup);*/


    ServiceResponse<SecuredRecordNode> loadAllSecureRecordNodes();

}