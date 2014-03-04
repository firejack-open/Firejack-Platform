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

package net.firejack.platform.service.securitymanager;


import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.securitymanager.ISecurityManagerService;
import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.TreeNodeSecuredRecord;
import net.firejack.platform.core.domain.IdLookup;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.CustomPK;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.securitymanager.broker.*;
import net.firejack.platform.web.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_SECURITY_MANAGER_SERVICE)
public class SecurityManagerServiceLocal implements ISecurityManagerService {

    @Autowired
    @Qualifier("saveSecuredRecordBroker")
    private SaveSecuredRecordBroker saveSecuredRecordBroker;
    @Autowired
    private SaveSecuredRecordsBroker saveSecuredRecordsBroker;
    @Autowired
    @Qualifier("updateSecuredRecordBroker")
    private UpdateSecuredRecordBroker updateSecuredRecordBroker;
    @Autowired
    @Qualifier("deleteSecuredRecordBroker")
    private DeleteSecuredRecordBroker deleteSecuredRecordBroker;
    @Autowired
    private DeleteSecuredRecordsBroker deleteSecuredRecordsBroker;
    @Autowired
    @Qualifier("moveSecuredRecordBroker")
    private MoveSecuredRecordBroker moveSecuredRecordBroker;
    @Autowired
    @Qualifier("readSecuredRecordBrokerEx")
    private ReadSecuredRecordBroker readSecuredRecordBroker;

    @Override
    public ServiceResponse<SecuredRecord> createSecuredRecord(Long id, String name, String type, Long parentId, String parentType) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_ID, id);
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_TYPE, type);
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_NAME, name);
        params.put(SaveSecuredRecordBroker.PARAM_PARENT_ID, parentId);
        params.put(SaveSecuredRecordBroker.PARAM_PARENT_TYPE, parentType);
        return saveSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse<SecuredRecord> createSecuredRecord(String id, String name, String type, Long parentId, String parentType) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_STRING_ID, id);
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_TYPE, type);
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_NAME, name);
        params.put(SaveSecuredRecordBroker.PARAM_PARENT_ID, parentId);
        params.put(SaveSecuredRecordBroker.PARAM_PARENT_TYPE, parentType);
        return saveSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse<SecuredRecord> createSecuredRecord(CustomPK id, String name, String type, Long parentId, String parentType) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_STRING_ID, id == null ? null : id.toString());
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_TYPE, type);
        params.put(SaveSecuredRecordBroker.PARAM_ENTITY_NAME, name);
        params.put(SaveSecuredRecordBroker.PARAM_PARENT_ID, parentId);
        params.put(SaveSecuredRecordBroker.PARAM_PARENT_TYPE, parentType);
        return saveSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse createSecuredRecords(List<TreeNodeSecuredRecord> securedRecords) {
        return saveSecuredRecordsBroker.execute(new ServiceRequest<TreeNodeSecuredRecord>(securedRecords));
    }

    @Override
    public ServiceResponse updateSecuredRecord(Long id, String type, String nameToUpdate) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(UpdateSecuredRecordBroker.PARAM_ENTITY_ID, id);
        params.put(UpdateSecuredRecordBroker.PARAM_ENTITY_TYPE, type);
        params.put(UpdateSecuredRecordBroker.PARAM_ENTITY_NAME, nameToUpdate);

        return updateSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse removeSecuredRecordPath(long id, String type) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(DeleteSecuredRecordBroker.PARAM_ENTITY_ID, id);
        params.put(DeleteSecuredRecordBroker.PARAM_ENTITY_TYPE, type);
        params.put(DeleteSecuredRecordBroker.PARAM_FORCE_DELETE, true);

        return deleteSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse removeSecuredRecord(long id, String type) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(DeleteSecuredRecordBroker.PARAM_ENTITY_ID, id);
        params.put(DeleteSecuredRecordBroker.PARAM_ENTITY_TYPE, type);
        params.put(DeleteSecuredRecordBroker.PARAM_FORCE_DELETE, true);

        return deleteSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse removeSecuredRecord(String stringId, String type) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(DeleteSecuredRecordBroker.PARAM_ENTITY_CUSTOM_ID, stringId);
        params.put(DeleteSecuredRecordBroker.PARAM_ENTITY_TYPE, type);
        params.put(DeleteSecuredRecordBroker.PARAM_FORCE_DELETE, true);

        return deleteSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse removeSecuredRecords(List<IdLookup> srIdLookupList) {
        return deleteSecuredRecordsBroker.execute(new ServiceRequest<IdLookup>(srIdLookupList));
    }

    @Override
    public ServiceResponse<SecuredRecord> moveSecuredRecord(
            Long id, String lookup, IdLookup parent, IdLookup... oldParents) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(MoveSecuredRecordBroker.PARAM_ENTITY_ID, id);
        params.put(MoveSecuredRecordBroker.PARAM_ENTITY_LOOKUP, lookup);
        params.put(MoveSecuredRecordBroker.PARAM_PARENT_ID, parent.getId());
        params.put(MoveSecuredRecordBroker.PARAM_PARENT_LOOKUP, parent.getLookup());

        return moveSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse<SecuredRecord> getSecuredRecordInfo(long id, String type) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ReadSecuredRecordBroker.PARAM_ENTITY_ID, id);
        params.put(ReadSecuredRecordBroker.PARAM_ENTITY_TYPE, type);

        return readSecuredRecordBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    /*@Override
    public boolean checkPermission(String name) {
        return checkPermission(name, null, null);
    }

    @Override
    public boolean checkPermission(String name, String entityType, Long entityId) {
        return ContextManager.checkUserPermission(
                new UserPermission(name, entityType, entityId == null ? null : String.valueOf(entityId)));
    }

    @Override
    public List<Long> getAllowedIdFilter(String permissionName, String entityTypeLookup) {
        List<Long> allowedIdList = new ArrayList<Long>();
        if (StringUtils.isNotBlank(permissionName) && StringUtils.isNotBlank(entityTypeLookup)) {
            Long currentSecuredRecordId = OPFContext.getContext().getSecuredRecordId();
            currentSecuredRecordId = currentSecuredRecordId == null ?
                    OpenFlameSecurityConstants.EMPTY_SECURED_RECORD_ID : currentSecuredRecordId;
            ServiceResponse<UserPermission> permissionsResponse =
                    OPFEngine.AuthorityService.loadPermissionsBySecuredRecord(currentSecuredRecordId);
            if (permissionsResponse.isSuccess()) {
                List<UserPermission> srContextPermissions = permissionsResponse.getData();
                if (srContextPermissions != null) {
                    for (UserPermission assignedUserPermission : srContextPermissions) {
                        if (permissionName.equalsIgnoreCase(assignedUserPermission.getPermission()) &&
                                entityTypeLookup.equalsIgnoreCase(assignedUserPermission.getEntityType()) &&
                                assignedUserPermission.getEntityId() != null) {
                            allowedIdList.add(Long.valueOf(assignedUserPermission.getEntityId()));
                        }
                    }
                }
            }
        }
        return allowedIdList;
    }

    @Override
    public List<Long> getAllowedIdList(String permissionName, String entityTypeLookup) {
        return ContextManager.getAllowedIdNumericList(permissionName, entityTypeLookup);
    }*/

    @Override
    public ServiceResponse<SecuredRecordNode> loadAllSecureRecordNodes() {
        Map<Long, SecuredRecordNode> securedRecordNodes =
                CacheManager.getInstance().getSecuredRecords();
        List<SecuredRecordNode> nodeList = securedRecordNodes == null ?
                new ArrayList<SecuredRecordNode>(0) :
                new ArrayList<SecuredRecordNode>(securedRecordNodes.values());
        return new ServiceResponse<SecuredRecordNode>(nodeList, null, true);
    }

}