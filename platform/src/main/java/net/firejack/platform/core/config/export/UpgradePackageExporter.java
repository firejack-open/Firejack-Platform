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

package net.firejack.platform.core.config.export;

import org.springframework.stereotype.Component;

@Component("upgradePackageExporter")
public class UpgradePackageExporter extends DefaultPackageExporter {

//    protected List<IEntityElement> getChildEntities(RegistryNodeModel parent, Map<Long, IEntityElement> cachedEntities) {
//        List<IEntityElement> configuredEntities = new ArrayList<IEntityElement>();
//        List<EntityModel> entityList = entityStore.findEntriesByParentId(parent, null, null);
//        if (entityList != null) {
//            for (EntityModel entity : entityList) {
//                if (!Boolean.TRUE.equals(entity.getReverseEngineer())) {
//                    getChildEntity(cachedEntities, configuredEntities, entity);
//                }
//            }
//        }
//        return configuredEntities;
//    }
//
//    protected List<IRelationshipElement> getRelationshipElements(String packageLookupPrefix) {
//        List<RelationshipModel> relationships = relationshipStore.findAllByLikeLookupPrefix(packageLookupPrefix);
//        List<IRelationshipElement> relationshipElements = new ArrayList<IRelationshipElement>();
//        for (RelationshipModel relationship : relationships) {
//            if (!Boolean.TRUE.equals(relationship.getReverseEngineer())) {
//                getRelationshipElement(relationshipElements, relationship);
//            }
//        }
//        return relationshipElements;
//    }

}
