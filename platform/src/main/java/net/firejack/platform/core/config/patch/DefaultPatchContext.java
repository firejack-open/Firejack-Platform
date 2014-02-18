/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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