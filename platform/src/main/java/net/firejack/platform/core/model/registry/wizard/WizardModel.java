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

package net.firejack.platform.core.model.registry.wizard;

import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.IEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

@javax.persistence.Entity
@DiscriminatorValue("WZD")
public class WizardModel extends RegistryNodeModel implements IEntity {
    private static final long serialVersionUID = 2370956176480737258L;

    private String serverName;
    private Integer port;
    private String parentPath;
    private String urlPath;
    private EntityProtocol protocol;
    private RegistryNodeStatus status;

    private List<WizardFieldModel> fields;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public EntityProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(EntityProtocol protocol) {
        this.protocol = protocol;
    }

    public RegistryNodeStatus getStatus() {
        return status;
    }

    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "wizard", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public List<WizardFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<WizardFieldModel> fields) {
        this.fields = fields;
    }

    @Override
	@Transient
	public RegistryNodeType getType() {
		return RegistryNodeType.WIZARD;
	}

}
