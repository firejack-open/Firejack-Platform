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

package net.firejack.platform.web.security.spring;

import net.firejack.platform.core.utils.StringUtils;

public class SpringSecurityAuthenticationRoleType {

    public static SpringSecurityAuthenticationRoleType ROLE_DEFAULT = new SpringSecurityAuthenticationRoleType("ROLE_DEFAULT");
    public static SpringSecurityAuthenticationRoleType ROLE_GUEST = new SpringSecurityAuthenticationRoleType("ROLE_GUEST");
    public static SpringSecurityAuthenticationRoleType ROLE_USER = new SpringSecurityAuthenticationRoleType("ROLE_USER");
    public static SpringSecurityAuthenticationRoleType ROLE_ADMIN = new SpringSecurityAuthenticationRoleType("ROLE_ADMIN");
    public static SpringSecurityAuthenticationRoleType ROLE_INSTALLER = new SpringSecurityAuthenticationRoleType("ROLE_INSTALLER");

    public static final String USER_ROLE_PREFIX = "ROLE_";

    private String name;

    /**
     * @param name
     */
    public SpringSecurityAuthenticationRoleType(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Role name should not be empty.");
        }
        this.name = name;
    }

    /**
     * @return
     */
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpringSecurityAuthenticationRoleType)) return false;

        SpringSecurityAuthenticationRoleType that = (SpringSecurityAuthenticationRoleType) o;

        if (!name.equalsIgnoreCase(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @param roleName
     * @return
     */
    public static String normalize(String roleName) {
        return USER_ROLE_PREFIX + roleName.toUpperCase();
    }
}