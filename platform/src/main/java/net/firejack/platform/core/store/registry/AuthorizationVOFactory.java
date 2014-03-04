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
