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