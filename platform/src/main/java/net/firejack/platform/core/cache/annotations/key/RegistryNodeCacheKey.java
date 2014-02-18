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

import net.firejack.platform.core.model.registry.RegistryNodeType;


public class RegistryNodeCacheKey extends CacheKey<Long> {

    private static final long serialVersionUID = -9033368498984237649L;
    private RegistryNodeType type;

    /***/
    public RegistryNodeCacheKey() {
    }

    /**
     * @param id
     * @param type
     */
    public RegistryNodeCacheKey(Long id, RegistryNodeType type) {
        super(id);
        this.type = type;
    }

    /**
     * @return
     */
    public RegistryNodeType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(RegistryNodeType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RegistryNodeCacheKey that = (RegistryNodeCacheKey) o;

        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

}
