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

import net.firejack.platform.api.process.domain.ActivityType;
import net.firejack.platform.core.config.meta.element.process.ActivityElement;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.StatusModel;


public class ActivityElementFactory extends
        PackageDescriptorConfigElementFactory<ActivityModel, ActivityElement> {

    /***/
    public ActivityElementFactory() {
        setElementClass(ActivityElement.class);
        setEntityClass(ActivityModel.class);
    }

    @Override
    protected void initEntitySpecific(ActivityModel activity, ActivityElement activityElement) {
        super.initEntitySpecific(activity, activityElement);
        activity.setActivityType(ActivityType.findByName(activityElement.getType()));
        activity.setSortPosition(activityElement.getOrder());
        activity.setDescription(activityElement.getDescription());
        activity.setNotify(activityElement.getNotify());
    }

    @Override
    protected void initDescriptorElementSpecific(ActivityElement activityElement, ActivityModel activity) {
        super.initDescriptorElementSpecific(activityElement, activity);
        activityElement.setName(activity.getName());
        activityElement.setType(activity.getActivityType().name());
        activityElement.setOrder(activity.getSortPosition());
        activityElement.setActor(activity.getActor().getLookup());
        activityElement.setNotify(activity.getNotify());
        StatusModel status = activity.getStatus();
        if (status != null)
            activityElement.setStatus(status.getName());
    }
}