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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "index-type")
@XmlEnum
public enum IndexType {

    @XmlEnumValue("INDEX")
    INDEX("INDEX", "INDEX"),

    @XmlEnumValue("PRIMARY_KEY")
    PRIMARY_KEY("PRIMARY KEY", "PRIMARY KEY"),

    @XmlEnumValue("UNIQUE")
    UNIQUE("UNIQUE", "INDEX");

    private String addKey;
    private String dropKey;

    IndexType(String addKey, String dropKey) {
        this.addKey = addKey;
        this.dropKey = dropKey;
    }

    /**
     * @return
     */
    public String getAddKey() {
        return addKey;
    }

    /**
     * @return
     */
    public String getDropKey() {
        return dropKey;
    }

}
