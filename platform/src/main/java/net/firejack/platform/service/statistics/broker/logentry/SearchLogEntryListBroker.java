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