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