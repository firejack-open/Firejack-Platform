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
