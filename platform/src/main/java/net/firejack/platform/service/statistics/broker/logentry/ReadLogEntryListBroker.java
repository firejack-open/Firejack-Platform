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
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.statistics.LogEntryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.statistics.ILogEntryStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Class encapsulates functionality of reading the log entry list
 */
@TrackDetails
@Component("readLogEntryListBrokerEx")
public class ReadLogEntryListBroker extends ListBroker<LogEntryModel, LogEntry, LogEntry> {

    @Autowired
    @Qualifier("logEntryStore")
	private ILogEntryStore logEntryStore;

    /**
     * Get list of models according input request
     * @param request request object that intent to provide input parameters
     * @return list of LogEntryModel
     * @throws BusinessFunctionException may throw BusinessFunctionException
     */
    @Override
    protected List<LogEntryModel> getModelList(ServiceRequest<LogEntry> request)
            throws BusinessFunctionException {
        return logEntryStore.findAll();
    }

}