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

package net.firejack.platform.service.directory.broker.profile;

import net.firejack.platform.api.directory.domain.UserProfileFieldGroupTree;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TrackDetails
@Component("readUserProfileFieldGroupsByRegistryNodeBroker")
public class ReadUserProfileFieldGroupsByRegistryNodeBroker
        extends ListBroker<UserProfileFieldGroupModel, UserProfileFieldGroupTree, SimpleIdentifier<Long>> {

	@Autowired
    @Qualifier("userProfileFieldGroupStore")
	protected IUserProfileFieldGroupStore store;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Override
	protected List<UserProfileFieldGroupModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
        Long registryNodeId = request.getData().getIdentifier();
        List<UserProfileFieldGroupModel> userProfileFieldGroupModels = store.findUserProfileFieldGroupsByRegistryNodeId(registryNodeId);
        UserProfileFieldGroupModel general = createGeneral(registryNodeId);
        userProfileFieldGroupModels.add(0, general);
        return userProfileFieldGroupModels;
	}

    private UserProfileFieldGroupModel createGeneral(Long registryNodeId) {
        UserProfileFieldGroupModel general = new UserProfileFieldGroupModel();
        general.setId(0L);
        general.setName("General");
        general.setCreated(new Date());
        general.setChildren(new ArrayList<RegistryNodeModel>());
        RegistryNodeModel registryNodeModel = registryNodeStore.findById(registryNodeId);
        if (registryNodeModel != null) {
            general.setPath(registryNodeModel.getLookup());
            general.setLookup(registryNodeModel.getLookup() + ".general");
            general.setParent(registryNodeModel);
        }
        return general;
    }
}
