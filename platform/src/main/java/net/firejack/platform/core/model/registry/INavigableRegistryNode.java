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

package net.firejack.platform.core.model.registry;

import net.firejack.platform.core.model.IIdentifierContainer;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.utils.IHasName;

import java.util.List;

public interface INavigableRegistryNode extends INavigable, IUrlPathContainer, IHasName, IIdentifierContainer {

    /**
     * @return
     */
    String getPath();

    /**
     * @param path
     */
    void setPath(String path);

    /**
     * @return
     */
    String getLookup();

    /**
     * @param lookup
     */
    void setLookup(String lookup);

    /**
     * @return
     */
    List<PermissionModel> getPermissions();

    /**
     * @param permissions
     */
    void setPermissions(List<PermissionModel> permissions);

}