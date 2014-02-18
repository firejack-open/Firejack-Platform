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

package net.firejack.platform.service.mail;


import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.mail.IMailService;
import net.firejack.platform.api.mail.domain.MailRecipient;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.mail.broker.SendMailBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class is an implementation of net.firejack.platform.api.mail.IMailService
 * Business layer is invoked to serve the requests locally
 */
@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_MAIL_SERVICE)
public class MailServiceLocal implements IMailService {
    
    @Autowired
    @Qualifier("sendMailBroker")
    private SendMailBroker sendMailBroker;

    @Override
    public ServiceResponse sendMail(MailRecipient mailRecipient) {
        List<MailRecipient> mailRecipients = new ArrayList<MailRecipient>();
        mailRecipients.add(mailRecipient);
        return sendMailBroker.execute(new ServiceRequest<MailRecipient>(mailRecipients));
    }

    @Override
    public ServiceResponse<MailRecipient> sendMails(List<MailRecipient> mailRecipients) {
        return sendMailBroker.execute(new ServiceRequest<MailRecipient>(mailRecipients));
    }

}