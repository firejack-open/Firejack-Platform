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