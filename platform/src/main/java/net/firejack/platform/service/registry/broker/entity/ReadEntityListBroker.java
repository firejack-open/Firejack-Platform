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
