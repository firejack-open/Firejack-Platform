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

package net.firejack.platform.core.model.registry.process;


import net.firejack.platform.core.model.BaseEntityModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "opf_activity_action")
public class ActivityActionModel extends BaseEntityModel {

    private String name;

    private String description;

    private StatusModel status;

    private ActivityModel activityFrom;

    private ActivityModel activityTo;


    @Column(length = 255, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 1024, nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status")
    @ForeignKey(name = "fk_aa_status")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public StatusModel getStatus() {
        return status;
    }

    public void setStatus(StatusModel status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_activity_from")
    @ForeignKey(name = "FK_ACTIVITY_ACTION_FROM")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ActivityModel getActivityFrom() {
        return activityFrom;
    }

    public void setActivityFrom(ActivityModel activityFrom) {
        this.activityFrom = activityFrom;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_activity_to")
    @ForeignKey(name = "FK_ACTIVITY_ACTION_TO")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ActivityModel getActivityTo() {
        return activityTo;
    }

    public void setActivityTo(ActivityModel activityTo) {
        this.activityTo = activityTo;
    }
}
