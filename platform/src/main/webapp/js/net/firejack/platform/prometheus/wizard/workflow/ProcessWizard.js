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
    'OPF.prometheus.wizard.workflow.model.ActivityFieldModel',
    'OPF.prometheus.wizard.workflow.model.ProcessEntityModel',
    'OPF.prometheus.wizard.workflow.model.ActivityActionModel',
    'OPF.prometheus.wizard.workflow.ManageActorsComponent',
    'OPF.prometheus.wizard.workflow.ManageStatusesComponent',
    'OPF.prometheus.wizard.workflow.ManageActivityComponent'
]);



Ext.define('OPF.prometheus.wizard.workflow.ProcessWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.process-wizard',

    statics: {
        id: 'workflowWizard'
    },

    id: null,
    title: 'Create a workflow',
    iconCls: 'add-process-icon',
    height: 750,

    constructor: function(id, cfg) {
        cfg = cfg || {};
        OPF.prometheus.wizard.workflow.ProcessWizard.superclass.constructor.call(this, Ext.apply({
            id: id
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.workflowNameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Name',
            name: 'name',
            anchor: '100%',
            disabled: true,
            emptyText: 'name of process'
        });

        this.parentDomainStore = Ext.create('Ext.data.Store', {
            autoLoad: true,
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
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id',
            emptyText: 'select parent domain of process',
            listeners: {
                select: function(combo, records, eOpts) {
                    me.workflowNameField.setDisabled(records.length == 0);
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.processDescriptionField = Ext.create('OPF.core.component.form.TextArea', {
            name: 'description',
            labelAlign: 'top',
            fieldLabel: 'Description',
            emptyText: 'description of process',
            anchor: '100%',
            height: 250
        });

        this.form = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            items: [
                this.parentDomainCombo,
                this.workflowNameField,
                this.processDescriptionField
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
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.process.Process', me.messagePanel, {
                        useBaseUrl: false
                    });

                    me.workflowNameField.customValidator = function(value) {
                        var msg = null;
                        if (me.lastNotUniqueName != value) {
                            if (OPF.isNotBlank(value)) {
                                me.checkUniqueEntityNameTask.delay(250);
                            }
                        } else {
                            msg = 'Name is not unique.';
                        }
                        return msg;
                    };
                }
            }
        });

        this.rootEntityStore = Ext.create('Ext.data.Store', {
            autoLoad: true,
            model: 'OPF.prometheus.wizard.workflow.model.ProcessEntityModel',
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
            fieldLabel: 'Attach Process To',
            anchor: '100%',
            editable: false,
            store: this.rootEntityStore,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            listeners: {
                change: function(combo) {
                    me.entityFieldsStore.removeAll();
                    me.loadEntityFields();
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.processTypeField = Ext.create('Ext.form.RadioGroup', {
            layout:'column',
            labelAlign: 'top',
            fieldLabel: 'Does the workflow create new data or use existing data?',
            items: [
                {
                    columnWidth: .5,
                    checked: true,
                    boxLabel: 'Uses Existing Data Record',
                    name: 'processType',
                    inputValue: 'RELATIONAL'
                },
                {
                    columnWidth: .5,
                    boxLabel: 'Creates New Data Record',
                    name: 'processType',
                    inputValue: 'CREATABLE'
                }
            ]
        });

        this.entityFieldsStore = Ext.create('Ext.data.Store', {
            model: 'OPF.prometheus.wizard.workflow.model.ActivityFieldModel',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                },
                writer: {
                    type: 'json'
                }
            }
        });
//
//        this.entityFieldsGrid = Ext.create('Ext.grid.Panel', {
//            anchor: '100%',
//            padding: '5 10 20 10',
//            store: this.entityFieldsStore,
//            cls: 'grid-panel',
//            height: 300,
//            columns: [
//                {
//                    xtype: 'gridcolumn',
//                    dataIndex: 'name',
//                    text: 'Name',
//                    sortable: false,
//                    renderer: 'htmlEncode',
//                    width: 200,
//                    menuDisabled: true
//                },
//                {
//                    xtype: 'gridcolumn',
//                    dataIndex: 'description',
//                    text: 'Description',
//                    sortable: false,
//                    renderer: 'htmlEncode',
//                    flex: 1,
//                    menuDisabled: true
//                },
//                {
//                    xtype: 'checkcolumn',
//                    dataIndex: 'track',
//                    text: 'Track',
//                    textAlign: 'center',
//                    sortable: false,
//                    width: 65,
//                    menuDisabled: true
//                }
//            ]
//        });

        this.activityFormCombo = Ext.create('OPF.core.component.form.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Type',
            name: 'activityForm',
            anchor: '100%',
            editable: false,
            store: Ext.create('Ext.data.Store', {
                fields: ['type', 'name'],
                data : [
                    {type: 'CUSTOM', name: 'Custom Step'},
                    {type: 'FORM', name: 'Form Step'}
                ]
            }),
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            value: 'CUSTOM',
            listeners: {
                change: function(combo, value) {
                    me.activityEntityFieldsGrid.setVisible(value == 'FORM');
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.activityNameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Name',
            name: 'name',
            anchor: '100%',
            emptyText: 'name of step',
            allowBlank: false,
            validator: function(val) {
                var errorMessage = null;
                if (OPF.isNotEmpty(val)) {
                    var updatedNode = this.ownerCt.getRecord();
                    var rootNode = me.actionActivityTree.getRootNode();
                    Ext.each(rootNode.childNodes, function(activityNode) {
                        if (activityNode.get('name') == val && !(updatedNode && updatedNode.id == activityNode.id)) {
                            errorMessage = 'Activity name is not unique.';
                            return false;
                        }
                        return true;
                    });
                }
                return errorMessage;
            }
        });

        this.activityDescriptionField = Ext.create('OPF.core.component.form.TextArea', {
            labelAlign: 'top',
            fieldLabel: 'Description',
            name: 'description',
            anchor: '100%',
            emptyText: 'step description'
        });

        this.activityActorField = Ext.create('OPF.prometheus.wizard.workflow.ManageActorsComponent', this.parentDomainCombo, {
            labelAlign: 'top',
            fieldLabel: 'Actor',
            anchor: '100%',
            name: 'actor'
        });

        this.activityEntityFieldsStore = Ext.create('Ext.data.Store', {
            model: 'OPF.prometheus.wizard.workflow.model.ActivityFieldModel',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                },
                writer: {
                    type: 'json'
                }
            }
        });

        this.activityEntityFieldsGrid = Ext.create('Ext.grid.Panel', {
            title: 'Activity Entity Fields:',
            anchor: '100%',
            store: this.activityEntityFieldsStore,
            cls: 'grid-panel',
            height: 330,
            hidden: true,
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    text: 'Name',
                    sortable: false,
                    renderer: 'htmlEncode',
                    width: 200,
                    menuDisabled: true
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'description',
                    text: 'Description',
                    sortable: false,
                    renderer: 'htmlEncode',
                    flex: 1,
                    menuDisabled: true
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'track',
                    text: 'Track',
                    textAlign: 'center',
                    sortable: false,
                    width: 65,
                    menuDisabled: true
                }
            ]
        });

        this.activityForm = Ext.create('Ext.form.Panel', {
            title: 'Add Step',
            ui: 'blue',
            layout: 'anchor',
            border: false,
            bodyPadding: 15,
            autoScroll: true,
            items: [
                this.activityFormCombo,
                this.activityNameField,
                this.activityDescriptionField,
                this.activityActorField,
                this.activityEntityFieldsGrid
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
                            width: 150,
                            height: 60,
                            text: 'Save',
                            formBind: true,
                            handler: function() {
                                me.saveActivity();
                            }
                        },
                        {
                            xtype: 'button',
                            ui: 'grey',
                            width: 150,
                            height: 60,
                            text: 'Cancel',
                            handler: function() {
                                me.showEmptyCard();
                            }
                        }
                    ]
                }
            ]
        });

        this.actionNameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Name',
            name: 'name',
            anchor: '100%',
            emptyText: 'name of action',
            allowBlank: false,
            validator: function(val) {
                var errorMessage = null;
                var updatedNode = this.ownerCt.getRecord();
                var parentActivity = updatedNode.parentNode;
                if (OPF.isNotEmpty(val) && OPF.isNotEmpty(parentActivity)) {
                    Ext.each(parentActivity.childNodes, function(actionNode) {
                        if ((!updatedNode && actionNode.get('name') == val) ||
                            (updatedNode && actionNode.get('name') == val && actionNode.id != updatedNode.id)) {
                            errorMessage = 'Action name is not unique.';
                            return false;
                        }
                        return true;
                    });
                }
                return errorMessage;
            }
        });

        this.actionDescriptionField = Ext.create('OPF.core.component.form.TextArea', {
            labelAlign: 'top',
            fieldLabel: 'Description',
            name: 'description',
            anchor: '100%',
            emptyText: 'action description'
        });

        this.actionActivityPanel = Ext.create('OPF.prometheus.wizard.workflow.ManageActivityComponent', {
            name: 'toActivity',
            labelAlign: 'top',
            fieldLabel: 'Next Step'
        });
        
        this.actionStatusField = Ext.create('OPF.prometheus.wizard.workflow.ManageStatusesComponent', {
            labelAlign: 'top',
            fieldLabel: 'Status',
            anchor: '100%',
            name: 'status'
        });

        this.actionForm = Ext.create('Ext.form.Panel', {
            border: false,
            layout: 'anchor',
            bodyPadding: 15,
            ui: 'blue',
            title: 'Add Action',
            items: [
//                {
//                    xtype: 'container',
//                    html: '<h2>Add Action</h2>'
//                },
                this.actionNameField,
                this.actionDescriptionField,
                this.actionActivityPanel,
                this.actionStatusField
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
                            width: 150,
                            height: 60,
                            text: 'Save',
                            formBind: true,
                            handler: function() {
                                me.saveAction();
                            }
                        },
                        {
                            xtype: 'button',
                            ui: 'grey',
                            width: 150,
                            height: 60,
                            text: 'Cancel',
                            handler: function () {
                                me.showEmptyCard();
                            }
                        }
                    ]
                }
            ]
        });

        this.activityActionStore = Ext.create('Ext.data.TreeStore', {
            model: 'OPF.prometheus.wizard.workflow.model.ActivityActionModel',
            root: {
                text: 'root',
                rendered: false,
                expanded: true,
                children: [
                    {
                        name: 'Start',
                        text: 'Start',
                        description: 'The very first step.',
                        activityForm: 'CUSTOM',
                        activityOrder: 'START',
                        editable: false,
                        expanded: true,
                        allowDrag: true,
                        children: [
                            {
                                name: 'Move to Next Step',
                                text: 'Start Workflow',
                                description: 'Do initial workflow step',
                                isActivity: false,
                                activityOrder: null,
                                children: [],
                                allowDrag: false,
                                isNew: false
                            },
                            {
                                name: 'Finish Process',
                                text: 'Finish Process',
                                description: 'Final workflow step',
                                isActivity: false,
                                activityOrder: null,
                                children: [],
                                allowDrag: false,
                                isNew: false
                            }
                        ],
                        isActivity: true,
                        isNew: false
                    },
                    {
                        name: 'Custom Step',
                        text: 'Custom Step',
                        description: 'Custom step...',
                        activityForm: 'CUSTOM',
                        activityOrder: 'MIDDLE',
                        editable: false,
                        expanded: true,
                        allowDrag: true,
                        children: [
                            {
                                name: 'Custom Action',
                                text: 'Custom Action',
                                description: 'Custom workflow step',
                                isActivity: false,
                                activityOrder: null,
                                children: [],
                                allowDrag: false,
                                isNew: false
                            }
                        ],
                        isActivity: true,
                        isNew: false
                    },
                    {
                        name: 'End',
                        text: 'End',
                        description: 'Final step.',
                        activityForm: 'CUSTOM',
                        activityOrder: 'END',
                        editable: false,
                        expanded: true,
                        allowDrag: true,
                        children: [],
                        isActivity: true,
                        isNew: false
                    }
                ]
            },
            folderSort: true
        });

        this.activityActionRowToolbar = Ext.create('OPF.core.component.grid.RowToolbarGridPlugin', {
            buttons: [
                {
                    name: 'addAction',
                    iconCls: 'add-btn',
                    tooltip: 'Add Action'
                },
                {
                    name: 'edit'
                },
                {
                    name: 'delete'
                }
            ],
            showBtnWidth: 25,
            showBtnHeight: 30,
            addActionFn: function(btn, e, options) {
                var record = options[1];
                me.showActionForm(null, record);
            },
            editFn: function(btn, e, options) {
                var record = options[1];
                var isActivity = record.get('isActivity');
                if (isActivity) {
                    me.showActivityForm(record);
                } else {
                    me.showActionForm(record, null);
                }
            },
            deleteFn: function(btn, e, options) {
                var record = options[1];
                var isActivity = record.get('isActivity');
                if (isActivity) {
                    me.deleteActivity(record);
                } else {
                    me.deleteAction(record);
                }
            },
            showFn: function(btn, e) {
                var rowSize = this.rowElement.getSize();
                this.rowCmp.setWidth(rowSize.width);
                this.showRowToolbarButton.hide();

                this.editRecordButton.show();

                var activityOrder = this.currentRecord.get('activityOrder');
                if (activityOrder != 'START' && activityOrder != 'END') {
                    this.deleteRecordButton.show();
                }

                var isActivity = this.currentRecord.get('isActivity');
                if (isActivity && activityOrder != 'END') {
                    this.addActionRecordButton.show();
                }
            },
            scope: this
        });

        this.actionActivityTree = Ext.create('Ext.tree.Panel', {
            store: this.activityActionStore,
            displayField: 'name',
            rootVisible: false,
            width: 320,
            viewConfig: {
                plugins: [
                    {
                        ptype: 'treeviewdragdrop',
                        enableDrag: true,
                        enableDrop: true,
                        appendOnly: true,
                        ddGroup: 'actionActivityGroup'
                    },
                    this.activityActionRowToolbar
                ]
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    ui: 'left-grid',
                    dock: 'top',
                    border: false,
                    cls: 'blue-bg',
                    items: [
                        {
                            ui: 'add-entity',
                            width: 60,
                            height: 60,
                            tooltip: 'Add Step',
                            handler: function() {
                                me.showActivityForm();
                            }
                        }
                    ]
                }
            ]
        });

        var rootNode = this.actionActivityTree.getRootNode();
        rootNode.childNodes[0].childNodes[0].set('toActivity', rootNode.childNodes[1]);
        rootNode.childNodes[0].childNodes[0].set('status', this.actionStatusField.getDefaultIntermediateStatus());

        var finalStatus = this.actionStatusField.getFinalStatus();
        rootNode.childNodes[0].childNodes[1].set('toActivity', rootNode.childNodes[2]);
        rootNode.childNodes[0].childNodes[1].set('status', finalStatus);

        rootNode.childNodes[1].childNodes[0].set('toActivity', rootNode.childNodes[2]);
        rootNode.childNodes[1].childNodes[0].set('status', finalStatus);

        this.activityMessagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true
        });

        this.activityPanelWrapper = Ext.create('Ext.container.Container', {
            layout: 'card',
            flex: 1,
            items: [
                {
                    xtype: 'panel',
                    ui: 'blue',
                    border: false,
                    title: 'Information',
                    bodyPadding: 10,
                    items: [
                        this.activityMessagePanel,
                        {
                            xtype: 'container',
                            html: '<p>Some instructions how to add/edit/delete actions and steps.</p>'
                        }
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
                                        me.goToDeployPanel();
                                    }
                                }
                            ]
                        }
                    ]
                },
                this.activityForm,
                this.actionForm
            ]
        });

        this.items = [
            {
                title: '1. Name & Description',
                layout: 'fit',
                items: [
                    this.form
                ],
                nextFrameFn: function() {
                    me.goToSelectFieldsPanel();
                }
            },
            {
                title: '2. Attach Data',
                layout: 'fit',
                items: {
                    xtype: 'panel',
                    border: false,
                    padding: 10,
                    layout: 'anchor',
                    items: [
                        this.rootEntityCombo,
                        this.processTypeField
//                        {
//                            xtype: 'container',
//                            html: '<div class="x-form-item-label">Track Fields</div>',
//                            padding: '0 10 0 10'
//                        },
//                        this.entityFieldsGrid
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
                                        me.goToActionsPanel();
                                    }
                                }
                            ]
                        }
                    ]
                },
                prevFrameFn: function() {
                    me.goToNameAndDescriptionPanel();
                },
                nextFrameFn: function() {
                    me.goToActionsPanel();
                }
            },
            {
                title: '3. Define Steps',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'panel',
                        border: false,
                        layout: 'fit',
                        items: this.actionActivityTree
                    },
                    this.activityPanelWrapper
                ],
                prevFrameFn: function() {
                    me.goToSelectFieldsPanel();
                },
                nextFrameFn: function() {
                    me.goToDeployPanel();
                }
            },
            {
                title: '4. Deploy Changes',
                yesFn: function() {
                    me.createWorkflow(true);
                },
                noFn: function() {
                    me.createWorkflow(false);
                },
                prevFrameFn: function() {
                    me.goToActionsPanel();
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
        this.checkUniqueEntityNameTask = new Ext.util.DelayedTask(function(){
            var name = me.workflowNameField.getValue();
            var parentDomain = me.parentDomainCombo.findRecordByValue(me.parentDomainCombo.getValue());
            if (OPF.isNotBlank(name) && parentDomain != null) {
                var path = parentDomain.get('lookup');
                var url = OPF.Cfg.restUrl('/registry/check/' + path + '/PROCESS', false);
                url = OPF.Cfg.addParameterToURL(url, 'name', name);
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    success: function (response) {
                        if (me.workflowNameField) {
                            var resp = Ext.decode(response.responseText);
                            if (resp.success) {
                                var activeErrors = me.workflowNameField.activeErrors;
                                if (activeErrors && activeErrors.length == 0) {
                                    me.workflowNameField.clearInvalid();
                                }
                            } else {
                                me.workflowNameField.markInvalid(resp.message);
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

    goToNameAndDescriptionPanel: function() {
        var layout = this.getCardPanelLayout();
        layout.setActiveItem(0);
    },

    goToSelectFieldsPanel: function() {
        if (this.isNameAndDescriptionPanelValid()) {
            var layout = this.getCardPanelLayout();
            layout.setActiveItem(1);
        } else if (OPF.isEmpty(this.parentDomainCombo.getValue())){
            this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Parent has not been selected.');
        } else {
            this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Workflow Name is not specified.');
        }
    },

    goToActionsPanel: function() {
        if (this.isAttachDataPanelValid()) {
            var layout = this.getCardPanelLayout();
            layout.setActiveItem(2);
        }
    },

    goToDeployPanel: function() {
        if (this.isDefineStepsPanelValid()) {
            var layout = this.getCardPanelLayout();
            layout.setActiveItem(3);
        }
    },

    isNameAndDescriptionPanelValid: function() {
        var parentId = this.parentDomainCombo.getValue();
        var workflowName = this.workflowNameField.getValue();
        return OPF.isNotEmpty(parentId) && OPF.isNotBlank(workflowName) && this.lastNotUniqueName != workflowName;
    },

    isAttachDataPanelValid: function() {
        return this.rootEntityCombo.validate() && this.isNameAndDescriptionPanelValid();
    },

    isDefineStepsPanelValid: function() {
        return this.isActionActivitiesPanelValid() && this.isAttachDataPanelValid();
    },

    validateForm: function(executeFn, scope) {
        var parentId = this.parentDomainCombo.getValue();
        if (OPF.isNotEmpty(parentId) && Ext.isNumeric(parentId) && this.isActionActivitiesPanelValid()) {
            if (this.form.getForm().isValid()) {
                executeFn(scope);
            }
        } else {
            this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Parent has not been selected.');
        }
    },

    isActionActivitiesPanelValid: function() {
        var rootNode = this.actionActivityTree.getRootNode();
        var startActivityInPlace = false;
        var finalActivityInPlace = false;
        var endStatusInPlace = false;
        var me = this;

        var activityErrorMessage = [];

        var activities = rootNode.childNodes;
        Ext.each(activities, function(activity) {
            var activityName = activity.get('name');
            var actor = activity.get('actor');
            if (OPF.isBlank(activityName) || OPF.isEmpty(actor) || actor == "") {
                activityErrorMessage.push({
                    level: OPF.core.validation.MessageLevel.ERROR,
                    msg: 'For activity \'' + activityName + '\' has not defined an actor'
                });
            }
            if (activity.get('activityOrder') == 'START') {
                startActivityInPlace |= true;
            }
            if (activity.get('activityOrder') == 'END') {
                finalActivityInPlace |= true;
            }
            var actions = activity.childNodes;
            Ext.each(actions, function(action) {
                var actionName = action.get('name');
                var toActivityNode = action.get('toActivity');
                if (OPF.isBlank(actionName) ) {
                    activityErrorMessage.push({
                        level: OPF.core.validation.MessageLevel.ERROR,
                        msg: 'The action has empty name for the activity \'' + activityName + '\''
                    });
                } else if (!toActivityNode) {
                    activityErrorMessage.push({
                        level: OPF.core.validation.MessageLevel.ERROR,
                        msg: 'For action \'' + actionName + '\' has not defined \'To activity\' for activity \'' + activityName + '\''
                    });
                } else {
                    var status = action.get('status');
                    if (OPF.isEmpty(status)) {
                        activityErrorMessage.push({
                            level: OPF.core.validation.MessageLevel.ERROR,
                            msg: 'For action \'' + actionName + '\' has not defined status for the activity \'' + activityName + '\''
                        });
                    } else if (me.actionStatusField.isFinalStatus(status)) {
                        if (toActivityNode.get('activityOrder') == 'END') {
                            endStatusInPlace = true;
                        }
                    }
                }
            });
        });
        if (!startActivityInPlace) {
            activityErrorMessage.push({
                level: OPF.core.validation.MessageLevel.ERROR,
                msg: 'Has not defined START activity for workflow'
            });
        }
        if (!finalActivityInPlace) {
            activityErrorMessage.push({
                level: OPF.core.validation.MessageLevel.ERROR,
                msg: 'Has not defined END activity for workflow'
            });
        }
        if (!endStatusInPlace) {
            activityErrorMessage.push({
                level: OPF.core.validation.MessageLevel.ERROR,
                msg: 'Has not find action which moved to END activity'
            });
        }
        var isValid = activityErrorMessage.length == 0;
        if (isValid) {
            this.activityMessagePanel.cleanActiveErrors();
        } else {
            this.activityMessagePanel.setNoticeContainer(activityErrorMessage);
        }
        return isValid && startActivityInPlace && finalActivityInPlace && endStatusInPlace;
    },

    showActivityForm: function(record) {
        var fields;
        this.activityEntityFieldsStore.removeAll();
        if (record) {
            this.activityForm.setTitle('Edit Step');
            this.activityForm.loadRecord(record);
            if (OPF.isEmpty(this.activityActorField.getValue())) {
                this.activityActorField.validate();
            }

            var actor = record.get('actor');
            this.activityActorField.setValue(actor);

            fields = record.activityFields().getRange();
            if (fields.length == 0) {
                OPF.StoreHelper.copyRecords(this.entityFieldsStore, this.activityEntityFieldsStore);
            } else {
                this.activityEntityFieldsStore.loadRecords(fields);
            }
        } else {
            this.activityForm.setTitle('Add Step');
            this.activityForm.getForm().reset(true);
            OPF.StoreHelper.copyRecords(this.entityFieldsStore, this.activityEntityFieldsStore);
        }
        this.activityPanelWrapper.getLayout().setActiveItem(1);
    },

    showActionForm: function(record, activityNode) {
        if (record) {
            this.actionForm.setTitle('Edit Action');
            this.actionForm.loadRecord(record);
        } else {
            this.actionForm.setTitle('Add Action');
            record = OPF.ModelHelper.createModelFromData('OPF.prometheus.wizard.workflow.model.ActivityActionModel', {
                editable: false,
                expanded: false,
                allowDrag: false,
                children: [],
                isActivity: false,
                isNew: true
            });
            record.parentNode = activityNode;

            this.actionForm.getForm().reset(true);
            this.actionForm.loadRecord(record);
        }
        this.activityPanelWrapper.getLayout().setActiveItem(2);
    },

    deleteActivity: function(record) {
        var rootNode = this.activityActionStore.getRootNode();
        var activities = rootNode.childNodes;
        Ext.each(activities, function(activity) {
            var actions = activity.childNodes;
            Ext.each(actions, function(action) {
                var toActivityNode = action.get('toActivity');
                if (toActivityNode == record) {
                    action.set('toActivity', null);
                }
            });
        });
        record.remove();
        this.showEmptyCard();
    },

    deleteAction: function(record) {
        record.remove();
        this.showEmptyCard();
    },

    showEmptyCard: function() {
        this.actionActivityTree.selModel.deselectAll(true);
        this.activityPanelWrapper.getLayout().setActiveItem(0);
        this.activityMessagePanel.cleanActiveErrors();
    },

    saveActivity: function () {
        var me = this;

        var form = this.activityForm.getForm();
        if (form.isValid()) {
            var record = form.getRecord();
            var values = form.getValues();
            if (record) {
                record.set(values);
                record.set('actor', this.activityActorField.getValue());
                record.activityFields().removeAll();
                var activityForm = record.get('activityForm');
                if (activityForm == 'FORM') {
                    record.activityFields().add(this.activityEntityFieldsStore.getRange());
                }
                record.commit();
            } else {
                var recordData = Ext.apply(values, {
                    editable: false,
                    expanded: true,
                    allowDrag: true,
                    children: [],
                    isActivity: true,
                    activityOrder: 'MIDDLE'
                });
                record = OPF.ModelHelper.createModelFromData('OPF.prometheus.wizard.workflow.model.ActivityActionModel', recordData);
                record.set('actor', this.activityActorField.getValue());
                record.activityFields().add(this.activityEntityFieldsStore.getRange());
                var rootNode = me.actionActivityTree.getRootNode();
                rootNode.appendChild(record);
            }
            me.showEmptyCard();
        }
    },

    saveAction: function () {
        var me = this;

        var form = this.actionForm.getForm();
        if (form.isValid()) {
            var record = form.getRecord();
            var values = form.getValues();

            var isNew = record.get('isNew');
            record.set(values);
            record.set('status', this.actionStatusField.getValue());
            record.set('toActivity', this.actionActivityPanel.getValue());
            record.set('isNew', false);
            record.commit();

            if (isNew) {
                record.parentNode.appendChild(record);
            }
            me.showEmptyCard();
        }
    },

    deleteActivityAction: function(btn, e, options) {
        var grid = options[0];
        var record = options[1];

        var rootNode = me.actionActivityTree.getRootNode();

    },

    createWorkflow: function(isDeploy) {
        var me = this;

        var workflowName = this.workflowNameField.getValue();
        var rootEntityId = this.rootEntityCombo.getValue();
        var parentDomainId = this.parentDomainCombo.getValue();
        var parentDomain = this.parentDomainCombo.findRecord('id', parentDomainId);
        var processType = this.processTypeField.getValue().processType;

        var entity = {
            id: rootEntityId
        };
        var processFields = [];

//        var fieldModels = this.entityFieldsStore.getRange();
//        if (fieldModels != null && fieldModels.length > 0) {
//            Ext.each(fieldModels, function(fieldModel) {
//                if (fieldModel.get('track')) {
//                    var field = {
//                        id: fieldModel.get('id')
//                    };
//                    var processField = {
//                        name: fieldModel.get('name'),
//                        field: field,
//                        registryNodeType: entity
//                    };
//                    processFields.push(processField);
//                }
//            });
//        }

        var process = {
            name: workflowName,
            path: parentDomain.get('lookup'),
            parentId: parentDomainId,
            processFields: processFields,
            processType: processType,
            entity: entity
        };

        var rootNode = this.actionActivityTree.getRootNode();
        process.activities = [];
        Ext.each(rootNode.childNodes, function(activityNode, index) {
            var activity = {
                name: activityNode.get('name'),
                description: activityNode.get('description'),
                activityOrder: activityNode.get('activityOrder'),
                activityForm: activityNode.get('activityForm')
            };

            activity.fields = [];
            var entityFields = activityNode.activityFields().getRange();
            Ext.each(entityFields, function(entityField) {
                var isTracked = entityField.get('track');
                if (isTracked) {
                    var fieldType = entityField.get('fieldType');
                    var activityField;
                    if (fieldType == 'ENTITY') {
                        activityField = {
                            relationship: {
                                id: entityField.get('relationshipId')
                            }
                        };
                    } else {
                        activityField = {
                            field: {
                                id: entityField.get('fieldId')
                            }
                        };
                    }
                    activity.fields.push(activityField);
                }
            });

            activity.activityActions = [];
            Ext.each(activityNode.childNodes, function(actionNode) {
                var activityFrom = {
                    name: activityNode.get('name')
                };
                var toActivityNode = actionNode.get('toActivity');
                var activityTo = {
                    name: toActivityNode.get('name')
                };
                var statusModel = actionNode.get('status');
                var status = {
                    name: statusModel.get('name'),
                    description: statusModel.get('description')
                };
                var action = {
                    name: actionNode.get('name'),
                    description: actionNode.get('description'),
                    status: status,
                    activityFrom: activityFrom,
                    activityTo: activityTo
                };
                activity.activityActions.push(action);
            });

            var actorModel = activityNode.get('actor');
            var actorId = actorModel.get('id');
            if (OPF.isEmpty(actorId)) {
                activity.actor = {
                    name: actorModel.get('name'),
                    description: actorModel.get('description'),
                    userActors: actorModel.get('userActors')
                };
            } else {
                activity.actor = {
                    id: actorId
                };
            }
            process.activities.push(activity);
        });

        this.getEl().mask();

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/process/workflow'),
            method: 'POST',
            jsonData: {"data": process},

            success: function (response, action) {
                var responseData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, responseData.message);
                if (responseData.success) {
                    document.location.reload(true);
                } else {
                    Ext.Msg.alert('Error', responseData.message);
                }
                me.getEl().unmask();
                me.close();
            },

            failure: function (response) {
                me.getEl().unmask();
                OPF.Msg.setAlert(false, response.message);
            }
        });

    },

    loadEntityFields: function() {
        var me = this;
        var entityId = this.rootEntityCombo.getValue();
        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/registry/entity-fields/' + entityId),
            method: 'GET',
            success: function (response, action) {
                var json = Ext.decode(response.responseText);
                if (json.success && OPF.isNotEmpty(json.data) && Ext.isArray(json.data) && json.data.length > 0) {
                    var entity = json.data[0];
                    var fields = OPF.isEmpty(entity.children) ? [] : entity.children;
                    Ext.each(fields, function(field) {
                        var fieldId = null;
                        var fieldType = null;
                        var relationshipId = null;
                        if (field.type == 'FIELD') {
                            fieldId = field.realId;
                            fieldType = field.parameters.fieldType;
                        } else {
                            fieldType = field.type;
                            var relationshipIds = field.parameters.relationshipIds;
                            if (relationshipIds && relationshipIds.length > 0) {
                                relationshipId = relationshipIds[0];
                            }
                        }

                        var activityFieldModel = OPF.ModelHelper.createModelFromData('OPF.prometheus.wizard.workflow.model.ActivityFieldModel', {
                            name: field.name,
                            description: field.description,
                            displayName: OPF.ifBlank(field.parameters.displayName, field.name),
                            fieldType: fieldType,
                            fieldId: fieldId,
                            relationshipId: relationshipId,
                            track: false
                        });
                        me.entityFieldsStore.add(activityFieldModel);
                    });
                } else {
                    Ext.Msg.alert('Error', json.message);
                }
            },
            failure: function(response) {
                Ext.Msg.alert('Error', response.message);
            }
        });
    }

});