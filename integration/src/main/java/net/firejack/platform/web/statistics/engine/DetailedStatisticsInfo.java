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