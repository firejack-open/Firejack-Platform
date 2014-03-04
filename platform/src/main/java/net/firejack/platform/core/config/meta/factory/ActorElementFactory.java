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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.process.ActorElement;
import net.firejack.platform.core.config.meta.element.process.GroupAssignElement;
import net.firejack.platform.core.config.meta.element.process.RoleAssignElement;
import net.firejack.platform.core.config.meta.element.process.UserAssignElement;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.UserActorModel;

import java.util.ArrayList;
import java.util.List;


public class ActorElementFactory extends PackageDescriptorConfigElementFactory<ActorModel, ActorElement> {

    /***/
    public ActorElementFactory() {
        setEntityClass(ActorModel.class);
        setElementClass(ActorElement.class);
    }

    @Override
    protected void initDescriptorElementSpecific(ActorElement actorElement, ActorModel actor) {
        List<UserActorModel> actors = actor.getUserActors();
        List<RoleModel> roles = actor.getRoles();
        List<GroupModel> groups = actor.getGroups();

        if (actors != null) {
            List<UserAssignElement> elements = new ArrayList<UserAssignElement>();
            for (UserActorModel userActor : actors) {
                UserAssignElement element = new UserAssignElement();
                element.setName(userActor.getUser().getUsername());
                elements.add(element);
            }
            actorElement.setUsers(elements);
        }

        if (roles != null) {
            List<RoleAssignElement> elements = new ArrayList<RoleAssignElement>();
            for (RoleModel role : roles) {
                RoleAssignElement element = new RoleAssignElement();
                element.setPath(role.getLookup());
                elements.add(element);
            }
            actorElement.setRoles(elements);
        }

        if (groups != null) {
            List<GroupAssignElement> elements = new ArrayList<GroupAssignElement>();
            for (GroupModel group : groups) {
                GroupAssignElement element = new GroupAssignElement();
                element.setPath(group.getLookup());
                elements.add(element);
            }
            actorElement.setGroups(elements);
        }

        actorElement.setDistributionEmail(actor.getDistributionEmail());

        super.initDescriptorElementSpecific(actorElement, actor);
    }

    @Override
    protected void initEntitySpecific(ActorModel entity, ActorElement descriptorElement) {
        super.initEntitySpecific(entity, descriptorElement);
        entity.setDistributionEmail(descriptorElement.getDistributionEmail());
    }
}