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

package net.firejack.platform.model.service.reverse.bean;

public enum Behavior {

    NO_ACTION (3),

    CASCADE (0),

    SET_NULL (2),

    RESTRICT (1),

    SET_DEFAULT (4);

    private int ruleIndex;

    Behavior(int ruleIndex) {
        this.ruleIndex = ruleIndex;
    }

    public int getRuleIndex() {
        return ruleIndex;
    }

    public static Behavior getByRuleIndex(int ruleIndex) {
        Behavior foundBehavior = null;
        for (Behavior behavior : values()) {
            if (behavior.getRuleIndex() == ruleIndex) {
                foundBehavior = behavior;
                break;
            }
        }
        return foundBehavior;
    }


}
