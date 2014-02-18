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

package net.firejack.platform.core.model.registry.statistics;


import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.core.model.BaseEntityModel;

import javax.persistence.*;


@Entity
@Table(name = "opf_metrics_entry")
public class MetricsEntryModel extends BaseEntityModel {

    private static final long serialVersionUID = -1993221090566274728L;
    private String lookup;
    private Double averageExecutionTime;
    private Long numberOfInvocations;
    private Double averageRequestSize;
    private Double averageResponseSize;
    private Double successRate;
    private Long userId;
    private String username;
    private Long systemAccountId;
    private String systemAccountName;
    private Long hourPeriod;
    private Long dayPeriod;
    private Long weekPeriod;
    private Long monthPeriod;
    private Long minResponseTime;
    private Long maxResponseTime;
    private LogEntryType type;

    /**
     * @return
     */
    @Column(length = 1023)
    public String getLookup() {
        return lookup;
    }

    /**
     * @param lookup
     */
    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    /**
     * @return
     */
    @Column(name = "average_execution_time")
    public Double getAverageExecutionTime() {
        return averageExecutionTime;
    }

    /**
     * @param averageExecutionTime
     */
    public void setAverageExecutionTime(Double averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
    }

    /**
     * @return
     */
    @Column(name = "number_of_invocations")
    public Long getNumberOfInvocations() {
        return numberOfInvocations;
    }

    /**
     * @param numberOfInvocations
     */
    public void setNumberOfInvocations(Long numberOfInvocations) {
        this.numberOfInvocations = numberOfInvocations;
    }

    /**
     * @return
     */
    @Column(name = "average_request_size")
    public Double getAverageRequestSize() {
        return averageRequestSize;
    }

    /**
     * @param averageRequestSize
     */
    public void setAverageRequestSize(Double averageRequestSize) {
        this.averageRequestSize = averageRequestSize;
    }

    /**
     * @return
     */
    @Column(name = "average_response_size")
    public Double getAverageResponseSize() {
        return averageResponseSize;
    }

    /**
     * @param averageResponseSize
     */
    public void setAverageResponseSize(Double averageResponseSize) {
        this.averageResponseSize = averageResponseSize;
    }

    /**
     * @return
     */
    @Column(name = "success_rate")
    public Double getSuccessRate() {
        return successRate;
    }

    /**
     * @param successRate
     */
    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    /**
     * @return
     */
    @Column(name = "id_user")
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     */
    @Column(name = "id_system_account")
    public Long getSystemAccountId() {
        return systemAccountId;
    }

    /**
     * @param systemAccountId
     */
    public void setSystemAccountId(Long systemAccountId) {
        this.systemAccountId = systemAccountId;
    }

    /**
     * @return
     */
    @Column(name = "system_account_name")
    public String getSystemAccountName() {
        return systemAccountName;
    }

    /**
     * @param systemAccountName
     */
    public void setSystemAccountName(String systemAccountName) {
        this.systemAccountName = systemAccountName;
    }

    /**
     * @return
     */
    @Column(name = "hour_period")
    public Long getHourPeriod() {
        return hourPeriod;
    }

    /**
     * @param hourPeriod
     */
    public void setHourPeriod(Long hourPeriod) {
        this.hourPeriod = hourPeriod;
    }

    /**
     * @return
     */
    @Column(name = "day_period")
    public Long getDayPeriod() {
        return dayPeriod;
    }

    /**
     * @param dayPeriod
     */
    public void setDayPeriod(Long dayPeriod) {
        this.dayPeriod = dayPeriod;
    }

    /**
     * @return
     */
    @Column(name = "week_period")
    public Long getWeekPeriod() {
        return weekPeriod;
    }

    /**
     * @param weekPeriod
     */
    public void setWeekPeriod(Long weekPeriod) {
        this.weekPeriod = weekPeriod;
    }

    /**
     * @return
     */
    @Column(name = "month_period")
    public Long getMonthPeriod() {
        return monthPeriod;
    }

    /**
     * @param monthPeriod
     */
    public void setMonthPeriod(Long monthPeriod) {
        this.monthPeriod = monthPeriod;
    }

    /**
     * @return
     */
    @Column(name = "min_execute_time")
    public Long getMinResponseTime() {
        return minResponseTime;
    }

    /**
     * @param minResponseTime
     */
    public void setMinResponseTime(Long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }

    /**
     * @return
     */
    @Column(name = "max_execute_time")
    public Long getMaxResponseTime() {
        return maxResponseTime;
    }

    /**
     * @param maxResponseTime
     */
    public void setMaxResponseTime(Long maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    @Enumerated(EnumType.STRING)
    public LogEntryType getType() {
        return type;
    }

    public void setType(LogEntryType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricsEntryModel)) return false;
        if (!super.equals(o)) return false;

        MetricsEntryModel that = (MetricsEntryModel) o;

        if (averageExecutionTime != null ? !averageExecutionTime.equals(that.averageExecutionTime) : that.averageExecutionTime != null)
            return false;
        if (averageRequestSize != null ? !averageRequestSize.equals(that.averageRequestSize) : that.averageRequestSize != null)
            return false;
        if (averageResponseSize != null ? !averageResponseSize.equals(that.averageResponseSize) : that.averageResponseSize != null)
            return false;
        if (dayPeriod != null ? !dayPeriod.equals(that.dayPeriod) : that.dayPeriod != null) return false;
        if (hourPeriod != null ? !hourPeriod.equals(that.hourPeriod) : that.hourPeriod != null) return false;
        if (lookup != null ? !lookup.equals(that.lookup) : that.lookup != null) return false;
        if (maxResponseTime != null ? !maxResponseTime.equals(that.maxResponseTime) : that.maxResponseTime != null)
            return false;
        if (minResponseTime != null ? !minResponseTime.equals(that.minResponseTime) : that.minResponseTime != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        if (monthPeriod != null ? !monthPeriod.equals(that.monthPeriod) : that.monthPeriod != null) return false;
        if (numberOfInvocations != null ? !numberOfInvocations.equals(that.numberOfInvocations) : that.numberOfInvocations != null)
            return false;
        if (successRate != null ? !successRate.equals(that.successRate) : that.successRate != null) return false;
        if (systemAccountId != null ? !systemAccountId.equals(that.systemAccountId) : that.systemAccountId != null)
            return false;
        if (systemAccountName != null ? !systemAccountName.equals(that.systemAccountName) : that.systemAccountName != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (weekPeriod != null ? !weekPeriod.equals(that.weekPeriod) : that.weekPeriod != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (lookup != null ? lookup.hashCode() : 0);
        result = 31 * result + (averageExecutionTime != null ? averageExecutionTime.hashCode() : 0);
        result = 31 * result + (numberOfInvocations != null ? numberOfInvocations.hashCode() : 0);
        result = 31 * result + (averageRequestSize != null ? averageRequestSize.hashCode() : 0);
        result = 31 * result + (averageResponseSize != null ? averageResponseSize.hashCode() : 0);
        result = 31 * result + (successRate != null ? successRate.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (systemAccountId != null ? systemAccountId.hashCode() : 0);
        result = 31 * result + (systemAccountName != null ? systemAccountName.hashCode() : 0);
        result = 31 * result + (hourPeriod != null ? hourPeriod.hashCode() : 0);
        result = 31 * result + (dayPeriod != null ? dayPeriod.hashCode() : 0);
        result = 31 * result + (weekPeriod != null ? weekPeriod.hashCode() : 0);
        result = 31 * result + (monthPeriod != null ? monthPeriod.hashCode() : 0);
        result = 31 * result + (minResponseTime != null ? minResponseTime.hashCode() : 0);
        result = 31 * result + (maxResponseTime != null ? maxResponseTime.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
