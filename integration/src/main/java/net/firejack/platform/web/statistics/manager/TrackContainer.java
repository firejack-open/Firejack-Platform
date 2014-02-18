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