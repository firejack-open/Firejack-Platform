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

package net.firejack.platform.api.securitymanager.domain;

import net.firejack.platform.core.domain.AbstractDTO;

import java.util.Arrays;

public class SecuredRecordNode extends AbstractDTO {

    private Long securedRecordId;
    private Long internalId;
    private String type;
    private SecuredRecordNodePath[] nodePaths;

    public Long getSecuredRecordId() {
        return securedRecordId;
    }

    public void setSecuredRecordId(Long securedRecordId) {
        this.securedRecordId = securedRecordId;
    }

    public Long getInternalId() {
        return internalId;
    }

    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SecuredRecordNodePath[] getNodePaths() {
        return nodePaths;
    }

    public void setNodePaths(SecuredRecordNodePath[] nodePaths) {
        this.nodePaths = nodePaths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecuredRecordNode that = (SecuredRecordNode) o;

        if (internalId != null ? !internalId.equals(that.internalId) : that.internalId != null) return false;
        if (!Arrays.equals(nodePaths, that.nodePaths)) return false;
        if (securedRecordId != null ? !securedRecordId.equals(that.securedRecordId) : that.securedRecordId != null)
            return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);
    }

    @Override
    public int hashCode() {
        int result = securedRecordId != null ? securedRecordId.hashCode() : 0;
        result = 31 * result + (internalId != null ? internalId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (nodePaths != null ? Arrays.hashCode(nodePaths) : 0);
        return result;
    }
}