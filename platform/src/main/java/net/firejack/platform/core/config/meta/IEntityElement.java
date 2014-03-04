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

package net.firejack.platform.core.config.meta;

import net.firejack.platform.core.config.meta.construct.CompoundKeyColumnsRule;
import net.firejack.platform.core.config.meta.construct.ReferenceObjectData;
import net.firejack.platform.core.config.meta.element.authority.EntityRole;


public interface IEntityElement extends IParentReferenceOwner, IEntityProvider, IFieldProvider, IIndexProvider {

    /**
     * @return
     */
    CompoundKeyColumnsRule[] getCompoundKeyColumnsRules();

    /**
     * @return
     */
    String getExtendedEntityPath();

    /**
     * @return
     */
    boolean isAbstractEntity();

    /**
     * @return
     */
    boolean isTypeEntity();

    /**
     * @param typeEntity
     */
    void setTypeEntity(boolean typeEntity);

    /**
     * @return
     */
    String getAlias();

    Boolean isSecurityEnabled();

    void setSecurityEnabled(Boolean securityEnabled);

    boolean isSubEntity();

    void  setSubEntity(boolean subEntity);

    EntityRole[] getAllowableContextRoles();

    void setAllowableContextRoles(EntityRole[] allowableContextRoles);

    String getDatabaseRefName();

    void setDatabaseRefName(String databaseRefName);

    ReferenceObjectData getReferenceObject();

    void setReferenceObject(ReferenceObjectData referenceObject);

    Boolean getReverseEngineer();

    void setReverseEngineer(Boolean reverseEngineer);

}