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

package net.firejack.aws.web.model;


import com.amazonaws.services.ec2.model.InstanceType;

import java.io.Serializable;
import java.util.List;

public class InstanceDetails implements Serializable{
    private static final long serialVersionUID = 1021787348112717882L;

    private InstanceType[] instanceTypes;
    private List<Dropdown> amis;
    private List<Dropdown> keys;
    private List<Dropdown> securityGroups;

    public InstanceType[] getInstanceTypes() {
        return instanceTypes;
    }

    public void setInstanceTypes(InstanceType[] instanceTypes) {
        this.instanceTypes = instanceTypes;
    }

    public List<Dropdown> getAmis() {
        return amis;
    }

    public void setAmis(List<Dropdown> amis) {
        this.amis = amis;
    }

    public List<Dropdown> getKeys() {
        return keys;
    }

    public void setKeys(List<Dropdown> keys) {
        this.keys = keys;
    }

    public List<Dropdown> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<Dropdown> securityGroups) {
        this.securityGroups = securityGroups;
    }
}
