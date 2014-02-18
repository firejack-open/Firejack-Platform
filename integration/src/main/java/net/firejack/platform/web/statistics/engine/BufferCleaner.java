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

import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.statistics.engine.aggregator.BaseMetricsEntryAggregator;
import net.firejack.platform.web.statistics.manager.IStatisticsManager;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.*;


public class BufferCleaner implements ApplicationListener<BufferEvent> {

    private static final String MSG_ERROR_ON_TRACKS_SAVE = "Error appeared on save DetailedTracks to DB.";
    private static final String MSG_ERROR_BUFFER_CLEANSING_HAS_INTERRUPTED = "The buffer cleansing has interrupted!";

    private static final Logger logger = Logger.getLogger(BufferCleaner.class);

    private int insertSize;
    private BaseMetricsEntryAggregator metricsEntryAggregator;

    private IStatisticsManager statisticsManager;

    /**
     * @param insertSize
     */
    public void setInsertSize(int insertSize) {
        this.insertSize = insertSize;
    }

    /**
     * @param statisticsManager
     */
    public void setStatisticsManager(IStatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    /**
     * @return
     */
    public IStatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    /**
     * @return metricsEntryAggregator
     */
    public BaseMetricsEntryAggregator getMetricsEntryAggregator() {
        if (metricsEntryAggregator == null) {
            metricsEntryAggregator = new BaseMetricsEntryAggregator();
        }
        return metricsEntryAggregator;
    }

    @Override
    public void onApplicationEvent(BufferEvent bufferEvent) {
        Buffer buffer = bufferEvent.getBuffer();
        BufferCleanerThread bufferCleanerThread = new BufferCleanerThread(buffer);
        bufferCleanerThread.start();
    }

    class BufferCleanerThread extends Thread {

        private Buffer buffer;
        private boolean isInterrupted = true;

        public BufferCleanerThread(Buffer buffer) {
            this.buffer = buffer;
        }

        public void interrupt() {
            super.interrupt();
            if (isInterrupted) {
                logger.warn(MSG_ERROR_BUFFER_CLEANSING_HAS_INTERRUPTED);
                cleanBuffer();
            }
        }

        public void run() {
            super.run();
            List<DetailedStatisticsInfo> detailedStatisticsInfoList = buffer.getBuffer();
            int bufferSize = detailedStatisticsInfoList.size();
            if (bufferSize > 0) {
                List<LogEntry> logEntryBunch = new ArrayList<LogEntry>();
                Map<Integer, Tuple<MetricsEntry, List<DetailedStatisticsInfo>>> detailedTrackHourly =
                        new HashMap<Integer, Tuple<MetricsEntry, List<DetailedStatisticsInfo>>>();
                for (int i = 0; i < bufferSize; i++) {
                    DetailedStatisticsInfo detailedStatisticsInfo = detailedStatisticsInfoList.get(i);

                    LogEntry logEntry = new LogEntry();
                    logEntry.setLookup(detailedStatisticsInfo.getLookup());
                    logEntry.setUserId(detailedStatisticsInfo.getUserId());
                    logEntry.setUsername(detailedStatisticsInfo.getUsername());
                    logEntry.setSuccess(detailedStatisticsInfo.getSuccess());
                    logEntry.setDetails(detailedStatisticsInfo.getDetails());
                    logEntry.setErrorMessage(detailedStatisticsInfo.getErrorMessage());
                    logEntry.setExecuteTime(detailedStatisticsInfo.getExecutionTime());
                    logEntry.setType(detailedStatisticsInfo.getType());
                    logEntryBunch.add(logEntry);

                    if (logEntryBunch.size() == insertSize) {
                        savePartOfDetailedTracks(logEntryBunch);
                        logEntryBunch.clear();
                    }

                    MetricsEntry metricsEntry = new MetricsEntry();
                    metricsEntry.setLookup(detailedStatisticsInfo.getLookup());
                    metricsEntry.setType(detailedStatisticsInfo.getType());
                    metricsEntry.setSystemAccountId(detailedStatisticsInfo.getSystemAccountId());
                    metricsEntry.setSystemAccountName(detailedStatisticsInfo.getSystemAccountName());
                    metricsEntry.setUserId(detailedStatisticsInfo.getUserId());
                    metricsEntry.setUsername(detailedStatisticsInfo.getUsername());
                    Date hourlyDate = detailedStatisticsInfo.getLogTime();
                    hourlyDate = DateUtils.truncate(hourlyDate, Calendar.HOUR);
                    metricsEntry.setHourPeriod(hourlyDate.getTime());
                    metricsEntry.setDayPeriod(DateUtils.truncate(hourlyDate, Calendar.DAY_OF_MONTH).getTime());
                    Date weekPeriod = net.firejack.platform.core.utils.DateUtils.truncateDateToWeek(hourlyDate);
                    metricsEntry.setWeekPeriod(weekPeriod.getTime());
                    metricsEntry.setMonthPeriod(DateUtils.truncate(hourlyDate, Calendar.MONTH).getTime());

                    Integer hash = metricsEntry.identityHashCode();
                    Tuple<MetricsEntry, List<DetailedStatisticsInfo>> tuple = detailedTrackHourly.get(hash);
                    if (tuple == null) {
                        List<DetailedStatisticsInfo> detailedStatisticsInfos = new ArrayList<DetailedStatisticsInfo>();
                        tuple = new Tuple<MetricsEntry, List<DetailedStatisticsInfo>>(metricsEntry, detailedStatisticsInfos);
                        detailedTrackHourly.put(hash, tuple);
                    }
                    if (tuple.getKey().getMaxResponseTime() == null ||
                            tuple.getKey().getMaxResponseTime() < detailedStatisticsInfo.getExecutionTime()) {
                        tuple.getKey().setMaxResponseTime(detailedStatisticsInfo.getExecutionTime());
                    }
                    if (tuple.getKey().getMinResponseTime() == null ||
                            tuple.getKey().getMinResponseTime() > detailedStatisticsInfo.getExecutionTime()) {
                        tuple.getKey().setMinResponseTime(detailedStatisticsInfo.getExecutionTime());
                    }
                    tuple.getValue().add(detailedStatisticsInfo);
                }
                if (!logEntryBunch.isEmpty()) {
                    savePartOfDetailedTracks(logEntryBunch);
                    logEntryBunch.clear();
                }

                getMetricsEntryAggregator().aggregateAndSave(detailedTrackHourly);
            }
            cleanBuffer();
            isInterrupted = false;
        }

        private void cleanBuffer() {
            buffer.getBuffer().clear();
            buffer.setState(BufferState.READY);
            logger.debug("Buffer:[" + buffer.getName() + "] has cleaned.");

            if (!Buffer.TEMP_BUFFER.equals(buffer.getName())) {
                logger.debug("Buffer event happened:[" + buffer.getName() + "] was cleaned.");
            }
        }

        private void savePartOfDetailedTracks(List<LogEntry> partLogEntryList) {
            try {
                getStatisticsManager().saveStatisticsBunch(partLogEntryList);
            } catch (Exception e) {
                logger.error(MSG_ERROR_ON_TRACKS_SAVE, e);
            }
            partLogEntryList.clear();
        }
    }

}