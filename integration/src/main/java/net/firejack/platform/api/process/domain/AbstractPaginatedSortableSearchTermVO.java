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
