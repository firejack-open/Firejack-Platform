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

package net.firejack.platform.service.directory.broker.systemuser;

import net.firejack.platform.api.directory.domain.SystemUser;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.user.SystemUserModel;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.store.user.ISystemUserStore;
import net.firejack.platform.service.directory.broker.UpdateBaseUserBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("updateSystemUserBroker")
public class UpdateSystemUserBroker extends UpdateBaseUserBroker<SystemUserModel, SystemUser> {

	@Autowired
    @Qualifier("systemUserStore")
	private ISystemUserStore store;

@Autowired
    @Qualifier("systemStore")
    private ISystemStore systemStore;


    @Override
    protected ISystemUserStore getStore() {
        return store;
    }

    @Override
	protected String getSuccessMessage(boolean isNew) {
		return "System User has updated successfully";
	}

	@Override
	protected SystemUserModel convertToEntity(SystemUser model) {
        SystemUserModel systemUserModel = factory.convertFrom(SystemUserModel.class, model);
        if (systemUserModel.getUserRoles() != null) {
            for (UserRoleModel userRole : systemUserModel.getUserRoles()) {
                userRole.setUser(systemUserModel);
            }
        }
        return systemUserModel;
	}

	@Override
	protected SystemUser convertToModel(SystemUserModel entity) {
		return factory.convertTo(SystemUser.class, entity);
	}

    @Override
	protected void save(SystemUserModel model) throws Exception {
        if (model.getSystem() != null) {
            SystemModel system = systemStore.findById(model.getSystem().getId());
            model.setConsumerKey(system.getServerName());
        }
		getStore().save(model);
	}

}
