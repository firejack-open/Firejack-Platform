/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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