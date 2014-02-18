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
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving the activities by process
 */
@TrackDetails
@Component("readActivitiesByProcessBroker")
public class ReadActivitiesByProcessBroker extends ListBroker<ActivityModel, Activity, SimpleIdentifier<Long>> {

    @Autowired
    private IActivityStore store;

    /**
     * Invokes data access layer in order to find teh activities by process ID
     * @param simpleIdentifierServiceRequest servcie request containing ID of the process to find teh activities by
     * @return list of found activities
     * @throws BusinessFunctionException
     */
    @Override
    protected List<ActivityModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> simpleIdentifierServiceRequest) throws BusinessFunctionException {
        return store.findByProcessId(simpleIdentifierServiceRequest.getData().getIdentifier(), getFilter());
    }

}
