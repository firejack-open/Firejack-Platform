/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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