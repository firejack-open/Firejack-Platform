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

package net.firejack.platform.core.cache.annotations.key;

import java.io.Serializable;


public class CacheKey<ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = -9165777721129385459L;
    private ID id;

    /***/
    public CacheKey() {
    }

    /**
     * @param id
     */
    public CacheKey(ID id) {
        this.id = id;
    }

    /**
     * @return
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheKey cacheKey = (CacheKey) o;

        if (id != null ? !id.equals(cacheKey.id) : cacheKey.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CacheKey");
        sb.append("{id=").append(id);
        sb.append('}');
        return sb.toString();
    }

}
