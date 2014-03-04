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

import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.ActionParameterModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.model.registry.resource.TextResourceModel;

import java.util.List;

public interface ITextResourceStore extends IResourceStore<TextResourceModel> {

    ResourceModel saveTextResource(RegistryNodeModel registryNode, String name, String text);

    void saveActionParameterDescriptions(
            ActionModel action, List<ActionParameterModel> parametersToProcess, CollectionModel collection);

    void deleteActionParameterDescriptions(List<ActionParameterModel> parametersToProcess);

    /**
     * @param action
     */
//    void createBlazeExample(Action action, Collection collection);

}