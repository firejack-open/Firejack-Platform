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