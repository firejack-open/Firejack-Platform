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
