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


import net.firejack.platform.api.registry.model.RelationshipOption;

public class ForeignKey {

    private String name;
    private Table table;
    private Column column;
    private Table referenceTable;
    private Column referenceColumn;
    private RelationshipOption onDelete;
    private RelationshipOption onUpdate;

    /***/
    public ForeignKey() {
    }

    /**
     * @param name
     * @param tableName
     */
    public ForeignKey(String name, String tableName) {
        this.name = name;
        this.table = new Table(tableName);
    }

    /**
     * @param name
     * @param tableName
     * @param columnName
     * @param referenceTableName
     * @param referenceColumnName
     */
    public ForeignKey(String name, String tableName, String columnName, String referenceTableName, String referenceColumnName) {
        this.name = name;
        this.table = new Table(tableName);
        this.column = new Column(columnName);
        this.referenceTable = new Table(referenceTableName);
        this.referenceColumn = new Column(referenceColumnName);
    }

    /**
     * @param name
     * @param table
     * @param column
     * @param referenceTable
     * @param referenceColumn
     */
    public ForeignKey(String name, Table table, Column column, Table referenceTable, Column referenceColumn) {
        this.name = name;
        this.table = table;
        this.column = column;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
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
    public Column getColumn() {
        return column;
    }

    /**
     * @param column
     */
    public void setColumn(Column column) {
        this.column = column;
    }

    /**
     * @return
     */
    public Table getReferenceTable() {
        return referenceTable;
    }

    /**
     * @param referenceTable
     */
    public void setReferenceTable(Table referenceTable) {
        this.referenceTable = referenceTable;
    }

    /**
     * @return
     */
    public Column getReferenceColumn() {
        return referenceColumn;
    }

    /**
     * @param referenceColumn
     */
    public void setReferenceColumn(Column referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    /**
     * @return
     */
    public RelationshipOption getOnDelete() {
        return onDelete;
    }

    /**
     * @param onDelete
     */
    public void setOnDelete(RelationshipOption onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * @return
     */
    public RelationshipOption getOnUpdate() {
        return onUpdate;
    }

    /**
     * @param onUpdate
     */
    public void setOnUpdate(RelationshipOption onUpdate) {
        this.onUpdate = onUpdate;
    }

}
