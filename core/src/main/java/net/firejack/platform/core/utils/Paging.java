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

package net.firejack.platform.core.utils;

import java.util.ArrayList;
import java.util.List;

public class Paging {

    private Integer offset;
    private Integer limit;
    private List<SortField> sortFields = new ArrayList<SortField>();

    /***/
    public Paging() {
    }

    /**
     * @param offset
     * @param limit
     */
    public Paging(Integer offset, Integer limit) {
        this.offset = offset != null ? offset : -1;
        this.limit = limit != null ? limit : -1;
    }

    /**
     * @param offset
     * @param limit
     * @param sortColumn
     * @param sortDirection
     */
    public Paging(Integer offset, Integer limit, String sortColumn, SortOrder sortDirection) {
        this(offset, limit);
        if (StringUtils.isNotBlank(sortColumn)) {
            SortField sortField = new SortField();
            sortField.setSortColumn(sortColumn);
            if (sortDirection != null) {
                sortField.setSortDirection(sortDirection);
            }
            this.sortFields.add(sortField);
        }
    }

    public Paging(Integer offset, Integer limit, String sortColumn, String sortDirection) {
        this(offset, limit);
        if (StringUtils.isNotBlank(sortColumn)) {
            SortField sortField = new SortField();
            sortField.setSortColumn(sortColumn);
            if (sortDirection != null) {
                sortField.setSortDirection(SortOrder.valueOf(sortDirection));
            }
            this.sortFields.add(sortField);
        }
    }

    public Paging(Integer offset, Integer limit, List<SortField> sortFields) {
        this.offset = offset;
        this.limit = limit;
        if (sortFields != null) {
            this.sortFields = sortFields;
        }
    }

    /**
     * @return
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * @param offset
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * @return
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    /**
     * @return
     */
    public String getSortColumn() {
        String sortColumn = null;
        if (!this.sortFields.isEmpty()) {
            sortColumn = this.sortFields.get(0).getSortColumn();
        }
        return sortColumn;
    }

    /**
     * @param sortColumn
     */
    public void setSortColumn(String sortColumn) {
        if (!this.sortFields.isEmpty()) {
            this.sortFields.get(0).setSortColumn(sortColumn);
        }
    }

    /**
     * @return
     */
    public SortOrder getSortDirection() {
        SortOrder sortDirection = null;
        if (!this.sortFields.isEmpty()) {
            sortDirection = this.sortFields.get(0).getSortDirection();
        }
        return sortDirection;
    }

    /**
     * @param sortDirection
     */
    public void setSortDirection(SortOrder sortDirection) {
        if (!this.sortFields.isEmpty()) {
            this.sortFields.get(0).setSortDirection(sortDirection);
        }
    }
}
