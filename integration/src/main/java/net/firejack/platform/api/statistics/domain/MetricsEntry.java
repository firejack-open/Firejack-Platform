/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.api.statistics.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class MetricsEntry extends AbstractDTO {
    private static final long serialVersionUID = 2934754967067565313L;

    @Property
    private String lookup;
    @Property
    private Double averageExecutionTime;
    @Property
    private Long numberOfInvocations;
    @Property
    private Double averageRequestSize;
    @Property
    private Double averageResponseSize;
    @Property
    private Double successRate;
    @Property
    private Long userId;
    @Property
    private String username;
    @Property
    private Long systemAccountId;
    @Property
    private String systemAccountName;
    @Property
    private Long hourPeriod;
    @Property
    private Long dayPeriod;
    @Property
    private Long weekPeriod;
    @Property
    private Long monthPeriod;
    @Property
    private Long minResponseTime;
    @Property
    private Long maxResponseTime;
    @Property
    private LogEntryType type;
    @Property
    private Date created;

    private Long startTime;
    private Long endTime;

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Double getAverageExecutionTime() {
        return averageExecutionTime;
    }

    public void setAverageExecutionTime(Double averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
    }

    public Long getNumberOfInvocations() {
        return numberOfInvocations;
    }

    public void setNumberOfInvocations(Long numberOfInvocations) {
        this.numberOfInvocations = numberOfInvocations;
    }

    public Double getAverageRequestSize() {
        return averageRequestSize;
    }

    public void setAverageRequestSize(Double averageRequestSize) {
        this.averageRequestSize = averageRequestSize;
    }

    public Double getAverageResponseSize() {
        return averageResponseSize;
    }

    public void setAverageResponseSize(Double averageResponseSize) {
        this.averageResponseSize = averageResponseSize;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
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

    public Long getHourPeriod() {
        return hourPeriod;
    }

    public void setHourPeriod(Long hourPeriod) {
        this.hourPeriod = hourPeriod;
    }

    public Long getDayPeriod() {
        return dayPeriod;
    }

    public void setDayPeriod(Long dayPeriod) {
        this.dayPeriod = dayPeriod;
    }

    public Long getWeekPeriod() {
        return weekPeriod;
    }

    public void setWeekPeriod(Long weekPeriod) {
        this.weekPeriod = weekPeriod;
    }

    public Long getMonthPeriod() {
        return monthPeriod;
    }

    public void setMonthPeriod(Long monthPeriod) {
        this.monthPeriod = monthPeriod;
    }

    public Long getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(Long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }

    public Long getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(Long maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public LogEntryType getType() {
        return type;
    }

    public void setType(LogEntryType type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricsEntry)) return false;

        MetricsEntry that = (MetricsEntry) o;

        if (averageExecutionTime != null ? !averageExecutionTime.equals(that.averageExecutionTime) : that.averageExecutionTime != null)
            return false;
        if (averageRequestSize != null ? !averageRequestSize.equals(that.averageRequestSize) : that.averageRequestSize != null)
            return false;
        if (averageResponseSize != null ? !averageResponseSize.equals(that.averageResponseSize) : that.averageResponseSize != null)
            return false;
        if (dayPeriod != null ? !dayPeriod.equals(that.dayPeriod) : that.dayPeriod != null) return false;
        if (hourPeriod != null ? !hourPeriod.equals(that.hourPeriod) : that.hourPeriod != null) return false;
        if (lookup != null ? !lookup.equals(that.lookup) : that.lookup != null) return false;
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
        int result = lookup != null ? lookup.hashCode() : 0;
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
        return result;
    }

    public int identityHashCode() {
        int result = (lookup != null ? lookup.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (systemAccountId != null ? systemAccountId.hashCode() : 0);
        result = 31 * result + (systemAccountName != null ? systemAccountName.hashCode() : 0);
        result = 31 * result + (hourPeriod != null ? hourPeriod.hashCode() : 0);
        return result;
    }

}