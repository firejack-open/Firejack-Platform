package net.firejack.platform.generate.beans.web.store;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.key.Key;

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

public class Param<T extends Param> implements Comparable<T> {
    private String name;
    private FieldType type;
    private boolean key;
    private Base domain;

    /***/
    public Param() {
    }

    /**
     * @param name
     * @param type
     */
    public Param(String name, FieldType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @param name
     * @param type
     * @param domain
     */
    public Param(String name, FieldType type, Model domain) {
        this.name = name;
        this.type = type;
        this.domain = domain;
    }

    public Param(String name, Key key) {
        this.name = name;
        this.type = key.getType();
        this.key = true;
        if (key.isComposite()) {
            this.domain = (Base) key;
        }
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public FieldType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     * @param type
     * @return
     */
    public boolean isType(FieldType type) {
        return this.type.equals(type);
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public Base getDomain() {
        return domain;
    }

    public void setDomain(Base domain) {
        this.domain = domain;
    }

    /**
     * @return
     */
    public String getParamName() {
        if (isType(FieldType.OBJECT) || isType(FieldType.LIST)) {
            return getDomain().getName();
        } else {
            return getType().getClassName();
        }
    }

    @Override
    public int compareTo(T param) {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Param)) return false;

        Param param = (Param) o;

        return !(name != null ? !name.equals(param.name) : param.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
