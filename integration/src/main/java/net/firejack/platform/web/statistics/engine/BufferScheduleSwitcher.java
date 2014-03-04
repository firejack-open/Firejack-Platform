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
