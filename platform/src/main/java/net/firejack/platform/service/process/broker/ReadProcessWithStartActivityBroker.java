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
import net.firejack.platform.api.process.domain.ActivityOrder;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.process.ActivityFieldModel;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Class encapsulates the functionality of retrieving a case
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readProcessWithStartActivityBroker")
public class ReadProcessWithStartActivityBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Process>> {

    @Autowired
    private IProcessStore processStore;
    @Autowired
    private IActivityStore activityStore;

    @Override
    protected ServiceResponse<Process> perform(ServiceRequest<NamedValues> request) throws Exception {
        String processLookup = (String) request.getData().get("processLookup");

        ServiceResponse<Process> response;

        ProcessModel processModel = processStore.findByLookup(processLookup);
        if (processModel != null) {
            ActivityModel startActivityModel = null;
            List<ActivityModel> activityModels = activityStore.findByProcessId(processModel.getId(), null);
            for (ActivityModel activityModel : activityModels) {
                if (ActivityOrder.START.equals(activityModel.getActivityOrder())) {
                    startActivityModel = activityModel;
                    break;
                }
            }
            if (startActivityModel != null) {
                for (ActivityFieldModel activityFieldModel : startActivityModel.getFields()) {
                    activityFieldModel.setActivity(null);
                    FieldModel field = activityFieldModel.getField();
                    String formattedFieldName = Utils.fieldModelFormatting(field.getName());
                    field.setName(formattedFieldName);
                }
                Activity startActivity = factory.convertTo(Activity.class, startActivityModel);

                Process process = factory.convertTo(Process.class, processModel);
                process.setExplanations(null);
                process.setEntity(null);
                process.setProcessFields(null);
                process.setStatuses(null);
                process.setParameters(null);
                process.setActivities(Arrays.asList(null, startActivity));

                response = new ServiceResponse<Process>(process, "Start Activity has been found successfully", true);
            } else {
                response = new ServiceResponse<Process>("Could not find Start Activity for Process: " + processLookup, false);
            }
        } else {
            response = new ServiceResponse<Process>("Could not find Process by: " + processLookup, false);
        }
        return response;
    }
}
