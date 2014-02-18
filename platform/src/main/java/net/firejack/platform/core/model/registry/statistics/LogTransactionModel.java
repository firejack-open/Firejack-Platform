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

package net.firejack.platform.core.model.registry.statistics;


import net.firejack.platform.core.model.BaseEntityModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "opf_log_transaction")
public class LogTransactionModel extends BaseEntityModel {
    private static final long serialVersionUID = 4107300499774656896L;

    private String packageLookup;

    private Long transactions;

    private Long entitiesLoaded;
    private Long entitiesUpdated;
    private Long entitiesInserted;
    private Long entitiesDeleted;
    private Long entitiesFetched;

    private Long collectionsLoaded;
    private Long collectionsUpdated;
    private Long collectionsRecreated;
    private Long collectionsRemoved;
    private Long collectionsFetched;

    private Long maxQueryTime;

    private Long hourPeriod;
    private Long dayPeriod;
    private Long weekPeriod;
    private Long monthPeriod;

    private Long startTime;
    private Long endTime;

    @Column(length = 2048)
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

    public void setCollectionsRecreated(Long collectionsInserted) {
        this.collectionsRecreated = collectionsInserted;
    }

    public Long getCollectionsRemoved() {
        return collectionsRemoved;
    }

    public void setCollectionsRemoved(Long collectionsDeleted) {
        this.collectionsRemoved = collectionsDeleted;
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

    @Transient
    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    @Transient
    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
