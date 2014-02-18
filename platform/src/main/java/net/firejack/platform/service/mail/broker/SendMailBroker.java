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

package net.firejack.platform.service.mail.broker;

import net.firejack.platform.api.mail.domain.MailRecipient;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.model.service.mail.IMailService;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("sendMailBroker")
public class SendMailBroker extends ServiceBroker<ServiceRequest<MailRecipient>, ServiceResponse> {

    @Autowired
    private IMailService mailService;

    @Override
    protected ServiceResponse perform(ServiceRequest<MailRecipient> request) throws Exception {
        List<MailRecipient> mailRecipients = request.getDataList();
        for (MailRecipient mailRecipient : mailRecipients) {
            mailService.sendMail(mailRecipient);
        }
        return new ServiceResponse("Emails have been sent.", true);
    }

}
