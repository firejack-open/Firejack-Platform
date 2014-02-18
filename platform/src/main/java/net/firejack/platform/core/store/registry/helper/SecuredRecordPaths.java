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

package net.firejack.platform.core.store.registry.helper;

import java.util.LinkedHashSet;
import java.util.Set;

public class SecuredRecordPaths {

    private Set<SecuredRecordPath> paths;

    /***/
    public SecuredRecordPaths() {
        this.paths = new LinkedHashSet<SecuredRecordPath>();
    }

    /**
     * @param paths
     */
    public SecuredRecordPaths(Set<SecuredRecordPath> paths) {
        this.paths = paths;
    }

    /**
     * @return
     */
    public Set<SecuredRecordPath> getPaths() {
        return paths;
    }

    /**
     * @param paths
     */
    public void setPaths(Set<SecuredRecordPath> paths) {
        this.paths = paths;
    }

    /**
     * @param path
     */
    public void addPath(SecuredRecordPath path) {
        if (this.paths == null) {
            this.paths = new LinkedHashSet<SecuredRecordPath>();
        }
        this.paths.add(path);
    }

    /**
     * @param path
     */
    public void removePath(SecuredRecordPath path) {
        if (this.paths != null) {
            this.paths.remove(path);
        }
    }

}
