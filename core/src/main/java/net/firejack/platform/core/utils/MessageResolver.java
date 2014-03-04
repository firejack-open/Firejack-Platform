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
