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

package net.firejack.platform.api.process.domain;

import net.firejack.platform.core.utils.StringUtils;

import java.util.Date;
import java.util.List;

public class CaseSearchTermVO extends AbstractPaginatedSortableSearchTermVO{
	private static final long serialVersionUID = 5888879899813906062L;


	private Long assigneeId;
	private Long processId;
	private Long activityId;
	private Long statusId;
	private Boolean active;
	private Date startDate;
	private Date endDate;
	private String lookupPrefix;
	String description;
	private List<CustomFieldsSearchVO> customFields;

	/**
	 * Gets the assignee ID
	 * @return ID of the user assigned to the case
	 */
	public Long getAssigneeId() {
	    return assigneeId;
	}

	/**
	 * Sets the assignee ID
	 * @param assigneeId - ID of the user assigned to the case
	 */
	public void setAssigneeId(Long assigneeId) {
	    this.assigneeId = assigneeId;
	}

	/**
	 * Gets the process ID
	 * @return - ID of the process for the case
	 */
	public Long getProcessId() {
	    return processId;
	}

	/**
	 * Gets the process ID
	 * @param processId - Id of the process for the case
	 */
	public void setProcessId(Long processId) {
	    this.processId = processId;
	}

	/**
	 * Gets the activity ID
	 * @return - ID of the activity
	 */
	public Long getActivityId() {
	    return activityId;
	}

	/**
	 * Sets the activity ID
	 * @param activityId - ID of the activity
	 */
	public void setActivityId(Long activityId) {
	    this.activityId = activityId;
	}

	/**
	 * Gets the status ID
	 * @return ID of the case status
	 */
	public Long getStatusId() {
	    return statusId;
	}

	/**
	 * Sets the status ID
	 * @param statusId - ID of the case status
	 */
	public void setStatusId(Long statusId) {
	    this.statusId = statusId;
	}

	/**
	 * Gets active
	 * @return flag showing whether the case is active
	 */
	public Boolean getActive() {
	    return active;
	}

	/**
	 * Sets active
	 * @param active - flag showing whether the case is active
	 */
	public void setActive(Boolean active) {
	    this.active = active;
	}

	/**
	 * Gets the start date
	 * @return date when the case started
	 */
	public Date getStartDate() {
	    return startDate;
	}

	/**
	 * Sets the start date
	 * @param startDate - date when the case started
	 */
	public void setStartDate(Date startDate) {
	    this.startDate = startDate;
	}

	/**
	 * Gets the end date
	 * @return - date when the case ended
	 */
	public Date getEndDate() {
	    return endDate;
	}

	/**
	 * Sets the end date
	 * @param endDate - date when the case ended
	 */
	public void setEndDate(Date endDate) {
	    this.endDate = endDate;
	}

	/**
	 * Gets the description
	 * @return - description of the case
	 */
	public String getDescription() {
	    return description;
	}

	/**
	 * Sets the description
	 * @param description - description of the case
	 */
	public void setDescription(String description) {
	    this.description = description;
	}

	/**
	 * Gets the lookup prefix
	 * @return lookup prefix
	 */
	public String getLookupPrefix() {
	    return lookupPrefix;
	}

	/**
	 * Sets the lookup prefix
	 * @param lookupPrefix - lookup prefix
	 */
	public void setLookupPrefix(String lookupPrefix) {
	    this.lookupPrefix = lookupPrefix;
	}

	public List<CustomFieldsSearchVO> getCustomFields() {
	    return customFields;
	}

	public void setCustomFields(List<CustomFieldsSearchVO> customFields) {
	    this.customFields = customFields;
	}

    @Override
    public void setSortColumn(String sortColumn) {
        super.setSortColumn(processSortColumn(sortColumn));
    }

    public static String processSortColumn(String sortColumnToProcess) {
        if (StringUtils.isNotBlank(sortColumnToProcess)) {
            if (sortColumnToProcess.equalsIgnoreCase("statusName")) {
                sortColumnToProcess = "status.name";
            } else if (sortColumnToProcess.equalsIgnoreCase("processName")) {
                sortColumnToProcess = "process.name";
            } else if (sortColumnToProcess.equalsIgnoreCase("assigneeName")) {
                sortColumnToProcess = "assignee.username";
            }
        }
        return sortColumnToProcess;
    }

}