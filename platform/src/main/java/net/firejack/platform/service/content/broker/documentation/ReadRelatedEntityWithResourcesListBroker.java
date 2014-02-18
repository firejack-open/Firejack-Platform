package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.*;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.domain.Relationship;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRelationshipStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@TrackDetails
@Component("ReadRelatedEntityWithResourcesListBroker")
public class ReadRelatedEntityWithResourcesListBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<EntityResourceRelationship>> {

	public static final String DESCRIPTION = "description";

	@Autowired
	@Qualifier("relationshipStore")
	private IRelationshipStore relationshipStore;

	@Autowired
	@Qualifier("resourceStore")
	private IResourceStore<ResourceModel> resourceStore;

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel<ResourceModel>> resourceVersionStore;


	@Override
	protected ServiceResponse<EntityResourceRelationship> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long registryNodeId = (Long) request.getData().get("id");
		String country = (String) request.getData().get("country");
		Cultures culture = Cultures.findByCountry(country);

		List<EntityResourceRelationship> entityResourceRelationshipVOs = new ArrayList<EntityResourceRelationship>();

		List<RelationshipModel> relationships = relationshipStore.findRelatedEntitiesByEntityId(registryNodeId, null);

		Set<EntityModel> uniqueEntities = new HashSet<EntityModel>();

		for (RelationshipModel relationship : relationships) {
			EntityModel sourceEntity = relationship.getSourceEntity();
			EntityModel targetEntity = relationship.getTargetEntity();
			EntityModel entity = null;
			if (sourceEntity.getId().equals(registryNodeId)) {
				entity = targetEntity;
			} else if (targetEntity.getId().equals(registryNodeId)) {
				entity = sourceEntity;
			}
			if (entity != null && !uniqueEntities.contains(entity)) {
				uniqueEntities.add(entity);

				entity.setFields(null); // lazy initialized, but we don't need them
				Entity entityVO = factory.convertTo(Entity.class, entity);

				relationship.getSourceEntity().setFields(null); // lazy initialized, but we don't need them
				relationship.getTargetEntity().setFields(null); // lazy initialized, but we don't need them
				Relationship relationshipVO = factory.convertTo(Relationship.class, relationship);

				EntityResourceRelationship entityResourceRelationshipVO = new EntityResourceRelationship();
				entityResourceRelationshipVO.setEntity(entityVO);
				entityResourceRelationshipVO.setRelationship(relationshipVO);

				ResourceModel resource = resourceStore.findByLookup(entity.getLookup() + "." + DESCRIPTION);
				if (resource != null) {
					AbstractResourceVersionModel<ResourceModel> resourceVersion = resourceVersionStore.findLastVersionByResourceIdCulture(resource.getId(), culture);
					resource.setResourceVersion(resourceVersion);
					AbstractResource resourceVO;
					if (resource.getType() == RegistryNodeType.HTML_RESOURCE) {
						resourceVO = factory.convertTo(HtmlResource.class, resource);
					} else if (resource.getType() == RegistryNodeType.TEXT_RESOURCE) {
						resourceVO = factory.convertTo(TextResource.class, resource);
					} else {
						resourceVO = factory.convertTo(Resource.class, resource);
					}
					entityResourceRelationshipVO.setResource(resourceVO);
				}
				entityResourceRelationshipVOs.add(entityResourceRelationshipVO);
			}
		}
		return new ServiceResponse<EntityResourceRelationship>(entityResourceRelationshipVOs, "Load successfully.", true);
	}
}
