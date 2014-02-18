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