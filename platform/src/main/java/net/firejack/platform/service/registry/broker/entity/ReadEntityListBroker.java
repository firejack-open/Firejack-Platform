package net.firejack.platform.service.registry.broker.entity;

import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component("readEntityListBroker")
public class ReadEntityListBroker extends ListBroker<EntityModel, Entity, NamedValues> {

	@Autowired
	private IEntityStore store;

	@Override
	protected List<EntityModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		String type = (String) request.getData().get("type");
		Long exceptId = (Long) request.getData().get("exceptId");
		SpecifiedIdsFilter<Long> filter = getFilter();
		if (exceptId != null) {
			List<Long> implementedEntityIds = getImplementedEntityIds(exceptId);
			filter.getUnnecessaryIds().addAll(implementedEntityIds);
		}
        List<EntityModel> entities;
        if ("security-enabled".equalsIgnoreCase(type)) {
            entities = store.getSecurityEnabledEntities();
        } else {
            boolean isStandardEntity = type.matches("(standard|classifier)");
            entities = store.findAllWithFilter(filter, !isStandardEntity);
        }
		/*for (EntityModel entity : entities) {
			entity.setFields(null);
		}*/

		return entities;
	}

	private List<Long> getImplementedEntityIds(Long entityId) {
		List<Object[]> pairIds = store.findAllIdAndExtendedId();
		List<Long> implementedEntityIds = new ArrayList<Long>();
		findImplementedEntityIds(implementedEntityIds, pairIds, entityId);
		return implementedEntityIds;
	}

	private void findImplementedEntityIds(List<Long> implementedEntityIds, List<Object[]> pairIds, Long foundEntityId) {
		implementedEntityIds.add(foundEntityId);
		for (Object[] pairId : pairIds) {
			Long entityId = (Long) pairId[0];
			Long extendedId = (Long) pairId[1];
			if (foundEntityId.equals(extendedId)) {
				findImplementedEntityIds(implementedEntityIds, pairIds, entityId);
			}
		}
	}

}
