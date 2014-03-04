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
