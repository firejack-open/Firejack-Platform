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

package net.firejack.platform.processor.interceptor;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.utils.ArrayUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.manager.TrackContainer;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Aspect
@SuppressWarnings("unused")
public class LogEntryDetailsInterceptor {

    private static final String MSG_ERROR_OCCURRED = "Error occurred during broker execution.";

    public static final String TEMPLATE_STATS_DESCRIPTION_CONFIG_NAME = "stats-desc-msg-tpl";
    public static final String STATS_DESCRIPTION_MESSAGE_TEMPLATE_PATH = "net.firejack.platform.defaults.stats-desc-msg-tpl";
    public static final String MESSAGE_TEMPLATE_DEFAULT_ACTION_LOOKUP = "net.firejack.platform.defaults.stats-desc-msg-tpl.default";

    private static final Logger logger = Logger.getLogger(LogEntryDetailsInterceptor.class);

    /**
     * @param pjp
     * @param broker
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* execute(..)) && target(broker) && @target(net.firejack.platform.web.statistics.annotation.TrackDetails)",
            argNames = "pjp,broker")
    public Object trackActionDetails(ProceedingJoinPoint pjp, ServiceBroker broker) throws Throwable {
        if (TrackContainer.getCurrentTrack() != null) {
            Object[] arguments = pjp.getArgs();
            ServiceRequest serviceRequest = (ServiceRequest) arguments[0];
            try {
                OPFContext context = OPFContext.getContext();
                String currentAction = context == null ? null : context.getCurrentActionLookup();
                String username = context == null ? "Undefined" : context.getPrincipal().getName();
                if (TrackContainer.getCurrentTrack() != null) {
                    if (currentAction != null) {
                        String actionName = humanNameFromLookup(currentAction);
                        String entityName = humanNameFromLookup(extractPathFromLookup(currentAction));
                        String messageTemplate = getMessageTemplateByActionLookup(currentAction);
                        if (messageTemplate != null) {
                            messageTemplate = messageTemplate.replaceAll("\\[USERNAME\\]", username);
                            messageTemplate = messageTemplate.replaceAll("\\[ACTION\\]", actionName);
                            messageTemplate = messageTemplate.replaceAll("\\[ENTITY\\]", entityName);

                            Object identifier = broker.getDetailedMessageArgs(serviceRequest);
                            if (identifier != null) {
                                messageTemplate = messageTemplate.replaceAll("\\[ID\\]", identifier.toString());
                            }
                            TrackContainer.getCurrentTrack().setDetails(messageTemplate);
                        } else {
                            String message = username + " performed the " + actionName + " operation for " + entityName + " records.";
                            TrackContainer.getCurrentTrack().setDetails(message);
                        }
                    } else {
                        TrackContainer.getCurrentTrack().setDetails("Action has not been defined.");
                    }
                }
            } catch (Throwable th) {
                logger.warn(th.getMessage(), th);
            }
        }
        try {
            return pjp.proceed();
        } catch (Throwable th) {
            logger.error(MSG_ERROR_OCCURRED);
            throw th;
        }
    }

    private String getMessageTemplateByActionLookup(String actionLookup) {
        String messageTemplateConfigLookup = actionLookup + "." + TEMPLATE_STATS_DESCRIPTION_CONFIG_NAME;
        String messageTemplate = getMessageTemplate(messageTemplateConfigLookup);
        if (messageTemplate == null) {
            String actionName = extractNameFromLookup(actionLookup);
            messageTemplateConfigLookup = STATS_DESCRIPTION_MESSAGE_TEMPLATE_PATH + "." + actionName;
            messageTemplate = getMessageTemplate(messageTemplateConfigLookup);
            if (messageTemplate == null) {
                messageTemplate = getMessageTemplate(MESSAGE_TEMPLATE_DEFAULT_ACTION_LOOKUP);
            }
        }
        return messageTemplate;
    }

    public String getMessageTemplate(String messageTemplateConfigLookup) {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(messageTemplateConfigLookup);
        return config != null ? config.getLookup() : null;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public String extractNameFromLookup(String lookup) {
        String[] names = lookup.split("\\.");
        return names[names.length - 1];
    }

    public String extractPathFromLookup(String lookup) {
        String[] names = lookup.split("\\.");
        names = (String[]) ArrayUtils.remove(names, names.length - 1);
        return StringUtils.join(names, ".");
    }

    public String humanNameFromLookup(String lookup) {
        String lookupName = extractNameFromLookup(lookup);
        String[] lookupNames = lookupName.split("-");
        String[] names = new String[lookupNames.length];
        for (int i = 0, lookupNamesLength = lookupNames.length; i < lookupNamesLength; i++) {
            names[i] = StringUtils.capitalize(lookupNames[i]);
        }
        return StringUtils.join(names, " ");
    }

}