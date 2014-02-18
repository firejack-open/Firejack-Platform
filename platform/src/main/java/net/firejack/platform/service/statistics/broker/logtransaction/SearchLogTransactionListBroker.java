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

package net.firejack.platform.service.statistics.broker.logtransaction;

import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.statistics.LogTransactionModel;
import net.firejack.platform.core.model.statistics.MetricGroupLevel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.statistics.ILogTransactionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Class encapsulates searching the log entries functionality
 */
@TrackDetails
@Component("searchLogTransactionListBrokerEx")
public class SearchLogTransactionListBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<LogTransaction>> {

    public static final String PARAM_LOOKUP = "lookup";
    public static final String PARAM_TERM = "term";
    public static final String PARAM_LEVEL = "level";
    public static final String PARAM_START_DATE = "startDate";
    public static final String PARAM_END_DATE = "endDate";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_SORT_COLUMN = "sortColumn";
    public static final String PARAM_SORT_DIRECTION = "sortDirection";

    @Autowired
    @Qualifier("logTransactionStore")
    private ILogTransactionStore logTransactionStore;

    /**
     * Invokes data access layer in order to search the log tracks by search term and time period
     *
     * @param request - the request passed to the business function with all data required
     * @return information about the operation success, number and list of found log tracks
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<LogTransaction> perform(ServiceRequest<NamedValues<Object>> request)
		    throws Exception {
        String term = (String) request.getData().get(PARAM_TERM);
        Date startDate = (Date) request.getData().get(PARAM_START_DATE);
        Date endDate = (Date) request.getData().get(PARAM_END_DATE);
        Integer offset = (Integer) request.getData().get(PARAM_OFFSET);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);
        String sortColumn = (String) request.getData().get(PARAM_SORT_COLUMN);
        String sortDirection = (String) request.getData().get(PARAM_SORT_DIRECTION);
        String lookup = (String) request.getData().get(PARAM_LOOKUP);
        MetricGroupLevel level = (MetricGroupLevel) request.getData().get(PARAM_LEVEL);

        ServiceResponse<LogTransaction> response;
        if (level == null) {
            response = new ServiceResponse<LogTransaction>("'level' parameter of type MetricGroupLevel has not set.", false);
        } else {
            long totalCount;
            List<LogTransactionModel> logTransactionModels;
            if (level.equals(MetricGroupLevel.HOUR)) {
                logTransactionModels = logTransactionStore.findAllByTermAndDates(offset, limit, term, lookup, startDate, endDate, sortColumn, sortDirection);
                totalCount = logTransactionStore.countAllByTermAndDates(term, lookup, startDate, endDate);
            } else {
                logTransactionModels = logTransactionStore.findAggregatedByTermAndDates(
                        offset, limit, term, lookup, startDate, endDate, sortColumn, sortDirection, level);
                for (LogTransactionModel logTransactionModel : logTransactionModels) {
                    switch (level) {
                        case DAY:
                            logTransactionModel.setStartTime(DateUtils.truncate(
                                    new Date(logTransactionModel.getStartTime()), Calendar.DAY_OF_MONTH).getTime());
                            logTransactionModel.setEndTime(net.firejack.platform.core.utils.DateUtils.ceilDateToDay(
		                            new Date(logTransactionModel.getEndTime())).getTime());
                            break;
                        case WEEK:
                            logTransactionModel.setStartTime(net.firejack.platform.core.utils.DateUtils.truncateDateToWeek(
		                            new Date(logTransactionModel.getStartTime())).getTime());
                            logTransactionModel.setEndTime(net.firejack.platform.core.utils.DateUtils.ceilDateToWeek(
		                            new Date(logTransactionModel.getEndTime())).getTime());
                            break;
                        case MONTH:
                            logTransactionModel.setStartTime(DateUtils.truncate(
                                    new Date(logTransactionModel.getStartTime()), Calendar.MONTH).getTime());
                            logTransactionModel.setEndTime(net.firejack.platform.core.utils.DateUtils.ceilDateToMonth(
		                            new Date(logTransactionModel.getEndTime())).getTime());
                            break;
                    }
                }
                totalCount = logTransactionStore.countAggregatedByTermAndDates(term, lookup, startDate, endDate, level);
            }
            List<LogTransaction> dtoList = factory.convertTo(LogTransaction.class, logTransactionModels);
            response = new ServiceResponse<LogTransaction>(dtoList, "Returned list of log transactions", true);
            response.setTotal((new Long(totalCount)).intValue());
        }
        return response;
    }

}