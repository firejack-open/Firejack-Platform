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
