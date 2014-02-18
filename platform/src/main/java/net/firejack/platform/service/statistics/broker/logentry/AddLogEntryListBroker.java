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

package net.firejack.platform.service.statistics.broker.logentry;

import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.statistics.LogEntryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.statistics.ILogEntryStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@Component("addLogEntryListBrokerEx")
public class AddLogEntryListBroker extends ServiceBroker<ServiceRequest<LogEntry>, ServiceResponse> {

    @Autowired
    @Qualifier("logEntryStore")
    private ILogEntryStore entryStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<LogEntry> request) throws Exception {
        ServiceResponse response;
        List<LogEntry> logEntries = request.getDataList();
        if (logEntries != null) {
            try {
                List<LogEntryModel> modelList = factory.convertFrom(LogEntryModel.class, logEntries);
                if (!modelList.isEmpty()) {
                    entryStore.saveOrUpdateAll(modelList);
                }
                response = new ServiceResponse("Statistic's bunch was saved successfully.", true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse(e.getMessage(), false);
            }
        } else {
            response = new ServiceResponse("Wrong input data.", false);
        }
        return response;
    }

}