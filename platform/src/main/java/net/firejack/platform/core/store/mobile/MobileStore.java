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

package net.firejack.platform.core.store.mobile;


import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.store.registry.INavigationElementStore;
import net.firejack.platform.service.content.broker.resource.text.ReadTextResourceByLookupBroker;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MobileStore extends BaseStore<BaseEntityModel, Long> implements IMobileStore {

	@Autowired
	private INavigationElementStore elementStore;
	@Autowired
	private ReadTextResourceByLookupBroker readTextResourceByLookupBroker;

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> loadMenu(String lookup) {
		List<Object[]> navigation = findByQuery(null, null, "Mobile.loadMenuTree", "lookup", lookup);
		List<Object[]> resource = findByQuery(null, null, "Mobile.loadMenuResource", "lookup", lookup);

		for (Object[] res : resource) {
			for (Object[] nav : navigation) {
				String resourceLookup = DiffUtils.lookup((String) nav[2], "Menu Name");
				if (resourceLookup.equals(res[0])){
					nav[3] = res[1];
					break;
				}
			}
		}
		return navigation;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RegistryNodeModel> loadMenuTemp(String lookup) {
		Criteria criteria = getSession().createCriteria(RegistryNodeModel.class);
		criteria.add(Restrictions.like("lookup", lookup + "%"));
        criteria.add(Restrictions.in("class", new String[]{"PKG", "DOM", "ENT", "RPT"}));
        return criteria.list();
	}
}
