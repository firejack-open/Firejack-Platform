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