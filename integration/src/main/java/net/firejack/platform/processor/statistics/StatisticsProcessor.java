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

package net.firejack.platform.processor.statistics;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.core.utils.DateUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class StatisticsProcessor {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired(required = false)
    protected List<HibernateTemplate> templates;

    @Scheduled(cron = "0 0/5 * * * *")
    public void schedulerStatisticsHandler() {
	    if (templates != null) {
		    for (HibernateTemplate template : templates) {
			    SessionFactory sessionFactory = template.getSessionFactory();
			    Statistics statistics = sessionFactory.getStatistics();
			    if (statistics.isStatisticsEnabled()) {
				    LogTransaction logTransaction = new LogTransaction();
				    logTransaction.setPackageLookup(OpenFlameSecurityConstants.getPackageLookup());
				    logTransaction.setTransactions(statistics.getSuccessfulTransactionCount());
				    logTransaction.setEntitiesLoaded(statistics.getEntityLoadCount());
				    logTransaction.setEntitiesInserted(statistics.getEntityInsertCount());
				    logTransaction.setEntitiesUpdated(statistics.getEntityUpdateCount());
				    logTransaction.setEntitiesDeleted(statistics.getEntityDeleteCount());
				    logTransaction.setEntitiesFetched(statistics.getEntityFetchCount());
				    logTransaction.setCollectionsLoaded(statistics.getCollectionLoadCount());
				    logTransaction.setCollectionsRecreated(statistics.getCollectionRecreateCount());
				    logTransaction.setCollectionsUpdated(statistics.getCollectionUpdateCount());
				    logTransaction.setCollectionsRemoved(statistics.getCollectionRemoveCount());
				    logTransaction.setCollectionsFetched(statistics.getCollectionFetchCount());
				    logTransaction.setMaxQueryTime(statistics.getQueryExecutionMaxTime());

				    Date hourlyDate = new Date();
				    logTransaction.setHourPeriod(DateUtils.truncate(hourlyDate, Calendar.HOUR).getTime());
				    logTransaction.setDayPeriod(DateUtils.truncate(hourlyDate, Calendar.DAY_OF_MONTH).getTime());
				    logTransaction.setWeekPeriod(DateUtils.truncateDateToWeek(hourlyDate).getTime());
				    logTransaction.setMonthPeriod(DateUtils.truncate(hourlyDate, Calendar.MONTH).getTime());

				    OPFEngine.StatisticsService.saveLogTransaction(logTransaction);
				    statistics.clear();
			    } else {
				    logger.warn("Hibernate Statistics is disabled.");
			    }
		    }
	    }
    }
}
