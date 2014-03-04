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

Ext.define('OPF.console.inbox.store.Assignees', {
    extend: 'Ext.data.Store',
    restful: false,
    constructor: function(cfg) {
        cfg = cfg || {};
        OPF.console.inbox.store.Assignees.superclass.constructor.call(this, Ext.apply({
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

    reloadStore: function(dlg) {
        var store = this;
        var url = OPF.core.utils.RegistryNodeType.TASK.generateUrl('/current-assignee-candidates/' + dlg.taskId);
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
                if (OPF.isNotEmpty(users) && OPF.isNotEmpty(users.length)) {
                    var userModels = [];
                    for (var i = 0; i < users.length; i++) {
                        var userModel = Ext.create('OPF.console.directory.model.UserModel', users[0]);
                        userModels.push(userModel);
                    }
                    //dlg.userField.store.loadData(userModels);
                    store.loadData(userModels);
                }
            }
        });
    }

});