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

Ext.require([
    'OPF.console.directory.model.UserModel'
]);

Ext.define('OPF.prometheus.component.manager.dialog.PerformActivityActionDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.prometheus.manager.perform-activity-action-dialog',
    ui: 'wizards',

    statics: {
        id: 'performActivityActionDialog'
    },

    id: 'performActivityActionDialog',
    title: 'PERFORM ACTION',

    width: 600,
    modal: true,
    draggable:false,
    shadow:false,
    resizable: false,


    initComponent: function() {
        var me = this;

        this.assigneeStore = Ext.create('Ext.data.Store', {
            autoLoad: true,
            model: 'OPF.console.directory.model.UserModel',
            proxy: {
                type: 'ajax',
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                beforeload: function (store, operation) {
                    store.proxy.url = OPF.Cfg.restUrl('/process/activity/assignee-candidates/' + me.activity.id, false);
                }
            }
        });

        this.assigneeCombo = Ext.ComponentManager.create({
            xtype: 'opf-combo',
            name: 'assigneeId',
            labelAlign: 'top',
            fieldLabel: 'Assignee',
            flex: 1,
            editable: false,
            store: this.assigneeStore,
            queryMode: 'local',
            displayField: 'username',
            valueField: 'id'
        });

        this.databaseDescriptionField = Ext.ComponentManager.create({
            xtype: 'opf-textarea',
            name: 'noteText',
            labelAlign: 'top',
            fieldLabel: 'Comment',
            anchor: '100%'
        });

        this.form = Ext.ComponentManager.create({
            xtype: 'form',
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            autoScroll: true,
            items: [
                {
                    xtype: 'container',
                    anchor: '100%',
                    layout: 'hbox',
                    items: [
                        this.assigneeCombo,
                        {
                            xtype: 'splitter'
                        },
                        {
                            xtype: 'button',
                            text: 'Claim',
                            ui: 'blue',
                            margin: '21 0 0 0',
                            width: 100,
                            height: 28,
                            handler: this.onClickClaim,
                            scope: this
                        }
                    ]
                },
                this.databaseDescriptionField
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
                            width: 200,
                            height: 50,
                            text: 'Perform',
                            formBind: true,
                            handler: this.perform,
                            scope: this
                        },
                        {
                            xtype: 'button',
                            ui: 'grey',
                            width: 200,
                            height: 50,
                            text: 'Cancel',
                            handler: this.cancel,
                            scope: this
                        }
                    ]
                }
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    setActivityActionData: function(data) {
        this.activityAction = data.activityAction;
        this.activity = data.activity;
        this.formComponent = data.formComponent;
    },

    onClickClaim: function () {
        this.assigneeCombo.setValue(OPF.Cfg.USER_INFO.id);
    },

    perform: function() {
        var me = this;
        this.form.getEl().mask();
        var data = this.formComponent.prepareDataForSaveOperation();
        this.formComponent.saveFormData(data.url, data.method, data.formData,
            function(message, responseData) {
                var recordId = responseData[0].id;
                me.executeActivityAction(recordId);
            },
            function(message, sourceResponse) {
                me.formComponent.validator.showValidationMessages(sourceResponse);
                me.close();
            }
        );
    },

    executeActivityAction: function(recordId) {
        var me = this;

        var assigneeId = this.assigneeCombo.getValue();
        var description = this.databaseDescriptionField.getValue();
        var jsonData = {
            assignee: {
                id: assigneeId
            },
            description: description
        };

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/process/workflow/perform-activity-action/' + this.activityAction.id + '/' + recordId, false),
            method: 'PUT',
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, responseData.message);

                document.location.href = OPF.Cfg.fullUrl('/inbox', true);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
                me.close();
            }
        });
    },

    cancel: function() {
        this.close();
    }

});