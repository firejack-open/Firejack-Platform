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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.engine.BasicBufferEngine;
import net.firejack.platform.web.statistics.engine.DetailedStatisticsInfo;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class BaseStatisticsManager implements IStatisticsManager {

    private static final Logger logger = Logger.getLogger(BaseStatisticsManager.class);

    protected BasicBufferEngine bufferEngine;

    public BasicBufferEngine getBufferEngine() {
        return bufferEngine;
    }

    public void setBufferEngine(BasicBufferEngine bufferEngine) {
        this.bufferEngine = bufferEngine;
    }

    @Override
    public void scheduleTracking() {
        getBufferEngine().scheduleStatisticsTracker();
    }

    public void destroy() {
        getBufferEngine().destroy();
    }

    @Override
    public void beforeExecution(HttpServletRequest httpRequest) {
        OPFContext context = OPFContext.getContext();
        String actionLookup = context.getCurrentActionLookup();
        String navigationElementLookup = context.getCurrentNavigationElementLookup();
        Long start = System.currentTimeMillis();
        if (StringUtils.isNotBlank(actionLookup)) {
            DetailedStatisticsInfo detailedStatisticsInfo = new DetailedStatisticsInfo(actionLookup);
            detailedStatisticsInfo.setType(LogEntryType.ACTION);
            TrackContainer.setCurrentTrack(detailedStatisticsInfo, start);
            IUserInfoProvider userInfo = context.getPrincipal().getUserInfoProvider();
            detailedStatisticsInfo.setUserId(userInfo.getId());
            detailedStatisticsInfo.setUsername(userInfo.getUsername());
            detailedStatisticsInfo.setLogTime(new Date());

            detailedStatisticsInfo.setSystemAccountId(1L);
            detailedStatisticsInfo.setSystemAccountName("system");

            long requestSize;
            String sLength = httpRequest.getHeader("Content-Length");
            if (StringUtils.isNumeric(sLength)) {
                requestSize = Long.parseLong(sLength);
            } else {
                requestSize = 0L;
            }
            detailedStatisticsInfo.setRequestSize(requestSize);
        } else if (StringUtils.isNotBlank(navigationElementLookup)) {
            DetailedStatisticsInfo detailedStatisticsInfo = new DetailedStatisticsInfo(navigationElementLookup);
            detailedStatisticsInfo.setType(LogEntryType.NAVIGATION);
            TrackContainer.setCurrentTrack(detailedStatisticsInfo, start);
            IUserInfoProvider userInfo = context.getPrincipal().getUserInfoProvider();
            detailedStatisticsInfo.setUserId(userInfo.getId());
            detailedStatisticsInfo.setUsername(userInfo.getUsername());
            detailedStatisticsInfo.setLogTime(new Date());
            detailedStatisticsInfo.setRequestSize(0L);
            NavigationElement navigationElement = context.getCurrentNavigationElement();
            detailedStatisticsInfo.setDetails(userInfo.getUsername() + " visited page " + navigationElement.getName());
        }
    }

    @Override
    public void afterExecution(HttpServletRequest request1, HttpServletResponse response,
                               Throwable throwable) throws IOException {
        DetailedStatisticsInfo detailedStatisticsInfo = TrackContainer.getCurrentTrack();
        if (detailedStatisticsInfo != null) {
            detailedStatisticsInfo.setSuccess(throwable == null);//todo consider unauthorized access cases
            detailedStatisticsInfo.setResponseSize(54321L);

            if (throwable != null) {
                detailedStatisticsInfo.setErrorMessage(throwable.getMessage());
            }
            Long end = System.currentTimeMillis();
            detailedStatisticsInfo.setExecutionTime(end - TrackContainer.getCurrentTrackStartTime());
            bufferEngine.put(detailedStatisticsInfo);
            TrackContainer.cleanup();
        }
    }

    @Override
    public void saveStatisticsBunch(List<LogEntry> partLogEntryList) {
        ServiceRequest<LogEntry> request = new ServiceRequest<LogEntry>();
        request.setDataList(partLogEntryList);
        ServiceResponse serviceResponse = OPFEngine.StatisticsService.saveStatisticsBunch(request);
        String time = DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date());
        if (serviceResponse.isSuccess()) {
            logger.info("Statistics bunch was committed successfully at " + time);
        } else {
            logger.error("Statistics API Service response has failure status at " + time +
                    ". Reason: " + serviceResponse.getMessage());
        }
    }
}