/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

Ext.define('OPF.console.domain.view.process.ProcessEntityDropPanel', {
    extend: 'OPF.core.component.RegistryNodeDropPanel',

    fieldLabel: 'Entity',
    subFieldLabel: '',

    name: 'processFields',

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.ProcessEntityDropPanel.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    onRender: function(ct, position) {
        this.callParent(arguments);
        // This will make sure we only drop to the view container
        var instance = this;
        var sourcePanelDropTarget = new Ext.dd.DropTarget(this.body.dom, {
            ddGroup: 'cloudNavigatorDDGroup',
            notifyEnter: function(ddSource, e, data) {
                //Add some flare to invite drop.
                var model = data.records[0];
                var elementType = model.get('type');
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(elementType);
                if (OPF.core.utils.RegistryNodeType.ENTITY == sourceType) {
                    //instance.body.stopFx();
                    instance.body.highlight();
                } else {
                    this.lock();
                    setTimeout(function() {
                        sourcePanelDropTarget.unlock();
                    }, 200);
                }
            },
            notifyDrop: function(ddSource, e, data){
                var model = data.records[0];
                var elementType = model.get('type');
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(elementType);
                if (OPF.core.utils.RegistryNodeType.ENTITY == sourceType) {
                    var newEntityId = SqGetIdFromTreeEntityId(model);
                    if (instance.getValue() && instance.getValue() != newEntityId) {
                        var gridRows = instance.editPanel.customFieldsGrid.store.getRange();
                        if (gridRows.length > 0) {
                            Ext.Msg.confirm('Replace Entity', 'By replacing the current entity all existing field configs will be lost. Are you sure?',
                                function(btn) {
                                    if ( btn == 'yes' ) {
                                        var fieldsToDelete = [];
                                        instance.editPanel.customFieldsGrid.store.each(function(model) {
                                            var global = model.get('global');
                                            if (OPF.isEmpty(global) || !global) {
                                                fieldsToDelete.push(model);
                                            }
                                        });
                                        if (fieldsToDelete.length > 0){
                                            instance.editPanel.customFieldsGrid.store.remove(fieldsToDelete);
                                        }
                                        instance.renderDraggableEntityFromNode(model);
                                    }
                            });
                            return true;
                        }
                    }
                    instance.renderDraggableEntityFromNode(model);
                    return true;
                } else {
                    Ext.MessageBox.alert('Error', 'Only entities can be associated here!');
                    return false;
                }
            }
        });
    },

    renderDraggableEntityFromNode: function(model) {
        var me = this;

        this.setValue(SqGetIdFromTreeEntityId(model.get('id')));
        this.draggableNodeName = model.get('text');
        var description = cutting(model.get('shortDescription'), 70);
        this.renderDraggableEntity(model.get('iconCls'), model.get('text'), description, model.get('lookup'));

        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.ENTITY.generateGetUrl(this.getValue()),
            method: 'GET',
            success: function(response) {
                var jsonData = Ext.decode(response.responseText);
                if (jsonData.success) {
                    if (OPF.isNotEmpty(jsonData.data) && Ext.isArray(jsonData.data)) {
                        me.entityFields = jsonData.data[0].fields;
                    }
                } else {
                    Ext.MessageBox.alert('Error', jsonData.message);
                }
            }
        });
    }

});

Ext.define('OPF.console.domain.view.process.CustomFieldEditorDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.process-field-editor-dlg',

    title: 'Custom Field Editor',
    modal: true,
    closable: true,
    closeAction: 'hide',

    autoHeight: true,
    autoWidth: true,
    layout: 'fit',
    resizable: false,

    grid: null,

    constructor: function(id, grid, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.CustomFieldEditorDialog.superclass.constructor.call(this, Ext.apply({
            id: id,
            grid: grid
        }, cfg));
    },

    initComponent: function(){
        this.registryNodeFieldCombo = OPF.Ui.comboFormField('registryNodeField', 'Field', {
            labelAlign: 'top',
            subFieldLabel: '',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender:true,
            queryMode: 'local',
            store: 'RegistryNodeFields',
            valueField: 'id',
            displayField: 'name',
            hiddenName: 'registryNodeField',
            marker: 'registryNodeFieldCombo'
        });

        this.nameField = OPF.Ui.textFormField('name', 'Display Name', {
            labelAlign: 'top',
            subFieldLabel: ''
        });
        this.valueTypeField = OPF.Ui.comboFormField('valueType', 'Type', {
            labelAlign: 'top',
            subFieldLabel: '',
            readOnly: true,
            editable: false
        });
        this.formatField = OPF.Ui.comboFormField('format', 'Format', {
            labelAlign: 'top',
            subFieldLabel: '',
            typeAhead: true,
            queryMode: 'local',
            store: new Ext.data.ArrayStore({
                fields: ['value', 'displayText']
            }),
            valueField: 'value',
            displayField: 'displayText'
        });

        this.globalField = OPF.Ui.formCheckBox('global', 'Global');

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            width: 450,
            height: 290,
            padding: 10,
            frame: true,
            border: false,
            headerAsText: false,
            monitorValid: true,
            layout: 'anchor',
            defaults: {
                bodyStyle: 'padding 5px;',
                anchor: '100%'
            },
            fbar: [
                OPF.Ui.createBtn('Save', 50, 'save-process-field', {formBind: true}),
                OPF.Ui.createBtn('Reset', 60, 'reset-process-field'),
                OPF.Ui.createBtn('Cancel', 60, 'cancel-process-field')
            ],
            items: [
                this.registryNodeFieldCombo,
                this.nameField,
                this.valueTypeField,
                this.globalField,
                this.formatField
            ]
        });

        this.items = this.form;
        this.addEvents({
            'loadFields': true
        });
        this.callParent(arguments)

        var constraints = Ext.create('OPF.core.validation.FormInitialisation', 'OPF.process.ProcessField');
        constraints.initConstraints(this.form, null);
    },

    getMatchingProcessFieldType: function(fieldType) {
        switch (fieldType) {
            case 'UNIQUE_ID':
            case 'CODE':
            case 'LABEL':
            case 'LOOKUP':
            case 'NAME':
            case 'DESCRIPTION':
            case 'PASSWORD':
            case 'SECRET_KEY':
            case 'RICH_TEXT':
            case 'TINY_TEXT':
            case 'SHORT_TEXT':
            case 'MEDIUM_TEXT':
            case 'URL':
            case 'LONG_TEXT':
            case 'UNLIMITED_TEXT':
            case 'FILE':
            case 'IMAGE_FILE':
            case 'AUDIO_FILE':
            case 'VIDEO_FILE':
            case 'PHONE_NUMBER':
            case 'SSN':
                return 'STRING';

            case 'FLAG':
                return 'FLAG';

            case 'DATE':
            case 'TIME':
            case 'EVENT_TIME':
            case 'CREATION_TIME':
            case 'UPDATE_TIME':
                return 'DATE';

            case 'INTEGER_NUMBER':
                return 'INTEGER';

            case 'NUMERIC_ID':
            case 'LARGE_NUMBER':
                return 'LONG';

            case 'DECIMAL_NUMBER':
            case 'CURRENCY':
                return 'DOUBLE';
        }
    },

    setFormatComboValues: function(processFieldType) {
        var predefinedFormats;
        switch(processFieldType) {
            case 'FLAG':
                predefinedFormats = ['yes/no', 'true/false'];
                break;

            case 'DATE':
                predefinedFormats = ['n/j/Y', 'Y-m-d'];
                break;

            case 'INTEGER':
            case 'LONG':
                predefinedFormats = ['0', '0,000'];
                break;

            case 'DOUBLE':
                predefinedFormats = ['0.00', '0,000.00'];
                break;
        }

        var storeData = [];
        Ext.each(predefinedFormats, function(format) {
            storeData.push([format, format]); // need a key-value pair in each array elem
        });
        this.formatField.store.loadData(storeData);
    },

    fillEditor: function(entityId, entityFields, rowIndex) {
        this.entityId = entityId;
        this.entityFields = entityFields;
        this.rowIndex = rowIndex;

        if (OPF.isNotEmpty(entityFields)) {
            this.fireEvent('loadFields', entityFields);
            //this.registryNodeFieldStore.loadData(entityFields);
        }

        if (OPF.isNotEmpty(rowIndex)) {
            var model = this.grid.store.getAt(rowIndex);
            if (OPF.isNotEmpty(model)) {
                this.registryNodeFieldCombo.setValue(model.data.fieldId);
                this.nameField.setValue(model.get('name'));
                this.valueTypeField.setValue(model.get('valueType'));
                this.globalField.setValue(model.get('global'));
                this.setFormatComboValues(model.get('valueType'));
                this.formatField.setValue(model.get('format'));
                return;
            }
        }

        this.registryNodeFieldCombo.setValue('');
        this.nameField.setValue('');
        this.valueTypeField.setValue('');
        this.globalField.setValue(false);
        this.formatField.setValue('');
    },

    startEditing: function(entityId, entityFields, rowIndex) {
        this.fillEditor(entityId, entityFields, rowIndex);
        this.show();
    },

    setGrid: function(grid) {
        this.grid = grid;
    }
});

Ext.define('OPF.console.domain.view.process.CustomFieldsGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.custom-fields-grid',
    height: 240,
    cls: 'border-radius-grid-body border-radius-grid-docked-top',
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.CustomFieldsGrid.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    ddGroup: 'customFieldsDDGroup',
    enableDragDrop: true,
    viewConfig: {
        plugins: {
            ptype: 'gridviewdragdrop',
            dragGroup: 'customFieldsDDGroup',
            dropGroup: 'customFieldsDDGroup'
        },
        listeners: {
            drop: function(node, data, dropModel, dropPosition) {
                var customFieldsGrid = OPF.Ui.getCmp('custom-fields-grid');
                customFieldsGrid.refreshOrderPositions();
            }
        }
    },

    columns: [
        OPF.Ui.populateColumn('name', 'Display Name', {flex: 1, renderer: 'htmlEncode'}),
        OPF.Ui.populateColumn('valueType', 'Type', {width: 100}),
        OPF.Ui.populateColumn('format', 'Format', {width: 100}),
        OPF.Ui.populateBooleanColumn('global', 'Global', {width: 60}),
        OPF.Ui.populateHiddenColumn('orderPosition')
    ],

    store: 'ProcessFields',

    initComponent: function() {
        this.tbar = [
            OPF.Ui.createBtn('Add', 45, 'add-process-field', {iconCls: 'silk-add'}),
            '-',
            OPF.Ui.createBtn('Delete', 60, 'delete-process-field', {iconCls: 'silk-delete'})
        ];

        this.callParent(arguments);
    },

    getEditorDialog: function() {
        var winId = 'customFieldsEditorDialog1951';
        var editorDialog = Ext.WindowMgr.get(winId);
        if (OPF.isEmpty(editorDialog)) {
            editorDialog = Ext.create('OPF.console.domain.view.process.CustomFieldEditorDialog', winId, this);
        } else {
            editorDialog.setGrid(this);
        }
        return editorDialog;
    },

    cleanFieldStore: function() {
        this.store.removeAll();
    },

    refreshOrderPositions: function() {
        var models = this.store.getRange();
        Ext.each(models, function(model, index) {
            var orderPosition = model.get('global') ? index - 1000 : index + 1; // global fields come first, up to 1001 of them supported
            model.set('orderPosition', orderPosition);
        });
    }

});

Ext.define('OPF.console.domain.view.process.CustomFieldsFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.customfields-fieldset',

    fieldLabel: 'Custom Fields',
    subFieldLabel: '',

    layout: 'anchor',
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.CustomFieldsFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {

        this.processEntityDropPanel = Ext.create('OPF.console.domain.view.process.ProcessEntityDropPanel', this);
        this.customFieldsGrid = Ext.create('OPF.console.domain.view.process.CustomFieldsGrid', this);

        this.items = [
            this.processEntityDropPanel,
            OPF.Ui.ySpacer(10),
            this.customFieldsGrid
        ];

        this.callParent(arguments);
    }

});
