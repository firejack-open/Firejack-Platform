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


var FILTER_ALL_WORK = 'allWork';
var FILTER_MY_WORK = 'myWork';
var FILTER_MY_TEAMS_WORK = 'myTeamsWork';
var FILTER_MY_COMPLETED_WORK = 'myCompletedWork';
var FILTER_PROCESS = 'process';
var FILTER_SEARCH = 'search';

/**
 *
 */
Ext.define('OPF.console.inbox.view.task.TaskFilterPanel', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.task-filter-panel',

    title: 'Filters',
    layout: 'accordion',
    activeItem: 0,
    split: true,
    border: false,

    managerLayout: null,

    filterBy: FILTER_MY_WORK,
    filterDisplayInactive: false,
    selectedProcessId: null,

    constructor: function(managerLayout, cfg) {
        cfg = cfg || {};
        this.lookupPrefix = null;
        OPF.console.inbox.view.task.TaskFilterPanel.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout
        }, cfg));
    },

    setLookupPrefix: function(lookupPrefix) {
        this.lookupPrefix = lookupPrefix;
    },

    initComponent: function() {
        var instance = this;

        this.treeStore = Ext.create('Ext.data.TreeStore', {
            model: 'OPF.console.domain.model.ProcessModel',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            root: {
                text: 'root',
                rendered: false,
                expanded: true
            }
        });

        this.createTreeNodeFilter = function(text, filterParameter) {
            return {
                text: text,
                leaf: true,
                id: filterParameter + 'NodeId',
                filterParameter: filterParameter
            }
        };
        var standardBranch = Ext.data.NodeInterface.decorate(
            Ext.create('OPF.core.model.RegistryNodeTreeModel', {
                text: 'Standard',
                expanded: true
            })
        );
        standardBranch.appendChild(this.createTreeNodeFilter('All work', FILTER_ALL_WORK));
        standardBranch.appendChild(this.createTreeNodeFilter('My work', FILTER_MY_WORK));
        standardBranch.appendChild(this.createTreeNodeFilter('My team\'s work', FILTER_MY_TEAMS_WORK));
        standardBranch.appendChild(this.createTreeNodeFilter('My completed work', FILTER_MY_COMPLETED_WORK));
        this.treeStore.getRootNode().appendChild(standardBranch);

        var processesBranch = Ext.data.NodeInterface.decorate(
            Ext.create('OPF.core.model.RegistryNodeTreeModel', {
                text: 'Processes',
                leaf: false,
                expanded: false
            })
        );
        this.treeStore.getRootNode().appendChild(processesBranch);

        Ext.Ajax.request({
			url: OPF.core.utils.RegistryNodeType.PROCESS.generateUrl('/my'),
			method: 'GET',
			success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (resp.success) {
                    var processes = resp.data;
                    if (OPF.isNotEmpty(processes) && processes.length > 0) {
                        for (var i = 0; i < processes.length; i++) {
                            var model = Ext.create('OPF.console.domain.model.ProcessModel', processes[i]);
                            model.set('text', model.get('name'));
                            model.set('leaf', true);
                            model.set('iconCls', 'tricon-process');
                            model.set('canUpdate', false);
                            model.set('canDelete', false);
                            model.set('expandable', false);
                            model.set('allowDrag', false);
                            model.set('allowDrop', false);
                            instance.treeStore.getRootNode().getChildAt(1).appendChild(model);
                            //processesBranch.appendChild(model);
                            //processesBranch.appendChild(Ext.data.NodeInterface.decorate(model));
                        }
                    }
                } else {
                    OPF.Msg.setAlert(false, resp.message);
                }
			},
            failure: function(o) {
                OPF.Msg.setAlert(false, 'Exception occurred.');
            }
		});

        this.savedFiltersTreePanel = Ext.create('Ext.tree.Panel', {
            border: false,
            rootVisible: false,
            store: this.treeStore,
            flex: 1
        });

        this.showInactiveChkbox = Ext.create('OPF.core.component.Checkbox', {
            name: 'showInactive',
            anchor: '90%',
            boxLabel: 'Display inactive',
            checked: this.filterDisplayInactive,
            handler: function(chkbox, checked) {
                instance.filterDisplayInactive = checked;
                instance.managerLayout.taskCasePanel.getLayout().getActiveItem().refreshGrids();
                //instance.managerLayout.currentGridPanel.refreshGrids();
            }
        });

        this.savedFiltersPanel = Ext.create('Ext.panel.Panel', {
            title: 'Saved',
            padding: 5,
            layout: { type: 'vbox', align: 'stretch' },
            items: [
                this.showInactiveChkbox,
                OPF.Ui.ySpacer(5),
                this.savedFiltersTreePanel
            ]
        });

        this.fieldDescription = OPF.Ui.textFormField('description', 'Description', { anchor: '90%' });

        this.fieldAssignee = OPF.Ui.comboFormField('assigneeId', 'Assignee', {
            anchor: '90%',
            selectOnFocus: true,
            allowBlank:true,
            forceSelection: true,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender:true,
            mode: 'remote',
            store: 'Users',
            valueField: 'id',
            displayField: 'username',
            hiddenName: 'assigneeId'
        });

        this.fieldActive = OPF.Ui.comboFormField('active', 'Active', {
            anchor: '90%',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender:true,
            mode: 'local',
            store: new Ext.data.ArrayStore({
                id: 0,
                fields: [ 'myId', 'displayText' ],
                data: [
                    ['true', 'Only active'],
                    ['false', 'Only inactive'],
                    ['', 'All']
                ]
            }),
            value: 'true',
            valueField: 'myId',
            displayField: 'displayText',
            hiddenName: 'active'
        });

        this.fieldProcess = OPF.Ui.comboFormField('processId', 'Process', {
            anchor: '90%',
            selectOnFocus: true,
            allowBlank:true,
            forceSelection: true,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender:true,
            mode: 'remote',
            store: 'MyProcesses',
            valueField: 'id',
            displayField: 'name',
            hiddenName: 'processId'
        });

        this.fieldActivity = OPF.Ui.comboFormField('activityId', 'Activity', {
            anchor: '90%',
            disabled: true,
            selectOnFocus: true,
            allowBlank:true,
            forceSelection: true,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender:true,
            mode: 'remote',
            store: 'ProcessActivities',
            valueField: 'id',
            displayField: 'name',
            hiddenName: 'activityId'
        });

        this.fieldStatus = OPF.Ui.comboFormField('statusId', 'Status', {
            anchor: '90%',
            disabled: true,
            selectOnFocus: true,
            allowBlank:true,
            forceSelection: true,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender:true,
            mode: 'remote',
            store: 'ProcessStatuses',
            valueField: 'id',
            displayField: 'name',
            hiddenName: 'statusId'
        });

        this.fieldAfter = OPF.Ui.formDate('startDate', 'After', { anchor: '90%' });
        this.fieldBefore = OPF.Ui.formDate('endDate', 'Before', { anchor: '90%' });

        this.form = Ext.create('Ext.form.Panel', {
            border: false,
            labelAlign: 'top',
            padding: 2,
            boxMinHeight: 450,
            fieldDefaults: { labelAlign: 'top' },
            fbar: [
                '->',
                OPF.Ui.createBtn('Search', 55, 'search', {cls: 'searchBtn'}),
                '->'
            ],
            /*fbar: {
                xtype: 'toolbar',
                buttonAlign: 'center',
                items: [
                    {
                        xtype: 'button',
                        text: 'Search',
                        width: 45,
                        handler: function(btn, ev) {
                            instance.filterBy = FILTER_SEARCH;
                            instance.managerLayout.taskCasePanel.getLayout().getActiveItem().refreshGrids();
                            //instance.managerLayout.currentGridPanel.refreshGrids();
                        }
                    }
                ]
            },*/
            items: [
                this.fieldDescription,
                this.fieldAssignee,
                this.fieldProcess,
                this.fieldActivity,
                this.fieldStatus,
                this.fieldAfter,
                this.fieldBefore,
                this.fieldActive
            ]
        });

        this.searchPanel = {
            xtype: 'panel',
            title: 'Search',
            layout: 'fit',
            border: false,
            padding: 5,
            autoScroll: true,
            items: [
                this.form
            ]
        };

        this.items = [
            this.savedFiltersPanel,
            this.searchPanel
        ];

        this.callParent(arguments);
    },

    getSearchParameters: function() {
        var formData = this.form.getForm().getValues();
        formData.startDate = this.fieldAfter.getValue();
        formData.endDate = this.fieldBefore.getValue();
        formData.lookupPrefix = this.lookupPrefix;
        if (OPF.isBlank(formData.active)) {
            delete formData.active; // don't send empty string
        }

        // need to send custom fields as an array of objects
        formData.customFields = [];
        Ext.each(this.customFieldDefs, function(customFieldDef) {
            var columnValue = formData['customField_' + customFieldDef.id];
            delete formData['customField_' + customFieldDef.id];
            if (OPF.isBlank(columnValue)) {
                return;
            }
            var columnName;
            if (customFieldDef.valueType == FIELD_TYPE_INTEGER) {
                columnName = 'integerValue';
            } else if (customFieldDef.valueType == FIELD_TYPE_STRING) {
                columnName = 'stringValue';
            } else if (customFieldDef.valueType == FIELD_TYPE_LONG) {
                columnName = 'longValue';
            } else if (customFieldDef.valueType == FIELD_TYPE_BOOLEAN) {
                columnName = 'booleanValue';
            } else if (customFieldDef.valueType == FIELD_TYPE_DATE) {
                columnName = 'dateValue';
                columnValue = sqFormatDateForJerseyParser(columnValue);
            } else if (customFieldDef.valueType == FIELD_TYPE_DOUBLE) {
                columnName = 'doubleValue';
            }
            var customField = {
                fieldId: customFieldDef.id,
                valueType: customFieldDef.valueType
            };
            customField[columnName] = columnValue;
            formData.customFields.push(customField);
        });

        return formData;
    },

    customFieldDefs: [],

    showCustomFields: function(processId) {
        var instance = this;

        Ext.each(this.customFieldDefs, function(customFieldDef) {
            instance.form.remove('cfId_' + customFieldDef.id);
        });

        instance.customFieldDefs = [];

        if (OPF.isBlank(processId)) {
            var formHeight = 450;
            instance.form.boxMinHeight = formHeight;
            instance.form.setHeight(formHeight);
            instance.form.doLayout();
            return;
        }

        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.PROCESS_FIELD.generateGetUrl(processId),
            method: 'GET',
            success: function(response, action) {
                var jsonData = Ext.decode(response.responseText);
                var customFields = jsonData.data;
                Ext.each(customFields, function(customFieldDef) {
                    var customField = instance.createCustomField(customFieldDef);
                    instance.form.add(customField);
                    instance.customFieldDefs.push(customFieldDef);
                });
                var formHeight = 450 + customFields.length * 50;
                instance.form.boxMinHeight = formHeight;
                instance.form.setHeight(formHeight);
                instance.form.doLayout();
            }
        });

    },

    createCustomField: function(customFieldDef) {
        var options = {};

        if (customFieldDef.valueType == FIELD_TYPE_INTEGER) {
            options = {
                xtype: 'numberfield',
                allowDecimals: false
            };
        } else if (customFieldDef.valueType == FIELD_TYPE_STRING) {
            options = {
                xtype: 'textfield'
            }
        } else if (customFieldDef.valueType == FIELD_TYPE_LONG) {
            options = {
                xtype: 'numberfield',
                allowDecimals: false
            };
        } else if (customFieldDef.valueType == FIELD_TYPE_BOOLEAN) {
            options = this.createBooleanCombo(customFieldDef);
        } else if (customFieldDef.valueType == FIELD_TYPE_DATE) {
            options = {
                xtype: 'datefield'
            };
        } else if (customFieldDef.valueType == FIELD_TYPE_DOUBLE) {
            options = {
                xtype: 'numberfield',
                allowDecimals: true
            };
        }

        return Ext.ComponentMgr.create(Ext.apply({
            id: 'cfId_' + customFieldDef.id,
            name: 'customField_' + customFieldDef.id,
            fieldLabel: customFieldDef.name,
            anchor: '90%'
        }, options));
    },

    createBooleanCombo: function(customFieldDef) {
        return {
            xtype: 'sqcombo',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender:true,
            mode: 'local',
            store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'myId',
                    'displayText'
                ],
                data: [
                    ['true', 'True'],
                    ['false', 'False'],
                    ['', 'All']
                ]
            }),
            valueField: 'myId',
            displayField: 'displayText',
            hiddenName: 'customField_' + customFieldDef.id
        };
    }


});
