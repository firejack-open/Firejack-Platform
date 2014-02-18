/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.statistics.engine;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
public class BufferScheduleSwitcher {

    private static final Logger logger = Logger.getLogger(BufferScheduleSwitcher.class);

    private ScheduledThreadPoolExecutor executorService;

    /***/
    @PostConstruct
    public void init() {
        scheduleTasks();
    }

    /***/
    @PreDestroy
    public void destroy() {
        cancelTasks();
    }

    /***/
    public void scheduleTasks() {
        executorService = new ScheduledThreadPoolExecutor(2);
        executorService.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

//        Calendar calendar = DateTimeUtils.getSynchTime(synchTime);
//        if (calendar.before(Calendar.getInstance())) {
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//        }
//        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        long delay = 0;
        executorService.scheduleAtFixedRate(new ImportTask(), delay, DateUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
    }

    /***/
    public void cancelTasks() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    class ImportTask implements Runnable {
        public void run() {
            try {
                Date transDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
//                SubscriptionProcessor.this.performDailyImport(transDate, false);
            } catch (Throwable e) {
                logger.error("Error while performing daily import", e);
            }
        }
    }

}
