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

import net.firejack.platform.core.config.meta.element.schedule.ScheduleElement;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.registry.IActionStore;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class ScheduleElementFactory extends PackageDescriptorConfigElementFactory<ScheduleModel, ScheduleElement> {

    @Autowired
    @Qualifier("actionStore")
    private IActionStore actionStore;

    public ScheduleElementFactory() {
        setElementClass(ScheduleElement.class);
        setEntityClass(ScheduleModel.class);
    }

    @Override
    protected void initDescriptorElementSpecific(ScheduleElement scheduleElement, ScheduleModel schedule) {
        super.initDescriptorElementSpecific(scheduleElement, schedule);
        if (Hibernate.isInitialized(schedule.getAction())) {
            scheduleElement.setActionRef(schedule.getAction().getLookup());
            scheduleElement.setPath(null);
        }
        scheduleElement.setName(schedule.getName());
        scheduleElement.setPath(schedule.getPath());
        scheduleElement.setDescription(schedule.getDescription());
        scheduleElement.setCronExpression(schedule.getCronExpression());
        scheduleElement.setEmailFailure(schedule.getEmailFailure());
        scheduleElement.setActive(schedule.getActive());
    }

    @Override
    protected void initEntitySpecific(ScheduleModel schedule, ScheduleElement scheduleElement) {
        super.initEntitySpecific(schedule, scheduleElement);
        ActionModel action = actionStore.findByLookup(scheduleElement.getActionRef());
        if (action == null) {
            throw new BusinessFunctionException(
                    "No such action found by lookup = '" + scheduleElement.getActionRef() + "'");
        }
        schedule.setAction(action);

        schedule.setName(scheduleElement.getName());
        schedule.setPath(scheduleElement.getPath());
        schedule.setDescription(scheduleElement.getDescription());
        schedule.setCronExpression(scheduleElement.getCronExpression());
        schedule.setEmailFailure(scheduleElement.getEmailFailure());
        schedule.setActive(scheduleElement.getActive());

        RegistryNodeModel parent = registryNodeStore.findByLookup(schedule.getPath());
        schedule.setParent(parent);
    }

}