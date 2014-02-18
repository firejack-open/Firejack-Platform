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

package net.firejack.platform.service.statistics.broker.metrics;

import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.statistics.AggregatedMetricsEntryModel;
import net.firejack.platform.core.model.statistics.MetricGroupLevel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.statistics.IMetricsEntryStore;
import net.firejack.platform.core.utils.DateUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Class encapsulates searching the metric tracks functionality
 */
@TrackDetails
@Component("searchMetricsListBrokerEx")
public class SearchMetricsListBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<MetricsEntry>> {

    public static final String PARAM_LOOKUP = "lookup";
    public static final String PARAM_TERM = "term";
    public static final String PARAM_START_DATE = "startDate";
    public static final String PARAM_END_DATE = "endDate";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_LEVEL = "level";
    public static final String PARAM_SORT_COLUMN = "sortColumn";
    public static final String PARAM_SORT_DIRECTION = "sortDirection";
    public static final String PARAM_LOG_ENTRY_TYPE = "logEntryType";

    @Autowired
    @Qualifier("metricsEntryStore")
    private IMetricsEntryStore metricsEntryStore;

    /**
     * Invokes the data access layer in order to search the metrics by search term and time period
     *
     * @param request - the request passed to the business function with all data required
     * @return information about the operation success, number and list of found metric tracks
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<MetricsEntry> perform(ServiceRequest<NamedValues<Object>> request)
		    throws Exception {
        String term = (String) request.getData().get(PARAM_TERM);
        Date startDate = (Date) request.getData().get(PARAM_START_DATE);
        Date endDate = (Date) request.getData().get(PARAM_END_DATE);
        Integer offset = (Integer) request.getData().get(PARAM_OFFSET);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);
        MetricGroupLevel level = (MetricGroupLevel) request.getData().get(PARAM_LEVEL);

        String lookup = (String) request.getData().get(PARAM_LOOKUP);
        String sortColumn = (String) request.getData().get(PARAM_SORT_COLUMN);
        String sortDirection = (String) request.getData().get(PARAM_SORT_DIRECTION);
        LogEntryType logEntryType = (LogEntryType) request.getData().get(PARAM_LOG_ENTRY_TYPE);

        long totalCount;
        List<AggregatedMetricsEntryModel> metricsEntries;

        ServiceResponse<MetricsEntry> response;
        if (level == null) {
            response = new ServiceResponse<MetricsEntry>("'level' parameter of type MetricGroupLevel has not set.", false);
        } else {
//            if (level.equals(MetricGroupLevel.HOUR)) {
//                metricsEntries = metricsEntryStore.findAllByTermAndDates(offset, limit, term, lookup, startDate, endDate,
//                        sortColumn, sortDirection, logEntryType);
//                totalCount = metricsEntryStore.countAllByTermAndDates(term, lookup, startDate, endDate, logEntryType);
//            } else {
            metricsEntries = metricsEntryStore.findAggregatedByTermAndDates(
                    offset, limit, term, lookup, startDate, endDate, sortColumn, sortDirection, level, logEntryType);
            List<MetricsEntry> dtoList = new LinkedList<MetricsEntry>();
            Calendar cal = Calendar.getInstance();
            for (AggregatedMetricsEntryModel aggregatedMetricsEntry : metricsEntries) {

                setDateRanges(aggregatedMetricsEntry, level, cal);
                MetricsEntry dto = factory.convertTo(MetricsEntry.class, aggregatedMetricsEntry);
                dto.setStartTime(aggregatedMetricsEntry.getStartTime());
                dto.setEndTime(aggregatedMetricsEntry.getEndTime());
                dtoList.add(dto);
            }
            totalCount = metricsEntryStore.countAggregatedByTermAndDates(term, lookup, startDate, endDate, level, logEntryType);
//            }
            //List<MetricsEntry> dtoList = factory.convertTo(MetricsEntry.class, metricsEntries);
            response = new ServiceResponse<MetricsEntry>(dtoList);
            response.setTotal((new Long(totalCount)).intValue());
        }
        return response;
    }

    private void setDateRanges(AggregatedMetricsEntryModel model, MetricGroupLevel level, Calendar cal) {
        if (level != null) {
            if (level == MetricGroupLevel.HOUR) {
                model.setStartTime(DateUtils.truncate(new Date(model.getStartTime()), Calendar.HOUR_OF_DAY).getTime());
            } else if (level == MetricGroupLevel.DAY) {
                model.setStartTime(DateUtils.truncate(new Date(model.getStartTime()), Calendar.DAY_OF_MONTH).getTime());
            } else if (level == MetricGroupLevel.WEEK) {
                model.setStartTime(DateUtils.truncateDateToWeek(new Date(model.getStartTime())).getTime());
            } else if (level == MetricGroupLevel.MONTH) {
                model.setStartTime(DateUtils.truncate(new Date(model.getStartTime()), Calendar.MONTH).getTime());
            }
            cal.setTimeInMillis(model.getEndTime());
            if (isDateRangeValid(cal, level)) {
                cal.add(Calendar.MILLISECOND, -1);
                model.setEndTime(cal.getTimeInMillis());
            } else if (level == MetricGroupLevel.HOUR) {
                model.setEndTime(DateUtils.ceilDateToHour(new Date(model.getEndTime())).getTime());
            } else if (level == MetricGroupLevel.DAY) {
                model.setEndTime(DateUtils.ceilDateToDay(new Date(model.getEndTime())).getTime());
            } else if (level == MetricGroupLevel.WEEK) {
                model.setEndTime(DateUtils.ceilDateToWeek(new Date(model.getEndTime())).getTime());
            } else if (level == MetricGroupLevel.MONTH) {
                model.setEndTime(DateUtils.ceilDateToMonth(new Date(model.getEndTime())).getTime());
            }
        }
    }

    private boolean isDateRangeValid(Calendar cal, MetricGroupLevel level) {
        boolean result;
        if (level == MetricGroupLevel.HOUR) {
            result = cal.get(Calendar.MINUTE) == 0 && cal.get(Calendar.SECOND) == 0 &&
                    cal.get(Calendar.MILLISECOND) == 0;
        } else if (level == MetricGroupLevel.DAY) {
            result = isDateRangeValid(cal, MetricGroupLevel.HOUR) &&
                    cal.get(Calendar.HOUR) == 0 && cal.get(Calendar.AM_PM) == Calendar.PM;
        } else if (level == MetricGroupLevel.WEEK) {
            result = isDateRangeValid(cal, MetricGroupLevel.DAY) && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
        } else if (level == MetricGroupLevel.MONTH) {
            if (isDateRangeValid(cal, MetricGroupLevel.DAY)) {
                int currentMonth = cal.get(Calendar.MONTH);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                result = currentMonth != cal.get(Calendar.DAY_OF_MONTH);
                cal.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                result = false;
            }
        } else {
            result = true;
        }
        return result;
    }

}