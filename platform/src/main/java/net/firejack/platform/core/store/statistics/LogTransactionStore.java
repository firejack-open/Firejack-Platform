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

package net.firejack.platform.core.store.statistics;

import net.firejack.platform.core.model.registry.statistics.LogTransactionModel;
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
public class LogTransactionStore extends BaseStore<LogTransactionModel, Long> implements ILogTransactionStore {

    @PostConstruct
    public void init() {
        setClazz(LogTransactionModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public LogTransactionModel findByLookupAndHourPeriod(String packageLookup, Long hourPeriod) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("packageLookup", packageLookup));
        criterions.add(Restrictions.eq("hourPeriod", hourPeriod));
        return findByCriteria(criterions, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllByTermAndDates(String term, String packageLookup, Date startDate, Date endDate) {
        List<Criterion> criterions = createCriterionsForTermAndDates(term, packageLookup, startDate, endDate);

        return count(criterions, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogTransactionModel> findAllByTermAndDates(Integer offset, Integer limit, String term, String packageLookup, Date startDate, Date endDate, String sortColumn, String sortDirection) {
        List<Criterion> criterions = createCriterionsForTermAndDates(term, packageLookup, startDate, endDate);

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

        List<LogTransactionModel> logTransactionModels = (order == null) ?
                findAllWithFilter(offset, limit, criterions, null) :
                findAllWithFilter(offset, limit, criterions, null, order);

        for(LogTransactionModel logTransactionModel : logTransactionModels) {
            logTransactionModel.setStartTime(logTransactionModel.getHourPeriod());
            logTransactionModel.setEndTime(logTransactionModel.getHourPeriod() + 3600 * 1000); // add one hour to the end time
        }
        return logTransactionModels;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogTransactionModel> findAggregatedByTermAndDates(Integer offset, Integer limit, String term, String lookup, Date startDate, Date endDate, String sortColumn, String sortDirection, MetricGroupLevel level) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList
                .add(Projections.min("hourPeriod"), "startTime")
                .add(Projections.max("hourPeriod"), "endTime")
                .add(Projections.groupProperty("packageLookup"), "packageLookup")
                .add(Projections.sum("transactions"), "transactions")
                .add(Projections.sum("entitiesLoaded"), "entitiesLoaded")
                .add(Projections.sum("entitiesUpdated"), "entitiesUpdated")
                .add(Projections.sum("entitiesInserted"), "entitiesInserted")
                .add(Projections.sum("entitiesDeleted"), "entitiesDeleted")
                .add(Projections.sum("entitiesFetched"), "entitiesFetched")
                .add(Projections.sum("collectionsLoaded"), "collectionsLoaded")
                .add(Projections.sum("collectionsUpdated"), "collectionsUpdated")
                .add(Projections.sum("collectionsRecreated"), "collectionsRecreated")
                .add(Projections.sum("collectionsRemoved"), "collectionsRemoved")
                .add(Projections.sum("collectionsFetched"), "collectionsFetched")
                .add(Projections.max("maxQueryTime"), "maxQueryTime");

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

        return findAllByProjection(offset, limit, term, lookup, startDate, endDate, sortColumn, sortDirection, projectionList);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAggregatedByTermAndDates(String term, String lookup, Date startDate, Date endDate, MetricGroupLevel level) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("packageLookup"), "packageLookup");

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

        List<Criterion> criterions = createCriterionsForTermAndDates(term, lookup, startDate, endDate);
        return findCountWithFilter(criterions, null, null, projectionList);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Criterion> createCriterionsForTermAndDates(String term, String nodeLookup, Date startDate, Date endDate) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (!StringUtils.isEmpty(term)) {
            Criterion termCriterion = Restrictions.sqlRestriction("{alias}.package_lookup LIKE '%" + term + "%'");
            criterions.add(termCriterion);
        }

        if (!StringUtils.isEmpty(nodeLookup)) {
            Criterion nodeLookupCriterion = Restrictions.sqlRestriction("{alias}.package_lookup LIKE '" + nodeLookup + "%'");
            criterions.add(nodeLookupCriterion);
        }

        if (startDate != null) {
            Criterion startDateCriterion = Restrictions.ge("created", startDate);
            criterions.add(startDateCriterion);
        }

        if (endDate != null) {
            Criterion endDateCriterion = Restrictions.lt("created", endDate);
            criterions.add(endDateCriterion);
        }
        return criterions;
    }

    private List<LogTransactionModel> findAllByProjection(Integer offset, Integer limit, String term, String lookup, Date startDate, Date endDate, String sortColumn, String sortDirection, Projection projectionList) {

        List<Criterion> criterions = createCriterionsForTermAndDates(term, lookup, startDate, endDate);

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

        List<LogTransactionModel> logTransactionModels = order == null ?
                findAllWithFilter(offset, limit, criterions, null, null, projectionList, LogTransactionModel.class, null) :
                findAllWithFilter(offset, limit, criterions, null, null, projectionList, LogTransactionModel.class, null, order);

        for(LogTransactionModel logTransactionModel : logTransactionModels) {
            logTransactionModel.setEndTime(logTransactionModel.getEndTime() + 3600 * 1000); // add one hour to the end time
        }

        return logTransactionModels;

    }

}
