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

package net.firejack.platform.web.security.model.principal;

import net.firejack.platform.core.model.user.IUserInfoProvider;

/**
 *
 */
public class UserPrincipal extends OpenFlamePrincipal {

    /**
     * Info for current logged user
     */
    private IUserInfoProvider userInfoProvider;

    /**
     * UserPrincipal constructor
     * @param userInfoProvider user details
     */
    public UserPrincipal(IUserInfoProvider userInfoProvider) {
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    public IUserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    @Override
    public UserType getType() {
        return UserType.USER;
    }

    /**
     * Grants an action permission specific to an individual entity with a specific
     * identifier. Actions indicate the entity type they pertain to implicitly.
     *
     * @param action String action name (i.e. "user.edit" or "user.create" )
     * @param entityIdentifier String representation of the unique identifier for the entity (i.e. "1245332")
     */
//    public void grantContextSpecificPermission(String action, String entityIdentifier)
//    {
//        String ctxtPermission = generateContextSpecificPermission(action, entityIdentifier);
//    }
    
}