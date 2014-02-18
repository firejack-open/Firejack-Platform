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

Ext.define('OPF.prometheus.wizard.entity.FieldWizard', {
    extend: 'Ext.window.Window',
    alias: 'widget.prometheus.wizard.entity-field-wizard',

    statics: {
        id: 'entityFieldWizard'
    },

    title: 'Add/Edit Field',
    closable: true,
    closeAction: 'hide',

    ui: 'wizards',
    width: 450,
    height: 980,
    resizable: false,

    layout: 'fit',
    modal: true,

    fieldNames: [],

    initComponent: function() {
        var me = this;

        me.addEvents(
            'aftersave'
        );

        this.fieldName = OPF.Ui.textFormField('name', 'Name', {
            labelAlign: 'top',
            getErrors: function(value) {
                var errors = this.getValidatorErrors(value);
                if (Ext.Array.contains(me.fieldNames, value)) {
                    errors.push('Field with the same name already exist.');
                }
                return errors;
            }
        });

        this.displayName = OPF.Ui.textFormField('displayName', 'Display Name', {
            labelAlign: 'top'
        });

        this.displayDescription = OPF.Ui.textFormField('displayDescription', 'Hint', {
            labelAlign: 'top'
        });

        this.fieldType = OPF.Ui.comboFormField('fieldType', 'Type', {
            labelAlign: 'top',
            editable: false,
            valueField: 'value',
            displayField: 'title',
            listeners: {
                change: function(combo, newValue, oldValue) {
                    me.switchFieldType(newValue);
                },
                select : function(combo, record, index) {
                    me.switchFieldType(combo.getValue());
                }
            }
        });

        this.fieldRequired = OPF.Ui.formCheckBox('required', 'Required', {

            inputValue: true
        });

        this.fieldSearchable = OPF.Ui.formCheckBox('searchable', 'Searchable', {
            inputValue: true
        });

        this.fieldDefaultValue = OPF.Ui.textFormField('defaultValue', 'Default Value', {
            labelAlign: 'top'
        });

        this.fieldDescription = OPF.Ui.textFormArea('description', 'Description', {
            height: 75,
            labelAlign: 'top'
        });

        this.allowedValueStore = Ext.create('Ext.data.Store', {
            fields: [
                { name: 'allowedValue', type: 'string' }
            ],
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    idProperty: 'allowedValue',
                    root: 'allowedValue'
                },
                writer: {
                    type: 'json'
                }
            },
            listeners: {
                datachanged: function(store) {
                    me.allowedValuesGrid.determineScrollbars();
                }
            }
        });

        this.allowedValuesGrid = Ext.create('Ext.grid.Panel', {
            height: 200,
            store: this.allowedValueStore,
            columns: [
                {
                    dataIndex: 'allowedValue',
                    text: 'Allowed Value',
                    sortable: true,
                    flex: 1,
                    editor: 'textfield',
                    renderer: 'htmlEncode',
                    allowBlank: false
                }
            ],
            selType: 'rowmodel',
            plugins: [
                Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToEdit: 2,
                    listeners: {
                        edit: function(rowEditing, context) {
                            var record = context.record;
                            record.commit();
                        },
                        canceledit: function(rowEditing, context) {
                            var record = context.record;
                            if (record && record.phantom) {
                                context.store.remove(record);
                            }
                        }
                    }
                })
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            text: 'Add',
                            ui: 'blue',
                            action: 'add',
                            handler: function() {
                                me.addAllowedValueRow();
                            }
                        },
                        '-',
                        {
                            text: 'Delete',
                            ui: 'grey',
                            action: 'delete',
                            handler: function(btn) {
                                me.deleteAllowedValueRow();
                            }
                        }
                    ]
                }
            ]
        });

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            padding: 10,
            frame: true,
            border: false,
            headerAsText: false,
            monitorValid: true,
            layoutConfig: {
                align: 'stretch'
            },

            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            text: 'Save',
                            formBind : true,
                            handler: function () {
                                me.save();
                            }
                        },
                        {
                            xtype: 'button',
                            ui: 'grey',
                            text: 'Cancel',
                            handler: function () {
                                me.close();
                            }
                        }
                    ]
                }
            ],
            items: [
                this.fieldName,
                this.displayName,
                this.displayDescription,
                this.fieldType,
                Ext.create('Ext.form.FieldSet', {
                    frame: false,
                    border: 0,
                    height: 40,
                    anchor: '100%',
                    layout: {
                        type: 'hbox',
                        pack: 'start',
                        align: 'middle'
                    },
                    items: [
                        this.fieldRequired,
                        OPF.Ui.xSpacer(20),
                        this.fieldSearchable
                    ]
                }),
                this.fieldDefaultValue,
                this.fieldDescription,
                Ext.create('OPF.core.component.LabelContainer', {
                    fieldLabel: 'Allow Values',
                    subFieldLabel: '',
                    labelCls: 'x-form-item-label',
                    labelMargin: '0 0 5 0',
                    layout: 'fit',
                    items: [
                        this.allowedValuesGrid
                    ]
                })
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Field', me.messagePanel, {
                        useBaseUrl: false
                    });
                }
            }
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    initWizardBeforeShow: function(fieldStore, record, rowIndex) {
        var me = this;

        this.rowIndex = rowIndex;
        this.fieldStore = fieldStore;
        if (record) {
            this.form.getForm().loadRecord(record);
        }

        this.allowedValueStore.removeAll();
        var allowedValues = record.get('allowedValues');
        if (OPF.isNotEmpty(allowedValues) && Ext.isArray(allowedValues)) {
            Ext.each(allowedValues, function(allowedValue) {
                me.allowedValueStore.add({
                    allowedValue: allowedValue
                });
            });
            this.allowedValueStore.commitChanges();
        }

        var fields = fieldStore.getRange();
        this.fieldNames = [];
        Ext.each(fields, function(field, index) {
            if (rowIndex != index) {
                me.fieldNames.push(field.get('name'));
            }
        });
    },

    save: function() {
        var allowedValues = [];
        var allowedValueModels = this.allowedValueStore.getRange();

        var formData = this.form.getForm().getValues();
        var i;
        for (i = 0; i < allowedValueModels.length; i++) {
            var allowedValue = allowedValueModels[i].get('allowedValue');
            if (OPF.isNotBlank(allowedValue)) {
                var allowedVal = OPF.console.domain.model.FieldModel.checkType(allowedValue, formData.fieldType);
                if (allowedVal == null) {
                    Ext.Msg.alert('Error', 'Allowed value should correspond to the specified field type.');
                    return;
                }
                allowedValues.push(allowedVal);
            }
        }

        var defaultValue = formData.defaultValue;
        if (OPF.isNotBlank(defaultValue)) {
            var val = OPF.console.domain.model.FieldModel.checkType(defaultValue, formData.fieldType);
            if (val == null) {
                Ext.Msg.alert('Error', 'Default value should correspond to the specified field type.');
                return;
            } else {
                this.fieldDefaultValue.setValue(val);
                formData.defaultValue = val;
                defaultValue = val;
            }
            if (allowedValues.length > 0) {
                var defaultValueIsIncorrect = true;
                for (i = 0; i < allowedValues.length; i++) {
                    if (allowedValues[i] == defaultValue) {
                        defaultValueIsIncorrect = false;
                        break;
                    }
                }
                if (defaultValueIsIncorrect) {
                    Ext.Msg.alert('Error', 'Default value should correspond to specified allowed values.');
                    return;
                }
            }
        }

        var fieldTypeRecord = this.fieldType.findRecordByValue(this.fieldType.getValue());
        formData.fieldTypeName = fieldTypeRecord.data.title;
        formData.allowedValues = allowedValues;

        if (OPF.isEmpty(this.rowIndex)) {
            this.fieldStore.add(formData);
        } else {
            var fieldModel = this.fieldStore.getAt(this.rowIndex);
            fieldModel.set('name', formData.name);
            fieldModel.set('displayName', formData.displayName);
            fieldModel.set('displayDescription', formData.displayDescription);
            fieldModel.set('defaultValue', defaultValue);
            fieldModel.set('fieldType', formData.fieldType);
            fieldModel.set('fieldTypeName', formData.fieldTypeName);
//            fieldModel.set('customFieldType', formData.customFieldType);
            fieldModel.set('required', formData.required);
            fieldModel.set('searchable', formData.searchable);
            fieldModel.set('description', formData.description);
            fieldModel.set('allowedValues', formData.allowedValues);
        }
        this.fireEvent('aftersave', this);
        this.close();
    },

    close: function() {
        this.callParent(arguments);
        this.allowedValueStore.removeAll();
        this.form.getForm().reset();
        this.fieldNames = [];
    },

    switchFieldType: function(value) {
//        if ('OBJECT' == value) {
//            this.customFieldType.enable();
//        } else {
//            this.customFieldType.disable();
//            this.customFieldType.setValue('');
//        }
        if (OPF.console.domain.model.FieldModel.fieldTypeIsString(value)) {
            this.fieldSearchable.enable();
        } else {
            this.fieldSearchable.disable();
            this.fieldSearchable.setValue(false);
        }
        this.fieldDefaultValue.setValue(null);
        if (OPF.console.domain.model.FieldModel.fieldTypeIsString(value) || OPF.console.domain.model.FieldModel.fieldTypeIsNumeric(value) ||
            OPF.console.domain.model.FieldModel.fieldTypeIsReal(value) || OPF.console.domain.model.FieldModel.fieldTypeIsBoolean(value)) {
            this.fieldDefaultValue.enable();
            this.disableAllowedValuesGrid(false);
        } else {
            this.fieldDefaultValue.disable();
            this.disableAllowedValuesGrid(true);
        }
    },

    addAllowedValueRow: function() {
        var allowedValueModel = this.allowedValueStore.createModel({allowedValue : ''});
        this.allowedValueStore.add(allowedValueModel);
        this.allowedValuesGrid.editingPlugin.startEdit(allowedValueModel, this.allowedValuesGrid.columns[0]);
    },

    deleteAllowedValueRow: function() {
        var records = this.allowedValuesGrid.getSelectionModel().getSelection();
        this.allowedValueStore.remove(records);
    },

    disableAllowedValuesGrid: function(disabled) {
        var addBtn = this.allowedValuesGrid.down('button[action=add]');
        var deleteBtn = this.allowedValuesGrid.down('button[action=delete]');
        if (disabled) {
            this.allowedValueStore.removeAll();
            addBtn.disable();
            deleteBtn.disable();
        } else {
            addBtn.enable();
            deleteBtn.enable();
        }
    }

});