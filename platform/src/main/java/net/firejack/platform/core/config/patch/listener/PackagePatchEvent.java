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

package net.firejack.platform.core.config.patch.listener;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.model.user.IUserInfoProvider;

import javax.sql.DataSource;


public class PackagePatchEvent {

    private IPackageDescriptor oldPackageDescriptor;
    private IPackageDescriptor newPackageDescriptor;
    private DataSource dataSource;
    private IUserInfoProvider user;


    /**
     * @param oldPackageDescriptor
     * @param newPackageDescriptor
     * @param dataSource
     * @param user
     */
    public PackagePatchEvent(IPackageDescriptor oldPackageDescriptor, IPackageDescriptor newPackageDescriptor,
                             DataSource dataSource, IUserInfoProvider user) {
        this.oldPackageDescriptor = oldPackageDescriptor;
        this.newPackageDescriptor = newPackageDescriptor;
        this.dataSource = dataSource;
        this.user = user;
    }

    /**
     * @return
     */
    public IPackageDescriptor getOldPackage() {
        return oldPackageDescriptor;
    }

    /**
     * @return
     */
    public IPackageDescriptor getNewPackage() {
        return newPackageDescriptor;
    }

    /**
     * @return
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @return
     */
    public IUserInfoProvider getUser() {
        return user;
    }
}