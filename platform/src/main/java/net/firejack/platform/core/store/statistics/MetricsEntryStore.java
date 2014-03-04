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

package net.firejack.platform.core.store.statistics;

import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.core.model.registry.statistics.AggregatedMetricsEntryModel;
import net.firejack.platform.core.model.registry.statistics.MetricsEntryModel;
import net.firejack.platform.core.model.statistics.MetricGroupLevel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
@SuppressWarnings("unused")
public class MetricsEntryStore extends BaseStore<MetricsEntryModel, Long> implements IMetricsEntryStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(MetricsEntryModel.class);
    }

    @Override
    @Transactional
    public void saveOrUpdate(MetricsEntryModel entity) {
        super.saveOrUpdate(entity);
    }

    @Override
    public List<AggregatedMetricsEntryModel> findAggregatedByTermAndDates(Integer offset, Integer limit,
            String term, String lookup, Date startDate, Date endDate,
            String sortColumn, String sortDirection, MetricGroupLevel level, LogEntryType logEntryType) {

        ProjectionList projectionList = Projections.projectionList();
        projectionList
                .add(Projections.min("hourPeriod"), "startTime")
                .add(Projections.max("hourPeriod"), "endTime")
                .add(Projections.groupProperty("lookup"), "lookup")
                .add(Projections.groupProperty("systemAccountName"), "systemAccountName")
                .add(Projections.groupProperty("username"), "username")
                .add(Projections.sum("numberOfInvocations"), "numberOfInvocations")
                .add(Projections.avg("averageExecutionTime"), "averageExecutionTime")
                .add(Projections.min("minResponseTime"), "minResponseTime")
                .add(Projections.max("maxResponseTime"), "maxResponseTime")
                .add(Projections.avg("successRate"), "successRate");

        switch (level) {
            case HOUR:
                projectionList.add(Projections.groupProperty("hourPeriod").as("hourPeriod"));
                break;
            case DAY:
                projectionList.add(Projections.groupProperty("dayPeriod").as("dayPeriod"));
                break;
            case WEEK:
                projectionList.add(Projections.groupProperty("weekPeriod").as("weekPeriod"));
                break;
            case MONTH:
                projectionList.add(Projections.groupProperty("monthPeriod").as("monthPeriod"));
                break;
        }

        return findAllByProjection(offset, limit, term, lookup, startDate, endDate, sortColumn, sortDirection, logEntryType, projectionList);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AggregatedMetricsEntryModel> findAllByTermAndDates(Integer offset, Integer limit,
            String term, String lookup, Date startDate, Date endDate,
            String sortColumn, String sortDirection, LogEntryType logEntryType) {

        ProjectionList projectionList = Projections.projectionList();
        projectionList
                .add(Projections.property("hourPeriod"), "startTime")
                .add(Projections.property("hourPeriod"), "endTime")
                .add(Projections.property("lookup"), "alias_lookup")
                .add(Projections.property("systemAccountName"), "alias_systemAccountName")
                .add(Projections.property("username"), "alias_username")
                .add(Projections.property("numberOfInvocations"), "numberOfInvocations")
                .add(Projections.property("averageExecutionTime"), "averageExecutionTime")
                .add(Projections.property("minResponseTime"), "minResponseTime")
                .add(Projections.property("maxResponseTime"), "maxResponseTime")
                .add(Projections.property("successRate"), "successRate");

        return findAllByProjection(offset, limit, term, lookup, startDate, endDate, sortColumn, sortDirection, logEntryType, projectionList);
    }

    private List<AggregatedMetricsEntryModel> findAllByProjection(Integer offset, Integer limit,
            String term, String lookup, Date startDate, Date endDate, String sortColumn, String sortDirection,
            LogEntryType logEntryType, Projection projectionList) {

        List<Criterion> criterions = createCriterionsForTermAndDates(term, lookup, startDate, endDate, logEntryType);

        Order order;
        if (StringUtils.isBlank(sortColumn)) {
            order = null;
        } else {
            if (SortOrder.DESC.name().equalsIgnoreCase(sortDirection)) {
                order = Order.desc(sortColumn);
            } else {
                order = Order.asc(sortColumn);
            }
        }

        List<AggregatedMetricsEntryModel> aggregatedMetricsEntries = order == null ?
                findAllWithFilter(offset, limit, criterions, null, null, projectionList, AggregatedMetricsEntryModel.class, null) :
                findAllWithFilter(offset, limit, criterions, null, null, projectionList, AggregatedMetricsEntryModel.class, null, order);

        for(AggregatedMetricsEntryModel aggregatedMetricsEntry : aggregatedMetricsEntries) {
            aggregatedMetricsEntry.setEndTime(aggregatedMetricsEntry.getEndTime() + 3600 * 1000); // add one hour to the end time
        }

        return aggregatedMetricsEntries;

    }

    @Override
    @Transactional(readOnly = true)
    public long countAllByTermAndDates(String term, String lookup, Date startDate, Date endDate, LogEntryType logEntryType) {
        List<Criterion> criterions = createCriterionsForTermAndDates(term, lookup, startDate, endDate, logEntryType);

        return count(criterions, null);
    }

    public long countAggregatedByTermAndDates(String term, String lookup, Date startDate, Date endDate,
                                              MetricGroupLevel level, LogEntryType logEntryType) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList
                .add(Projections.groupProperty("lookup"), "lookup")
                .add(Projections.groupProperty("systemAccountName"), "systemAccountName")
                .add(Projections.groupProperty("username"), "username");

        switch (level) {
            case HOUR:
                projectionList.add(Projections.groupProperty("hourPeriod").as("hourPeriod"));
                break;
            case DAY:
                projectionList.add(Projections.groupProperty("dayPeriod").as("dayPeriod"));
                break;
            case WEEK:
                projectionList.add(Projections.groupProperty("weekPeriod").as("weekPeriod"));
                break;
            case MONTH:
                projectionList.add(Projections.groupProperty("monthPeriod").as("monthPeriod"));
                break;
        }

        List<Criterion> criterions = createCriterionsForTermAndDates(term, lookup, startDate, endDate, logEntryType);
        return findCountWithFilter(criterions, null, null, projectionList);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Criterion> createCriterionsForTermAndDates(String term, String lookup, Date startDate, Date endDate, LogEntryType logEntryType) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (StringUtils.isNotEmpty(term)) {
            Criterion lookupCriterion = Restrictions.sqlRestriction("{alias}.lookup LIKE '%" + term + "%'");
            Criterion usernameCriterion = Restrictions.sqlRestriction("{alias}.username LIKE '%" + term + "%'");
            Criterion systemAccountNameCriterion = Restrictions.sqlRestriction("{alias}.system_account_name LIKE '%" + term + "%'");
            Criterion termCriterion = Restrictions.or(Restrictions.or(lookupCriterion, usernameCriterion), systemAccountNameCriterion);
            criterions.add(termCriterion);
        }

        if (logEntryType != null && !LogEntryType.ALL.equals(logEntryType)) {
            criterions.add(Restrictions.eq("type", logEntryType));
        }

        if (StringUtils.isNotEmpty(lookup)) {
            Criterion lookupCriterion = Restrictions.sqlRestriction("{alias}.lookup LIKE '%" + lookup + "%'");
            criterions.add(lookupCriterion);
        }

        if (startDate != null) {
            Criterion startDateCriterion = Restrictions.ge("hourPeriod", startDate.getTime());
            criterions.add(startDateCriterion);
        }

        if (endDate != null) {
            Criterion endDateCriterion = Restrictions.le("hourPeriod", endDate.getTime());
            criterions.add(endDateCriterion);
        }

        return criterions;
    }
}
