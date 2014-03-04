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

Ext.define('OPF.core.component.form.ComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias : ['widget.opf-form-combo', 'widget.opf-combo'],

    cls: 'opf-combo-field',

    mixins: {
        subLabelable: 'OPF.core.component.form.SubLabelable',
        errorHandler: 'OPF.core.component.form.ErrorHandler'
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    },

    onListSelectionChange: function(list, selectedRecords) {
        var me = this,
            isMulti = me.multiSelect,
            hasRecords = selectedRecords.length > 0;


        if (!me.ignoreSelection && me.isExpanded) {
            if (!isMulti && hasRecords) {
                Ext.defer(me.collapse, 1, me);
            }

            if (isMulti || (hasRecords && me.picker)) {
                me.setValue(selectedRecords, false);
            }
            if (hasRecords) {
                me.fireEvent('select', me, selectedRecords);
            }
            me.inputEl.focus();
        }
    },

    onExpand: function() {
        this.callParent();
        var picker = this.getPicker();
        if (picker && picker.pagingToolbar) {
            picker.pagingToolbar.doLayout();
        }
    },

    setDisplayField: function(displayField) {
        this.displayField = displayField;
        this.displayTpl = new Ext.XTemplate(
            '<tpl for=".">',
                '{[typeof values === "string" ? values : values["' + this.displayField + '"]]}',
                '<tpl if="xindex < xcount">' + this.delimiter + '</tpl>',
            '</tpl>'
        );
    }

});