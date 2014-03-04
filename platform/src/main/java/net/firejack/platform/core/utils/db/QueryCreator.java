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

package net.firejack.platform.core.utils.db;

import net.firejack.platform.api.process.domain.AbstractPaginatedSortableSearchTermVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to make query using StringBuffer.append() and putting the query params in a Map. Subclasses should be created to
 * select objects with isCount = false and to find the count of records with isCount = true and all other properties set the same (i.e.
 * should be reused in queryForList() and count() methods with only simple isCount switch, in order to keep list & count query conditions
 * in one place).
 */
public abstract class QueryCreator {

    protected StringBuffer queryBuff = new StringBuffer();
    protected Map<String, Object> queryParams = new HashMap<String, Object>();
    protected List<String> additionalSelectColumns;
    protected Integer offset;
    protected Integer limit;
    protected String sortColumn;
    protected String sortDirection;
    protected boolean isCount;

    protected QueryCreator(AbstractPaginatedSortableSearchTermVO paging, boolean isCount) {
        if (paging != null) {
            this.offset = paging.getOffset();
            this.limit = paging.getLimit();
            this.sortColumn = paging.getSortColumn();
            this.sortDirection = paging.getSortDirection();
        }
        this.isCount = isCount;
    }

    public void setAdditionalSelectColumns(List<String> additionalSelectColumns) {
        this.additionalSelectColumns = additionalSelectColumns;
    }

    protected void appendQuery(String query) {
        queryBuff.append(query);
    }

    protected void appendQuery(Object query) {
        queryBuff.append(query);
    }

    protected void addParam(String name, Object value) {
        queryParams.put(name, value);
    }

    public String getQuery() {
        return queryBuff.toString();
    }

    public Map getQueryParams() {
        return queryParams;
    }
}
