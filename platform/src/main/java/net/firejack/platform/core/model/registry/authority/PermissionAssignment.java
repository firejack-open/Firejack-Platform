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

package net.firejack.platform.core.model.registry.authority;


import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "opf_permission_assignment")
public class PermissionAssignment extends BaseEntityModel {

    private RoleModel role;
    private PermissionModel permission;
    private PackageModel sourcePackage;

    /**
     * @return
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_role")
    public RoleModel getRole() {
        return role;
    }

    /**
     * @param role
     */
    public void setRole(RoleModel role) {
        this.role = role;
    }

    /**
     * @return
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_permission")
    public PermissionModel getPermission() {
        return permission;
    }

    /**
     * @param permission
     */
    public void setPermission(PermissionModel permission) {
        this.permission = permission;
    }

    /**
     * @return
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_source_package")
    public PackageModel getSourcePackage() {
        return sourcePackage;
    }

    /**
     * @param sourcePackage
     */
    public void setSourcePackage(PackageModel sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

}