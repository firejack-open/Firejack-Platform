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
