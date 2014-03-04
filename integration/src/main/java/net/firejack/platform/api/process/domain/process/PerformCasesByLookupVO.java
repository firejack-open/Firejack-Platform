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

package net.firejack.platform.api.process.domain.process;

import net.firejack.platform.core.domain.AbstractDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/** Class provides data required for performing cases by lookup */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PerformCasesByLookupVO extends AbstractDTO {
	private static final long serialVersionUID = -8702625476913233104L;

	private String processLookup;
	private String entityLookup;
	private List<Long> entityIds;
	private Long explanationId;
	private String noteText;
	private Long assigneeId;

	/** @return  */
	public String getProcessLookup() {
		return processLookup;
	}

	/** @param processLookup  */
	public void setProcessLookup(String processLookup) {
		this.processLookup = processLookup;
	}

	/** @return  */
	public String getEntityLookup() {
		return entityLookup;
	}

	/** @param entityLookup  */
	public void setEntityLookup(String entityLookup) {
		this.entityLookup = entityLookup;
	}

	/** @return  */
	public List<Long> getEntityIds() {
		return entityIds;
	}

	/** @param entityIds  */
	public void setEntityIds(List<Long> entityIds) {
		this.entityIds = entityIds;
	}

	/** @return  */
	public Long getExplanationId() {
		return explanationId;
	}

	/** @param explanationId  */
	public void setExplanationId(Long explanationId) {
		this.explanationId = explanationId;
	}

	/** @return  */
	public String getNoteText() {
		return noteText;
	}

	/** @param noteText  */
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}

	/** @return  */
	public Long getAssigneeId() {
		return assigneeId;
	}

	/** @param assigneeId  */
	public void setAssigneeId(Long assigneeId) {
		this.assigneeId = assigneeId;
	}
}
