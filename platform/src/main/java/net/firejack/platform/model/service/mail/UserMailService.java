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
