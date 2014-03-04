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

package net.firejack.platform.core.model.registry.config;

import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.annotation.PlaceHolders;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_config")
@PlaceHolders(name = "config", holders = {
        @PlaceHolder(key = "{lookup}", value = "{value}")
})
public class ConfigModel extends LookupModel<RegistryNodeModel> {

    private static final long serialVersionUID = 6412443928883180524L;
    private String value;

    /**
     * @return
     */
    @Column(length = 1024)
    @PlaceHolder(key = "value")
    public String getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.CONFIG;
    }

}
