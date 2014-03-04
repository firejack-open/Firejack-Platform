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

package net.firejack.platform.api.statistics;

import net.firejack.platform.api.BaseOpenFlameAPITest;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.registry.listener.DomainExecutionListener;
import net.firejack.platform.api.registry.listener.EntityExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.model.statistics.MetricGroupLevel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        DomainExecutionListener.class,
        EntityExecutionListener.class
})
public class StatisticsAPITests extends BaseOpenFlameAPITest {
    private static final Logger logger = Logger.getLogger(StatisticsAPITests.class);
    private static final String VAL_TEST_ACTION_LOOKUP = "action.lookup.for.tracking.test";

    @Test
    public void checkStatisticsAPI() {
        OpenFlamePrincipal principal = OPFContext.getContext().getPrincipal();
        IUserInfoProvider user = principal.getUserInfoProvider();
        //=============== Log Entries ===============
        logger.info("Trying to read all log-entries...");
        ServiceResponse<LogEntry> logEntryResponse = OPFEngine.StatisticsService.readAllLogEntries();
        checkResponse(logEntryResponse);
        logger.info("logEntryResponse.getData().size() = " + logEntryResponse.getData().size());

        logger.info("Trying to find already existing test log entries.");
        logEntryResponse = searchTestLogEntries();
        int testEntriesCount = logEntryResponse.getData() == null ? 0 : logEntryResponse.getData().size();

        logger.info("Trying to create log entry");

        LogEntry logEntry1 = populateTestLogEntry(user, Boolean.TRUE);
        LogEntry logEntry2 = populateTestLogEntry(user, Boolean.FALSE);

        List<LogEntry> logEntriesToSave = new ArrayList<LogEntry>();
        logEntriesToSave.add(logEntry1);
        logEntriesToSave.add(logEntry2);

        ServiceRequest<LogEntry> request = new ServiceRequest<LogEntry>();
        request.setDataList(logEntriesToSave);

        ServiceResponse statusResponse = OPFEngine.StatisticsService.saveStatisticsBunch(request);
        checkResponse(statusResponse);

        logger.info("Trying to find just created log entries.");
        logEntryResponse = searchTestLogEntries();
        Assert.assertNotNull("logEntryResponse.getData() should not be null.", logEntryResponse.getData());
        Assert.assertTrue("testEntriesCount should be equal to " + (testEntriesCount + 2),
                logEntryResponse.getData().size() ==  testEntriesCount + 2);

        //================= Metrics =================
        logger.info("Trying to get count of already existent test metric tracks.");
        ServiceResponse<MetricsEntry> metricsResponse = searchTestMetrics();
        checkResponse(metricsResponse);
        int testMetricsCount = metricsResponse.getData() == null ? 0 : metricsResponse.getData().size();

        logger.info("Trying to save MetricsEntry...");
        MetricsEntry metricsEntry = new MetricsEntry();
        metricsEntry.setUserId(user.getId());
        metricsEntry.setUsername(user.getUsername());
        metricsEntry.setLookup(VAL_TEST_ACTION_LOOKUP);
        metricsEntry.setAverageExecutionTime(randomExecutionTime().doubleValue());
        metricsEntry.setAverageRequestSize(2000D);
        metricsEntry.setAverageResponseSize(2000D);
        metricsEntry.setMaxResponseTime(randomExecutionTime());
        metricsEntry.setMinResponseTime(randomExecutionTime());
        metricsEntry.setNumberOfInvocations(24L);
        metricsEntry.setSuccessRate(24D);
        Date hourlyDate = new Date();
        hourlyDate = DateUtils.truncate(hourlyDate, Calendar.HOUR);
        metricsEntry.setHourPeriod(hourlyDate.getTime());
        metricsEntry.setDayPeriod(DateUtils.truncate(hourlyDate, Calendar.DAY_OF_MONTH).getTime());
        Date weekPeriod = net.firejack.platform.core.utils.DateUtils.truncateDateToWeek(hourlyDate);
        metricsEntry.setWeekPeriod(weekPeriod.getTime());
        metricsEntry.setMonthPeriod(DateUtils.truncate(hourlyDate, Calendar.MONTH).getTime());

        ServiceRequest<MetricsEntry> metricsRequest = new ServiceRequest<MetricsEntry>(metricsEntry);
        statusResponse = OPFEngine.StatisticsService.saveMetricsEntry(metricsRequest);
        checkResponse(statusResponse);

        logger.info("Trying to get count of test metric tracks after saving one metric.");
        metricsResponse = searchTestMetrics();
        checkResponse(metricsResponse);
        Assert.assertNotNull("metricsResponse.getData() should nul be null", metricsResponse.getData());
        Assert.assertTrue(
                "metricsResponse.getData().size() should be more or equal to " + testMetricsCount,
                metricsResponse.getData().size() >= testMetricsCount);

        logger.info("Trying to find metric by example...");
        metricsResponse = OPFEngine.StatisticsService.findMetricsEntryByExample(metricsRequest);
        checkResponse(metricsResponse);
        Assert.assertNotNull("metricsResponse.getData() should not be null.", metricsResponse.getData());
        logger.info("Tested all service methods successfully!");
    }

    private ServiceResponse<LogEntry> searchTestLogEntries() {
        ServiceResponse<LogEntry> logEntryResponse = OPFEngine.StatisticsService.searchAllLogEntries(
                VAL_TEST_ACTION_LOOKUP, null, null, null, null, null, null, null, null, null, LogEntryType.ALL.name());
        checkResponse(logEntryResponse);
        return logEntryResponse;
    }

    private ServiceResponse<MetricsEntry> searchTestMetrics() {
        ServiceResponse<MetricsEntry> metricsResponse = OPFEngine.StatisticsService.findMetricsEntry(
                null, null, null, null, null, MetricGroupLevel.HOUR.name(), VAL_TEST_ACTION_LOOKUP,
                null, null, null, null, LogEntryType.ALL.name());
        checkResponse(metricsResponse);
        return metricsResponse;
    }

    private void checkResponse(ServiceResponse logEntryResponse) {
        Assert.assertNotNull(
                "API method call should not return null",
                logEntryResponse);
        Assert.assertTrue(
                "logEntryResponse has failure status. Reason: " + logEntryResponse.getMessage(),
                logEntryResponse.isSuccess());
    }

    private LogEntry populateTestLogEntry(IUserInfoProvider user, Boolean successExecution) {
        LogEntry logEntry = new LogEntry();
        logEntry.setUserId(user.getId());
        logEntry.setUsername(user.getUsername());
        logEntry.setDetails("Test execution.");
        logEntry.setSuccess(successExecution);
        if (!successExecution) {
            logEntry.setErrorMessage("Test execution failure message.");
        }
        logEntry.setExecuteTime(randomExecutionTime());
        logEntry.setLookup(VAL_TEST_ACTION_LOOKUP);

        return logEntry;
    }

    private Long randomExecutionTime() {
        return Math.round(400 * Math.random());
    }

}