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

package net.firejack.platform.core.model.user;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("USR")
public class UserModel extends BaseUserModel implements IUserInfoProvider {
    private static final long serialVersionUID = 4405327240937266432L;

    private String password;
    private String firstName;
    private String middleName;
    private String lastName;

    private Long facebookId;
    private Long twitterId;
    private String linkedInId;
    private String ldapUserDN;

    private String resetPasswordToken;

    public UserModel() {
    }

    public UserModel(Long id) {
        super(id);
    }

    @Column(length = 32)
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

     /**
     * @return
     */
    @Column(length = 255)
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName
     */
    @Column(length = 255)
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @return
     */
    @Column(length = 255)
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    @Transient
    public boolean isGuest() {
        return false;
    }

    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    @Column(name = "twitter_id")
    public Long getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(Long twitterId) {
        this.twitterId = twitterId;
    }

    @Column(name = "linkedin_id", length = 64)
    public String getLinkedInId() {
        return linkedInId;
    }

    public void setLinkedInId(String linkedInId) {
        this.linkedInId = linkedInId;
    }

    @Column(length = 255)
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    @Column(name="ldap_user_dn", length = 255)
    public String getLdapUserDN() {
        return ldapUserDN;
    }

    public void setLdapUserDN(String ldapUserDN) {
        this.ldapUserDN = ldapUserDN;
    }
}