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