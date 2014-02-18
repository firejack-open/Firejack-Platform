/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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
