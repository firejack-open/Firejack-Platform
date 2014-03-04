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
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRelationshipStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@TrackDetails
public class ReadDirectChildrenTypesBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<Entity>> {

    @Autowired
    private IRelationshipStore relationshipStore;

    @Override
    protected ServiceResponse<Entity> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String typeLookup = request.getData().getIdentifier();
        ServiceResponse<Entity> response;
        if (StringUtils.isBlank(typeLookup)) {
            response = new ServiceResponse<Entity>("Type Lookup is not specified.", false);
        } else {
            Map<String, String> aliases = new HashMap<String, String>();
            aliases.put("sourceEntity", "child");
            aliases.put("targetEntity", "target");
            LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
            restrictions.add(Restrictions.eq("relationshipType", RelationshipType.PARENT_CHILD));
            restrictions.add(Restrictions.eq("target.lookup", typeLookup));

            ProjectionList projections = Projections.projectionList()
                    .add(Projections.property("child.id")).add(Projections.property("child.lookup"));
            List<Object[]> childrenTypesData =
                    relationshipStore.searchWithProjection(restrictions, projections, aliases, null, false);
            List<Entity> childrenTypes = new ArrayList<Entity>(childrenTypesData.size());
            for (Object[] typeData : childrenTypesData) {
                Long entityId = (Long) typeData[0];
                String entityLookup = (String) typeData[1];
                Entity entity = new Entity();
                entity.setId(entityId);
                entity.setLookup(entityLookup);
                childrenTypes.add(entity);
            }
            response = new ServiceResponse<Entity>(childrenTypes, "Success", true);
        }
        return response;
    }

}