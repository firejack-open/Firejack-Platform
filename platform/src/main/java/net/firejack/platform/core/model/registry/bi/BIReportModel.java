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

package net.firejack.platform.core.model.registry.bi;


import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.IEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("BIRPT")
public class BIReportModel extends RegistryNodeModel implements IEntity {
    private static final long serialVersionUID = 5824151706909855523L;

    private String serverName;
    private Integer port;
    private String parentPath;
    private String urlPath;
    private EntityProtocol protocol;
    private RegistryNodeStatus status;
    private List<BIReportFieldModel> fields;

    @Column(length = 1024)
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

    @Column(length = 255)
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @Column(length = 2048)
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Enumerated
    public EntityProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(EntityProtocol protocol) {
        this.protocol = protocol;
    }

    @Enumerated
    public RegistryNodeStatus getStatus() {
        return status;
    }

    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
   	@OnDelete(action = OnDeleteAction.CASCADE)
    public List<BIReportFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<BIReportFieldModel> fields) {
        this.fields = fields;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.BI_REPORT;
    }
}
