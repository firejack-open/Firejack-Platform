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