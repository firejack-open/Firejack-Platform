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

package net.firejack.platform.api.securitymanager;

import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.securitymanager.domain.MoveSecuredRecordInfo;
import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.TreeNodeSecuredRecord;
import net.firejack.platform.core.domain.IdLookup;
import net.firejack.platform.core.model.CustomPK;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;

import java.util.List;

public class SecurityManagerServiceProxy extends AbstractServiceProxy implements ISecurityManagerService{

	public SecurityManagerServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
    public ServiceResponse<SecuredRecord> createSecuredRecord(Long id, String name, String type, Long parentId, String parentType) {
        if (id == null || StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Required parameters(id, name, type) should not be null.");
        }
        SecuredRecord sr = new SecuredRecord();
        sr.setName(name);
        sr.setExternalNumberId(id);
        sr.setRegistryNodeLookup(type);

        ServiceResponse<SecuredRecord> response;
        try {
            response = post("", sr, "parentType", parentType, "parentId", parentId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse<SecuredRecord>(e.getMessage(), false);
        }
        return response;
    }

	@Override
    public ServiceResponse<SecuredRecord> createSecuredRecord(String id, String name, String type, Long parentId, String parentType) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Required parameters(id, name, type) should not be blank.");
        }
        SecuredRecord sr = new SecuredRecord();
        sr.setName(name);
        sr.setExternalStringId(id);
        sr.setRegistryNodeLookup(type);

        ServiceResponse<SecuredRecord> response;
        try {
            response = post("", sr, "parentType", parentType, "parentId", parentId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse<SecuredRecord>(e.getMessage(), false);
        }
        return response;
    }

	@Override
    public ServiceResponse<SecuredRecord> createSecuredRecord(CustomPK id, String name, String type, Long parentId, String parentType) {
        if (id == null) {
            throw new IllegalArgumentException("Id info should not be empty.");
        } else {
            String stringVal = id.toString();
            return createSecuredRecord(stringVal, name, type, parentId, parentType);
        }
    }

    @Override
    public ServiceResponse createSecuredRecords(List<TreeNodeSecuredRecord> securedRecords) {
        ServiceResponse response;
        try {
            response = post("/many", securedRecords);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse(e.getMessage(), false);
        }
        return response;
    }

    @Override
    public ServiceResponse updateSecuredRecord(Long id, String type, String nameToUpdate) {
        if (id == null || StringUtils.isBlank(type) || StringUtils.isBlank(nameToUpdate)) {
            return new ServiceResponse("Parameters should not be empty", false);
        }
        SecuredRecord sr = new SecuredRecord();
        sr.setExternalNumberId(id);
        sr.setName(nameToUpdate);
        sr.setRegistryNodeLookup(type);

        try {
            return put("", sr);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse removeSecuredRecordPath(long id, String typeLookup) {
        if (StringUtils.isBlank(typeLookup)) {
            return new ServiceResponse("typeLookup parameter should not be blank", false);
        }
        try {
            return delete("", "typeLookup", typeLookup);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse removeSecuredRecord(long id, String type) {
        try {
            return delete("", "entityId", id, "typeLookup", type);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse removeSecuredRecord(String stringId, String type) {
        try {
            return delete("", "entityCustomId", stringId, "typeLookup", type);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse removeSecuredRecords(List<IdLookup> srIdLookupList) {
        try {
            return delete("/many", srIdLookupList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<SecuredRecord> moveSecuredRecord(
            Long id, String lookup, IdLookup parent, IdLookup... oldParents) {
        MoveSecuredRecordInfo data = new MoveSecuredRecordInfo();
        data.setId(id);
        data.setLookup(lookup);
        data.setParent(parent);
        data.setOldParents(oldParents);

        try {
            return post2("/move", data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<SecuredRecord>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<SecuredRecord> getSecuredRecordInfo(long entityId, String typeLookup) {
        try {
            return get("", "entityId", entityId, "typeLookup", typeLookup);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<SecuredRecord>(e.getMessage(), false);
        }
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
    public List<Long> getAllowedIdFilter(String permissionLookup, String entityTypeLookup) {
        List<Long> allowedIdList = new ArrayList<Long>();
        if (StringUtils.isNotBlank(permissionLookup) && StringUtils.isNotBlank(entityTypeLookup)) {
            Long currentSecuredRecordId = OPFContext.getContext().getSecuredRecordId();
            currentSecuredRecordId = currentSecuredRecordId == null ?
                    OpenFlameSecurityConstants.EMPTY_SECURED_RECORD_ID : currentSecuredRecordId;
            ServiceResponse<UserPermission> permissionsResponse =
                    OPFEngine.AuthorityService.loadPermissionsBySecuredRecord(currentSecuredRecordId);
            if (permissionsResponse.isSuccess()) {
                List<UserPermission> srContextPermissions = permissionsResponse.getData();
                if (srContextPermissions != null) {
                    for (UserPermission assignedUserPermission : srContextPermissions) {
                        if (permissionLookup.equalsIgnoreCase(assignedUserPermission.getPermission()) &&
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
        try {
            return get("/all-nodes");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<SecuredRecordNode>(e.getMessage(), false);
        }
    }

    @Override
    public String getServiceUrlSuffix() {
        return "/authority/secured-record";
    }
}