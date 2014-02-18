package net.firejack.platform.generate.beans.web.store;

import net.firejack.platform.api.registry.model.FieldType;

import static net.firejack.platform.api.registry.model.FieldType.*;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

public enum MethodType {
    create(false, OBJECT),
    update(false, OBJECT),
    delete(false, FLAG),
    read(true, OBJECT),
    readAll(true, LIST),
    readAllWithFilter(true, LIST),
    search(true, LIST),
    searchCount(true, INTEGER_NUMBER),
    searchWithFilter(true, LIST),
    searchCountWithFilter(true, INTEGER_NUMBER),
    advancedSearch(true, LIST),
    advancedSearchCount(true, INTEGER_NUMBER),
    advancedSearchWithIdsFilter(true, LIST),
    advancedSearchCountWithIdsFilter(true, INTEGER_NUMBER);

    private boolean readOnly;
    private FieldType returnType;

    private MethodType(boolean readOnly, FieldType returnType) {
        this.readOnly = readOnly;
        this.returnType = returnType;
    }

    MethodType(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    public FieldType getReturnType() {
        return returnType;
    }

    /**
     * @param name
     * @return
     */
    public static MethodType find(String name) {
        try {
            return MethodType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
