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

package net.firejack.platform.core.model;

import java.util.ArrayList;
import java.util.List;


public class SpecifiedIdsFilter<ID> {

    private Boolean all;
    private List<ID> necessaryIds = new ArrayList<ID>();
    private List<ID> unnecessaryIds = new ArrayList<ID>();

    /**
     * @return
     */
    public Boolean getAll() {
        return all;
    }

    /**
     * @param all
     */
    public void setAll(Boolean all) {
        this.all = all;
    }

    /**
     * @return
     */
    public boolean hasGlobalReadPermission() {
        return all != null && all;
    }

    /**
     * @return
     */
    public List<ID> getNecessaryIds() {
        return necessaryIds;
    }

    /**
     * @param necessaryIds
     */
    public void setNecessaryIds(List<ID> necessaryIds) {
        this.necessaryIds = necessaryIds;
    }

    /**
     * @return
     */
    public List<ID> getUnnecessaryIds() {
        return unnecessaryIds;
    }

    /**
     * @param unnecessaryIds
     */
    public void setUnnecessaryIds(List<ID> unnecessaryIds) {
        this.unnecessaryIds = unnecessaryIds;
    }
}
