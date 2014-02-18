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


public class CompoundKeyParticipantColumn {

    private Reference ref;
    private String columnName;
    private Boolean refToParent;

    /**
     * @return
     */
    public Reference getRef() {
        return ref;
    }

    /**
     * @param ref
     */
    public void setRef(Reference ref) {
        this.ref = ref;
    }

    /**
     * @return
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @param refToParent
     */
    public void setRefToParent(boolean refToParent) {
        this.refToParent = refToParent ? Boolean.TRUE : null;
    }

    /**
     * @return
     */
    public boolean isRefToParent() {
        return refToParent != null && refToParent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundKeyParticipantColumn)) return false;

        CompoundKeyParticipantColumn that = (CompoundKeyParticipantColumn) o;

        return isRefToParent() && that.isRefToParent() ||
                !(columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) &&
                        !(ref != null ? !ref.equals(that.ref) : that.ref != null);
    }

    @Override
    public int hashCode() {
        int result = ref != null ? ref.hashCode() : 0;
        result = 31 * result + (refToParent == Boolean.TRUE ? refToParent.hashCode() : 0);
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }
}