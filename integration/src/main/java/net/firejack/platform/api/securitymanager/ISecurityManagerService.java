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