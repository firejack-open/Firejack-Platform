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
import java.util.List;


public interface IPatchContext {

    DialectType getSqlDialect();

    ISqlNameResolver getSqlNamesResolver();

    List<IPackageDescriptorElementSqlDecorator<IRelationshipElement>> getRelationshipDecorators();

    List<IPackageDescriptorElementSqlDecorator<IEntityElement>> getEntityDecorators();

    DataSource getDataSource();

    DataSource getSourceDataSource();

    IUserInfoProvider getUser();

    IPackageDescriptor getNewPackage();

    IPackageDescriptor getOldPackage();

	boolean isDataInserted();

	void setDataInserted(boolean dataInserted);

}