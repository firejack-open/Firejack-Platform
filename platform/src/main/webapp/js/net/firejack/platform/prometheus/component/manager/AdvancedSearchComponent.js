/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

Ext.define('OPF.prometheus.component.manager.AdvancedSearchComponent', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.prometheus.component.advanced-search-component',

    cls: 'advanced-search-component',

    border: false,

    model: null,
    managerPanel: null,
    store: null,

    visibleOnly: false,

    searchMode: 'ADVANCED_SEARCH',

    configs: null,

    operations: {
        'LIKECS': {
            displayName: 'contains',
            description: 'Operation LIKECS',
            isValuable: true
        },
        'LIKECSFIRST': {
            displayName: 'start with',
            description: 'Operation LIKECSFIRST',
            isValuable: true
        },
        'LIKE': {
            displayName: 'contains',
            description: 'Operation LIKE',
            isValuable: true
        },
        'LIKEFIRST': {
            displayName: 'starts with',
            description: 'Operation LIKEFIRST',
            isValuable: true
        },
        'EQUALS': {
            displayName: '=',
            description: 'Operation EQUALS',
            isValuable: true
        },
        'NOTEQUALS': {
            displayName: '!=',
            description: 'Operation NOTEQUALS',
            isValuable: true
        },
        'LESSTHAN': {
            displayName: '<',
            description: 'Operation LESSTHAN',
            isValuable: true
        },
        'GREATERTHAN': {
            displayName: '>',
            description: 'Operation GREATERTHAN',
            isValuable: true
        },
        'ISNULL': {
            displayName: 'is null',
            description: 'Operation ISNULL',
            isValuable: false
        },
        'ISNOTNULL': {
            displayName: 'is not null',
            description: 'Operation ISNOTNULL',
            isValuable: false
        },
        'ISEMPTY': {
            displayName: 'is empty',
            description: 'Operation ISEMPTY',
            isValuable: false
        },
        'ISNOTEMPTY': {
            displayName: 'is not empty',
            description: 'Operation ISNOTEMPTY',
            isValuable: false
        },
        'IN': {
            displayName: 'one of value',
            description: 'Operation IN',
            isValuable: true,
            isMultiply: true
        },
        'PAST': {
            displayName: 'days ago',
            description: 'Operation PAST',
            isValuable: true
        },
        'OLDER': {
            displayName: 'older than',
            description: 'Operation OLDER',
            isValuable: true,
            template: '<div class="field-name">{0}</div>' +
                '<div class="operation">{1}</div>' +
                '<div class="value">{2} day(s)</div>'
        },
        'FIELDNOTEQUALS': {
            displayName: 'field !=',
            description: 'Operation FIELDNOTEQUALS'
        },
        'FIELDEQUALS': {
            displayName: 'field =',
            description: 'Operation FIELDEQUALS'
        },
        'FIELDLESSTHAN': {
            displayName: 'field <',
            description: 'Operation FIELDLESSTHAN'
        },
        'FIELDGREATERTHAN': {
            displayName: 'field >',
            description: 'Operation FIELDGREATERTHAN'
        }
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        this.preventionOfRecursion = new Ext.util.MixedCollection();

        var fieldData = [];
        var model = Ext.create(this.model);
        me.fillFields(fieldData, model, '', '', 0);

        this.fieldNameStore = Ext.create('Ext.data.Store', {
            fields: [
                {name: 'uid',  type: 'string'},
                {name: 'name',  type: 'string'},
                {name: 'displayName',   type: 'string'},
                {name: 'fieldType', type: 'string'},
                {name: 'model', type: 'string'},
                {name: 'associationType', type: 'string'}
            ],
            data: fieldData
        });

        this.searchFieldNameCombo = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'opf-combo',
            name: 'searchFieldName',
            cls: 'opf-combo-field',
            fieldLabel: 'Field',
            labelAlign: 'top',
            store: this.fieldNameStore,
            queryMode: 'local',
            editable: false,
            typeAhead: false,
            forceSelection: true,
            valueField: 'uid',
            displayField: 'displayName',
            margin: '0 5 0 0',
            listConfig: {
                getInnerTpl: function() {
                    return '<div data-qtip="{displayName}">{displayName}</div>';
                }
            },
            listeners: {
                select: {
                    fn: this.onSelectSearchFieldNameCombo,
                    scope: this
                }
            }

        }, this.configs.searchFieldNameCombo));

        this.operationStore = Ext.create('Ext.data.Store', {
            fields: [
                {name: 'name',  type: 'string'},
                {name: 'displayName',   type: 'string'},
                {name: 'description', type: 'string'},
                {name: 'isValuable', type: 'boolean'},
                {name: 'isMultiply', type: 'boolean', defaultValue: false}
            ],
            data: []
        });

        this.searchOperationCombo = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'opf-combo',
            name: 'searchOperationName',
            cls: 'opf-combo-field',
            fieldLabel: 'Operation',
            labelAlign: 'top',
            store: this.operationStore,
            queryMode: 'local',
            editable: false,
            typeAhead: false,
            forceSelection: true,
            valueField: 'name',
            displayField: 'displayName',
            margin: '0 5 0 0',
            width: 110,
            listConfig: {
                getInnerTpl: function() {
                    return '<div data-qtip="{description}">{displayName}</div>';
                }
            },
            listeners: {
                select: {
                    fn: this.onSelectSearchOperationCombo,
                    scope: this
                }
            }
        }, this.configs.searchOperationCombo));

        this.searchValueIntegerNumberField = Ext.ComponentMgr.create({
            xtype: 'opf-number',
            name: 'searchValue',
            cls: 'opf-number-field',
            useThousandSeparator: false,
            allowDecimals: false,
            flex: 1,
            hidden: true
        });

        this.searchValueDecimalNumberField = Ext.ComponentMgr.create({
            xtype: 'opf-number',
            name: 'searchValue',
            cls: 'opf-number-field',
            useThousandSeparator: false,
            decimalPrecision: 10,
            flex: 1,
            hidden: true
        });

        this.searchValueDateField = Ext.ComponentMgr.create({
            xtype: 'opf-date',
            name: 'searchValue',
            cls: 'opf-date-field',
            format: 'Y-m-d',
            flex: 1,
            hidden: true
        });

        this.searchValueTimeField = Ext.ComponentMgr.create({
            xtype: 'opf-time',
            name: 'searchValue',
            cls: 'opf-time-field',
            format: 'g:i A',
            flex: 1,
            hidden: true
        });

        this.searchValueDateTimeField = Ext.ComponentMgr.create({
            xtype: 'opf-datetime',
            name: 'searchValue',
            cls: 'opf-date-field',
            dateFormat: 'Y-m-d',
            timeFormat: 'g:i A',
            dateTimeFormat: 'Y-m-d\\TH:i:s.000O',
            flex: 1,
            hidden: true
        });

        this.searchValueTextField = Ext.ComponentMgr.create({
            xtype: 'opf-text',
            name: 'searchValue',
            cls: 'opf-text-field',
            flex: 1,
            hidden: true
        });

        this.searchValueCurrencyField = Ext.ComponentMgr.create({
            xtype: 'opf-number',
            name: 'searchValue',
            cls: 'opf-number-field',
//            fieldLabel: 'Value',
//            labelAlign: 'top',
            currencySymbol: '$',
            alwaysDisplayDecimals: true,
            flex: 1,
            hidden: true
        });

        this.searchValueFlagField = Ext.ComponentMgr.create({
            xtype: 'opf-checkbox',
            name: 'searchValue',
            cls: 'opf-check-box',
//            fieldLabel: 'Value',
//            labelAlign: 'top',
            flex: 1,
            hidden: true
        });

        this.searchValueAssociationField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            name: 'searchValue',
            cls: 'opf-combo',
            flex: 1,
            queryMode: 'local',
            editable: false,
            hidden: true,
            pageSize: 10
        });

        this.searchListValuesContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            border: true,
            cls: 'search-tags-bar',
            hidden: true
        });

        this.andSearchValueButton = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'button',
            text: '+',
            handler: this.onClickAddSearchValueButton,
            scope: this,
            height: 28,
            width: 28,
            hidden: true
        }, this.configs.andSearchValueButton));

        this.andSearchConditionButton = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'button',
            text: 'AND',
            handler: this.onClickAddSearchConditionButton,
            scope: this,
            height: 28,
            width: 45
        }, this.configs.andSearchConditionButtonConfigs));

        this.orSearchConditionButton = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'button',
            text: 'OR',
            handler: this.onClickOrSearchConditionButton,
            scope: this,
            height: 28,
            width: 45
        }, this.configs.orSearchConditionButtonConfigs));

        this.searchConditionContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            hidden: true
        });

        this.searchValueFieldContainer = Ext.ComponentMgr.create({
            xtype: 'opf-fieldcontainer',
            layout: 'hbox',
            fieldLabel: 'Value',
            labelAlign: 'top',
            flex: 1,
            margin: '0 5 0 0',
            items: [
                this.searchValueIntegerNumberField,
                this.searchValueDecimalNumberField,
                this.searchValueCurrencyField,
                this.searchValueDateField,
                this.searchValueTimeField,
                this.searchValueDateTimeField,
                this.searchValueFlagField,
                this.searchValueTextField,
                this.searchValueAssociationField,
                this.andSearchValueButton
            ]
        });

        this.items = [
            {
                xtype: 'form',
                monitorValid: true,
                border: false,
                hidden: me.visibleOnly,
                items: [
                    {
                        xtype: 'opf-fieldcontainer',
                        layout: 'hbox',
                        flex: 1,
                        items: [
                            this.searchFieldNameCombo,
                            { xtype: 'splitter' },
                            this.searchOperationCombo,
                            { xtype: 'splitter' },
                            this.searchValueFieldContainer,
                            {
                                xtype: 'container',
                                layout: 'hbox',
                                width: 100,
                                cls: 'search-operand-buttons',
                                items: [
                                    { xtype: 'splitter' },
                                    this.andSearchConditionButton,
                                    { xtype: 'splitter' },
                                    this.orSearchConditionButton
                                ]
                            }
                        ]
                    },
                    this.searchListValuesContainer
                ]
            },
            this.searchConditionContainer
        ];

        var firstFieldRecord = this.searchFieldNameCombo.getStore().getAt(0);
        this.searchFieldNameCombo.select(firstFieldRecord);
        this.searchFieldNameCombo.fireEvent('select', this.searchFieldNameCombo, [firstFieldRecord]);

        this.callParent();
    },

    fillFields: function(fieldData, model, fieldPrefix, namePrefix, deep) {
        var me = this;

        var modelName = model.$className;
        var count = this.preventionOfRecursion.get(modelName);
        count = OPF.ifEmpty(count, 0);
        if (count <= 2) {
            this.preventionOfRecursion.add(modelName, ++count);

            Ext.each(model.fields.items, function(field) {
                if (field.fieldType) {
                    var displayName = (field.displayName || field.name);
                    fieldData.push({
                        uid: fieldPrefix + field.name,
                        name: fieldPrefix + field.name,
                        displayName: namePrefix + displayName,
                        fieldType: field.fieldType,
                        deep: deep
                    });
                }
            });

            Ext.each(model.associations.items, function(association) {
                var associationModel = Ext.create(association.model);
                var associationPrefix = fieldPrefix + association.name + '.';
                var displayName = (association.displayName || OPF.getSimpleClassName(association.model));
                var associationNamePrefix = namePrefix + displayName + ' ';

                var searchMode = ['MODEL', 'FIELDS'];
                if (association.searchMode) {
                    if (Ext.isArray(association.searchMode)) {
                        searchMode = association.searchMode;
                    } else {
                        searchMode = [association.searchMode];
                    }
                }
                if (Ext.Array.contains(searchMode, 'MODEL')) {
                    fieldData.push({
                        uid: associationPrefix,
                        name: associationPrefix + associationModel.idProperty,
                        displayName: namePrefix + displayName,
                        fieldType: 'ASSOCIATION',
                        deep: deep,
                        model: association.model,
                        associationType: association.type
                    });
                }
                if (Ext.Array.contains(searchMode, 'FIELDS')) {
                    me.fillFields(fieldData, associationModel, associationPrefix, associationNamePrefix, ++deep);
                }
            });
        }
    },

    getOperationsDataByFieldType: function(fieldType, associationType) {
        var me = this;

        var operations = [];
        switch(fieldType) {
            case 'NUMERIC_ID':
            case 'INTEGER_NUMBER':
            case 'LARGE_NUMBER':
            case 'DECIMAL_NUMBER':
            case 'CURRENCY':
                operations = ['EQUALS', 'NOTEQUALS', 'LESSTHAN', 'GREATERTHAN', 'ISNULL', 'ISNOTNULL', 'IN'];
                break;
            case 'TIME':
                operations = ['EQUALS', 'NOTEQUALS', 'LESSTHAN', 'GREATERTHAN', 'ISNULL', 'ISNOTNULL', 'IN'];
                break;
            case 'DATE':
            case 'EVENT_TIME':
            case 'CREATION_TIME':
            case 'UPDATE_TIME':
                operations = ['EQUALS', 'NOTEQUALS', 'LESSTHAN', 'GREATERTHAN', 'ISNULL', 'ISNOTNULL', 'IN', 'PAST', 'OLDER'];
                break;
            case 'FLAG':
                operations = ['EQUALS', 'NOTEQUALS'];
                break;
            case 'UNIQUE_ID':
            case 'PASSWORD':
            case 'NAME':
            case 'DESCRIPTION':
            case 'TINY_TEXT':
            case 'SHORT_TEXT':
            case 'MEDIUM_TEXT':
            case 'LONG_TEXT':
            case 'UNLIMITED_TEXT':
            case 'RICH_TEXT':
            case 'PHONE_NUMBER':
            case 'SSN':
            case 'CODE':
            case 'SECRET_KEY':
            case 'LABEL':
                operations = ['EQUALS', 'NOTEQUALS', 'LIKECSFIRST', 'LIKE', 'LIKEFIRST', 'ISNULL', 'ISNOTNULL', 'IN'];
                break;
            case 'ASSOCIATION':
                if (associationType == 'belongsTo') {
                    operations = ['EQUALS', 'NOTEQUALS', 'ISNULL', 'ISNOTNULL'];
                } else if (associationType == 'hasMany') {
                    operations = ['EQUALS', 'NOTEQUALS', 'ISEMPTY', 'ISNOTEMPTY'];
                } else {
                    operations = ['EQUALS', 'NOTEQUALS'];
                }
                break;
            default:
                operations = ['EQUALS', 'NOTEQUALS'];
                break;
        }

        var operationsData = [];
        Ext.each(operations, function(operationName) {
            var operation = me.operations[operationName];
            var operationData = {
                name: operationName,
                displayName: operation.displayName,
                description: operation.description,
                isValuable: operation.isValuable,
                isMultiply: operation.isMultiply
            };
            operationsData.push(operationData);
        });
        return operationsData;
    },

    displaySearchValueFieldByFieldType: function(fieldType) {
        var me = this;

        this.searchValueIntegerNumberField.hide();
        this.searchValueDecimalNumberField.hide();
        this.searchValueCurrencyField.hide();
        this.searchValueDateField.hide();
        this.searchValueTimeField.hide();
        this.searchValueDateTimeField.hide();
        this.searchValueFlagField.hide();
        this.searchValueTextField.hide();
        this.searchValueAssociationField.hide();

        var searchValueField;
        switch(fieldType) {
            case 'NUMERIC_ID':
            case 'INTEGER_NUMBER':
            case 'LARGE_NUMBER':
                searchValueField = this.searchValueIntegerNumberField;
                break;
            case 'DECIMAL_NUMBER':
                searchValueField = this.searchValueDecimalNumberField;
                break;
            case 'CURRENCY':
                searchValueField = this.searchValueCurrencyField;
                break;
            case 'DATE':
                searchValueField = this.searchValueDateField;
                break;
            case 'TIME':
                searchValueField = this.searchValueTimeField;
                break;
            case 'EVENT_TIME':
            case 'CREATION_TIME':
            case 'UPDATE_TIME':
                searchValueField = this.searchValueDateTimeField;
                break;
            case 'FLAG':
                searchValueField = this.searchValueFlagField;
                break;
            case 'UNIQUE_ID':
            case 'PASSWORD':
            case 'NAME':
            case 'DESCRIPTION':
            case 'TINY_TEXT':
            case 'SHORT_TEXT':
            case 'MEDIUM_TEXT':
            case 'LONG_TEXT':
            case 'UNLIMITED_TEXT':
            case 'RICH_TEXT':
            case 'PHONE_NUMBER':
            case 'SSN':
            case 'CODE':
                searchValueField = this.searchValueTextField;
                break;
            case 'ASSOCIATION':
                searchValueField = this.searchValueAssociationField;
                break;
            default:
                searchValueField = this.searchValueTextField;
                break;
        }
        searchValueField.show();
        return searchValueField;
    },

    onSelectSearchFieldNameCombo: function(combo, records) {
        var selectedRecord = records[0];
        var fieldType = selectedRecord.get('fieldType');
        var associationType = selectedRecord.get('associationType');
        var operationsData = this.getOperationsDataByFieldType(fieldType, associationType);
        this.operationStore.loadData(operationsData);
        var operationRecord = this.searchOperationCombo.getStore().getAt(0);
        this.searchOperationCombo.select(operationRecord);
        var isValuable = operationRecord.get('isValuable');
        var isMultiply = operationRecord.get('isMultiply');

        this.searchValueField = this.displaySearchValueFieldByFieldType(fieldType);
        if (isValuable) {
            this.searchValueField.enable();
            if (fieldType == 'ASSOCIATION') {
                var model = selectedRecord.get('model');
                var associationModel = Ext.create(model);
                var store = Ext.create('Ext.data.Store', {
                    model: model,
                    proxy: {
                        type: 'ajax',
                        url : associationModel.self.restSuffixUrl,
                        reader: {
                            type: 'json',
                            root: 'data'
                        },
                        startParam: 'offset',
                        limitParam: 'limit'
                    },
                    pageSize: 10,
                    autoLoad: false,
                    autoDestroy: true
                });
                this.searchValueField.valueField = associationModel.idProperty;
                this.searchValueField.setDisplayField(associationModel.displayProperty);
                this.searchValueField.bindStore(store, false);
                store.load();
            }
        } else {
            this.searchValueField.disable();
        }
        this.andSearchValueButton.setVisible(isMultiply);
        if (isMultiply) {
            this.searchValueFieldContainer.addCls('add-value-active');
        } else {
            this.searchValueFieldContainer.removeCls('add-value-active');
        }
    },

    onSelectSearchOperationCombo: function(combo, records) {
        var operationRecord = records[0];
        var searchFieldNameUID = this.searchFieldNameCombo.getValue();
        var searchFieldNameRecord = this.fieldNameStore.findRecord('uid', searchFieldNameUID);
        var fieldType = searchFieldNameRecord.get('fieldType');
        if (fieldType == 'DATE' || fieldType == 'EVENT_TIME' || fieldType == 'CREATION_TIME' || fieldType == 'UPDATE_TIME') {
            var operationName = operationRecord.get('name');
            if (operationName == 'PAST' || operationName == 'OLDER') {
                this.searchValueField = this.displaySearchValueFieldByFieldType('INTEGER_NUMBER');
            } else {
                this.searchValueField = this.displaySearchValueFieldByFieldType(fieldType);
            }
        }
        var isValuable = operationRecord.get('isValuable');
        this.searchValueField.setDisabled(!isValuable);
        var isMultiply = operationRecord.get('isMultiply');
        this.andSearchValueButton.setVisible(isMultiply);
        if (isMultiply) {
            this.searchValueFieldContainer.addCls('add-value-active');
        } else {
            this.searchValueFieldContainer.removeCls('add-value-active');
        }
    },

    onClickOrSearchConditionButton: function() {
        this.addSearchCondition(true);
    },

    onClickAddSearchConditionButton: function() {
        this.addSearchCondition(false);
    },

    onClickSearchButton: function() {
        var queryParameters = this.getQueryParameters();
        if (queryParameters.length == 0) {
            this.addSearchCondition(true);
        }
        this.searchPanel.executeSearch(this.searchMode);
    },

    onClickAddSearchValueButton: function() {
        var me = this;

        var itemValue = this.searchValueField.getValue();
        if (OPF.isNotEmpty(itemValue)) {
            var searchDisplayItemValue = itemValue;
            if (this.searchValueField.getDisplayValue) {
                searchDisplayItemValue = this.searchValueField.getDisplayValue();
            }

            this.searchValueField.reset();

            var searchItemValue = Ext.ComponentMgr.create({
                xtype: 'container',
//                cls: 'search-item-value',
                border: true,
                cls: 'search-tag',
                html: '<div class="value">' + searchDisplayItemValue + '</div>',
                searchDisplayValue: searchDisplayItemValue,
                searchValue: itemValue
            });

            var deleteSearchItemValueButton = Ext.ComponentMgr.create({
                xtype: 'button',
                ui: 'close',
                width: 12,
                height: 12,
                scale: 'small',
                handler: function() {
                    me.searchListValuesContainer.remove(searchItemValue);
                    if (me.searchListValuesContainer.items.items.length == 0) {
                        me.searchListValuesContainer.hide();
                    }
                }
            });
            searchItemValue.add(deleteSearchItemValueButton);
            this.searchListValuesContainer.add(searchItemValue);
            this.searchListValuesContainer.show();
        }
    },

    createConditionContainer: function() {
        var me = this;

        var conditionContainer = Ext.create('Ext.container.Container', {
            cls: 'search-tags-bar',
            border: true,
            listeners: {
                render: function(container) {
                    if (!me.visibleOnly) {
                        me.initializeComponentDragZone(container);
                        me.initializeComponentDropZone(container);
                    }
                }
            }
        });

        this.searchConditionContainer.add(conditionContainer);
        this.searchConditionContainer.show();

        return conditionContainer;
    },

    addSearchCondition: function(isNeedNewOrContainer) {
        var me = this;

        var searchFieldNameUID = this.searchFieldNameCombo.getValue();
        var searchOperation = this.searchOperationCombo.getValue();
        var isValuable = this.operations[searchOperation].isValuable;
        var isMultiply = this.operations[searchOperation].isMultiply;
        var searchValue = null;
        var searchDisplayValue = '';
        if (this.searchValueField.isVisible() && isValuable) {
            if (isMultiply) {
                var searchListValues = this.searchListValuesContainer.items.items;
                if (searchListValues.length > 0) {
                    searchValue = [];
                    var searchDisplayValues = [];
                    Ext.each(searchListValues, function(searchItemValue) {
                        searchValue.push(searchItemValue.searchValue);
                        searchDisplayValues.push(searchItemValue.searchDisplayValue);
                    });
                    searchDisplayValue = searchDisplayValues.join(', ');
                    this.searchListValuesContainer.removeAll();
                    this.searchListValuesContainer.hide();
                }
            } else {
                searchValue = this.searchValueField.getValue();
                searchDisplayValue = searchValue;
                if (this.searchValueField.getDisplayValue) {
                    searchDisplayValue = this.searchValueField.getDisplayValue();
                }
            }
        }

        if (OPF.isNotEmpty(searchValue) || !isValuable) {
            var searchFieldNameRecord = this.fieldNameStore.findRecord('uid', searchFieldNameUID);
            var searchFieldName = searchFieldNameRecord.get('name');
            var searchFieldType = searchFieldNameRecord.get('fieldType');

            if (searchFieldType == 'ASSOCIATION' && !isValuable) {
                searchFieldName = searchFieldName.substring(0, searchFieldName.lastIndexOf('.'));
            }

            var searchQueryParameter = {
                field: searchFieldName,
                operation: searchOperation
            };
            if (OPF.isNotEmpty(searchValue) && isValuable) {
                searchQueryParameter.value = searchValue;
            }

            var humanSearchFieldName = searchFieldNameRecord.get('displayName');
            var humanSearchOperation = this.operations[searchOperation].displayName;
            var formattedContent = this.operations[searchOperation].template;
            if (OPF.isEmpty(formattedContent)) {
                formattedContent = '<div class="field-name">{0}</div>' +
                    '<div class="operation">{1}</div>' +
                    '<div class="value">{2}</div>'
            }
            var searchCondition = Ext.ComponentMgr.create({
                xtype: 'container',
                cls: 'search-tag',
                border: true,
                html: Ext.String.format(formattedContent, humanSearchFieldName, humanSearchOperation, searchDisplayValue),
                searchQueryParameter: searchQueryParameter
            });

            var conditionContainer = null;
            if (isNeedNewOrContainer) {
                conditionContainer = this.createConditionContainer();
            } else {
                Ext.each(this.searchConditionContainer.items.items, function(item) {
                    conditionContainer = item;
                });
                if (OPF.isEmpty(conditionContainer)) {
                    conditionContainer = this.createConditionContainer();
                }
            }

            var deleteSearchConditionButton = Ext.ComponentMgr.create({
                xtype: 'button',
                ui: 'close',
                height: '12',
                width: '12',
                handler: function() {
                    Ext.each(me.searchConditionContainer.items.items, function(conditionContainer) {
                        conditionContainer.remove(searchCondition);
                    });
                    me.removeEmptyConditionContainer();
                    me.searchPanel.executeSearch(me.searchMode);
                }
            });
            searchCondition.add(deleteSearchConditionButton);

            conditionContainer.add(searchCondition);
            this.searchConditionContainer.show();
        } else {
            // here we can highlight search field and indicate that field is empty
        }
    },

    getQueryParameters: function() {
        var orQueryParameters = [];
        Ext.each(this.searchConditionContainer.items.items, function(conditionContainer) {
            var andQueryParameters = [];
            Ext.each(conditionContainer.items.items, function(searchCondition) {
                andQueryParameters.push(searchCondition.searchQueryParameter);
            });
            orQueryParameters.push(andQueryParameters);
        });
        return orQueryParameters;
    },

    setQueryParameters: function(orQueryParameters) {
        var me = this;

        Ext.each(this.searchConditionContainer.items.items, function(conditionContainer) {
            conditionContainer.removeAll();
        });
        this.removeEmptyConditionContainer();

        Ext.each(orQueryParameters, function(andQueryParameters) {
            var conditionContainer = me.createConditionContainer();

            Ext.each(andQueryParameters, function(queryParameter) {
                var searchFieldNameRecord = me.fieldNameStore.findRecord('name', queryParameter.field);

                var humanSearchFieldName = searchFieldNameRecord.get('displayName');
                var humanSearchOperation = me.operations[queryParameter.operation].displayName;
                var isMultiply = me.operations[queryParameter.operation].isMultiply;

                var searchDisplayValue = queryParameter.value;
                if (isMultiply) {
                    searchDisplayValue = queryParameter.value.join(', ');
                }

                var searchCondition = Ext.ComponentMgr.create({
                    xtype: 'container',
                    cls: 'search-tag',
                    border: true,
                    html:
                        '<div class="field-name">' + humanSearchFieldName + '</div>' +
                        '<div class="operation">' + humanSearchOperation + '</div>' +
                        '<div class="value">' + OPF.ifEmpty(searchDisplayValue, '') + '</div>',
                    searchQueryParameter: queryParameter
                });

                if (!me.visibleOnly) {
                    var deleteSearchConditionButton = Ext.ComponentMgr.create({
                        xtype: 'button',
                        ui: 'close',
                        height: '12',
                        width: '12',
                        handler: function() {
                            Ext.each(me.searchConditionContainer.items.items, function(conditionContainer) {
                                conditionContainer.remove(searchCondition);
                            });
                            me.removeEmptyConditionContainer();
                            me.searchPanel.executeSearch(me.searchMode);
                        }
                    });
                    searchCondition.add(deleteSearchConditionButton);
                }
                conditionContainer.add(searchCondition);
            });
        });
        this.searchConditionContainer.show();
    },

    initializeComponentDropZone: function(container) {
        var me = this;

        container.dropZone = new Ext.dd.DropZone(container.el, {

            // If the mouse is over a grid row, return that node. This is
            // provided as the "target" parameter in all "onNodeXXXX" node event handling functions
            getTargetFromEvent: function(e) {
                return container;
            },

            // On entry into a target node, highlight that node.
            onNodeEnter : function(container, dd, e, data){
                container.addCls('advanced-search-condition-highlight');
            },

            // On exit from a target node, unhighlight that node.
            onNodeOut : function(container, dd, e, data){
                container.removeCls('advanced-search-condition-highlight');
            },

            // While over a target node, return the default drop allowed class which
            // places a "tick" icon into the drag proxy.
            onNodeOver : function(container, dd, e, data){
                return Ext.dd.DropZone.prototype.dropAllowed;
            },

            // On node drop we can interrogate the target to find the underlying
            // application object that is the real target of the dragged data.
            // In this case, it is a Record in the GridPanel's Store.
            // We can use the data set up by the DragZone's getDragData method to read
            // any data we decided to attach in the DragZone's getDragData method.
            onNodeDrop : function(container, dd, e, data){
                if (container != data.sourceContainer) {
                    container.add(data.sourceComponent);
                }
                me.removeEmptyConditionContainer();
                return true;
            }
        });
    },

    initializeComponentDragZone: function(container) {
        container.dragZone = new Ext.dd.DragZone(container.getEl(), {
    //      On receipt of a mousedown event, see if it is within a DataView node.
    //      Return a drag data object if so.
            getDragData: function(e) {

    //          Use the DataView's own itemSelector (a mandatory property) to
    //          test if the mousedown is within one of the DataView's nodes.
//                    var sourceEl = e.getTarget(v.itemSelector, 10);
                var sourceEl = e.getTarget('.search-tag', 10), d;

    //          If the mousedown is within a DataView node, clone the node to produce
    //          a ddel element for use by the drag proxy. Also add application data
    //          to the returned data object.
                if (sourceEl) {
                    d = sourceEl.cloneNode(true);
                    d.id = Ext.id();

                    var sourceComponent = Ext.getCmp(sourceEl.id);
                    var sourceSize = sourceComponent.getSize();

                    d.style.width = sourceSize.width + 'px';
                    d.style.height = sourceSize.height + 'px';

                    return {
                        ddel: d,
                        sourceEl: sourceEl,
                        sourceContainer: container,
                        sourceComponent: sourceComponent,
                        repairXY: Ext.fly(sourceEl).getXY()
                    }
                }
            },

    //      Provide coordinates for the proxy to slide back to on failed drag.
    //      This is the original XY coordinates of the draggable element captured
    //      in the getDragData method.
            getRepairXY: function() {
                return this.dragData.repairXY;
            }
        });
    },

    removeEmptyConditionContainer: function() {
        var me = this;

        var removedConditionContainers = [];
        Ext.each(me.searchConditionContainer.items.items, function(conditionContainer) {
            if (conditionContainer.items.items.length == 0) {
                removedConditionContainers.push(conditionContainer);
            }
        });
        Ext.each(removedConditionContainers, function(conditionContainer) {
            me.searchConditionContainer.remove(conditionContainer);
        });
    },

    resetSearch: function() {
        Ext.each(this.searchConditionContainer.items.items, function(conditionContainer) {
            conditionContainer.removeAll();
        });
        this.removeEmptyConditionContainer();
        this.searchPanel.executeSearch(this.searchMode);
    }

});