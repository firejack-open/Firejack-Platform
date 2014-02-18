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

package net.firejack.platform.model.upgrader.dbengine.bean;

import java.util.List;

public class IndexKey {

    private String name;
    private Table table;
    private List<Column> columns;
    private IndexType type;

    /***/
    public IndexKey() {
    }

    /**
     * @param name
     * @param table
     */
    public IndexKey(String name, Table table) {
        this.name = name;
        this.table = table;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public Table getTable() {
        return table;
    }

    /**
     * @param table
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * @return
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * @param columns
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * @return
     */
    public IndexType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(IndexType type) {
        this.type = type;
    }

}
