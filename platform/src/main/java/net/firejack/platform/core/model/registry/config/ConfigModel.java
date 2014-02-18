package net.firejack.platform.core.model.registry.config;

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

import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.annotation.PlaceHolders;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_config")
@PlaceHolders(name = "config", holders = {
        @PlaceHolder(key = "{lookup}", value = "{value}")
})
public class ConfigModel extends LookupModel<RegistryNodeModel> {

    private static final long serialVersionUID = 6412443928883180524L;
    private String value;

    /**
     * @return
     */
    @Column(length = 1024)
    @PlaceHolder(key = "value")
    public String getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.CONFIG;
    }

}
