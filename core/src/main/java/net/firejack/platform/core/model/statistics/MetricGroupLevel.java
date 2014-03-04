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

package net.firejack.platform.core.model.statistics;

/**
 * List possible metric group levels
 */
public enum MetricGroupLevel {

    HOUR,

    DAY,

    WEEK,

    MONTH;

    /**
     * Finds metric group level by name
     * @param name - name of the metric group level
     * @return metric group level
     */
    public static MetricGroupLevel findByName(String name) {
        MetricGroupLevel value = null;
        for (MetricGroupLevel e : values()) {
            if (e.name().equals(name)) {
                value = e;
                break;
            }
        }
        return value;
    }

}
