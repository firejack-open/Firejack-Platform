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

Ext.define('OPF.prometheus.layout.AbstractLayout', {
    extend: 'Ext.container.Container',

    listeners: {
        afterrender: function(container) {
            Ext.ComponentMgr.each(function(key, component) {
                if ('prometheus.component.top-menu-component' == component.xtype ||
                    'prometheus.component.left-menu-component' == component.xtype) {
                    component.initMenu();
                }
            });

            var batchResourceLookupData = [];
            var resourceComponents = new Ext.util.MixedCollection();
            Ext.ComponentMgr.each(function(key, component, lenght) {
                if ('text-resource-control' == component.xtype ||
                    'image-resource-control' == component.xtype) {
                    batchResourceLookupData.push(component.getResourceLookup());
                    resourceComponents.add(component.getResourceLookup(), component);
                }
            });

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/by-lookup-list'),
                method: 'POST',
                jsonData: {"data": {
                    lookup: batchResourceLookupData
                }},

                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        Ext.each(jsonData.data, function(resourceData) {
                            var resourceLookup = resourceData.lookup;
                            var resourceComponent = resourceComponents.getByKey(resourceLookup);
                            if (OPF.isNotEmpty(resourceComponent)) {
                                resourceComponent.initResource(resourceData);
                            }
                        });
                    }
                },
                failure: function(response) {
                    var errorJsonData = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(false, errorJsonData.message);
                }
            });
        }
    }

});