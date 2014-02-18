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

/**
 * A column that will apply the appropriate sort name based on the associated model property's sort key
 */
Ext.define('OPF.core.component.grid.Column', {
    extend: 'Ext.grid.column.Column',
    alias: 'widget.opf-column',

    /**
     * @cfg alternatePropertyName An alternate simple property that will be used if the display field property or
     * association is not populated.  This is useful if you have a foreign key field that sometimes needs to be specifed
     * as a string that may not have a relationship.  I.e. a candidate name that has not had a full candidate
     * object put into the database yet.
     */

    displayField: 'name',

    statics: {
        renderer: function (value, metadata, record, rowIndex, colIndex, store, view) {
            //Get the fields from the model associated with the grid
            //var fields = this.getOwnerHeaderCt().ownerCt.store.model.prototype.fields;
            //var fieldConfig = fields.get(this.dataIndex);
            var column = view.headerCt.getGridColumns()[colIndex];
            var associations = store.model.prototype.associations;
            var associationConfig = associations.get(column.dataIndex);
            if (associationConfig && record[associationConfig.instanceName]) {
                var obj = record[associationConfig.instanceName];

                var fkName = OPF.ModelHelper.getProperty(obj, column.displayField);
                if (fkName || !column.alternatePropertyName) {
                    return fkName;
                }
            }

            if (column.alternatePropertyName) {
                return record.get(column.alternatePropertyName);
            } else {
                return value;
            }
        }
    },

    renderer: function(value, metadata, record, rowIndex, colIndex, store, view) {
        return OPF.core.component.grid.Column.renderer(value, metadata, record, rowIndex, colIndex, store, view);
    },

    /**
     * Returns the parameter to sort upon when sorting this header.
     * @return {String}
     */
    getSortParam: function() {
        var sortParam = this.dataIndex;
        if (this.displayField) {
            sortParam += '.' + this.displayField;
        }
        return sortParam;
    }

});