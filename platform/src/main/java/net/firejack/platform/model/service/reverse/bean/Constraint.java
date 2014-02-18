/*
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

package net.firejack.platform.model.service.reverse.bean;

public class Constraint {

    private String name;
    private Column sourceColumn;
    private Column destinationColumn;
    private Behavior onDelete;
    private Behavior onUpdate;
    private Table table;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(Column sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public Column getDestinationColumn() {
        return destinationColumn;
    }

    public void setDestinationColumn(Column destinationColumn) {
        this.destinationColumn = destinationColumn;
    }

    public Behavior getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(Behavior onDelete) {
        this.onDelete = onDelete;
    }

    public Behavior getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(Behavior onUpdate) {
        this.onUpdate = onUpdate;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "table=" + table.getName() +
                ", name='" + name + '\'' +
                ", sourceColumn=" + sourceColumn.getName() +
                ", destinationColumn=" + destinationColumn.getName() +
                ", onDelete=" + onDelete +
                ", onUpdate=" + onUpdate +
                '}';
    }
}
