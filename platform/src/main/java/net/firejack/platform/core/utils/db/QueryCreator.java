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
