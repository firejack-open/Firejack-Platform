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

package net.firejack.platform.core.config.patch;

import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.IRelationshipElement;
import net.firejack.platform.core.config.translate.IPackageDescriptorElementSqlDecorator;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


public class DefaultPatchContext implements IPatchContext {

    private DataSource dataSource;
    private DataSource sourceDataSource;
    private IUserInfoProvider user;
    private IPackageDescriptor newPackageDescriptor;
    private IPackageDescriptor oldPackageDescriptor;
    private ISqlNameResolver sqlNameResolver;
    private DialectType sqlDialect;
	private boolean dataInserted;
    private List<IPackageDescriptorElementSqlDecorator<IRelationshipElement>> relationshipDecorators;
    private List<IPackageDescriptorElementSqlDecorator<IEntityElement>> entityDecorators;

    public DefaultPatchContext(DataSource dataSource, IUserInfoProvider user) {
        this.dataSource = dataSource;
        this.user = user;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

	public DataSource getSourceDataSource() {
		return sourceDataSource;
	}

	public void setSourceDataSource(DataSource sourceDataSource) {
		this.sourceDataSource = sourceDataSource;
	}

	@Override
    public IUserInfoProvider getUser() {
        return user;
    }

    @Override
    public IPackageDescriptor getNewPackage() {
        return newPackageDescriptor;
    }

    public void setNewPackage(IPackageDescriptor newPackageDescriptor) {
        this.newPackageDescriptor = newPackageDescriptor;
    }

    @Override
    public IPackageDescriptor getOldPackage() {
        return oldPackageDescriptor;
    }

    public void setOldPackage(IPackageDescriptor oldPackageDescriptor) {
        this.oldPackageDescriptor = oldPackageDescriptor;
    }

    @Override
    public DialectType getSqlDialect() {
        return sqlDialect;
    }

    public void setSqlDialect(DialectType sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    @Override
    public ISqlNameResolver getSqlNamesResolver() {
        return sqlNameResolver;
    }

    public void setSqlNamesResolver(ISqlNameResolver sqlNameResolver) {
        this.sqlNameResolver = sqlNameResolver;
    }

    public void addRelationshipDecorator(IPackageDescriptorElementSqlDecorator<IRelationshipElement> decorator) {
        getRelationshipDecorators().add(decorator);
    }

    public void addEntityDecorator(IPackageDescriptorElementSqlDecorator<IEntityElement> decorator) {
        getEntityDecorators().add(decorator);
    }

    @Override
    public List<IPackageDescriptorElementSqlDecorator<IRelationshipElement>> getRelationshipDecorators() {
        if (relationshipDecorators == null) {
            relationshipDecorators = new ArrayList<IPackageDescriptorElementSqlDecorator<IRelationshipElement>>();
        }
        return relationshipDecorators;
    }

    @Override
    public List<IPackageDescriptorElementSqlDecorator<IEntityElement>> getEntityDecorators() {
        if (entityDecorators == null) {
            entityDecorators = new ArrayList<IPackageDescriptorElementSqlDecorator<IEntityElement>>();
        }
        return entityDecorators;
    }

	public boolean isDataInserted() {
		return dataInserted;
	}

	public void setDataInserted(boolean dataInserted) {
		this.dataInserted = dataInserted;
	}

}