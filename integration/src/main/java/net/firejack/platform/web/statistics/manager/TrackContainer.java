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

package net.firejack.platform.web.statistics.manager;

import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.statistics.engine.DetailedStatisticsInfo;

public class TrackContainer {

    private static ThreadLocal<Tuple<Long, DetailedStatisticsInfo>> detailedStatisticsHolder =
            new InheritableThreadLocal<Tuple<Long, DetailedStatisticsInfo>>();

    public static DetailedStatisticsInfo getCurrentTrack() {
        DetailedStatisticsInfo statisticsInfo = null;
        if (detailedStatisticsHolder.get() != null) {
            statisticsInfo = detailedStatisticsHolder.get().getValue();
        }
        return statisticsInfo;
    }

    /**
     * @return
     */
    public static Long getCurrentTrackStartTime() {
        Long startTime = null;
        if (detailedStatisticsHolder.get() != null) {
            startTime = detailedStatisticsHolder.get().getKey();
        }
        return startTime;
    }

    public static void setCurrentTrack(DetailedStatisticsInfo statisticsInfo, long start) {
        detailedStatisticsHolder.set(new Tuple<Long, DetailedStatisticsInfo>(start, statisticsInfo));
    }

    /***/
    public static void cleanup() {
        detailedStatisticsHolder.remove();
    }

}