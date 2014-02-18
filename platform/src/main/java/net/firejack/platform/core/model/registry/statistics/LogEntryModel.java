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