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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;


@Entity
@DiscriminatorValue("DOM")
public class DomainModel extends RegistryNodeModel implements IPrefixContainer, IDatabaseAssociated, IAllowDrag, IAllowDrop, IAllowCreateAutoDescription {

	private static final long serialVersionUID = -1983156757590680739L;
	private String prefix;
    private Boolean dataSource;
    private DatabaseModel database;
    private String wsdlLocation;
    private boolean xaSupport;

    @Override
    @Column(name = "prefix", length = 64)
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getDataSource() {
        return dataSource;
    }

    public void setDataSource(Boolean dataSource) {
        this.dataSource = dataSource;
    }

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_database")
	@ForeignKey(name = "FK_DATABASE_ASSOCIATION_DOMAIN")
	public DatabaseModel getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseModel database) {
		this.database = database;
	}

    @Column(name = "urlPath")
    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public boolean isXaSupport() {
        return xaSupport;
    }

    public void setXaSupport(boolean xaSupport) {
        this.xaSupport = xaSupport;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.DOMAIN;
    }

}
