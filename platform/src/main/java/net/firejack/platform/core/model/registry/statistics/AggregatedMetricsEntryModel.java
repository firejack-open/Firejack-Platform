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

package net.firejack.platform.core.model.registry.statistics;

/**
 * Class hold aggregated values of metrics entry properties
 */
public class AggregatedMetricsEntryModel extends MetricsEntryModel {

	private static final long serialVersionUID = 9215189970387444869L;
	private Long startTime;
    private Long endTime;

    /**
     * Gets the start time
     * @return start time
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time
     * @param startTime - start time
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time
     * @return end time
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time
     * @param endTime - end time
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets alias lookup
     * @param alias_lookup - alias lookup
     */
    public void setAlias_lookup(String alias_lookup){
        setLookup(alias_lookup);
    }

    /**
     * Sets alias system account name
     * @param alias_systemAccountName - alias system account name
     */
    public void setAlias_systemAccountName(String alias_systemAccountName){
        setSystemAccountName(alias_systemAccountName);
    }

    /**
     * Sets alias username
     * @param alias_username - alias username
     */
    public void setAlias_username(String alias_username){
        setUsername(alias_username);
    }


}
