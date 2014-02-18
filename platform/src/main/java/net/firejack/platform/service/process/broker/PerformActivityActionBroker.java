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

import net.firejack.platform.api.process.domain.ActivityAction;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IActivityActionStore;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@TrackDetails
public class PerformActivityActionBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<ActivityAction>> {

    public static final String PARAM_ENTITY_ID = "entityId";
    public static final String PARAM_ACTION_ACTIVITY_ID = "actionActivityId";
    public static final String PARAM_TASK = "task";

    @Autowired
    private IActivityActionStore activityActionStore;
    @Autowired
    private ICaseStore caseStore;
    @Autowired
    private IActorStore actorStore;

    @Override
    protected ServiceResponse<ActivityAction> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        Long entityId = (Long) request.getData().get(PARAM_ENTITY_ID);
        Long activityActionId = (Long) request.getData().get(PARAM_ACTION_ACTIVITY_ID);
        Task taskDetails = (Task) request.getData().get(PARAM_TASK);
        ServiceResponse<ActivityAction> response;
        if (entityId == null) {
            response = new ServiceResponse<ActivityAction>("Entity Id information is not specified.", false);
        } else if (activityActionId == null) {
            response = new ServiceResponse<ActivityAction>("Activity Action Id information is not specified.", false);
        } else if (taskDetails == null) {
            response = new ServiceResponse<ActivityAction>("Some task details are not specified.", false);
        } else {
            OPFContext context = OPFContext.getContext();
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                response = new ServiceResponse<ActivityAction>("Guest users are not authorized to perform an activity action.", false);
            } else {
                ActivityActionModel activityActionModel = activityActionStore.findById(activityActionId);
                if (activityActionModel == null) {
                    response = new ServiceResponse<ActivityAction>("Activity Action is not found by specified id.", false);
                } else {
                    Long userId = context.getPrincipal().getUserInfoProvider().getId();
                    String validationMessage = validateIfBelongsToProperActor(userId, activityActionModel);
                    if (StringUtils.isBlank(validationMessage)) {
                        Long assigneeId = taskDetails.getAssignee() == null || taskDetails.getAssignee().getId() == null ?
                                null : taskDetails.getAssignee().getId();
                        CaseModel caseModel = caseStore.moveCaseToActivity(entityId, activityActionId, assigneeId,
                                userId, taskDetails.getDescription());
                        if (caseModel == null) {
                            response = new ServiceResponse<ActivityAction>("Failed to perform activity action using provided input parameters.", false);
                        } else {
                            response = new ServiceResponse<ActivityAction>("Activity Action performed successfully.", true);
                        }
                    } else {
                        response = new ServiceResponse<ActivityAction>(validationMessage, false);
                    }
                }
            }
        }
        return response;
    }


    private String validateIfBelongsToProperActor(Long userId, ActivityActionModel model) {
        boolean userInActorSetByActivity = actorStore.isUserInActorSetByActivity(userId, model.getActivityFrom().getId());
        return userInActorSetByActivity ? null : "Current user is not in actor set to perform the activity.";
    }

}