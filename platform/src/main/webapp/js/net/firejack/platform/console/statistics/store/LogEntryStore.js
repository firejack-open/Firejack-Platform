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

Ext.define('OPF.console.statistics.store.LogEntryStore', {
    extend: 'Ext.data.Store',
    registryNodeType: null,
    remoteSort: true,

    constructor: function(cfg) {
        cfg = cfg || {};
        OPF.console.statistics.store.LogEntryStore.superclass.constructor.call(this, Ext.apply({
            registryNodeType: OPF.core.utils.RegistryNodeType.LOG_ENTRY,
            model: OPF.core.utils.RegistryNodeType.LOG_ENTRY.getModel(),
            proxy: {
                type: 'ajax',
                url : OPF.core.utils.RegistryNodeType.LOG_ENTRY.generateUrl(),
                simpleSortMode: true,
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                }
            }
        }, cfg));
    }
});
