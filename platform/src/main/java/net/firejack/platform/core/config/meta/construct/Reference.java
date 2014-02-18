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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.utils.StringUtils;


public class Reference {

    private String refName;
    private String refPath;
    private String constraintName;

    public Reference() {
    }

    public Reference(String refName, String refPath) {
        this.refName = refName;
        this.refPath = refPath;
    }

    /**
     * @return
     */
    public String getRefName() {
        return refName;
    }

    /**
     * @param refName
     */
    public void setRefName(String refName) {
        this.refName = refName;
    }

    /**
     * @return
     */
    public String getRefPath() {
        return refPath;
    }

    /**
     * @param refPath
     */
    public void setRefPath(String refPath) {
        this.refPath = refPath;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;

        Reference reference = (Reference) o;

        return StringUtils.equals(refName, reference.refName) &&
                StringUtils.equals(refPath, reference.refPath) &&
                StringUtils.equals(constraintName, reference.constraintName);

    }

    @Override
    public int hashCode() {
        int result = refName != null ? refName.hashCode() : 0;
        result = 31 * result + (refPath != null ? refPath.hashCode() : 0);
        result = 31 * result + (constraintName != null ? constraintName.hashCode() : 0);
        return result;
    }
}