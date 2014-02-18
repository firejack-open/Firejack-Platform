//@tag opf-editor
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


Ext.apply(Ext.form.field.VTypes, {
    busyname: function(value, field) {
        return !Ext.Array.contains(field.busyNames, value);
    },
    busynameText: 'Field with the same name already exist.'
});

Ext.define('OPF.core.component.editor.FieldEditorDialog', {
    extend: 'Ext.window.Window',

    id: 'fieldEditorDialog',
    title: 'Field Editor',
    modal: true,
    closable: true,
    closeAction: 'hide',

    grid: null,
    rowIndex: null,

    layout: 'fit',
    width: 450,
    height: 690,
    resizable: false,

    fieldNames: [],

    constructor: function(grid, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.FieldEditorDialog.superclass.constructor.call(this, Ext.apply({
            grid: grid
        }, cfg));
    },

    initComponent: function(){
        var instance = this;

        this.fieldName = OPF.Ui.textFormField('name', 'Name', {
            labelAlign: 'top',
            subFieldLabel: '',
            busyNames: this.fieldNames,
            vtype: 'busyname'
        });
        this.displayName = OPF.Ui.textFormField('displayName', 'Display Name', {
            labelAlign: 'top',
            subFieldLabel: ''
        });
        this.displayDescription = OPF.Ui.textFormField('displayDescription', 'Hint', {
            labelAlign: 'top',
            subFieldLabel: ''
        });

        this.fieldType = OPF.Ui.comboFormField('fieldType', 'Type', {
            labelAlign: 'top',
            subFieldLabel: '',
            editable: false,
            valueField: 'value',
            displayField: 'title',
            listeners: {
                change: function(combo, newValue, oldValue) {
                    instance.switchFieldType(newValue);
                },
                select : function(combo, record, index) {
                    instance.switchFieldType(combo.getValue());
                }
            }
        });

        this.customFieldType = OPF.Ui.comboFormField('customFieldType', 'Type Entity', {
            disabled: true,
            labelAlign: 'top',
            subFieldLabel: '',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            mode: 'remote',
            store: new Ext.data.Store({
                model: 'OPF.console.domain.model.EntityModel',
                proxy: {
                    type: 'ajax',
                    url : OPF.core.utils.RegistryNodeType.ENTITY.generateUrl('/standard'),
                    reader: {
                        type: 'json',
                        totalProperty: 'total',
                        successProperty: 'success',
                        idProperty: 'id',
                        root: 'data',
                        messageProperty: 'message'
                    }
                }
            }),
            valueField: 'lookup',
            displayField: 'lookup'
        });

        this.fieldRequired = OPF.Ui.formCheckBox('required', 'Required', {inputValue: true});
        this.fieldSearchable = OPF.Ui.formCheckBox('searchable', 'Searchable', {inputValue: true});
        this.fieldDefaultValue = OPF.Ui.textFormField('defaultValue', 'Default Value', {
            labelAlign: 'top',
            subFieldLabel: ''
        });
        this.fieldDescription = OPF.Ui.textFormArea('description', 'Description', {
            height: 75,
            labelAlign: 'top',
            subFieldLabel: ''
        });
        this.gridAllowedValues = Ext.create('OPF.core.component.editor.FieldAllowedValuesGrid');
        this.saveButton = Ext.create('Ext.button.Button', {
            text: 'Save',
            formBind : true,
            handler: function () {
                instance.save();
            }
        });
        this.resetButton = Ext.create('Ext.button.Button', {
            text: 'Reset',
            handler: function () {
                instance.reset();
            }
        });
        this.cancelButton = Ext.create('Ext.button.Button', {
            text: 'Cancel',
            handler: function () {
                instance.cancel();
            }
        });

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            padding: 10,
            frame: true,
            border: false,
            headerAsText: false,
            //layout: 'anchor',
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
                        this.saveButton,
                        this.resetButton,
                        this.cancelButton
                    ]
                }
            ],
            items: [
                this.fieldName,
                this.displayName,
                this.displayDescription,
                this.fieldType,
                this.customFieldType,
                Ext.create('Ext.form.FieldSet', {
                    frame: false,
                    border: 0,
                    height: 25,
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
                    cls: '',
                    labelCls: '',
                    labelMargin: '0 0 5 0',
                    layout: 'fit',
                    items: [
                        this.gridAllowedValues
                    ]
                })
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);

        var constraints = Ext.create('OPF.core.validation.FormInitialisation', 'OPF.registry.Field');
        constraints.initConstraints(this.form);
    },

    setGrid: function(grid) {
        this.grid = grid;
    },

    fillingEditor: function(rowIndex) {
        var instance = this;
        if (OPF.isNotEmpty(rowIndex)) {
            this.rowIndex = rowIndex;
            var model = this.grid.store.getAt(rowIndex);
            if (OPF.isNotEmpty(model)) {
                this.fieldName.setValue(model.get('name'));
                this.displayName.setValue(model.get('displayName'));
                this.displayDescription.setValue(model.get('displayDescription'));
                var fieldType = model.get('fieldType');
                this.fieldType.setValue(fieldType);
                this.switchFieldType(fieldType);
                if ('OBJECT' == fieldType) {
                    this.customFieldType.setValue(model.get('customFieldType'));
                }
                this.fieldRequired.setValue(model.get('required'));
                this.fieldSearchable.setValue(model.get('searchable'));
                this.fieldDefaultValue.setValue(model.get('defaultValue'));
                this.fieldDescription.setValue(model.get('description'));
                this.gridAllowedValues.cleanFieldStore();
                var allowedValues = model.get('allowedValues');
                if (OPF.isNotBlank(allowedValues) && Ext.isArray(allowedValues)) {
                    Ext.each(allowedValues, function(value) {
                        instance.gridAllowedValues.store.add({
                            allowedValue: value
                        });
                    });
                }
            }
        } else {
            //
            this.fieldName.setValue('');
            this.displayName.setValue('');
            this.displayDescription.setValue('');
            this.fieldType.setValue(null);
            this.fieldRequired.setValue(false);
            this.fieldSearchable.setValue(false);
            this.fieldDefaultValue.setValue(null);
            this.fieldDescription.setValue(null);

            this.gridAllowedValues.cleanFieldStore();
            //
            this.customFieldType.setValue('');
            this.customFieldType.disable();
        }
    },

    startEditing: function(rowIndex) {
        var me = this;

        var busyNames = [];
        var fields = this.grid.store.getRange();
        Ext.each(fields, function(field, index) {
            if (rowIndex != index) {
                busyNames.push(field.get('name'));
            }
        });
        this.fieldName.busyNames = busyNames;

        this.show();
        var pos = this.getPosition();
        this.setPosition(pos[0], 0);

        this.fillingEditor(rowIndex);
    },

    stopEditing: function() {
        this.hide();
        this.gridAllowedValues.cleanFieldStore();
        this.form.getForm().reset();
        this.rowIndex = null;
    },

    save: function() {
        var allowedValues = [];
        var allowedValueModels = this.gridAllowedValues.store.getRange();

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
            this.grid.store.add(formData);
        } else {
            var fieldModel = this.grid.store.getAt(this.rowIndex);
            fieldModel.set('name', formData.name);
            fieldModel.set('displayName', formData.displayName);
            fieldModel.set('displayDescription', formData.displayDescription);
            fieldModel.set('defaultValue', defaultValue);
            fieldModel.set('fieldType', formData.fieldType);
            fieldModel.set('fieldTypeName', formData.fieldTypeName);
            fieldModel.set('customFieldType', formData.customFieldType);
            fieldModel.set('required', formData.required);
            fieldModel.set('searchable', formData.searchable);
            fieldModel.set('description', formData.description);
            fieldModel.set('allowedValues', formData.allowedValues);
        }
        this.stopEditing();
    },

    cancel: function() {
        this.stopEditing();
    },

    reset: function() {
        this.fillingEditor(this.rowIndex);
    },

    switchFieldType: function(value) {
        if ('OBJECT' == value) {
            this.customFieldType.enable();
        } else {
            this.customFieldType.disable();
            this.customFieldType.setValue('');
        }
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
            this.gridAllowedValues.disableAllowedValuesGrid(false);
        } else {
            this.fieldDefaultValue.disable();
            this.gridAllowedValues.disableAllowedValuesGrid(true);
        }
    }

});
