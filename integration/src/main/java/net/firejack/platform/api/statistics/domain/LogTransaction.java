/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
public class LogTransaction extends AbstractDTO {
    private static final long serialVersionUID = 7507975705654081626L;

    @Property
    private String packageLookup;
    @Property
    private Long transactions;
    @Property
    private Long entitiesLoaded;
    @Property
    private Long entitiesUpdated;
    @Property
    private Long entitiesInserted;
    @Property
    private Long entitiesDeleted;
    @Property
    private Long entitiesFetched;
    @Property
    private Long collectionsLoaded;
    @Property
    private Long collectionsUpdated;
    @Property
    private Long collectionsRecreated;
    @Property
    private Long collectionsRemoved;
    @Property
    private Long collectionsFetched;
    @Property
    private Long maxQueryTime;
    @Property
    private Long hourPeriod;
    @Property
    private Long dayPeriod;
    @Property
    private Long weekPeriod;
    @Property
    private Long monthPeriod;
    @Property
    private Date created;
    @Property
    private Date startTime;
    @Property
    private Date endTime;

    public String getPackageLookup() {
        return packageLookup;
    }

    public void setPackageLookup(String packageLookup) {
        this.packageLookup = packageLookup;
    }

    public Long getTransactions() {
        return transactions;
    }

    public void setTransactions(Long transactions) {
        this.transactions = transactions;
    }

    public Long getEntitiesLoaded() {
        return entitiesLoaded;
    }

    public void setEntitiesLoaded(Long entitiesLoaded) {
        this.entitiesLoaded = entitiesLoaded;
    }

    public Long getEntitiesUpdated() {
        return entitiesUpdated;
    }

    public void setEntitiesUpdated(Long entitiesUpdated) {
        this.entitiesUpdated = entitiesUpdated;
    }

    public Long getEntitiesInserted() {
        return entitiesInserted;
    }

    public void setEntitiesInserted(Long entitiesInserted) {
        this.entitiesInserted = entitiesInserted;
    }

    public Long getEntitiesDeleted() {
        return entitiesDeleted;
    }

    public void setEntitiesDeleted(Long entitiesDeleted) {
        this.entitiesDeleted = entitiesDeleted;
    }

    public Long getEntitiesFetched() {
        return entitiesFetched;
    }

    public void setEntitiesFetched(Long entitiesFetched) {
        this.entitiesFetched = entitiesFetched;
    }

    public Long getCollectionsLoaded() {
        return collectionsLoaded;
    }

    public void setCollectionsLoaded(Long collectionsLoaded) {
        this.collectionsLoaded = collectionsLoaded;
    }

    public Long getCollectionsUpdated() {
        return collectionsUpdated;
    }

    public void setCollectionsUpdated(Long collectionsUpdated) {
        this.collectionsUpdated = collectionsUpdated;
    }

    public Long getCollectionsRecreated() {
        return collectionsRecreated;
    }

    public void setCollectionsRecreated(Long collectionsRecreated) {
        this.collectionsRecreated = collectionsRecreated;
    }

    public Long getCollectionsRemoved() {
        return collectionsRemoved;
    }

    public void setCollectionsRemoved(Long collectionsRemoved) {
        this.collectionsRemoved = collectionsRemoved;
    }

    public Long getCollectionsFetched() {
        return collectionsFetched;
    }

    public void setCollectionsFetched(Long collectionsFetched) {
        this.collectionsFetched = collectionsFetched;
    }

    public Long getMaxQueryTime() {
        return maxQueryTime;
    }

    public void setMaxQueryTime(Long maxQueryTime) {
        this.maxQueryTime = maxQueryTime;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
