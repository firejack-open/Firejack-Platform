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

package net.firejack.platform.model.service.mail;

import net.firejack.platform.api.mail.domain.MailRecipient;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserMailService extends AbstractMailService implements IUserMailService {
    private static final Logger logger = Logger.getLogger(UserMailService.class);

    /**
     * Send reset password email
     *
     * @param userModel - Task for which will send email
     */
    @Override
    @Async
    public void sendResetPasswordMail(UserModel userModel, String resetPasswordPageUrl) {
        if (ConfigContainer.isAppInstalled()) {
            if (getMailSender() != null) {
                MailRecipient recipient = new MailRecipient();
                recipient.setEmailTo(userModel.getEmail());

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("user", userModel);

                String resetPasswordUrl = resetPasswordPageUrl + "?token=" + userModel.getResetPasswordToken();
                model.put("resetPasswordUrl", resetPasswordUrl);

                String message = generateMailMessage(OpenFlame.RESET_PASSWORD_TEMPLATE, model);
                if (StringUtils.isNotBlank(message)) {
                    recipient.setSubject("Reset Password");
                    recipient.setMessage(message);
                    try {
                        getMailSender().sendEmail(recipient);
                    } catch (Exception e) {
                        logger.warn("Occurred error then email was sending.", e);
                    }
                } else {
                    logger.warn("Can't initialize reset password template");
                }
            } else {
                logger.warn("MailServer has not initializer yet.");
            }
        }
    }

    /**
     * Send new password email
     *
     * @param userModel - Task for which will send email
     * @param newPassword - new generated password
     */
    @Override
    @Async
    public void sendGeneratedPasswordMail(UserModel userModel, String newPassword) {
        if (ConfigContainer.isAppInstalled()) {
            if (getMailSender() != null) {
                MailRecipient recipient = new MailRecipient();
                recipient.setEmailTo(userModel.getEmail());

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("user", userModel);

                model.put("newPassword", newPassword);

                String message = generateMailMessage(OpenFlame.NEW_PASSWORD_TEMPLATE, model);
                if (StringUtils.isNotBlank(message)) {
                    recipient.setSubject("New Password");
                    recipient.setMessage(message);
                    try {
                        getMailSender().sendEmail(recipient);
                    } catch (Exception e) {
                        logger.warn("Occurred error then email was sending.", e);
                    }
                } else {
                    logger.warn("Can't initialize new password template");
                }
            } else {
                logger.warn("MailServer has not initializer yet.");
            }
        }
    }
}
