/**
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
package net.firejack.platform.core.store.registry;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNodePath;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.utils.StringUtils;

import java.util.*;

public class AuthorizationVOFactory {

	public static User convert(BaseUserModel user) {
	    User userInfo = new User();
	    userInfo.setId(user.getId());
	    userInfo.setUsername(user.getUsername());
        if (user instanceof IUserInfoProvider) {
            userInfo.setPassword(((IUserInfoProvider) user).getPassword());
        }
	    return userInfo;
	}

	public static SecuredRecordNode convert(SecuredRecordModel securedRecord) {
	    if (securedRecord == null) {
	        return null;
	    }
	    SecuredRecordNode recInfo = new SecuredRecordNode();
	    recInfo.setSecuredRecordId(securedRecord.getId());
	    recInfo.setInternalId(securedRecord.getExternalNumberId());
	    recInfo.setType(securedRecord.getRegistryNode().getLookup());

	    String sRecPaths = securedRecord.getPaths();
	    if (StringUtils.isNotBlank(sRecPaths) && sRecPaths.length() > 3) {
	        //strip paths from []
	        sRecPaths = sRecPaths.substring(1, sRecPaths.length() - 1);
	        List<SecuredRecordNodePath> srPaths = new ArrayList<SecuredRecordNodePath>();
	        String[] paths = sRecPaths.split(",");
	        for (String path : paths) {
	            if (StringUtils.isNotBlank(path)) {
	                srPaths.add(SecuredRecordNodePath.parse(path));
	            }
	        }
	        recInfo.setNodePaths(srPaths.toArray(new SecuredRecordNodePath[srPaths.size()]));
	    } else {
	        recInfo.setNodePaths(new SecuredRecordNodePath[0]);
	    }

	    return recInfo;
	}

	public static Map<Long, SecuredRecordNode> convertSecuredRecords(List<SecuredRecordModel> securedRecords) {
	    List<SecuredRecordNode> recInfoList = new ArrayList<SecuredRecordNode>();
	    for (SecuredRecordModel securedRecord : securedRecords) {
	        recInfoList.add(convert(securedRecord));
	    }
	    Map<Long, SecuredRecordNode> srMap = new HashMap<Long, SecuredRecordNode>();
	    for (SecuredRecordNode srInfo : recInfoList) {
	        srMap.put(srInfo.getSecuredRecordId(), srInfo);
	    }
	    return srMap;
	}

	public static UserPermission convert(PermissionModel permission) {
	    return new UserPermission(permission.getLookup());
	}

	public static List<UserPermission> convertPermissions(List<PermissionModel> permissions) {
	    List<UserPermission> userPermissions = new LinkedList<UserPermission>();
	    for (PermissionModel permission : permissions) {
	        userPermissions.add(convert(permission));
	    }
  	    return userPermissions;
	}

	public static List<UserPermission> convertPermissionsEx(List<String> permissions) {
	    List<UserPermission> userPermissions = new LinkedList<UserPermission>();
	    for (String permission : permissions) {
	        userPermissions.add(new UserPermission(permission));
	    }
	    return userPermissions;
	}

	public static <T extends LookupModel<?>> List<String> retrieveLookupList(List<T> items) {
	    List<String> result;
	    if (items == null) {
	        result = null;
	    } else {
	        result = new ArrayList<String>();
	        for (T item : items) {
	            result.add(item.getLookup());
	        }
	    }
	    return result;
	}

}
