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

package net.firejack.platform.api.mail.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@RuleSource("OPF.mail.MailRecipient")
public class MailRecipient extends AbstractDTO {

    private String emailTo;
    private String emailFrom;
    private String subject;
    private String message;
    private String[] to;
    private String[] cc;

    /**
     * @return
     */
    @NotBlank
    public String getEmailTo() {
        return emailTo;
    }

    /**
     * @param emailTo
     */
    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    /**
     * @return
     */
    public String getEmailFrom() {
        return emailFrom;
    }

    /**
     * @param emailFrom
     */
    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    /**
     * @return
     */
    @NotBlank
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return
     */
    @NotBlank
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return
     */
    public String[] getCc() {
        return cc;
    }

    /**
     * @param cc
     */
    public void setCc(String[] cc) {
        this.cc = cc;
    }
}
