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
