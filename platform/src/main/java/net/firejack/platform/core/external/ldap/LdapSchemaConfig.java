/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.external.ldap;

import net.firejack.platform.core.domain.AbstractDTO;


public class LdapSchemaConfig extends AbstractDTO {

    private Boolean allowEncodedPassword;
    private String passwordEncodingType;
    private String peopleBaseDN;
    private String peopleObjectclass;
    private String uidAttributeName;
    private String groupsBaseDN;
    private String groupsObjectclass;
    private String memberAttributeName;
    private String emailAttributeName;
    private String rdnAttributeName;
    private String passwordAttribute;

    public Boolean getAllowEncodedPassword() {
        return allowEncodedPassword;
    }

    public void setAllowEncodedPassword(Boolean allowEncodedPassword) {
        this.allowEncodedPassword = allowEncodedPassword;
    }

    public String getPasswordEncodingType() {
        return passwordEncodingType;
    }

    public void setPasswordEncodingType(String passwordEncodingType) {
        this.passwordEncodingType = passwordEncodingType;
    }

    public String getPeopleBaseDN() {
        return peopleBaseDN;
    }

    public void setPeopleBaseDN(String peopleBaseDN) {
        this.peopleBaseDN = peopleBaseDN;
    }

    public String getPeopleObjectclass() {
        return peopleObjectclass;
    }

    public void setPeopleObjectclass(String peopleObjectclass) {
        this.peopleObjectclass = peopleObjectclass;
    }

    public String getUidAttributeName() {
        return uidAttributeName;
    }

    public void setUidAttributeName(String uidAttributeName) {
        this.uidAttributeName = uidAttributeName;
    }

    public String getGroupsBaseDN() {
        return groupsBaseDN;
    }

    public void setGroupsBaseDN(String groupsBaseDN) {
        this.groupsBaseDN = groupsBaseDN;
    }

    public String getGroupsObjectclass() {
        return groupsObjectclass;
    }

    public void setGroupsObjectclass(String groupsObjectclass) {
        this.groupsObjectclass = groupsObjectclass;
    }

    public String getMemberAttributeName() {
        return memberAttributeName;
    }

    public void setMemberAttributeName(String memberAttributeName) {
        this.memberAttributeName = memberAttributeName;
    }

    public String getEmailAttributeName() {
        return emailAttributeName;
    }

    public void setEmailAttributeName(String emailAttributeName) {
        this.emailAttributeName = emailAttributeName;
    }

    public String getRdnAttributeName() {
        return rdnAttributeName;
    }

    public void setRdnAttributeName(String rdnAttributeName) {
        this.rdnAttributeName = rdnAttributeName;
    }

    public String getPasswordAttribute() {
        return passwordAttribute;
    }

    public void setPasswordAttribute(String passwordAttribute) {
        this.passwordAttribute = passwordAttribute;
    }
}