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

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.model.registry.resource.TextResourceVersionModel;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.generate.VelocityGenerator;
import net.firejack.platform.model.event.ChangeConfigEvent;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMailService implements ApplicationListener<ChangeConfigEvent> {

    private static final Logger logger = Logger.getLogger(AbstractMailService.class);

    private static final Map<String, String> SMTP_CONFIGS = new HashMap<String, String>();

    static {
        SMTP_CONFIGS.put(OpenFlame.SMTP_HOST, null);
        SMTP_CONFIGS.put(OpenFlame.SMTP_PORT, null);
        SMTP_CONFIGS.put(OpenFlame.SMTP_TLS, null);
        SMTP_CONFIGS.put(OpenFlame.SMTP_USERNAME, null);
        SMTP_CONFIGS.put(OpenFlame.SMTP_PASSWORD, null);
        SMTP_CONFIGS.put(OpenFlame.SMTP_EMAIL, null);
        SMTP_CONFIGS.put(OpenFlame.SMTP_SENDER, null);
        SMTP_CONFIGS.put(OpenFlame.SMTP_DEFAULT_EMAIL, null);
    }

    private MailSender mailSender;

    @Autowired
    @Qualifier("textResourceVersionStore")
    private IResourceVersionStore<TextResourceVersionModel> textResourceVersionStore;

    @Autowired
    @Qualifier("velocityEngine")
    protected VelocityEngine velocityEngine;

    @Autowired
    @Qualifier("velocityGenerator")
    private VelocityGenerator velocityGenerator;

    @Override
    public void onApplicationEvent(ChangeConfigEvent changeConfigEvent) {
        Config config = changeConfigEvent.getConfig();
        if (config != null) {
            String lookup = config.getLookup();
            if (SMTP_CONFIGS.containsKey(lookup)) {
                SMTP_CONFIGS.put(lookup, config.getValue());
                int configCount = 0;
                for (String value : SMTP_CONFIGS.values()) {
                    if (value != null) {
                        configCount++;
                    }
                }
                if (configCount == SMTP_CONFIGS.size()) {
                    refresh();
                } else {
                    logger.error("Can't find all required configuration properties for initialize the mail service.");
                }
            }
        }
    }

    public void init() {
        if (ConfigContainer.isAppInstalled() && mailSender == null) {
            ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
            int configCount = 0;
            for (String key : SMTP_CONFIGS.keySet()) {
                Config config = configCacheManager.getConfig(key);
                if (config != null) {
                    String value = config.getValue();
                    SMTP_CONFIGS.put(key, value);
                    configCount++;
                }
            }
            if (configCount == SMTP_CONFIGS.size()) {
                refresh();
            } else {
                logger.error("Can't find all required configuration properties for initialize the mail service.");
            }
        }
    }

    /***/
    public void refresh() {
        this.mailSender = new MailSender(
                SMTP_CONFIGS.get(OpenFlame.SMTP_HOST),
                Integer.parseInt(SMTP_CONFIGS.get(OpenFlame.SMTP_PORT)),
                Boolean.parseBoolean(SMTP_CONFIGS.get(OpenFlame.SMTP_TLS)),
                SMTP_CONFIGS.get(OpenFlame.SMTP_USERNAME),
                SMTP_CONFIGS.get(OpenFlame.SMTP_PASSWORD),
                SMTP_CONFIGS.get(OpenFlame.SMTP_EMAIL),
                SMTP_CONFIGS.get(OpenFlame.SMTP_SENDER)
        );
    }

    /**
     * @return
     */
    public MailSender getMailSender() {
        init();
        return this.mailSender;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Generator mail message by velocity template
     *
     * @param templateName - velocity template name
     * @param model - data
     * @return - message
     */
    protected String generateMailMessage(String templateName, Map<String, Object> model) {
        String message = null;
        TextResourceVersionModel textResourceVersion = textResourceVersionStore.findLastVersionByLookup(templateName);
        if (textResourceVersion != null) {
            String templateSource = textResourceVersion.getText();
            try {
                Template template = velocityGenerator.generateTemplateFromString(templateName, templateSource);
                if (template != null) {
                    message = velocityGenerator.mergeTemplate(model, template);
                }
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.warn("Can't find task-assign template as text resource.");
        }
        return message;
    }

}
