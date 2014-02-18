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

Ext.require([
    'OPF.prometheus.wizard.AbstractWizard',
    'OPF.prometheus.wizard.wizard.model.WizardFieldModel'
]);

Ext.define('OPF.prometheus.wizard.wizard.CreatorWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.creator-wizard',

    statics: {
        id: 'creatorWizard'
    },

    title: 'Add a wizard',
    iconCls: 'add-wizard-icon',

    initComponent: function() {
        var me = this;

        this.wizardNameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Name',
            name: 'name',
            anchor: '100%',
            disabled: true
        });

        this.rootEntityStore = Ext.create('Ext.data.Store', {
            fields: [
                { name: 'id', type: 'int' },
                { name: 'parameters' },
                { name: 'path' },
                { name: 'lookup' },
                { name: 'name', type: 'string',
                    convert: function(value, record) {
                        return record.get('parameters').domainName + '.' + value;
                    }
                }
            ],
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/entities/by-lookup/' + this.packageLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            }
        });

        this.rootEntityCombo = Ext.create('OPF.core.component.form.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Data',
            anchor: '100%',
            editable: false,
            store: this.rootEntityStore,
            queryMode: 'remote',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            listeners: {
                change: function(combo) {
                    me.entityFieldsStore.getRootNode().removeAll();
                    me.entityFieldsStore.getRootNode().isLoaded = false;
                    me.wizardFormFieldsStore.setRootNode(me.getRootNodeJson());
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.parentDomainStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.domain.model.DomainModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/domains/by-lookup/' + this.packageLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            }
        });

        this.parentDomainCombo = Ext.create('OPF.core.component.form.ComboBox', {
            name: 'parentId',
            labelAlign: 'top',
            fieldLabel: 'Select Domain',
            anchor: '100%',
            editable: false,
            store: this.parentDomainStore,
            queryMode: 'remote',
            displayField: 'name',
            valueField: 'id',
            listeners: {
                select: function(combo, records, eOpts) {
                    me.wizardNameField.setDisabled(records.length == 0);
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.form = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            items: [
                this.parentDomainCombo,
                this.wizardNameField,
                this.rootEntityCombo
            ],
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
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToSelectFieldsPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Wizard', me.messagePanel, {
                        useBaseUrl: false
                    });

                    me.wizardNameField.customValidator = function(value) {
                        var msg = null;
                        if (me.lastNotUniqueName != value) {
                            if (OPF.isNotBlank(value)) {
                                me.checkUniqueNameTask.delay(250);
                            }
                        } else {
                            msg = 'Name is not unique.';
                        }
                        return msg;
                    };
                }
            }
        });

        this.entityFieldsStore = Ext.create('Ext.data.TreeStore', {
            autoLoad: false,
            model: 'OPF.core.model.RegistryNodeTreeModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/entity-fields/' + this.packageLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            folderSort: true,
            listeners: {
                load: function(store, node, models, successful, eOpts ) {
                    if (OPF.isNotEmpty(models) && Ext.isArray(models)) {
                        for (var i = 0; i < models.length; i++) {
                            me.initializeChildNodes(models[i]);
                        }
                    }
                    store.getRootNode().getChildAt(0).expand();
                    store.getRootNode().isLoaded = true;
                },
                beforeload: function(store, operation) {
                    var entityId = me.rootEntityCombo.getValue();
                    if (entityId) {
                        store.proxy.url = OPF.Cfg.restUrl('/registry/entity-fields/' + entityId);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });

        this.entityFieldsTree = Ext.create('Ext.tree.Panel', {
            store: this.entityFieldsStore,
            rootVisible: false,
            region:'west',
            split: true,
            columns: [
                {
                    xtype: 'treecolumn',
                    sortable: false,
                    dataIndex: 'text',
                    flex: 1,
                    renderer: function(value, metaData, record) {
                        var type = record.get('type');
                        var required = record.get('parameters').required;
                        var isRequired = type == 'FIELD' && required;
                        return value + (isRequired ? '*' : '');
                    }
                }
            ],
            viewConfig: {
                copy: true,
                plugins: {
                    ptype: 'treeviewdragdrop',
                    enableDrag: true,
                    enableDrop: false
                }
            },
            width: 200
        });

        this.wizardFormFieldsStore = Ext.create('Ext.data.TreeStore', {
            autoLoad: false,
            model: 'OPF.prometheus.wizard.wizard.model.WizardFieldModel',
            root: this.getRootNodeJson()
        });

        this.rowEditor = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToEdit: 2
        });

        this.wizardFormFieldsTree = Ext.create('Ext.tree.Panel', {
            store: this.wizardFormFieldsStore,
            rootVisible: false,
            ui: 'white',
            region: 'center',
            flex: 1,
            columns: [
                {
                    xtype: 'treecolumn',
                    text: 'Name',
                    width: 150,
                    sortable: false,
                    dataIndex: 'name'
                },
                {
                    text: 'Type',
                    width: 120,
                    sortable: false,
                    dataIndex: 'fieldType'
                },
                {
                    text: 'Display Name',
                    flex: 1,
                    sortable: false,
                    dataIndex: 'displayName',
                    editor: {
                        xtype: 'textfield',
                        vtype: 'denyBlank',
                        allowBlank: false
                    }
                },
//                {
//                    xtype: 'checkcolumn',
//                    text: 'Editable',
//                    align: 'center',
//                    trueText: 'Yes',
//                    falseText: 'No',
//                    dataIndex: 'editable',
//                    width: 76,
//                    editor: {
//                        xtype: 'checkbox',
//                        allowBlank: false
//                    }
//                },
                {
                    xtype:'actioncolumn',
                    width: 40,
                    items: [
                        {
                            iconCls: 'grid-delete',
                            tooltip: 'Delete',
                            handler: function(grid, rowIndex, colIndex) {
                                var fieldModel = grid.getStore().getAt(rowIndex);

                                var type = fieldModel.get('fieldType');
                                var id = fieldModel.get('fieldId');
                                if (type == 'FORM') {
                                    Ext.each(fieldModel.childNodes, function(child) {
                                        var id = child.get('fieldId');
                                        for (var key in me.entityFieldsStore.tree.nodeHash) {
                                            var node = me.entityFieldsStore.tree.nodeHash[key];
                                            var fieldId = node.get('realId');
                                            if (fieldId == id) {
                                                node.allowDrag = true;
                                            }
                                        }
                                    });
                                } else {
                                    for (var key in me.entityFieldsStore.tree.nodeHash) {
                                        var node = me.entityFieldsStore.tree.nodeHash[key];
                                        var fieldId = node.get('realId');
                                        if (fieldId == id) {
                                            node.allowDrag = true;
                                        }
                                    }
                                }
                                fieldModel.parentNode.removeChild(fieldModel);
                            }
                        }
                    ]
                }
            ],
            selType: 'rowmodel',
            plugins: [
                this.rowEditor
            ],
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    enableDrag: true,
                    enableDrop: true
                },
                listeners: {
                    beforedrop: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
                        var record = data.records[0];
                        if (record.$className == 'OPF.core.model.RegistryNodeTreeModel') {
                            if (record.isLeaf() && record.allowDrag !== false) {
                                record.allowDrag = false;

                                var fieldId = null;
                                var fieldType = null;
                                var relationshipId = null;
                                if (record.get('type') == 'FIELD') {
                                    fieldId = record.get('realId');
                                    fieldType = record.get('parameters').fieldType;

                                    record.set('cls', '');
//                                    record.set('qtip', '');
//                                    record.set('qtitle', '');
//                                    record.commit();
                                } else {
                                    fieldType = record.get('type');
                                    var relationshipIds = record.get('parameters').relationshipIds;
                                    if (relationshipIds && relationshipIds.length > 0) {
                                        relationshipId = relationshipIds[0];
                                    }
                                }

                                data.records[0] = OPF.ModelHelper.createModelFromData('OPF.prometheus.wizard.wizard.model.WizardFieldModel', {
                                    name: record.get('name'),
                                    displayName: OPF.ifBlank(record.get('parameters').displayName, record.get('name')),
                                    fieldType: fieldType,
                                    fieldId: fieldId,
                                    relationshipId: relationshipId,
                                    editable: true,
                                    allowDrop: false
                                });
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return true;
                        }
                    }
                }
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
                            width: 250,
                            height: 60,
                            text: 'Next',
                            handler: function() {
                                me.goToDeployPanel();
                            }
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 120,
                            height: 40,
                            text: 'Add Form',
                            handler: function() {
                                me.addFormNode();
                            }
                        }
                    ]
                }
            ]
        });

        this.items = [
            {
                title: '1. Provide Details',
                layout: 'fit',
                items: [
                    this.form
                ],
                nextFrameFn: function() {
                    me.goToSelectFieldsPanel();
                }
            },
            {
                title: '2. Select Fields',
                layout: 'border',
                items: [
                    this.entityFieldsTree,
                    this.wizardFormFieldsTree
                ],
                prevFrameFn: function() {
                    me.goToChooseMainEntityPanel();
                },
                nextFrameFn: function() {
                    me.goToDeployPanel();
                }
            },
            {
                title: '3. Deploy Your Function',
                yesFn: function() {
                    me.createWizard(true);
                },
                noFn: function() {
                    me.createWizard(false);
                },
                prevFrameFn: function() {
                    me.goToSelectFieldsPanel();
                }
            }
        ];

        this.messagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.form
        });
        this.form.insert(0, this.messagePanel);

        this.lastNotUniqueName = null;
        this.checkUniqueNameTask = new Ext.util.DelayedTask(function(){
            var name = me.wizardNameField.getValue();
            var parentDomain = me.parentDomainCombo.findRecordByValue(me.parentDomainCombo.getValue());
            if (OPF.isNotBlank(name) && parentDomain != null) {
                var path = parentDomain.get('lookup');
                var url = OPF.Cfg.restUrl('/registry/check/' + path + '/WIZARD', false);
                url = OPF.Cfg.addParameterToURL(url, 'name', name);
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    success: function (response) {
                        if (me.wizardNameField) {
                            var resp = Ext.decode(response.responseText);
                            if (resp.success) {
                                var activeErrors = me.wizardNameField.activeErrors;
                                if (activeErrors && activeErrors.length == 0) {
                                    me.wizardNameField.clearInvalid();
                                }
                            } else {
                                me.wizardNameField.markInvalid(resp.message);
                                me.lastNotUniqueName = name;
                            }
                        }
                    },
                    failure: function () {
                        Ext.Msg.alert('Error', 'Connection error!');
                    }
                });
            }
        });

        this.callParent(arguments);
    },

    goToChooseMainEntityPanel: function() {
        var layout = this.getCardPanelLayout();
        layout.setActiveItem(0);
    },

    goToSelectFieldsPanel: function() {
        this.validateForm(function(scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(1);
            if (scope.entityFieldsStore.getRootNode().isLoaded !== true) {
                scope.entityFieldsStore.setRootNode({
                    text: 'root',
                    rendered: false,
                    expanded: true
                });
            }
        }, this);
    },

    goToDeployPanel: function() {
        this.validateForm(function(scope) {
            if (scope.validateFields()) {
                var layout = scope.getCardPanelLayout();
                layout.setActiveItem(2);
            }
        }, this);
    },

    validateForm: function(executeFn, scope) {
        var parentId = this.rootEntityCombo.getValue();
        if (OPF.isNotEmpty(parentId) && Ext.isNumeric(parentId)) {
            if (this.form.getForm().isValid()) {
                executeFn(scope);
            }
        } else {
            this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Parent has not been selected.');
        }
    },

    validateFields: function() {
        var rootEntity = this.entityFieldsStore.getRootNode().firstChild;
        var isValid = true;
        Ext.each(rootEntity.childNodes, function(child) {
            var type = child.get('type');
            var required = child.get('parameters').required;
            var isRequired = type == 'FIELD' && required;
            var isAllowDrag = child.allowDrag !== false;
            if (isRequired && isAllowDrag) {
                child.set('cls', 'invalid-node');
//                child.set('qtip', 'It is required field and it should be added to any form.');
//                child.set('qtitle', 'Error');
                isValid &= false;
            }
        });
        return isValid;
    },

    initializeChildNodes: function(model) {
        var i;
        if (OPF.isEmpty(model.childNodes) || (model.childNodes.length == 0)) {
            var children = model.get('children');
            if (OPF.isNotEmpty(children) && Ext.isArray(children)) {
                var nodes = [];
                for (i = 0; i < children.length; i++) {
                    var child = children[i];
                    if (child.type == 'ENTITY' || !child.parameters.autoGenerated) {
                        child.leaf = true;
                        child.allowDrag = true;
                        var childModel = Ext.create('OPF.core.model.RegistryNodeTreeModel', child);
                        nodes.push(childModel);
                    }
                }
                model.appendChild(nodes);
            }
        }
    },

    addFormNode: function() {
        var rootNode = this.wizardFormFieldsStore.getRootNode();
        var index = rootNode.childNodes.length;
        var formNode = OPF.ModelHelper.createModelFromData('OPF.prometheus.wizard.wizard.model.WizardFieldModel', {
            name: 'Form ' + (index + 1),
            displayName: '',
            fieldType: 'FORM',
            editable: false,
            expanded: true,
            allowDrag: false,
            children: []
        });
        rootNode.appendChild(formNode);
        this.wizardFormFieldsTree.editingPlugin.startEdit(formNode, this.wizardFormFieldsTree.columns[0]);
    },

    getRootNodeJson: function() {
        return {
            text: 'RootNode',
            expanded: true,
            allowDrop: false,
            children: [
                {
                    name: 'Form 1',
                    displayName: 'Form 1',
                    fieldType: 'FORM',
                    editable: false,
                    expanded: true,
                    allowDrag: false,
                    children: []
                }
            ]
        }
    },

    createWizard: function(isDeploy) {
        var me = this;

        var wizardName = this.wizardNameField.getValue();
        var rootEntityId = this.rootEntityCombo.getValue();
        var parentDomainId = this.parentDomainCombo.getValue();
        var rootNode = this.wizardFormFieldsStore.getRootNode();

        var wizardForms = [];
        Ext.each(rootNode.childNodes, function(form, index) {
            var wizardForm = {
                displayName: form.get('displayName'),
                editable: form.get('editable'),
                position: index
            };

            var wizardFields = [];
            var fields = form.childNodes;
            Ext.each(fields, function(field, index) {
                var wizardField = {
                    displayName: field.get('displayName'),
                    editable: field.get('editable'),
                    position: index
                };
                var fieldId = field.get('fieldId');
                if (fieldId) {
                    wizardField.field = {
                        id: fieldId
                    };
                }
                var relationshipId = field.get('relationshipId');
                if (relationshipId) {
                    wizardField.relationship = {
                        id: relationshipId
                    };
                }
                wizardFields.push(wizardField);
            });
            wizardForm.fields = wizardFields;
            wizardForms.push(wizardForm);
        });

        var jsonData = {
            name: wizardName,
            parentId: parentDomainId,
            main: {
                id: rootEntityId
            },
            fields: wizardForms
        };

        this.getEl().mask();

        this.save(OPF.Cfg.restUrl('/registry/wizard'), jsonData, isDeploy);
    }

});