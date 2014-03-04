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

package net.firejack.platform.service.mobile.broker;

import net.firejack.platform.api.mobile.domain.MenuItem;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.mobile.IMobileStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static net.firejack.platform.core.model.registry.RegistryNodeType.DOMAIN;

@TrackDetails
@Component
public class LoadMenuBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<MenuItem>> {

	@Autowired
	private IMobileStore mobileStore;

	@Override
	protected ServiceResponse<MenuItem> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
		String lookup = request.getData().getIdentifier();

		//List<Object[]> navigation = mobileStore.loadMenu(lookup);
		//List<MenuItem> items = buildTree(navigation);

		List<RegistryNodeModel> registryNodeModels = mobileStore.loadMenuTemp(lookup);
		List<MenuItem> items = buildTree(registryNodeModels);

		return new ServiceResponse(items, "Tree Menu.", true);
	}

	/*private List<MenuItem> buildTree(List<Object[]> navigation) {
		Map<Long, MenuItem> map = new LinkedHashMap<Long, MenuItem>();
		Map<String, List<String>> permissions = new HashMap<String, List<String>>();

		for (Object[] nav : navigation) {
			Long id = (Long) nav[0];
			String entity = (String) nav[3];
			List permission = nav[4] != null ? Arrays.asList(((String) nav[4]).split(",")) : Collections.emptyList();
			MenuItem item = map.get(id);
			if (item == null) {
				item = new MenuItem((String) nav[2], entity);
				map.put(id, item);
			}
			permissions.put(entity, permission);
			buildTree(id, item, navigation, map, permissions);
		}


		Collection<MenuItem> items = map.values();
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>(items);
		for (MenuItem menuItem : items) {
			List<MenuItem> child = menuItem.getItems();
			if (child != null) {
				menuItems.removeAll(child);
			}
		}

		OpenFlamePrincipal principal = OPFContext.getContext().getPrincipal();
		checkPermission(items, permissions, principal);

		return menuItems;
	}

	public void buildTree(Long id, MenuItem parent, List<Object[]> navigation, Map<Long, MenuItem> map, Map<String, List<String>> permissions) {
		for (Object[] nav : navigation) {
			Long childId = (Long) nav[0];
			Long parentId = (Long) nav[1];
			String entity = (String) nav[3];
			List permission = nav[4] != null ? Arrays.asList(((String) nav[4]).split(",")) : Collections.emptyList();
			if (id.equals(parentId)) {
				MenuItem child = map.get(childId);
				if (child == null) {
					child = new MenuItem((String) nav[2], entity);
					map.put(childId, child);
				}
				List<MenuItem> items = parent.getItems();
				if (items == null) {
					items = new ArrayList<MenuItem>();
					parent.setItems(items);
				}
				if (!items.contains(child)) {
					items.add(child);
				}
				permissions.put(entity, permission);
				buildTree(childId, child, navigation, map, permissions);
			}
		}
	}
*/
	private void checkPermission(Collection<MenuItem> menu, Map<String, List<String>> permissions, OpenFlamePrincipal principal) {
		if (menu != null) {
			for (Iterator<MenuItem> iterator = menu.iterator(); iterator.hasNext(); ) {
				MenuItem item = iterator.next();
				List<String> list = permissions.get(item.getEntity());
				if (principal.checkUserPermission(list)) {
					checkPermission(item.getItems(), permissions, principal);
				} else {
					iterator.remove();
				}
			}
		}
	}

	private List<MenuItem> buildTree(List<RegistryNodeModel> registryNodeModels){
		Map<Long, RegistryNodeModel> temp = new LinkedHashMap<Long, RegistryNodeModel>();
		List<MenuItem> items = new ArrayList<MenuItem>();

		RegistryNodeModel root = null;
		for (RegistryNodeModel model : registryNodeModels) {
			if (model instanceof PackageModel) {
				root = model;
			}
			temp.put(model.getId(),model);
		}

		if (root != null) {
			construct(root.getId(),items, temp);
		}

		return items;
	}

	private void construct(Long parentId, List<MenuItem> items, Map<Long, RegistryNodeModel> temp) {
		for (Map.Entry<Long, RegistryNodeModel> entry : temp.entrySet()) {
			Long id = entry.getKey();
			RegistryNodeModel model = entry.getValue();
			if (parentId.equals(model.getParent().getId())) {
				MenuItem item = new MenuItem(model.getName(), model.getLookup());
                item.setType(StringUtils.capitalize(model.getType().name().toLowerCase()));
				items.add(item);

				if (model.getType() == DOMAIN) {
					List<MenuItem> list = new ArrayList<MenuItem>();
					construct(id, list, temp);
					item.setItems(list);
				} else {
					construct(id, items, temp);
				}
			}
		}
	}
}
