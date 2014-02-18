package net.firejack.aws.web.model;
/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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


import com.amazonaws.services.ec2.model.InstanceType;

import java.io.Serializable;
import java.util.List;

public class InstanceDetails implements Serializable{
    private static final long serialVersionUID = 1021787348112717882L;

    private InstanceType[] instanceTypes;
    private List<Dropdown> amis;
    private List<Dropdown> keys;
    private List<Dropdown> securityGroups;

    public InstanceType[] getInstanceTypes() {
        return instanceTypes;
    }

    public void setInstanceTypes(InstanceType[] instanceTypes) {
        this.instanceTypes = instanceTypes;
    }

    public List<Dropdown> getAmis() {
        return amis;
    }

    public void setAmis(List<Dropdown> amis) {
        this.amis = amis;
    }

    public List<Dropdown> getKeys() {
        return keys;
    }

    public void setKeys(List<Dropdown> keys) {
        this.keys = keys;
    }

    public List<Dropdown> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<Dropdown> securityGroups) {
        this.securityGroups = securityGroups;
    }
}
