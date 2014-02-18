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

package net.firejack.platform.core.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import java.util.Locale;
import java.util.MissingResourceException;


public class MessageResolver implements MessageSourceAware {

    private static Log logger = LogFactory.getLog(MessageResolver.class);

    private static MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * @param message
     * @param locale
     * @param args
     * @return
     */
    public static String messageFormatting(String message, Locale locale, Object... args) {
        if (locale == null) locale = Locale.ENGLISH;
        String messageI18n = message;
        try {
            if (messageSource != null) {
                messageI18n = messageSource.getMessage(message, args, locale);
//                if (args != null) {
//                    messageI18n = MessageFormat.format(messageI18n, args);
//                }
            }
        } catch (MissingResourceException e) {
            logger.warn("Can't find message: " + message, e);
        }
        return messageI18n;
    }

}
