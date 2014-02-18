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

package net.firejack.platform.web.security.spring.openid;

import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import net.firejack.platform.web.security.spring.authenticator.IAuthenticationSource;

import java.util.Map;


public class OpenIDAuthenticationSource implements IAuthenticationSource {

    private String email;
    private Map<SupportedOpenIDAttribute, String> attributes;

    /**
     * @param email
     * @param attributes
     */
    public OpenIDAuthenticationSource(String email, Map<SupportedOpenIDAttribute, String> attributes) {
        this.email = email;
        this.attributes = attributes;
    }

    @Override
    public String getPrincipal() {
        return email;
    }

    @Override
    public String getCredential() {
        return null;
    }

    /**
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return
     */
    public Map<SupportedOpenIDAttribute, String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes
     */
    public void setAttributes(Map<SupportedOpenIDAttribute, String> attributes) {
        this.attributes = attributes;
    }

}