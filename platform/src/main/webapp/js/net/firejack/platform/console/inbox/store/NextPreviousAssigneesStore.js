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

Ext.define('OPF.console.inbox.store.NextPreviousAssignees', {
    extend: 'Ext.data.Store',
    restful: false,
    performRollbackDialog: null,

    constructor: function(performRollbackDialog, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.store.NextPreviousAssignees.superclass.constructor.call(this, Ext.apply({
            performRollbackDialog: performRollbackDialog,
            model: 'OPF.console.directory.model.UserModel',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    idProperty: 'id',
                    root: 'data'
                },
                writer: {
                    type: 'json'
                }
            }
        }, cfg));
    },

    reloadStore: function(processId) {
        var dlg = this.performRollbackDialog;

        if (OPF.isEmpty(dlg.id) || (OPF.isEmpty(dlg.showUserField) && !dlg.showUserField)) {
            return false;
        }

        var caseTaskId = dlg.objectIdField.getValue();
        var type = dlg.isCaseDialog ? OPF.core.utils.RegistryNodeType.CASE : OPF.core.utils.RegistryNodeType.TASK;
        var url = type.generateUrl(dlg.isPerformDialog ?
            '/read-next-team-member/' : '/read-previous-team-member/') + caseTaskId;

        Ext.Ajax.request({// /read-next-team-member/
            url: url, method: 'GET',

            success: function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (OPF.isNotEmpty(resp)) {
                    if (!resp.success) {
                        Ext.Msg.alert('Error', resp.message);
                        return;
                    }
                }
                var users = resp.data;
                if (OPF.isNotEmpty(users) && OPF.isNotEmpty(users.length) &&
                    users.length > 0 && OPF.isNotEmpty(users[0])) {
                    var userModel = Ext.create('OPF.console.directory.model.UserModel', users[0]);
                    dlg.userField.store.loadData([userModel]);
                    dlg.userField.setValue(userModel);
                    dlg.userField.show();
                } else {
                    // if no next team member exist then load all assignee candidates
                    url = type.generateUrl(dlg.isPerformDialog ?
                        '/next-assignee-candidates/' : '/previous-assignee-candidates/') + caseTaskId;
                    Ext.Ajax.request({
                        url: url, method: 'GET',
                        success: function(response, action) {
                            var resp = Ext.decode(response.responseText);
                            if (OPF.isNotEmpty(resp)) {
                                if (!resp.success) {
                                    Ext.Msg.alert('Error', resp.message);
                                    dlg.userField.hide();
                                    return;
                                }
                            }
                            var users = resp.data;
                            if (OPF.isNotEmpty(users) && OPF.isNotEmpty(users.length) && users.length > 0) {
                                dlg.userField.allowBlank = false;
                                var models = [];
                                var currentUser = null;
                                for (var i = 0; i < users.length; i++) {
                                    if (OPF.isNotEmpty(users[i])) {
                                        var userModel = Ext.create('OPF.console.directory.model.UserModel', users[i]);
                                        models.push(userModel);
                                        if (userModel.get('id') == OPF.Cfg.USER_INFO.id) {
                                            currentUser = userModel;
                                        }
                                    }
                                }
                                dlg.userField.store.loadData(models);
                                if (currentUser != null) {
                                    dlg.userField.setValue(currentUser);
                                    dlg.userField.show();
                                } else {
                                    dlg.userField.allowBlank = true;
                                    dlg.userField.reset();
                                    dlg.userField.hide();
                                }
                            } else {
                                dlg.userField.allowBlank = true;
                                dlg.userField.hide();
                            }
                        }
                    });
                }
            }
        });
    }
});