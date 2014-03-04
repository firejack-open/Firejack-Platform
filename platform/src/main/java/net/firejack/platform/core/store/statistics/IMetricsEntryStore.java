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
import net.firejack.platform.core.store.IStore;

import java.util.Date;
import java.util.List;


public interface IMetricsEntryStore extends IStore<MetricsEntryModel, Long> {

    List<AggregatedMetricsEntryModel> findAggregatedByTermAndDates(Integer offset, Integer limit,
            String term, String lookup, Date startDate, Date endDate,
            String sortColumn, String sortDirection, MetricGroupLevel level, LogEntryType logEntryType);

    List<AggregatedMetricsEntryModel> findAllByTermAndDates(Integer offset, Integer limit,
            String term, String lookup, Date startDate, Date endDate,
            String sortColumn, String sortDirection, LogEntryType logEntryType);

    /**
     * @param term
     * @param lookup
     * @param startDate
     * @param endDate
     * @return
     */
    long countAllByTermAndDates(String term, String lookup, Date startDate, Date endDate, LogEntryType logEntryType);

    /**
     * @param term
     * @param lookup
     * @param startDate
     * @param endDate
     * @param level
     * @return
     */
    long countAggregatedByTermAndDates(String term, String lookup, Date startDate, Date endDate, MetricGroupLevel level, LogEntryType logEntryType);

}
