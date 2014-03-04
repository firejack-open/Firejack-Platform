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

package net.firejack.platform.core.model.user;

import net.firejack.platform.core.model.registry.domain.SystemModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@Entity
@DiscriminatorValue("SYS")
public class SystemUserModel extends BaseUserModel {
    private static final long serialVersionUID = 7159344576475499176L;

    private SystemModel system;

    private String consumerKey;
    private String consumerSecret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_system")
    @ForeignKey(name = "FK_SYSTEM_USER_SYSTEM")
    public SystemModel getSystem() {
        return system;
    }

    public void setSystem(SystemModel system) {
        this.system = system;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

}
