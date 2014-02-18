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

package net.firejack.platform.model.service.mail;

import net.firejack.platform.api.mail.domain.MailRecipient;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a Task Mail Service which send email about creating or assigning tasks.
 * Email Message constructs from velocity template.
 * Sender methods work as  asynchronous execution.
 */
@Component
public class TaskMailService extends AbstractMailService implements ITaskMailService {
    private static final Logger logger = Logger.getLogger(TaskMailService.class);

    /**
     * Send email about new task has been created
     *
     * @param taskModel - Task for which will send email
     */
    @Override
    @Async
    public void sendEmailTaskIsCreated(TaskModel taskModel) {
        if (ConfigContainer.isAppInstalled()) {
            if (getMailSender() != null) {
                MailRecipient recipient = new MailRecipient();
                UserModel assignee = taskModel.getAssignee();
                recipient.setEmailTo(assignee.getEmail());

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("task", taskModel);

                String message = generateMailMessage(OpenFlame.TASK_CREATE_TEMPLATE, model);
                if (StringUtils.isNotBlank(message)) {
                    recipient.setSubject(taskModel.getDescription() + ": " + taskModel.getActivity().getName());
                    recipient.setMessage(message);
                    try {
                        getMailSender().sendEmail(recipient);
                    } catch (Exception e) {
                        logger.warn("Occurred error then email was sending.", e);
                    }
                } else {
                    logger.warn("Can't initialize task-assign template");
                }
            } else {
                logger.warn("MailServer has not initializer yet.");
            }
        }
    }

    /**
     * Send email about task has been assigned to user
     *
     * @param taskModel - Task for which will send email
     */
    @Override
    @Async
    public void sendEmailUserAssigned(TaskModel taskModel) {
        if (ConfigContainer.isAppInstalled()) {
            if (getMailSender() != null) {
                MailRecipient recipient = new MailRecipient();
                UserModel assignee = taskModel.getAssignee();
                recipient.setEmailTo(assignee.getEmail());

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("task", taskModel);

                String message = generateMailMessage(OpenFlame.TASK_ASSIGN_TEMPLATE, model);
                if (StringUtils.isNotBlank(message)) {
                    recipient.setSubject(taskModel.getDescription() + ": " + taskModel.getActivity().getName());
                    recipient.setMessage(message);
                    try {
                        getMailSender().sendEmail(recipient);
                    } catch (Exception e) {
                        logger.warn("Occurred error then email was sending.", e);
                    }
                } else {
                    logger.warn("Can't initialize task-assign template");
                }
            } else {
                logger.warn("MailServer has not initializer yet.");
            }
        }
    }

}
