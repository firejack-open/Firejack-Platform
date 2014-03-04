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

package net.firejack.platform.service.authority.broker.role;


import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("saveRoleBrokerEx")
public class SaveRoleBroker extends SaveBroker<RoleModel, Role, Role> {

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Override
    protected RoleModel convertToEntity(Role dto) {
        return factory.convertFrom(RoleModel.class, dto);
    }

    @Override
    protected Role convertToModel(RoleModel model) {
        return factory.convertTo(Role.class, model);
    }

    @Override
    protected void save(RoleModel entity) throws Exception {
        roleStore.save(entity);
    }

}