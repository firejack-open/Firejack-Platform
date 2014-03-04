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

import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.core.model.BaseEntityModel;

import javax.persistence.*;


@Entity
@Table(name = "opf_log_entry")
public class LogEntryModel extends BaseEntityModel {

	private static final long serialVersionUID = -186256499165575931L;
	private String lookup;
    private Long userId;
    private String username;
    private Boolean success;
    private Long executeTime;
    private String errorMessage;
    private String details;
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
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return
     */
    @Column(name = "execute_time")
    public Long getExecuteTime() {
        return executeTime;
    }

    /**
     * @param executeTime
     */
    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    /**
     * @return
     */
    @Column(name = "error_message", length = 1023)
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    @Enumerated(EnumType.STRING)
    public LogEntryType getType() {
        return type;
    }

    public void setType(LogEntryType type) {
        this.type = type;
    }

}