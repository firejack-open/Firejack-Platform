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

package net.firejack.platform.web.statistics.engine.aggregator;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.statistics.engine.DetailedStatisticsInfo;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class BaseMetricsEntryAggregator {

    private static final Logger logger = Logger.getLogger(BaseMetricsEntryAggregator.class);

    public void aggregateAndSave(Map<Integer, Tuple<MetricsEntry, List<DetailedStatisticsInfo>>> detailedTrackHourly) {
        for (Map.Entry<Integer, Tuple<MetricsEntry, List<DetailedStatisticsInfo>>> entry : detailedTrackHourly.entrySet()) {
            MetricsEntry metricsEntryKey = entry.getValue().getKey();
            List<DetailedStatisticsInfo> detailedStatisticsInfos = entry.getValue().getValue();
            long numberOfInvocations = detailedStatisticsInfos.size();
            Long executionTime = 0L;
            Long requestSize = 0L;
            Long responseSize = 0L;
            long numberOfSuccessInvocations = 0L;
            for (DetailedStatisticsInfo detailedStatisticsInfo : detailedStatisticsInfos) {
                if (detailedStatisticsInfo.getSuccess()) {
                    numberOfSuccessInvocations++;
                }
                executionTime += detailedStatisticsInfo.getExecutionTime();
                requestSize += detailedStatisticsInfo.getRequestSize();
                responseSize += detailedStatisticsInfo.getResponseSize();
            }

            long newNumberOfInvocations;
            Double newAverageExecutionTime;
            Double newAverageRequestSize;
            Double newAverageResponseSize;
            long newNumberOfSuccessInvocations;

            MetricsEntry foundMetricsEntry = findMetricsEntryByExample(metricsEntryKey);
            if (foundMetricsEntry != null) {
                Long foundNumberOfInvocations = foundMetricsEntry.getNumberOfInvocations();
                foundNumberOfInvocations = foundNumberOfInvocations == null ? 0 : foundNumberOfInvocations;
                Double foundAverageExecutionTime = foundMetricsEntry.getAverageExecutionTime();
                foundAverageExecutionTime = foundAverageExecutionTime == null ? 0 : foundAverageExecutionTime;
                Double foundAverageRequestSize = foundMetricsEntry.getAverageRequestSize();
                foundAverageRequestSize = foundAverageRequestSize == null ? 0 : foundAverageRequestSize;
                Double foundAverageResponseSize = foundMetricsEntry.getAverageResponseSize();
                foundAverageResponseSize = foundAverageResponseSize == null ? 0 : foundAverageResponseSize;
                Double foundSuccessRate = foundMetricsEntry.getSuccessRate();
                foundSuccessRate = foundSuccessRate == null ? 0 : foundSuccessRate;

                newNumberOfInvocations = foundNumberOfInvocations + numberOfInvocations;
                newAverageExecutionTime = (foundAverageExecutionTime * foundNumberOfInvocations + executionTime)
                        / newNumberOfInvocations;
                newAverageRequestSize = (foundAverageRequestSize * foundNumberOfInvocations + requestSize)
                        / newNumberOfInvocations;
                newAverageResponseSize = (foundAverageResponseSize * foundNumberOfInvocations + responseSize)
                        / newNumberOfInvocations;
                long foundNumberOfSuccessInvocations = (long) (foundSuccessRate * foundNumberOfInvocations);
                newNumberOfSuccessInvocations = foundNumberOfSuccessInvocations + numberOfSuccessInvocations;

                long newMaxExecutionTime = foundMetricsEntry.getMaxResponseTime() == null ||
                        metricsEntryKey.getMaxResponseTime() > foundMetricsEntry.getMaxResponseTime() ?
                        metricsEntryKey.getMaxResponseTime() : foundMetricsEntry.getMaxResponseTime();
                long newMinExecutionTime = foundMetricsEntry.getMinResponseTime() == null ||
                        metricsEntryKey.getMinResponseTime() < foundMetricsEntry.getMinResponseTime() ?
                        metricsEntryKey.getMinResponseTime() : foundMetricsEntry.getMinResponseTime();
                foundMetricsEntry.setMaxResponseTime(newMaxExecutionTime);
                foundMetricsEntry.setMinResponseTime(newMinExecutionTime);
            } else {
                foundMetricsEntry = metricsEntryKey;
                newNumberOfInvocations = numberOfInvocations;
                newAverageExecutionTime = executionTime.doubleValue() / numberOfInvocations;
                newAverageRequestSize = requestSize.doubleValue() / numberOfInvocations;
                newAverageResponseSize = responseSize.doubleValue() / numberOfInvocations;
                newNumberOfSuccessInvocations = numberOfSuccessInvocations;
            }
            foundMetricsEntry.setNumberOfInvocations(newNumberOfInvocations);
            foundMetricsEntry.setAverageExecutionTime(newAverageExecutionTime);
            foundMetricsEntry.setAverageRequestSize(newAverageRequestSize);
            foundMetricsEntry.setAverageResponseSize(newAverageResponseSize);
            foundMetricsEntry.setSuccessRate((double) newNumberOfSuccessInvocations / (double) newNumberOfInvocations);

            saveMetricsEntryInfo(foundMetricsEntry);
        }
    }

    protected MetricsEntry findMetricsEntryByExample(MetricsEntry metricsEntry) {
        ServiceResponse<MetricsEntry> response = OPFEngine.StatisticsService.findMetricsEntryByExample(
                new ServiceRequest<MetricsEntry>(metricsEntry));
        if (response == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (response.isSuccess()) {
            return response.getItem();
        } else {
            throw new IllegalStateException("API Service response has failure status. Reason: " + response.getMessage());
        }
    }

    protected void saveMetricsEntryInfo(MetricsEntry metricsEntry) {
        ServiceResponse serviceResponse = OPFEngine.StatisticsService.saveMetricsEntry(
                new ServiceRequest<MetricsEntry>(metricsEntry));
        if (serviceResponse == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (serviceResponse.isSuccess()) {
            logger.info("Metrics Entry was saved successfully.");
        } else {
            logger.error("Failed to save metrics entry. Reason: " + serviceResponse.getMessage());
        }
    }

}