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

package net.firejack.platform.service.statistics.endpoint;


import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Class lists services for adding and retrieving log entry and metrics entry information
 */
@Component("statisticsEndpoint")
@Path("statistic")
public class StatisticsEndpoint implements IStatisticsEndpoint {

    // READ-ALL-REQUEST:
	// /rest/tracking/log-entry --> GET method
    /**
     * Service retrieves all log entries
     * @return list of all log tracks
     */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/log-entry")
    @Override
    public ServiceResponse<LogEntry> readAllLogEntries() {
        return OPFEngine.StatisticsService.readAllLogEntries();
    }

    //    SEARCH-ALL-REQUEST:
    //    /rest/tracking/log-entry/search/[term] --> GET method
    /**
     * Service searches the log entries by search parameters
     * @param lookup - lookup to search by
     * @param term - term to search by
     * @param startDate - start date of the period to search within
     * @param startTime - start time of the period to search within
     * @param endDate - end date of the period to search within
     * @param endTime - end time of the period to search within
     * @param offset - item to start listing from
     * @param limit - number of items to be listed
     * @param sortColumn - column to sort by
     * @param sortDirection - sorting direction
     * @return list of found log tracks
     */
    @GET
    @Path("/log-entry/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<LogEntry> searchAllLogEntries(
             @QueryParam("lookup") String lookup, @QueryParam("term") String term,
             @QueryParam("startDate") String startDate, @QueryParam("startTime") String startTime,
             @QueryParam("endDate") String endDate, @QueryParam("endTime") String endTime,
             @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
             @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
             @QueryParam("logEntryType") String logEntryType) {
        return OPFEngine.StatisticsService.searchAllLogEntries(
                lookup, term, startDate, startTime, endDate, endTime, offset,
                limit, sortColumn, sortDirection, logEntryType);
    }

    /**
     * Service adds log entries
     * @param request - log track data
     * @return information about the success of the operation
     */
    @POST
    @Path("/log-entry/add-statistics")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse saveStatisticsBunch( ServiceRequest<LogEntry> request) {
        return OPFEngine.StatisticsService.saveStatisticsBunch(request);
    }

//    SEARCH-ALL-REQUEST:
//    /rest/tracking/log-transaction/search/[term] --> GET method
    /**
     * Service searches the log transactions by search parameters
     * @param lookup - lookup to search by
     * @param term - term to search by
     * @param startDate - start date of the period to search within
     * @param startTime - start time of the period to search within
     * @param endDate - end date of the period to search within
     * @param endTime - end time of the period to search within
     * @param offset - item to start listing from
     * @param limit - number of items to be listed
     * @param sortColumn - column to sort by
     * @param sortDirection - sorting direction
     * @return list of found log tracks
     */
    @GET
    @Path("/log-transaction/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<LogTransaction> searchAllLogTransactions(
             @QueryParam("lookup") String lookup, @QueryParam("term") String term, @QueryParam("level") String level,
             @QueryParam("startDate") String startDate, @QueryParam("startTime") String startTime,
             @QueryParam("endDate") String endDate, @QueryParam("endTime") String endTime,
             @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
             @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection) {
        return OPFEngine.StatisticsService.searchAllLogTransactions(
                lookup, term, level, startDate, startTime, endDate, endTime, offset,
                limit, sortColumn, sortDirection);
    }

    /**
     * Service saves log transaction
     * @param request - log transaction data
     * @return information about the success of the operation
     */
    @POST
    @Path("/log-transaction")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse saveLogTransaction(ServiceRequest<LogTransaction> request) {
        return OPFEngine.StatisticsService.saveLogTransaction(request.getData());
    }

    @GET
    @Path("/metrics/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<MetricsEntry> searchAllMetricsEntries(
            @QueryParam("startDate") String startDate, @QueryParam("startTime") String startTime,
            @QueryParam("endDate") String endDate, @QueryParam("endTime") String endTime,
            @QueryParam("term") String term, @QueryParam("level") String level, @QueryParam("lookup") String lookup,
            @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
            @QueryParam("logEntryType") String logEntryType) {
        return OPFEngine.StatisticsService.findMetricsEntry(
                startDate, startTime, endDate, endTime, term, level, lookup,
                offset, limit, sortColumn, sortDirection, logEntryType);
    }

    @POST
    @Override
    @Path("/metrics/search-by-example")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<MetricsEntry> findMetricsEntryByExample(ServiceRequest<MetricsEntry> request) {
        return OPFEngine.StatisticsService.findMetricsEntryByExample(request);
    }

    @POST
    @Path("/metrics/save-external")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse saveMetricsEntry(ServiceRequest<MetricsEntry> request) {
        return OPFEngine.StatisticsService.saveMetricsEntry(request);
    }

}