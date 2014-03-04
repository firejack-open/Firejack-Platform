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