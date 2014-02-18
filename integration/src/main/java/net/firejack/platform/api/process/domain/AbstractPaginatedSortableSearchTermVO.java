/**
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
package net.firejack.platform.api.process.domain;

import net.firejack.platform.core.domain.AbstractDTO;

public class AbstractPaginatedSortableSearchTermVO extends AbstractDTO {
	private static final long serialVersionUID = 8744970555443388904L;

	private Integer offset;
	private Integer limit;
	private String sortColumn;
	private String sortDirection;

	public AbstractPaginatedSortableSearchTermVO() {
	}

	public AbstractPaginatedSortableSearchTermVO(Integer offset, Integer limit, String sortColumn, String sortDirection) {
	    this.offset = offset;
	    this.limit = limit;
	    this.sortColumn = sortColumn;
	    this.sortDirection = sortDirection;
	}

	/**
	 * Gets the offset
	 *
	 * @return property showing from which item to start the listing from
	 */
	public Integer getOffset() {
	    return offset;
	}

	/**
	 * Sets the offset
	 *
	 * @param -      property showing from which item to start the listing from
	 * @param offset
	 */
	public void setOffset(Integer offset) {
	    this.offset = offset;
	}

	/**
	 * Gets the limit
	 *
	 * @return property showing how many items should be listed
	 */
	public Integer getLimit() {
	    return limit;
	}

	/**
	 * Sets the limit
	 *
	 * @param limit - property showing how many items should be listed
	 */
	public void setLimit(Integer limit) {
	    this.limit = limit;
	}

	/**
	 * Gets the sort column
	 *
	 * @return name of the column to sort by
	 */
	public String getSortColumn() {
	    return sortColumn;
	}

	/**
	 * Sets the sort column
	 *
	 * @param sortColumn - name of the column to sort by
	 */
	public void setSortColumn(String sortColumn) {
	    this.sortColumn = sortColumn;
	}

	/**
	 * Gets the sort direction
	 *
	 * @return sorting direction
	 */
	public String getSortDirection() {
	    return sortDirection;
	}

	/**
	 * Sets the sort direction
	 *
	 * @param sortDirection - sorting direction
	 */
	public void setSortDirection(String sortDirection) {
	    this.sortDirection = sortDirection;
	}

}
