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

package net.firejack.platform.web.security.spring.authenticator;

import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;


public class DefaultAuthenticationDetails implements IAuthenticationDetails {

    private List<GrantedAuthority> assignedAuthorities;
    private IAuthenticationSource authenticationSource;
    private IUserInfoProvider details;
    private IPermissionContainer permissionContainer;

    /***/
    public DefaultAuthenticationDetails() {
    }

    /**
     * @param assignedAuthorities
     * @param authenticationSource
     * @param details
     * @param permissionContainer
     */
    public DefaultAuthenticationDetails(List<GrantedAuthority> assignedAuthorities,
                                        IAuthenticationSource authenticationSource,
                                        IUserInfoProvider details,
                                        IPermissionContainer permissionContainer) {
        this.assignedAuthorities = assignedAuthorities;
        this.authenticationSource = authenticationSource;
        this.details = details;
        this.permissionContainer = permissionContainer;
    }

    /**
     * @param assignedAuthorities
     */
    public void setAssignedAuthorities(List<GrantedAuthority> assignedAuthorities) {
        this.assignedAuthorities = assignedAuthorities;
    }

    /**
     * @param authenticationSource
     */
    public void setAuthenticationSource(IAuthenticationSource authenticationSource) {
        this.authenticationSource = authenticationSource;
    }

    /**
     * @param details
     */
    public void setDetails(IUserInfoProvider details) {
        this.details = details;
    }

    @Override
    public List<GrantedAuthority> getAssignedAuthorities() {
        return assignedAuthorities;
    }

    @Override
    public IAuthenticationSource getAuthenticationSource() {
        return authenticationSource;
    }

    @Override
    public IUserInfoProvider getDetails() {
        return details;
    }

    @Override
    public IPermissionContainer getPermissionContainer() {
        return permissionContainer;
    }

}