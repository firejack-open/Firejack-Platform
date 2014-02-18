/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Activity;
import net.firejack.platform.api.process.domain.ActivityAction;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.domain.Status;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.process.ActivityActionModel;
import net.firejack.platform.core.model.registry.process.ActivityFieldModel;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IActivityActionStore;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.core.store.process.IStatusStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class encapsulates the functionality of retrieving a case
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readProcessWithActionActivityBroker")
public class ReadProcessWithActionActivityBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Process>> {

    @Autowired
    private IProcessStore processStore;
    @Autowired
    private IActivityStore activityStore;
    @Autowired
    private IStatusStore statusStore;
    @Autowired
    private IActivityActionStore activityActionStore;

    @Override
    protected ServiceResponse<Process> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long activityActionId = (Long) request.getData().get("activityActionId");

        ServiceResponse<Process> response;

        ActivityActionModel activityActionModel = activityActionStore.findById(activityActionId);
        if (activityActionModel != null) {
            Long toActivityId = activityActionModel.getActivityTo().getId();
            Long fromActivityId = activityActionModel.getActivityFrom().getId();

            ActivityModel fromActivityModel = activityStore.findWithFieldsById(fromActivityId, null);
            ActivityModel toActivityModel = activityStore.findWithFieldsById(toActivityId, null);
            if (toActivityModel != null && fromActivityModel != null) {
                formatFieldNames(toActivityModel);
                Activity toActivity = factory.convertTo(Activity.class, toActivityModel);

                formatFieldNames(fromActivityModel);
                Activity fromActivity = factory.convertTo(Activity.class, fromActivityModel);

                if (activityActionModel.getStatus() != null && activityActionModel.getStatus().getId() != null) {
                    StatusModel statusModel = statusStore.findById(activityActionModel.getStatus().getId());
                    Status status = factory.convertTo(Status.class, statusModel);
                    fromActivity.setStatus(status);
                }

                ActivityAction activityAction = factory.convertTo(ActivityAction.class, activityActionModel);
                List<ActivityAction> activityActions = new ArrayList<ActivityAction>();
                activityActions.add(activityAction);
                toActivity.setActivityActions(activityActions);

                Process process = factory.convertTo(Process.class, toActivityModel.getParent());
                process.setExplanations(null);
                process.setEntity(null);
                process.setProcessFields(null);
                process.setStatuses(null);
                process.setParameters(null);
                process.setActivities(Arrays.asList(fromActivity, toActivity));

                response = new ServiceResponse<Process>(process, "To Activity has been found successfully", true);
            } else {
                response = new ServiceResponse<Process>("Could not find To Activity by ID: " + toActivityId, false);
            }

        } else {
            response = new ServiceResponse<Process>("Could not find Activity Action by ID: " + activityActionId, false);
        }
        return response;
    }

    private void formatFieldNames(ActivityModel toActivityModel) {
        for (ActivityFieldModel activityFieldModel : toActivityModel.getFields()) {
            activityFieldModel.setActivity(null);
            FieldModel field = activityFieldModel.getField();
            if (field != null) {
                String formattedFieldName = Utils.fieldModelFormatting(field.getName());
                field.setName(formattedFieldName);
            }
        }
    }
}
