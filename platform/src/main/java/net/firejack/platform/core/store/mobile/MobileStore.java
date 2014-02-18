package net.firejack.platform.core.store.mobile;
/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
