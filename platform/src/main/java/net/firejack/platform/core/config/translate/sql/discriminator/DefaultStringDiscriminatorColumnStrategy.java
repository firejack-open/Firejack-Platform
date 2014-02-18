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

package net.firejack.platform.core.config.translate.sql.discriminator;

import net.firejack.platform.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;


class DefaultStringDiscriminatorColumnStrategy implements IDiscriminatorColumnStrategy {

    private static final String CONSONANT_LETTERS = "bcdfghgklmnpqrstvwxzBCDFGHGKLMNPQRSTVWXZ";
    private Map<String, String> predefinedValues = new HashMap<String, String>();

    public DefaultStringDiscriminatorColumnStrategy(Map<String, String> predefinedValues) {
        if (predefinedValues != null) {
            this.predefinedValues.putAll(predefinedValues);
        }
    }

    @Override
    public String getDiscriminatorValue(String entityName) {
        if (StringUtils.isBlank(entityName)) {
            return null;
        }
        String value = predefinedValues.get(entityName);
        if (value != null) {
            return value;
        }
        if (entityName.length() > 3) {
            value = entityName.substring(0, 2);
            for (int i = 2; i < entityName.length(); i++) {
                String s = entityName.substring(i, i + 1);
                if (CONSONANT_LETTERS.contains(s)) {
                    value += s;
                    break;
                }
            }
            if (value.length() == 2) {
                value += entityName.substring(2, 3);
            }
            value = value.toUpperCase();
        } else {
            value = entityName.toUpperCase();
        }
        predefinedValues.put(entityName, value);
        return value;
    }

}