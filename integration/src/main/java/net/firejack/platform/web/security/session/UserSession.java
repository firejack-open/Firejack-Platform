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

package net.firejack.platform.web.security.session;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.user.IUserInfoProvider;

import java.util.List;

/**
 *
 */
public class UserSession extends AbstractDTO {
    private static final long serialVersionUID = -9092154535114978622L;

    private String token;
    private IUserInfoProvider user;
    private long expirationTime;
    private List<Long> roleIds;

    /**
     * @param token
     * @param user
     * @param roleIds
     * @param expirationTime
     */
    public UserSession(String token, IUserInfoProvider user, List<Long> roleIds, long expirationTime) {
        this.token = token;
        this.user = user;
        this.roleIds = roleIds;
        this.expirationTime = expirationTime;
    }

    /**
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * @return
     */
    public IUserInfoProvider getUser() {
        return user;
    }

    /**
     * @return
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param expirationTime
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * @param roleIds
     */
    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * @return
     */
    public List<Long> getRoleIds() {
        return roleIds;
    }
}