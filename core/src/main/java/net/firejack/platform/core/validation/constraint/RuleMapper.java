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

package net.firejack.platform.core.validation.constraint;

import net.firejack.platform.core.utils.ContextRefreshListener;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class RuleMapper extends ContextRefreshListener {

    private static Map<String, Class<?>> CONSTRAINT_LOCATIONS = new HashMap<String, Class<?>>();
    private static final Logger logger = Logger.getLogger(RuleMapper.class);


    protected void onContextRefreshed(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, Object> beans =  context.getBeansWithAnnotation(RuleSource.class);

        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            RuleSource ruleSource = clazz.getAnnotation(RuleSource.class);
            if (ruleSource != null) {
                if (StringUtils.isNotBlank(ruleSource.value())) {
                    CONSTRAINT_LOCATIONS.put(ruleSource.value(), clazz);
                }
            }
        }
    }

    /**
     * @param id
     * @return
     */
    public static ConstraintsSourceClass getConstrainedType(String id) {
        ConstraintsSourceClass sourceClass = null;
        if (StringUtils.isBlank(id)) {
            return sourceClass;
        }
        String[] values = id.split("\\?");
        if (values.length == 1) {
            sourceClass = new ConstraintsSourceClass(CONSTRAINT_LOCATIONS.get(id));
        } else {
            sourceClass = new ConstraintsSourceClass(CONSTRAINT_LOCATIONS.get(values[0]));
            Map<String, String> params = new HashMap<String, String>();
            String[] parameters = values[1].split("\\|");
            for (String parameter : parameters) {
                String[] p = parameter.split("=");
                if (p.length == 2) {
                    params.put(p[0], p[1]);
                } else {
                    params.put(p[0], null);
                }
            }
            sourceClass.setParams(params);
        }
        return sourceClass;
    }
}