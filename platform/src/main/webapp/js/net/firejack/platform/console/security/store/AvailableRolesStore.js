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

Ext.define('OPF.console.security.AvailableRolesStore', {
    extend: 'Ext.data.Store',
    model: OPF.console.security.AssignedRoleModel,
    securityWin: null,

    proxy: {
        type: 'ajax',//'rest'
        method: 'GET',
        url: OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE.generateUrl('/'),
        reader: {
            type: 'json',
            successProperty: 'success',
            idProperty: 'id',
            root: 'data',
            messageProperty: 'message',
            totalProperty: 'total'
        },
        writer: new Ext.data.JsonWriter({ // The new DataWriter component.
            encode: false                 // <-- don't return encoded JSON -- causes Ext.Ajax#request to send data using jsonData config rather than HTTP params
        }),                               // <-- plug a DataWriter into the store just as you would a Reader
        listeners: {
            exception: function(proxy, response, operation, eOpts) {
                OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
            }
        }
    },
    listeners: {
        beforeload: function(store, operation, eOpts) {
            store.proxy.url = OPF.console.security.AssignedRoleModel.getAssignmentRoleUrl(this.securityWin, false);
            store.proxy.method = 'GET';
        },
        update: function(store, model, operation, eOpts) {
            if (operation == Ext.data.Model.EDIT) {
                store.proxy.url = OPF.console.security.AssignedRoleModel.getAssignmentRoleUrl(this.securityWin, true);
                store.proxy.method = 'PUT';
                var assigned = model.get('assigned');
                this.securityWin.saveRoleAssignment(model, assigned);
                //store.load();
            }
        }
    },

    constructor: function(securityWin, cfg) {
        cfg = cfg || {};
        OPF.console.security.AvailableRolesStore.superclass.constructor.call(this, Ext.apply({
            securityWin: securityWin
        }, cfg));
    }
});