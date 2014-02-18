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

import net.firejack.platform.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SecuredRecordPath {

    private List<Long> ids;

    /***/
    public SecuredRecordPath() {
        this.ids = new ArrayList<Long>();
    }

    /**
     * @param ids
     */
    public SecuredRecordPath(List<Long> ids) {
        this.ids = ids;
    }

    /**
     * @return
     */
    public List<Long> getIds() {
        return ids;
    }

    /**
     * @param ids
     */
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    /**
     * @param id
     */
    public void addId(Long id) {
        if (this.ids == null) {
            this.ids = new ArrayList<Long>();
        }
        this.ids.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecuredRecordPath that = (SecuredRecordPath) o;

        String thisPath = StringUtils.join(this.ids.iterator(), ":");
        String thatPath = StringUtils.join(that.ids.iterator(), ":");

        return thisPath.equals(thatPath);
    }

    @Override
    public int hashCode() {
        return ids != null ? ids.hashCode() : 0;
    }

}
