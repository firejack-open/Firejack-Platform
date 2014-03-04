/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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