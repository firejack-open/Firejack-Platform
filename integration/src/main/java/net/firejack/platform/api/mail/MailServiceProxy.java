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

package net.firejack.platform.api.mail;


import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.mail.domain.MailRecipient;
import net.firejack.platform.core.response.ServiceResponse;

import java.util.List;

/**
 * Class is an implementation of net.firejack.platform.api.process.IMailService
 * The services are invoked in RESTful manner
 */
public class MailServiceProxy extends AbstractServiceProxy implements IMailService {

	public MailServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
    public String getServiceUrlSuffix() {
        return "/mail";
    }

    @Override
    public ServiceResponse sendMail(MailRecipient mailRecipient) {
        return post2AsSystem(getSystemUserSessionToken(), "", mailRecipient);
    }

    @Override
    public ServiceResponse<MailRecipient> sendMails(List<MailRecipient> mailRecipients) {
        return post2AsSystem(getSystemUserSessionToken(), "", mailRecipients);
    }

}