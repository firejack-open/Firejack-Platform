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

Ext.define('OPF.core.component.form.SearchComboBox', {
    extend: 'OPF.core.component.form.FieldContainer',
    alias: ['widget.opf-searchcombo'],

    layout: 'anchor',

    name: null,          //association.name + '_' + associationModel.idProperty,
    fieldLabel: null,    //association.displayName || OPF.getSimpleClassName(association.model),
    subFieldLabel: null, //association.displayDescription,
    multiSelect: true,

    readOnly: false,

    model: null,
    validator: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        this.addEvents(
            'onBeforeStoreLoad'
        );
        OPF.core.component.form.SearchComboBox.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        this.initializedModel = Ext.create(this.model);

        this.selectedItemsStore = Ext.create('Ext.data.Store', {
            model: this.model
        });

        var tpl;
        var searchFields;
        if (OPF.isNotEmpty(this.initializedModel.self.template)) {
            tpl = this.initializedModel.self.template.join('');
            searchFields = this.getFieldsFromTemplate();
        } else {
            tpl = '{' + this.initializedModel.displayProperty + '}';
            var field = me.findModelField(this.initializedModel, this.initializedModel.displayProperty);
            searchFields = [field];
        }
        var selectedItemTpl = new Ext.XTemplate(
//            '<div class="x-boxselect x-form-field x-form-text">',
            	'<ul class="x-boxselect-list">',
                    '<tpl for=".">',
                        '<li class="x-boxselect-item item-wrap">',
                            '<div class="x-boxselect-item-text">',
                                tpl,
                            '</div>',

                            '<tpl if="this.allowToDelete()">',
                                '<div class="x-tab-close-btn x-boxselect-item-close"></div>',
                            '</tpl>',

                        '</li>',
                    '</tpl>',
                '</ul>',
//            '</div>'
            {
                disableFormats: true,
                // member functions:
                allowToDelete: function(){
                   return !me.readOnly;
                }
            }
        );

        this.selectedItemsView = Ext.create('Ext.view.View', {
            store: this.selectedItemsStore,
            tpl: selectedItemTpl,
            itemSelector: 'li.item-wrap',
            readOnly: this.readOnly,
            listeners: {
                itemclick: function (view, record, el, i, e) {
                    if (e.getTarget('.x-boxselect-item-close')) {
                        me.removeSelectSearchedItem(record);
                    }
                }
            }
        });

        this.searchComboStore = Ext.create('Ext.data.Store', {
            model: this.model,
            proxy: {
                type: 'ajax',
                url : this.initializedModel.self.restSuffixUrl + '/advanced-search',
                reader: {
                    type: 'json',
                    root: 'data'
                },
                startParam: 'offset',
                limitParam: 'limit'
            },
            pageSize: 10,
            autoLoad: false,
            listeners: {
                beforeload: function(store) {
                    var searchTerm = me.searchCombo.getRawValue();
                    me.fireEvent('onBeforeStoreLoad', me, store, searchTerm);
                }
            }
        });

        var comboValidator = OPF.isNotEmpty(this.validator) && Ext.isFunction(this.validator) ? this.validator : null;
        var searchComboConfig = {
            store: this.searchComboStore,
            excludeStore: this.selectedItemsStore,
            cls: 'opf-combo-field',
            anchor: '100%',
            valueField: this.initializedModel.idProperty,
            displayField: this.initializedModel.displayProperty,
            searchFields: searchFields,
            queryMode: 'remote',
            queryCaching: false,
            typeAhead: true,
            typeAheadDelay: 250,
            getParams: this.getParams,
            forceSelection: false,
            triggerAction: 'query',
            minChars: 0,
//            multiSelect: this.multiSelect,
            pageSize: 10,
            listeners: {
                select: this.selectSearchedItem,
                scope: this
            },
            listConfig: {
                loadingText: 'Searching...',
                emptyText: 'No matching items found.',
                getInnerTpl: function() {
                    return tpl;
                }
            },
            hidden: this.readOnly,
            validator: comboValidator
        };

        this.searchCombo = Ext.create('OPF.core.component.form.ComboBox', searchComboConfig);

        this.items = [
            this.selectedItemsView,
            this.searchCombo
        ];

        me.callParent();
    },

    getParams: function (queryString) {
        var me = this;

        var excludeIds = [];
        var excludeRecords = this.excludeStore.getRange();
        Ext.each(excludeRecords, function(excludeRecord) {
            excludeIds.push(excludeRecord.get(me.valueField));
        });

        var queryParameters = [];
        Ext.each(this.searchFields, function(searchField) {
            if (searchField.type.type == 'string') {
                var queryParam = [];
                if (OPF.isNotBlank(queryString)) {
                    queryParam.push({
                        field: searchField.name,
                        operation: 'LIKE',
                        value: queryString
                    });
                    if (excludeIds.length > 0) {
                        queryParam.push({
                            field: me.valueField,
                            operation: 'NOTIN',
                            value: excludeIds
                        });
                    }
                    queryParameters.push(queryParam);
                }
            }
        });

        if (queryParameters.length == 0 && excludeIds.length > 0) {
            queryParameters.push([
                {
                    field: me.valueField,
                    operation: 'NOTIN',
                    value: excludeIds
                }
            ])
        }

        return {
            queryParameters: Ext.encode(queryParameters)
        };
    },

    selectSearchedItem: function(combo, records, eOpts) {
        var me = this;
        Ext.Array.each(records, function(record) {
            if (!me.multiSelect) {
                me.selectedItemsStore.removeAll();
            }
            me.selectedItemsStore.add(record);
        });
        combo.clearValue();
    },

    removeSelectSearchedItem: function(record) {
        this.selectedItemsStore.remove(record);
    },

    getValue: function() {
        var me = this;
        var values = [];
        var records = this.selectedItemsStore.getRange();
        Ext.Array.each(records, function(record) {
            values.push(record.get(me.initializedModel.idProperty));
        });
        return values;
    },

    getSubmitValue: function(){
        return this.getValue();
    },

    getSubmitData: function(){
        var me = this,
        data = null;
        if (!me.disabled && me.submitValue && !me.isFileUpload()) {
            data = {};
            data[me.getName()] = me.getSubmitValue();
        }
        return data;
    },

    setValue: function(records) {
        this.selectedItemsStore.removeAll();
        if (records) {
            this.selectedItemsStore.add(records);
        }
    },

    setReadOnly: function(readOnly) {
        var me = this,
            old = me.readOnly;

        if (readOnly != old) {
            me.readOnly = readOnly;
            this.searchCombo.setVisible(!readOnly);
            this.selectedItemsView.readOnly = readOnly;
            this.selectedItemsView.refresh();
        }
    },

//    private methods
    getFieldsFromTemplate: function() {
        var me = this;

        var template = this.initializedModel.self.template[0];
        var fields = [];
        var matches = template.match(/\{[\w\.]+\}/gi);
        Ext.each(matches, function(match) {
            var fieldName = match.substring(1, match.length - 1);
            var field = me.findModelField(me.initializedModel, fieldName);
            if (OPF.isNotEmpty(field)) {
                fields.push(field);
            }
        });
        return fields;
    },

    findModelField: function(model, fieldName) {
        var foundField = null;
        Ext.each(model.fields.items, function(field) {
            if (field.name == fieldName) {
                foundField = field;
            }
        });
        return foundField;
    }

});