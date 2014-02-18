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

package net.firejack.platform.model.upgrader.dbengine.bean;

import net.firejack.platform.api.registry.model.FieldType;

public class Column {

    private String name;

    private FieldType type;

    private Object defaultValue;

    private boolean notNull;

    private boolean autoIncrement;

    /***/
    public Column() {
    }

    /**
     * @param name
     */
    public Column(String name) {
        this.name = name;
    }

    /**
     * @param name
     * @param type
     */
    public Column(String name, FieldType type) {
        this.name = name;
        this.type = type;
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
     * @return
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return
     */
    public boolean isNotNull() {
        return notNull;
    }

    /**
     * @param notNull
     */
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    /**
     * @return
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * @param autoIncrement
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
}
