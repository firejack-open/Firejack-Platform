package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.AbstractResource;
import net.firejack.platform.api.content.domain.FieldResource;
import net.firejack.platform.api.content.domain.Resource;
import net.firejack.platform.api.content.domain.TextResource;
import net.firejack.platform.api.registry.field.Field;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.FieldContainerRegistryNode;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IFieldContainerStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
@Component("ReadFieldWithResourcesListBroker")
public class ReadFieldWithResourcesListBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<FieldResource>> {

	public static final String FIELDS = "fields";

	@Autowired
	@Qualifier("fieldableStore")
	private IFieldContainerStore<EntityModel> fieldableStore;

	@Autowired
	@Qualifier("folderStore")
	private IRegistryNodeStore<FolderModel> folderStore;

	@Autowired
	@Qualifier("resourceStore")
	private IResourceStore<ResourceModel> resourceStore;

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel<ResourceModel>> resourceVersionStore;

	@Override
	protected ServiceResponse<FieldResource> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long registryNodeId = (Long) request.getData().get("id");
		String country = (String) request.getData().get("country");
		Cultures culture = Cultures.findByCountry(country);

		RegistryNodeModel registryNode = fieldableStore.findById(registryNodeId);

		List<FieldResource> fieldResourceVOs = new ArrayList<FieldResource>();

		if (registryNode != null) {
			List<FieldModel> fields = ((FieldContainerRegistryNode) registryNode).getFields();

			FolderModel fieldsFolder = folderStore.findByLookup(registryNode.getLookup() + "." + FIELDS);

			List<ResourceModel> resources = null;
			if (fieldsFolder != null) {
				resources = resourceStore.findChildrenByParentIdAndTypes(fieldsFolder.getId(), null, TextResourceModel.class);
			}

			for (FieldModel field : fields) {
				Field fieldVO = factory.convertTo(Field.class, field);
				FieldResource fieldResourceVO = new FieldResource();
				fieldResourceVO.setField(fieldVO);
				if (resources != null) {
					for (ResourceModel resource : resources) {
						if (StringUtils.normalize(field.getName()).equals(StringUtils.normalize(resource.getName()))) {
							AbstractResourceVersionModel<ResourceModel> resourceVersion = resourceVersionStore.findLastVersionByResourceIdCulture(resource.getId(), culture);
							resource.setResourceVersion(resourceVersion);
							AbstractResource resourceVO;
							if (resource.getType() == RegistryNodeType.TEXT_RESOURCE) {
								resourceVO = factory.convertTo(TextResource.class, resource);
							} else {
								resourceVO = factory.convertTo(Resource.class, resource);
							}
							fieldResourceVO.setResource(resourceVO);
							break;
						}
					}
				}
				fieldResourceVOs.add(fieldResourceVO);
			}
		}

		return new ServiceResponse<FieldResource>(fieldResourceVOs, "Load successfully.", true);
	}
}
