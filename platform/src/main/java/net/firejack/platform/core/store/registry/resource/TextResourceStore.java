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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.ActionParameterModel;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TextResourceStore extends ResourceStore<TextResourceModel> implements ITextResourceStore {

	@Autowired
	@Qualifier("collectionStore")
	private ICollectionStore collectionStore;

	@Autowired
	@Qualifier("folderStore")
	private IFolderStore folderStore;

//    @Autowired
//    private MessageResolver messageResolver;

	/***/
	@PostConstruct
	public void init() {
		setClazz(TextResourceModel.class);
	}

	@Override
	@Transactional
	public void saveActionParameterDescriptions(
			ActionModel action, List<ActionParameterModel> parametersToProcess, CollectionModel collection) {
		if (parametersToProcess != null) {
			FolderModel parametersFolder = null;
			for (ActionParameterModel parameter : parametersToProcess) {
				if (StringUtils.isNotBlank(parameter.getDescription())) {
					if (parametersFolder == null) {
						parametersFolder = folderStore.findOrCreateActionParametersFolder(action);
					}
					ResourceModel resource = saveTextResource(
							parametersFolder, parameter.getName(), parameter.getDescription());
					collectionStore.associateCollectionWithReference(collection, resource);
				}
			}
		}
	}

	@Override
	@Transactional
	public void deleteActionParameterDescriptions(List<ActionParameterModel> parametersToProcess) {
		if (parametersToProcess != null && !parametersToProcess.isEmpty()) {
			ActionModel actionModel = parametersToProcess.get(0).getParent();
			StringBuilder sb = new StringBuilder(actionModel.getLookup());
			sb.append('.').append(FolderStore.DEFAULT_DOC_FOLDER_NAME)
					.append('.').append(FolderStore.DEFAULT_ACTION_PARAMETERS_FOLDER_NAME)
					.append('.');
			String lookupPrefix = sb.toString();
			List<TextResourceModel> resourcesToDelete = new ArrayList<TextResourceModel>();
			for (ActionParameterModel parameter : parametersToProcess) {
				TextResourceModel textResourceModel = findByLookup(
						lookupPrefix + StringUtils.normalize(parameter.getName()));
				if (textResourceModel != null) {
					resourcesToDelete.add(textResourceModel);
				}
			}
			if (!resourcesToDelete.isEmpty()) {
				deleteAll(resourcesToDelete);
			}
		}
	}

	@Override
	@Transactional
	public ResourceModel saveTextResource(RegistryNodeModel registryNode, String name, String text) {
		String lookup = registryNode.getLookup() + "." + StringUtils.normalize(name);
		TextResourceModel resource = findByLookup(lookup);
		if (resource == null) {
			resource = new TextResourceModel();
			resource.setName(name);
			resource.setParent(registryNode);
			resource.setLastVersion(1);
			resource.setPublishedVersion(1);

			TextResourceVersionModel resourceVersion = new TextResourceVersionModel();
			resourceVersion.setText(text);
			resource.setResourceVersion(resourceVersion);

			//TODO may be need to create a rest example for all cultures
			resourceVersion.setResource(resource);
			resourceVersion.setCulture(Cultures.AMERICAN);
			resourceVersion.setStatus(ResourceStatus.PUBLISHED);
			resourceVersion.setUpdated(new Date());
			resourceVersion.setVersion(resource.getLastVersion());
			save(resource);
		} else {
			AbstractResourceVersionModel resourceVersion = resourceVersionStore.findByResourceIdCultureAndVersion(
					resource.getId(), Cultures.AMERICAN, resource.getLastVersion());
			((TextResourceVersionModel) resourceVersion).setText(text);
			resourceVersion.setUpdated(new Date());
			resourceVersionCacheService.removeCacheByResourceVersion(resourceVersion);
			resourceVersionStore.saveOrUpdate(resourceVersion);
		}
		return resource;
	}

}
