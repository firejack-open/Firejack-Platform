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
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

public class MailSender {

    private Logger logger = Logger.getLogger(this.getClass());

    private JavaMailSenderImpl mailSender;
    private String defaultSenderEmail;
    private String defaultSenderName;

    public MailSender(String host, Integer port, Boolean tls,
                      String username, String password, String defaultSenderEmail, String defaultSenderName) {
        this(host, port, tls, username, password, defaultSenderEmail, defaultSenderName, null);
    }

    public MailSender(String host, Integer port, Boolean tls, String username, String password,
                      String defaultSenderEmail, String defaultSenderName, Map<String, Object> additionalProperties) {
        this.mailSender = new JavaMailSenderImpl();
        this.mailSender.setHost(host);
        this.mailSender.setPort(port);

        Properties javaMailProperties = new Properties();
        if (StringUtils.isNotBlank(username)) {
            this.mailSender.setUsername(username);
            this.mailSender.setPassword(password);
            javaMailProperties.setProperty("mail.smtp.auth", "true");
            if (tls) {
                javaMailProperties.put("mail.smtp.starttls.enable", "true");
                javaMailProperties.put("mail.smtp.socketFactory.port", port);
                javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                javaMailProperties.put("mail.smtp.socketFactory.fallback", "false");
            }
        } else {
            javaMailProperties.setProperty("mail.smtp.auth", "false");
        }
        if (additionalProperties != null && !additionalProperties.isEmpty()) {
            for (Map.Entry<String, Object> entry : additionalProperties.entrySet()) {
                javaMailProperties.put(entry.getKey(), entry.getValue());
            }
        }
        this.mailSender.setJavaMailProperties(javaMailProperties);
        this.defaultSenderEmail = defaultSenderEmail;
        this.defaultSenderName = defaultSenderName;
    }

    public void sendEmail(final MailRecipient recipient) {
        if ((recipient.getTo() == null || recipient.getTo().length == 0) && StringUtils.isBlank(recipient.getEmailTo())) {
            logger.error("Email is not sent. Reason: recipient is not set.");
        } else {
            MimeMessagePreparator preparation = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    if (StringUtils.isBlank(recipient.getEmailTo())) {
                        message.setTo(recipient.getTo());
                    } else {
                        message.setTo(recipient.getEmailTo());
                    }
                    String emailFrom = StringUtils.defaultIfEmpty(recipient.getEmailFrom(), defaultSenderEmail);
                    message.setFrom(emailFrom);
                    message.setSubject(recipient.getSubject());
                    message.setText(recipient.getMessage(), true);
                    if (recipient.getCc() != null && recipient.getCc().length > 0) {
                        message.setCc(recipient.getCc());
                    }
                }
            };
            this.mailSender.send(preparation);
        }
    }

}
