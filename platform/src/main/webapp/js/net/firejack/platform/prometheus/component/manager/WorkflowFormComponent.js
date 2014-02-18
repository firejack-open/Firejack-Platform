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
    'OPF.prometheus.component.manager.FormComponent'
]);

Ext.define('OPF.prometheus.component.manager.WorkflowFormComponent', {
    extend: 'OPF.prometheus.component.manager.FormComponent',
    alias: 'widget.prometheus.component.workflow-form-component',


    workflowData: null,

    initComponent: function() {
        this.workflowData = this.getWorkflowData();

        this.callParent(arguments);
    },

    initWorkflowPanel: function() {
        this.workflowPanel = Ext.ComponentMgr.create({
            xtype: 'panel',
            cls: 'workflow-info',
            title: 'Workflow Information',
            margin: '0 0 15',
            bodyPadding: 10,
            items: [
                {
                    xtype: 'container',
                    tpl: '<h3><b>Process:</b> {process.name}</h3>',
                    margin: '0 0 10 0',
                    data: this.workflowData
                },
                {
                    xtype: 'container',
                    layout: 'column',
                    defaults: {
                      border: false
                    },
                    items: [
                        {
                            cls: 'workflow-activity',
                            padding: '15 10',
                            tpl: [
                                '<tpl if="fromActivity">',
                                    '<span><b>Current Activity:</b> {fromActivity.name}</span>',
                                    '<br><span><b>Current Status:</b> {fromActivity.status.name}</span>',
                                '<tpl else>',
                                    '<span>Creating a new instance of record and launch workflow</span>',
                                '</tpl>'
                            ],
                            data: this.workflowData,
                            columnWidth: 0.5
                        },
                        {
                            cls: 'workflow-arrow',
                            padding: '15 0',
                            textAlign: 'center',
                            html: '>>',
                            width: 21
                        },
                        {
                            cls: 'workflow-activity',
                            padding: '15 10',
                            tpl: [
                                '<span><b>Next Activity:</b> {toActivity.name}</span>'
                            ],
                            data: this.workflowData,
                            columnWidth: 0.5
                        }
                    ]
                }
            ]
        });

        this.items = Ext.Array.merge([this.workflowPanel], this.items);
    },

    getWorkflowData: function() {
        var process = OPF.Cfg.EXTRA_PARAMS.process;
        var workflowData = null;
        if (OPF.isNotEmpty(process) && Ext.isArray(process.activities) && process.activities.length == 2) {
            var fromActivity = process.activities[0];
            var toActivity = process.activities[1];
            workflowData = {
                process: {
                    id: process.id,
                    name: process.name
                },
                toActivity: {
                    id: toActivity.id,
                    name: toActivity.name,
                    form: toActivity.activityForm
                }
            };
            if (fromActivity) {
                workflowData.fromActivity = {
                    id: fromActivity.id,
                    name: fromActivity.name,
                    status: {
                        id: fromActivity.status.id,
                        name: fromActivity.status.name
                    }
                };
            }
            if (toActivity.activityForm == 'FORM') {
                var activityFields = [];
                Ext.each(toActivity.fields, function(activityField) {
                    if (activityField.field) {
                        activityFields.push({
                            name: activityField.field.name,
                            type: 'FIELD'
                        });
                    } else if (activityField.relationship) {
                        var entity = activityField.relationship.targetEntity;
                        activityFields.push({
                            name: entity.name,
                            lookup: entity.lookup,
                            type: 'ENTITY'
                        });
                    }
                });
                workflowData.toActivity.activityFields = activityFields;
            }
            if (toActivity.activityActions && toActivity.activityActions.length > 0) {
                var activityAction = toActivity.activityActions[0];
                workflowData.activityAction = {
                    id: activityAction.id,
                    name: activityAction.name
                }
            }
            workflowData.entity = OPF.Cfg.EXTRA_PARAMS.entity;
        }
        return workflowData;
    },

    prepareFormFields: function () {
        var me = this;

        var fields = [];
        Ext.each(this.modelInstance.fields.items, function (field) {
            if (me.isWorkflowField(field, 'FIELD')) {
                var components = me.getComponentFromModelField(field, me.modelInstance);
                fields = Ext.Array.merge(fields, components);
            }
        });

        var parentEntityCombo = null;
        Ext.each(this.modelInstance.associations.items, function (association) {
            if (me.isWorkflowField(association, 'ENTITY')) {
                var component = me.getComponentFromModelAssociation(association);
                var parentIdParam = OPF.getQueryParam('parentId');
                if (OPF.isNotEmpty(parentIdParam) && association.type == 'belongsTo' && association.name == 'parent') {
                    parentEntityCombo = component;
                } else {
                    fields.push(component);
                }
            }
        });

        if (parentEntityCombo != null) {
            fields.splice(0, 0, parentEntityCombo);
        }

        this.parentLinksContainer = Ext.create('Ext.container.Container', {
            html: ''
        });

        fields.splice(0, 0, this.parentLinksContainer);

        var nestedChildrenButtons = [];
        Ext.each(this.modelInstance.children, function (child) {
            nestedChildrenButtons.push({
                text: 'Add ' + child.displayName,
                formBind: true,
                handler: function (b) {
                    var data = me.prepareDataForSaveOperation();
                    me.saveFormData(data.url, data.method, data.formData, function (message, respData) {
                        if (Ext.isArray(respData) && respData.length > 0) {
                            me.redirectToChildPage(respData[0], me.modelInstance, child);
                        }
                    }, function (message, sourceResponse) {
                        var parentEntityId;
                        if (OPF.isNotEmpty(data.formData) && OPF.isNotEmpty(parentEntityId = data.formData.id)) {
                            me.redirectToChildPage(data.formData, me.modelInstance, child);
                        } else {
                            me.validator.showValidationMessages(sourceResponse);
                        }
                    });
                }
            });
        });

        if (nestedChildrenButtons.length > 0) {
            nestedChildrenButtons.push('->');
            fields.push(Ext.create('Ext.toolbar.Toolbar', {
                ui: 'footer',
                items: nestedChildrenButtons
            }));
        }
        return fields;
    },

    prepareFormButtons: function() {
        var buttons = this.callParent();

        if (this.workflowData.activityAction) {
            var executeActionButton = {
                text: this.workflowData.activityAction.name,
                ui: 'save',
                itemId: 'action',
                formBind: true,
                scope: this,
                handler: this.onExecuteActionClick
            };
            buttons = Ext.Array.merge([executeActionButton], buttons);
        }
        return buttons;
    },

    isWorkflowField: function(field, type) {
        var result = true;

        if (OPF.isNotEmpty(this.workflowData) && Ext.isArray(this.workflowData.toActivity.activityFields)) {
            result = false;
            Ext.each(this.workflowData.toActivity.activityFields, function(activityField) {
                if (activityField.type == type) {
                    if (activityField.type == 'FIELD') {
                        result |= activityField.name == field.name;
                    } else if (activityField.type == 'ENTITY') {
                        var associationModel = 'CMV.' + activityField.lookup + '.model.' + activityField.name + 'Model';
                        result |= associationModel == field.model;
                    }
                }
            });
        }
        return result;
    },

    onExecuteActionClick: function() {
        var activityAction = {
            id: this.workflowData.activityAction.id,
            name: this.workflowData.activityAction.name
        };
        this.showPerformActivityActionDialog(activityAction, this.workflowData.toActivity);
    },

    onSaveClick: function() {
        var result = this.prepareDataForSaveOperation();
        if (result != null) {
            if (this.workflowData.activityAction) {
                this.saveFormData(result.url, result.method, result.formData, Ext.Function.bind(this.onCancelClick, this));
            } else {
                this.saveFormData(result.url, result.method, result.formData, Ext.Function.bind(this.createWorkflowTask, this));
            }
        }
    },

    onCancelClick: function() {
        document.location.href = OPF.Cfg.fullUrl('inbox', true);
    },

    createWorkflowTask: function(responseMessage, responseData) {
        var me = this;

        var url = OPF.Cfg.restUrl('/process/workflow/task/record', false);
        url = OPF.Cfg.addParameterToURL(url, 'recordId', responseData[0].id);
        url = OPF.Cfg.addParameterToURL(url, 'processId', this.workflowData.process.id);
        if (this.workflowData.entity) {
            url = OPF.Cfg.addParameterToURL(url, 'entityLookup', this.workflowData.entity.lookup);
        } else {
            url = OPF.Cfg.addParameterToURL(url, 'entityLookup', this.modelInstance.self.lookup);
        }

        Ext.Ajax.request({
            url: url,
            method: 'POST',

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (resp.success) {
                    me.onCancelClick();
                }
            },

            failure:function(response) {
                me.validator.showValidationMessages(response);
            }
        });
    },

    showPerformActivityActionDialog: function(activityAction, activity) {
        var dialog = Ext.WindowMgr.get(OPF.prometheus.component.manager.dialog.PerformActivityActionDialog.id);
        if (!dialog) {
            dialog = Ext.ComponentMgr.create({
                xtype: 'prometheus.manager.perform-activity-action-dialog'
            });
            Ext.WindowMgr.register(dialog);
        }
        dialog.setTitle('PERFORM ACTION: ' + activityAction.name);
        dialog.setActivityActionData({
            formComponent: this,
            activityAction: activityAction,
            activity: activity
        });
        dialog.show();
    }

});
