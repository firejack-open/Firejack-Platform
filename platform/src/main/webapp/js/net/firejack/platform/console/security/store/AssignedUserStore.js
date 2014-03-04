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

Ext.define('OPF.console.security.AssignedUserStore', {
    extend: 'Ext.data.Store',

    // store configs
    autoSave: false,
    model: OPF.console.directory.model.UserModel,
    autoDestroy: true,
    securityWin: null,
    proxy : {
        type: 'ajax',
        url: OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE.generateUrl('/users/'),
        method: 'GET',
        reader: {
            type: 'json',
            totalProperty: 'total',
            successProperty: 'success',
            idProperty: 'id',
            root: 'data',
            messageProperty: 'message' // <-- New "messageProperty" meta-data
        },
        listeners: {
            exception: function(proxy, type, action, options, response) {
                OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
            }
        }
    },

    constructor: function(securityWin, cfg) {
        cfg = cfg || {};
        OPF.console.security.AssignedUserStore.superclass.constructor.call(this, Ext.apply({
            securityWin: securityWin
        }, cfg));
    },

    listeners: {
        beforeload: function(store, options) {
            var selectedNodeId = this.securityWin.getSelectedNodeId();
            var selectedNodeEntityLookup = this.securityWin.getSelectNodeEntityType();
            store.proxy.url = OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE.generateUrl('/users/') +
                selectedNodeId + '/' + selectedNodeEntityLookup;
            store.proxy.method = 'GET';
        }
    }
});