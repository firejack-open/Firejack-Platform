/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.api.mail;


import net.firejack.platform.api.mail.domain.MailRecipient;
import net.firejack.platform.core.response.ServiceResponse;

import java.util.List;

/**
 * Class is interface to mail services
 */
public interface IMailService {

    /**
     * Service retrieves the process
     * @param mailRecipient the Mail Recipient for sending to user
     * @return success result
     */
    ServiceResponse sendMail(MailRecipient mailRecipient);

    /**
     * Service retrieves the process
     * @param mailRecipients the Mail Recipients for sending to user
     * @return success result
     */
    ServiceResponse sendMails(List<MailRecipient> mailRecipients);

}