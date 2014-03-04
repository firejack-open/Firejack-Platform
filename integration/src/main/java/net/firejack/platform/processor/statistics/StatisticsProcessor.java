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
