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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.patch.EntityElementManager;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;

public class GeneratedFieldContext {

    private IEntityElement entity;
    private ISqlNameResolver nameResolver;
    private EntityElementManager entityElementManager;
    private Reference reference;

    /**
     * @param entity
     * @param nameResolver
     */
    public GeneratedFieldContext(IEntityElement entity, ISqlNameResolver nameResolver) {
        this.entity = entity;
        this.nameResolver = nameResolver;
    }

    /**
     * @return
     */
    public IEntityElement getEntity() {
        return entity;
    }

    /**
     * @param entity
     */
    public void setEntity(IEntityElement entity) {
        this.entity = entity;
    }

    /**
     * @return
     */
    public ISqlNameResolver getNameResolver() {
        return nameResolver;
    }

    /**
     * @param nameResolver
     */
    public void setNameResolver(ISqlNameResolver nameResolver) {
        this.nameResolver = nameResolver;
    }

    /**
     * @return
     */
    public EntityElementManager getEntityElementManager() {
        return entityElementManager;
    }

    /**
     * @param entityElementManager
     */
    public void setEntityElementManager(EntityElementManager entityElementManager) {
        this.entityElementManager = entityElementManager;
    }

    /**
     * @return
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * @param reference
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

}