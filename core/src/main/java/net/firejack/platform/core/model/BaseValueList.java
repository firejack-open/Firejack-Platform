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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BaseValueList<T> implements Serializable {
    private static final long serialVersionUID = 7154239362453464182L;

    protected List<T> entries;

    protected BaseValueList() {
        getEntries();
    }

    protected BaseValueList(List<T> entries) {
        this.entries = entries;
    }

    /**
     * @return
     */
    public List<T> getEntries() {
        if (entries == null) {
            entries = new ArrayList<T>();
        }
        return entries;
    }

    /**
     * @param entries
     */
    public void setEntries(List<T> entries) {
        this.entries = entries;
    }

    /**
     * @param id
     */
    public void add(T id) {
        getEntries().add(id);
    }

    /**
     * @param index
     * @param id
     */
    public void add(int index, T id) {
        if (getEntries().size() > index) {
            getEntries().add(index, id);
        } else {
            getEntries().add(id);
        }
    }

    /**
     * @param id
     * @return
     */
    public int indexOf(T id) {
        return (entries == null) ? -1 : entries.indexOf(id);
    }

    /**
     * @param id
     */
    public void remove(T id) {
        if (entries != null) {
            entries.remove(id);
        }
    }

    /**
     * @param ids
     */
    public void removeAll(List<T> ids) {
        if (entries != null) {
            entries.removeAll(ids);
        }
    }

    /**
     * @param id
     * @return
     */
    public boolean contains(T id) {
        return entries != null && entries.contains(id);
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return entries == null || entries.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseValueList)) {
            return false;
        }

        BaseValueList that = (BaseValueList) o;
        return !((entries != null) ? !entries.equals(that.entries) : (that.entries != null));
    }

    @Override
    public int hashCode() {
        return entries != null ? entries.hashCode() : 0;
    }
}
