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

import net.firejack.platform.api.process.domain.ActivityType;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of searching for the processes
 */
@TrackDetails
@Component("searchProcessListBroker")
public class SearchProcessListBroker extends ListBroker<ProcessModel, Process, NamedValues> {

    public static final String PARAM_TERM = "term";
    public static final String PARAM_HUMAN_PROCESS = "humanProcess";

    @Autowired
    private IProcessStore store;

    /**
     * Invokes data access layer in order to find processes by search term
     * @param namedValuesServiceRequest service request containing the serach term
     * @return list of found processes
     * @throws BusinessFunctionException
     */
    @Override
    protected List<ProcessModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        String term = (String) namedValuesServiceRequest.getData().get(PARAM_TERM);
        Boolean humanProcess = (Boolean) namedValuesServiceRequest.getData().get(PARAM_HUMAN_PROCESS);
        return humanProcess != null && humanProcess ?
                store.findAllBySearchTermAndActivityType(term, ActivityType.HUMAN, "name", "ASC", null) :
                store.findAllBySearchTerm(term, "name", "ASC");
    }
   
}
