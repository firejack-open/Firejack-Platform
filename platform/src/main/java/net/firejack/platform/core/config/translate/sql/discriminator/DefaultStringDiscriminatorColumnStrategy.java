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