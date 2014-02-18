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
    'OPF.prometheus.wizard.AbstractWizard'
]);

Ext.define('OPF.prometheus.wizard.report.ReportWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.report-wizard',

    statics: {
        id: 'reportWizard'
    },

    title: 'Add a report',
    iconCls: 'add-report-icon',

    initComponent: function() {
        var me = this;

        this.reportNameField = Ext.create('OPF.core.component.form.Text', {
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
                select: function(combo, records, eOpts) {
                    me.reportNameField.setDisabled(records.length == 0);
                },
                change: function(combo) {
                    me.availableFieldsStore.getRootNode().removeAll();
                    me.availableFieldsStore.getRootNode().isLoaded = false;
                    me.selectedFieldsStore.removeAll();
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.availableFieldsStore = Ext.create('Ext.data.TreeStore', {
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

        this.fieldsTree = Ext.create('Ext.tree.Panel', {
            store: this.availableFieldsStore,
            rootVisible: false,
            region:'west',
            split: true,
            viewConfig: {
                copy: true,
                plugins: {
                    ptype: 'treeviewdragdrop',
                    dragGroup: 'reportFieldOrderDDGroup'
                }
            },
            width: 200
        });

        this.selectedFieldsStore = Ext.create('Ext.data.Store', {
            id: 'SelectedFieldsStore',
            model: 'OPF.console.domain.model.ReportFieldModel'
        });

        this.selectedFieldsGrid = Ext.create('Ext.grid.Panel', {
            store: this.selectedFieldsStore,
            flex: 1,
            region: 'center',
            border: false,
            columns: [
                {
                    xtype: 'opf-column',
                    text: 'Name',
                    width: 100,
                    sortable: false,
                    dataIndex: 'field',
                    displayField: 'name'
                },
                {
                    xtype: 'opf-column',
                    text: 'Type',
                    width: 120,
                    sortable: false,
                    dataIndex: 'field',
                    displayField: 'fieldType'
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
                {
                    xtype: 'checkcolumn',
                    cls: 'visible-ico',
                    tooltip: 'Visible',
                    align: 'center',
                    dataIndex: 'visible',
                    width: 50,
                    resizable: false,
                    editor: {
                        xtype: 'checkbox',
                        allowBlank: false
                    }
                },
                {
                    xtype: 'checkcolumn',
                    cls: 'searchable-ico',
                    tooltip: 'Searchable',
                    align: 'center',
                    dataIndex: 'searchable',
                    width: 50,
                    resizable: false,
                    editor: {
                        xtype: 'checkbox',
                        allowBlank: false
                    }
                }
            ],
            selType: 'rowmodel',
            plugins: [
                Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToEdit: 2
                }),
                Ext.create('OPF.core.component.grid.RowToolbarGridPlugin', {
                    buttons: [
                        {
                            name: 'delete'
                        }
                    ],
                    deleteFn: function(btn, e, options) {
                        var grid = options[0];
                        var record = options[1];
                        grid.getStore().remove(record);

                        var id = OPF.ModelHelper.getFk(record, 'field', 'id');
                        for (var key in me.availableFieldsStore.tree.nodeHash) {
                            var node = me.availableFieldsStore.tree.nodeHash[key];
                            var fieldId = node.get('realId');
                            if (fieldId == id) {
                                node.allowDrag = true;
                            }
                        }
                    },
                    showFn: function(btn, e) {
                        var rowSize = this.rowElement.getSize();
                        this.rowCmp.setWidth(rowSize.width);
                        this.showRowToolbarButton.hide();
                        this.deleteRecordButton.show();
                    },
                    scope: this
                })
            ],
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'reportFieldOrderDDGroup',
                    dropGroup: 'reportFieldOrderDDGroup',
                    dragText: 'Drag and drop to reorganize'
                },
                listeners: {
                    beforedrop: function(node, data, overModel, dropPosition, dropFunction, eOpts) {
                        var record = data.records[0];
                        if (record.$className == 'OPF.core.model.RegistryNodeTreeModel') {
                            if (record.isLeaf() && record.allowDrag !== false) {
                                record.allowDrag = false;

                                var relationshipIds = record.get('parameters').relationshipIds;
                                var relationships = [];
                                Ext.each(relationshipIds, function(relationshipId) {
                                    relationships.push({
                                        id: relationshipId
                                    });
                                });

                                data.records[0] = OPF.ModelHelper.createModelFromData('OPF.console.domain.model.ReportFieldModel', {
                                    displayName: OPF.ifBlank(record.get('parameters').displayName, record.get('name')),
                                    visible: true,
                                    searchable: true,
                                    report: null,
                                    relationships: relationships,
                                    field: {
                                        id: record.get('realId'),
                                        name: record.get('name'),
                                        fieldType: record.get('parameters').fieldType
                                    }
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
                }
            ]
        });

        this.form = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            items: [
                this.rootEntityCombo,
                this.reportNameField
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
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Report', me.messagePanel, {
                        useBaseUrl: false
                    });

                    me.reportNameField.customValidator = function(value) {
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
                    this.fieldsTree,
                    this.selectedFieldsGrid
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
                    me.createReport(true);
                },
                noFn: function() {
                    me.createReport(false);
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
            var name = me.reportNameField.getValue();
            var rootRecord = me.rootEntityCombo.findRecordByValue(me.rootEntityCombo.getValue());
            if (OPF.isNotBlank(name) && rootRecord != null) {
                var path = rootRecord.get('lookup');
                var url = OPF.Cfg.restUrl('/registry/check/' + path + '/REPORT', false);
                url = OPF.Cfg.addParameterToURL(url, 'name', name);
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    success: function (response) {
                        if (me.reportNameField) {
                            var resp = Ext.decode(response.responseText);
                            if (resp.success) {
                                var activeErrors = me.reportNameField.activeErrors;
                                if (activeErrors && activeErrors.length == 0) {
                                    me.reportNameField.clearInvalid();
                                }
                            } else {
                                me.reportNameField.markInvalid(resp.message);
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

    initializeChildNodes: function(model) {
        if (OPF.isEmpty(model.childNodes) || (model.childNodes.length == 0)) {
            var children = model.get('children');
            if (OPF.isNotEmpty(children) && Ext.isArray(children)) {
                var i, nodes = [];
                for (i = 0; i < children.length; i++) {
                    var child = children[i];
                    child.leaf = !child.children || child.children.length == 0;
                    var childModel = Ext.create('OPF.core.model.RegistryNodeTreeModel', child);
                    nodes.push(childModel);
                }
                model.appendChild(nodes);
            }
        }
        if (OPF.isNotEmpty(model.childNodes)) {
            for (i = 0; i < model.childNodes.length; i++) {
                this.initializeChildNodes(model.childNodes[i]);
            }
        }
    },

    goToChooseMainEntityPanel: function() {
        var layout = this.getCardPanelLayout();
        layout.setActiveItem(0);
    },

    goToSelectFieldsPanel: function() {
        this.validateForm(function(scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(1);
            if (scope.availableFieldsStore.getRootNode().isLoaded !== true) {
                scope.availableFieldsStore.setRootNode({
                    text: 'root',
                    rendered: false,
                    expanded: true
                });
            }
        }, this);
    },

    goToDeployPanel: function () {
        this.validateForm(function(scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(2);
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

    createReport: function(isDeploy) {
        var me = this;

        var reportName = this.reportNameField.getValue();
        var rootEntityId = this.rootEntityCombo.getValue();
        var fields = this.selectedFieldsStore.getRange();

        var reportFields = [];
        Ext.each(fields, function(field) {
            var relationships = field.relationshipsStore.getRange();
            var relationshipIds = [];
            Ext.each(relationships, function(relationship) {
                relationshipIds.push({
                    id: relationship.get('id')
                });
            });
//            var relationshipIds = field.data.relationships;
            var reportField = {
                displayName: field.get('displayName'),
                visible: field.get('visible'),
                searchable: field.get('searchable'),
                report: null,
                relationships: relationshipIds,
                field: {
                    id: OPF.ModelHelper.getFk(field, 'field', 'id')
                }
            };
            reportFields.push(reportField);
        });

        var jsonData = {
            name: reportName,
            parentId: rootEntityId,
            fields: reportFields
        };

        this.getEl().mask();

        this.save(OPF.Cfg.restUrl('/registry/report'), jsonData, isDeploy);

//        Ext.Ajax.request({
//            url: OPF.Cfg.restUrl('/registry/report'),
//            method: 'POST',
//            jsonData: {"data": jsonData},
//
//            success:function(response, action) {
//                var responseData = Ext.decode(response.responseText);
//                OPF.Msg.setAlert(true, responseData.message);
//                me.getEl().unmask();
//                me.close();
//
//                if (isDeploy) {
//                    me.redeploy();
//                }
//            },
//
//            failure:function(response) {
//                me.getEl().unmask();
//                OPF.Msg.setAlert(false, response.message);
//            }
//        });

    }

});