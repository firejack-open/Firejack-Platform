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

import net.firejack.platform.api.statistics.domain.LogEntryType;

import java.util.Date;



public class DetailedStatisticsInfo {

    private String lookup;
    private Long executionTime;
    private Boolean success;
    private String details;
    private String errorMessage;
    private Long requestSize;
    private Long responseSize;
    private Long userId;
    private String username;
    private Long systemAccountId;
    private String systemAccountName;
    private Date logTime;
    private LogEntryType type;

    public DetailedStatisticsInfo() {
    }

    public DetailedStatisticsInfo(String lookup) {
        this.lookup = lookup;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getRequestSize() {
        return requestSize;
    }

    public void setRequestSize(Long requestSize) {
        this.requestSize = requestSize;
    }

    public Long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(Long responseSize) {
        this.responseSize = responseSize;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSystemAccountId() {
        return systemAccountId;
    }

    public void setSystemAccountId(Long systemAccountId) {
        this.systemAccountId = systemAccountId;
    }

    public String getSystemAccountName() {
        return systemAccountName;
    }

    public void setSystemAccountName(String systemAccountName) {
        this.systemAccountName = systemAccountName;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public LogEntryType getType() {
        return type;
    }

    public void setType(LogEntryType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DetailedStatisticsInfo");
        sb.append("{lookup='").append(lookup).append('\'');
        sb.append(", executionTime=").append(executionTime);
        sb.append(", success=").append(success);
        sb.append(", details='").append(details).append('\'');
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", requestSize=").append(requestSize);
        sb.append(", responseSize=").append(responseSize);
        sb.append(", userId=").append(userId);
        sb.append(", username='").append(username).append('\'');
        sb.append(", systemAccountId=").append(systemAccountId);
        sb.append(", systemAccountName='").append(systemAccountName).append('\'');
        sb.append(", logTime=").append(logTime);
        sb.append(", logEntryType=").append(type);
        sb.append('}');
        return sb.toString();
    }
    
}