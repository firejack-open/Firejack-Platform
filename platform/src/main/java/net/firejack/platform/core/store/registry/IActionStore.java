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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.RESTMethod;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.IEntity;
import net.firejack.platform.core.utils.documentation.DocumentationEntryType;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface IActionStore extends IRegistryNodeStore<ActionModel>, IResourceAccessFieldsStore<ActionModel> {

    /**
     * @param filter
     * @return
     */
    List<ActionModel> findAllWithFilterWithoutFields(SpecifiedIdsFilter<Long> filter);

    /**
     * @return
     */
    List<ActionModel> findAllWithPermissions();

    /**
     * @return
     */
    Map<String, List<ActionModel>> findAllWithPermissionsByPackage();

    /**
     * @param baseLookup
     * @return
     */
    List<ActionModel> findAllWithPermissions(String baseLookup);

    /**
     * @param lookupPrefix
     * @param name
     * @return
     */
    List<ActionModel> findAllByLikeLookupAndName(String lookupPrefix, String name);

    /**
     * @param urlPath
     * @param protocol
     * @return
     */
    ActionModel findByUrlPath(String urlPath, EntityProtocol protocol);

    /**
     * @param action
     * @param examplesToCreate
     */
    void saveForGenerator(ActionModel action, Set<DocumentationEntryType> examplesToCreate);

    /**
     * @param action
     * @param requiredToRegenerateExamples
     */
    void saveWithGeneratingExamples(ActionModel action, boolean requiredToRegenerateExamples);

    /**
     * @param entity
     * @param restMethod
     * @return
     */
    ActionModel createWithPermissionByEntity(IEntity entity, RESTMethod restMethod);

	void updateActionPath(ActionModel action);

}