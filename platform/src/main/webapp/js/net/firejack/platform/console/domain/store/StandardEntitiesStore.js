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

Ext.define('OPF.console.domain.store.StandardEntities', {
    extend: 'Ext.data.Store',
    restful: false,
    //remoteSort: true,

    constructor: function(cfg) {
        cfg = cfg || {};
        OPF.console.domain.store.StandardEntities.superclass.constructor.call(this, Ext.apply({
            model: 'OPF.console.domain.model.EntityModel',
            proxy: {
                type: 'ajax',
                url : OPF.core.utils.RegistryNodeType.ENTITY.generateUrl('/standard'),
                method: 'GET',
                simpleSortMode: true,
                reader: {
                    type: 'json',
                    idProperty: 'id',
                    root: 'data',

                    totalProperty: 'total',
                    successProperty: 'success',
                    messageProperty: 'message'
                },
                /*writer: {
                    type: 'json'
                }*/
                listeners: {
                    beforeload: {
                        fn: function(store, options) {
                            var instance = OPF.Ui.getCmp('entity-editor');
                            var params = isNotBlank(instance.nodeBasicFields.idField.getValue()) ?
                                '?exceptId=' + instance.nodeBasicFields.idField.getValue() : '';
                            store.proxy.url = OPF.core.utils.RegistryNodeType.ENTITY.generateUrl('/standard') + params;
                        }
                    }
                }
            }
        }, cfg));
    }
});