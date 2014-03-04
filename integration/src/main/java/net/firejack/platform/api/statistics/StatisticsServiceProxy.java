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

package net.firejack.platform.api.statistics;

import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.model.statistics.MetricGroupLevel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;


public class StatisticsServiceProxy extends AbstractServiceProxy implements IStatisticsService {

	public StatisticsServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
    public String getServiceUrlSuffix() {
        return "/statistic"; //  "/log-entry"  "/metrics"
    }

    @Override
    public ServiceResponse saveMetricsEntry(ServiceRequest<MetricsEntry> request) {
        try {
            return post("/metrics/save-external", request.getData());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<MetricsEntry> findMetricsEntryByExample(ServiceRequest<MetricsEntry> requestWithExample) {
        try {
            return post("/metrics/search-by-example", requestWithExample.getData());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<MetricsEntry>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<MetricsEntry> findMetricsEntry(
            String startDate, String startTime, String endDate, String endTime, String term,
            String level, String lookup, Integer offset, Integer limit,
            String sortColumn, String sortDirection, String logEntryType) {
        MetricGroupLevel groupLevel = MetricGroupLevel.findByName(level);
        ServiceResponse<MetricsEntry> response;
        if (groupLevel == null) {
            response = new ServiceResponse<MetricsEntry>("'level' parameter of type MetricGroupLevel has not set.", false);
        } else {
            try {
                response = get("/metrics/search",
                        "startDate", startDate,
                        "startTime", startTime,
                        "endDate", endDate,
                        "endTime", endTime,
                        "term", term,
                        "level", groupLevel.name(),
                        "lookup", lookup,
                        "offset", offset,
                        "limit", limit,
                        "sort", sortColumn,
                        "dir", sortDirection,
                        "logEntryType", logEntryType);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<MetricsEntry>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<LogEntry> readAllLogEntries() {
        try {
            return get("/log-entry");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<LogEntry>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<LogEntry> searchAllLogEntries(
            String lookup, String term, String startDate, String startTime, String endDate,
            String endTime, Integer offset, Integer limit, String sortColumn, String sortDirection, String logEntryType) {
        try {
            return get("/log-entry/search",
                    "startDate", startDate,
                    "startTime", startTime,
                    "endDate", endDate,
                    "endTime", endTime,
                    "term", term,
                    "lookup", lookup,
                    "offset", offset,
                    "limit", limit,
                    "sort", sortColumn,
                    "dir", sortDirection,
                    "logEntryType", logEntryType);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<LogEntry>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse saveStatisticsBunch(ServiceRequest<LogEntry> request) {
        try {
            return post("/log-entry/add-statistics", request.getDataList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<LogTransaction> searchAllLogTransactions(
            String lookup, String term, String level,
            String startDate, String startTime, String endDate, String endTime,
            Integer offset, Integer limit, String sortColumn, String sortDirection) {
        try {
            return get("/log-transaction/search",
                    "startDate", startDate,
                    "startTime", startTime,
                    "endDate", endDate,
                    "endTime", endTime,
                    "level", level,
                    "term", term,
                    "lookup", lookup,
                    "offset", offset,
                    "limit", limit,
                    "sort", sortColumn,
                    "dir", sortDirection);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<LogTransaction>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse saveLogTransaction(LogTransaction logTransaction) {
        try {
            return post("/log-transaction", logTransaction);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse(e.getMessage(), false);
        }
    }
}