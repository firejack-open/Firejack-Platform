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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.utils.Paging;

import java.util.List;
import java.util.Map;


public interface IEntityStore extends IFieldContainerStore<EntityModel>, IResourceAccessFieldsStore<EntityModel> {

    EntityModel findById(final Long id);

    EntityModel findByLookup(String lookup);

    List<EntityModel> findParentTypesByEntityLookup(String entityLookup);

    List<EntityModel> findEntitiesUpInHierarchy(String entityLookup);

    Map<String, List<EntityModel>> findAllEntitiesUpHierarchies();

    EntityModel findWithInheritedFieldsById(final Long id);

    /**
     * @param lookup
     * @return
     */
    EntityModel findWithInheritedFieldsByLookup(final String lookup);

    /**
     * @param id
     * @return
     */
    List<EntityModel> findAllExtendedEntities(final Long id);

    /**
     * @param filter
     * @param isTypeEntity
     * @return
     */
    List<EntityModel> findAllWithFilter(SpecifiedIdsFilter<Long> filter, Boolean isTypeEntity);

    List<Object[]> findAllIdAndExtendedId();

    void save(EntityModel entity);

    void saveForm(DomainModel domainModel, List<EntityModel> entityModels, List<RelationshipModel> relationshipModels);

    /**
     * @param entity
     * @param extendedEntity
     */
    void saveExtendedEntity(EntityModel entity, EntityModel extendedEntity);

    /**
     *
     * @param ent
     * @param entityFields
     * @param indexList
     * @return
     */
    EntityModel mergeForGenerator(EntityModel ent, List<FieldModel> entityFields, List<IndexModel> indexList);

    List<EntityModel> getSecurityEnabledEntities(List<String> entityLookupList);

    List<EntityModel> getSecurityEnabledEntities();

    void saveEntityRoles(Map<String, List<String>> entityRolesInfo);

    String generateEntityTableName(EntityModel entity);

    void setSecurityEnabledOnPackage(String packageLookup, Boolean securityEnabled);

    void resetReverseEngineerMark(String parentLookup);

    Integer searchCountByDomain(String terms, List<String> domainLookupPrefixes);

    List<EntityModel> searchByDomain(String terms, List<String> domainLookupPrefixes, Paging paging);

}
