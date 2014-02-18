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

package net.firejack.platform.service.statistics;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.statistics.IStatisticsService;
import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.statistics.MetricGroupLevel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.DateUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.service.statistics.broker.logentry.AddLogEntryListBroker;
import net.firejack.platform.service.statistics.broker.logentry.ReadLogEntryListBroker;
import net.firejack.platform.service.statistics.broker.logentry.SearchLogEntryListBroker;
import net.firejack.platform.service.statistics.broker.logtransaction.SaveLogTransactionBroker;
import net.firejack.platform.service.statistics.broker.logtransaction.SearchLogTransactionListBroker;
import net.firejack.platform.service.statistics.broker.metrics.SaveMetricsEntryBroker;
import net.firejack.platform.service.statistics.broker.metrics.SearchMetricsEntryByExampleBroker;
import net.firejack.platform.service.statistics.broker.metrics.SearchMetricsListBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_STATISTICS_SERVICE)
public class StatisticsServiceLocal implements IStatisticsService {

    @Autowired
    @Qualifier("readLogEntryListBrokerEx")
    private ReadLogEntryListBroker readLogEntryListBroker;
    @Autowired
    @Qualifier("searchLogEntryListBrokerEx")
    private SearchLogEntryListBroker searchLogEntryListBroker;
    @Autowired
    @Qualifier("addLogEntryListBrokerEx")
    private AddLogEntryListBroker addLogEntryListBroker;
    @Autowired
    @Qualifier("searchLogTransactionListBrokerEx")
    private SearchLogTransactionListBroker searchLogTransactionListBroker;
    @Autowired
    @Qualifier("saveLogTransactionBroker")
    private SaveLogTransactionBroker saveLogTransactionBroker;
    @Autowired
    @Qualifier("saveMetricsEntryBrokerEx")
    private SaveMetricsEntryBroker saveMetricsEntryBroker;
    @Autowired
    @Qualifier("searchMetricsEntryByExampleBrokerEx")
    private SearchMetricsEntryByExampleBroker searchMetricsEntryByExampleBroker;
    @Autowired
    @Qualifier("searchMetricsListBrokerEx")
    private SearchMetricsListBroker searchMetricsListBroker;

    @Override
    public ServiceResponse saveMetricsEntry(ServiceRequest<MetricsEntry> request) {
        return saveMetricsEntryBroker.execute(request);
    }

    @Override
    public ServiceResponse<MetricsEntry> findMetricsEntryByExample(ServiceRequest<MetricsEntry> requestWithExample) {
        return searchMetricsEntryByExampleBroker.execute(requestWithExample);
    }

    @Override
    public ServiceResponse<MetricsEntry> findMetricsEntry(
            String startDate, String startTime, String endDate, String endTime, String term,
            String level, String lookup, Integer offset, Integer limit,
            String sortColumn, String sortDirection, String logEntryType) {

        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchMetricsListBroker.PARAM_START_DATE, processDateParam(startDate, startTime));
        params.put(SearchMetricsListBroker.PARAM_END_DATE, processDateParam(endDate, endTime));
        params.put(SearchMetricsListBroker.PARAM_TERM, term);
        params.put(SearchMetricsListBroker.PARAM_LOOKUP, lookup);
        params.put(SearchMetricsListBroker.PARAM_LEVEL, MetricGroupLevel.findByName(level));
        params.put(SearchMetricsListBroker.PARAM_LIMIT, limit);
        params.put(SearchMetricsListBroker.PARAM_OFFSET, offset);
        params.put(SearchMetricsListBroker.PARAM_SORT_COLUMN, sortColumn);
        params.put(SearchMetricsListBroker.PARAM_SORT_DIRECTION, sortDirection);
        params.put(SearchMetricsListBroker.PARAM_LOG_ENTRY_TYPE, LogEntryType.findByName(logEntryType));

        return searchMetricsListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<LogEntry> readAllLogEntries() {
        return readLogEntryListBroker.execute(new ServiceRequest<LogEntry>());
    }

    @Override
    public ServiceResponse<LogEntry> searchAllLogEntries(
            String lookup, String term, String startDate, String startTime, String endDate,
            String endTime, Integer offset, Integer limit, String sortColumn, String sortDirection, String logEntryType) {

        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchLogEntryListBroker.PARAM_START_DATE, processDateParam(startDate, startTime));
        params.put(SearchLogEntryListBroker.PARAM_END_DATE, processDateParam(endDate, endTime));
        params.put(SearchLogEntryListBroker.PARAM_TERM, term);
        params.put(SearchLogEntryListBroker.PARAM_LOOKUP, lookup);
        params.put(SearchLogEntryListBroker.PARAM_LIMIT, limit);
        params.put(SearchLogEntryListBroker.PARAM_OFFSET, offset);
        params.put(SearchLogEntryListBroker.PARAM_SORT_COLUMN, sortColumn);
        params.put(SearchLogEntryListBroker.PARAM_SORT_DIRECTION, sortDirection);
        params.put(SearchLogEntryListBroker.PARAM_LOG_ENTRY_TYPE, LogEntryType.findByName(logEntryType));

        return searchLogEntryListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse saveStatisticsBunch(ServiceRequest<LogEntry> request) {
        return addLogEntryListBroker.execute(request);
    }

    private Date processDateParam(String dateParam, String timeParam) {
        Date date = null;
        if (StringUtils.isNotBlank(dateParam)) {
            date = DateUtils.parseDateTime(dateParam, timeParam);
        }
        return date;
    }

    @Override
    public ServiceResponse saveLogTransaction(LogTransaction logTransaction) {
        return saveLogTransactionBroker.execute(new ServiceRequest<LogTransaction>(logTransaction));
    }

    @Override
    public ServiceResponse<LogTransaction> searchAllLogTransactions(
            String lookup, String term, String level,
            String startDate, String startTime, String endDate, String endTime,
            Integer offset, Integer limit, String sortColumn, String sortDirection) {

        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchLogTransactionListBroker.PARAM_START_DATE, processDateParam(startDate, startTime));
        params.put(SearchLogTransactionListBroker.PARAM_END_DATE, processDateParam(endDate, endTime));
        params.put(SearchLogTransactionListBroker.PARAM_TERM, term);
        params.put(SearchLogTransactionListBroker.PARAM_LEVEL, MetricGroupLevel.findByName(level));
        params.put(SearchLogTransactionListBroker.PARAM_LOOKUP, lookup);
        params.put(SearchLogTransactionListBroker.PARAM_LIMIT, limit);
        params.put(SearchLogTransactionListBroker.PARAM_OFFSET, offset);
        params.put(SearchLogTransactionListBroker.PARAM_SORT_COLUMN, sortColumn);
        params.put(SearchLogTransactionListBroker.PARAM_SORT_DIRECTION, sortDirection);

        return searchLogTransactionListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

}