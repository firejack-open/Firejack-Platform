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

import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;


public interface IStatisticsService {

    ServiceResponse saveMetricsEntry(ServiceRequest<MetricsEntry> request);

    ServiceResponse<MetricsEntry> findMetricsEntryByExample(ServiceRequest<MetricsEntry> requestWithExample);

    ServiceResponse<MetricsEntry> findMetricsEntry(
            String startDate, String startTime, String endDate, String endTime, String term,
            String level, String lookup, Integer offset, Integer limit,
            String sortColumn, String sortDirection, String logEntryType);

    ServiceResponse<LogEntry> readAllLogEntries();

    ServiceResponse<LogEntry> searchAllLogEntries(
            String lookup, String term, String startDate, String startTime,
            String endDate, String endTime, Integer offset, Integer limit,
            String sortColumn, String sortDirection, String logEntryType);

    ServiceResponse saveStatisticsBunch(ServiceRequest<LogEntry> request);

    ServiceResponse saveLogTransaction(LogTransaction logTransaction);

    ServiceResponse<LogTransaction> searchAllLogTransactions(
            String lookup, String term, String level,
            String startDate, String startTime, String endDate, String endTime,
            Integer offset, Integer limit, String sortColumn, String sortDirection);

}