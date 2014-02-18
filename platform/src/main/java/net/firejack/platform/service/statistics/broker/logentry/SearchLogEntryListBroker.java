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
import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.statistics.LogEntryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.statistics.ILogEntryStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * Class encapsulates searching the log entries functionality
 */
@TrackDetails
@Component("searchLogEntryListBrokerEx")
public class SearchLogEntryListBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<LogEntry>> {

    public static final String PARAM_LOOKUP = "lookup";
    public static final String PARAM_TERM = "term";
    public static final String PARAM_START_DATE = "startDate";
    public static final String PARAM_END_DATE = "endDate";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_SORT_COLUMN = "sortColumn";
    public static final String PARAM_SORT_DIRECTION = "sortDirection";
    public static final String PARAM_LOG_ENTRY_TYPE = "logEntryType";

    @Autowired
    @Qualifier("logEntryStore")
    private ILogEntryStore logEntryStore;

    /**
     * Invokes data access layer in order to search the log tracks by search term and time period
     *
     * @param request - the request passed to the business function with all data required
     * @return information about the operation success, number and list of found log tracks
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<LogEntry> perform(ServiceRequest<NamedValues<Object>> request)
		    throws Exception {
        String term = (String) request.getData().get(PARAM_TERM);
        Date startDate = (Date) request.getData().get(PARAM_START_DATE);
        Date endDate = (Date) request.getData().get(PARAM_END_DATE);
        Integer offset = (Integer) request.getData().get(PARAM_OFFSET);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);
        String sortColumn = (String) request.getData().get(PARAM_SORT_COLUMN);
        String sortDirection = (String) request.getData().get(PARAM_SORT_DIRECTION);
        String registryNodeLookup = (String) request.getData().get(PARAM_LOOKUP);
        LogEntryType logEntryType = (LogEntryType) request.getData().get(PARAM_LOG_ENTRY_TYPE);

        List<LogEntryModel> logEntries = logEntryStore.findAllByTermAndDates(
                offset, limit, term, registryNodeLookup, startDate, endDate, sortColumn, sortDirection, logEntryType);
        long totalCount = logEntryStore.countAllByTermAndDates(term, registryNodeLookup, startDate, endDate, logEntryType);
        List<LogEntry> dtoList = factory.convertTo(LogEntry.class, logEntries);
        ServiceResponse<LogEntry> response = new ServiceResponse<LogEntry>(dtoList);
        response.setTotal((new Long(totalCount)).intValue());
        return response;
    }

}