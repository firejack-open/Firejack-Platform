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

package net.firejack.platform.core.model;

import java.util.ArrayList;
import java.util.List;


public class SpecifiedIdsFilter<ID> {

    private Boolean all;
    private List<ID> necessaryIds = new ArrayList<ID>();
    private List<ID> unnecessaryIds = new ArrayList<ID>();

    /**
     * @return
     */
    public Boolean getAll() {
        return all;
    }

    /**
     * @param all
     */
    public void setAll(Boolean all) {
        this.all = all;
    }

    /**
     * @return
     */
    public boolean hasGlobalReadPermission() {
        return all != null && all;
    }

    /**
     * @return
     */
    public List<ID> getNecessaryIds() {
        return necessaryIds;
    }

    /**
     * @param necessaryIds
     */
    public void setNecessaryIds(List<ID> necessaryIds) {
        this.necessaryIds = necessaryIds;
    }

    /**
     * @return
     */
    public List<ID> getUnnecessaryIds() {
        return unnecessaryIds;
    }

    /**
     * @param unnecessaryIds
     */
    public void setUnnecessaryIds(List<ID> unnecessaryIds) {
        this.unnecessaryIds = unnecessaryIds;
    }
}
