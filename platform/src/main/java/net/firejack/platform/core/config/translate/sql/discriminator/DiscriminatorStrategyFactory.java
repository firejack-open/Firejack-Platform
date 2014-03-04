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

import java.util.Map;


public class DiscriminatorStrategyFactory {

    private static DiscriminatorStrategyFactory instance;

    private DiscriminatorStrategyFactory() {
    }

    /**
     * @param type
     * @param predefinedValues
     * @return
     */
    public IDiscriminatorColumnStrategy populateDefaultDiscriminatorStrategy(DiscriminatorType type, Map<String, String> predefinedValues) {
        if (type == DiscriminatorType.STRING) {
            return new DefaultStringDiscriminatorColumnStrategy(predefinedValues);
        } else {
            throw new UnsupportedOperationException("Discriminator columns of type [" + type + " does not supported.");
        }
    }

    /**
     * @return
     */
    public IDiscriminatorColumnStrategy populateFullNameDiscriminatorStrategy() {
        return new FullNameDiscriminatorColumnStrategy();
    }

    /**
     * @return
     */
    public static DiscriminatorStrategyFactory getInstance() {
        if (instance == null) {
            instance = new DiscriminatorStrategyFactory();
        }
        return instance;
    }
}