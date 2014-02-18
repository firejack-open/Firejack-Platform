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

package net.firejack.platform.web.security.spring.token;

import net.firejack.platform.web.security.permission.IPermissionContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpSession;
import java.util.Collection;


public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = -8074055316495929620L;

    private HttpSession session;
    private IPermissionContainer permissionContainer;

    /**
     * @param principal
     * @param credentials
     */
    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    /**
     * @param principal
     * @param credentials
     * @param authorities
     * @param permissionContainer
     */
    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, IPermissionContainer permissionContainer) {
        super(principal, credentials, authorities);
        this.permissionContainer = permissionContainer;
    }


    /**
     * @return
     */
    public HttpSession getSession() {
        return session;
    }

    /**
     * @param session
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }

    /**
     * @return
     */
    public IPermissionContainer getPermissionHolder() {
        return permissionContainer;
    }

    /**
     * @param permissionContainer
     */
    public void setPermissionHolder(IPermissionContainer permissionContainer) {
        this.permissionContainer = permissionContainer;
    }

}
