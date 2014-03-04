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

Ext.ns('OPF.console.documentation.manager');

Ext.define('OPF.console.documentation.view.DocumentationView', {

    init: function() {
        this.initCultureSelector();

        this.initCloudNavigation();

        if (OPF.isNotEmpty(OPF.console.documentation.manager.DocumentationManager)) {
            var documentationManager = new OPF.console.documentation.manager.DocumentationManager();

            documentationManager.initEditors();
            documentationManager.initAddButton();
            documentationManager.initDocumentationElements();
        }
    },

    initCloudNavigation: function() {
        Ext.ComponentMgr.create(
            {
                xtype: 'container',
                renderTo: 'left-doc-panel',
                layout: 'fit',
                width: 200,
                items: [
                    {
                        xtype: 'documentation-tree'
                    }
                ]
            }
        )
    },

    initCultureSelector: function() {
        var cultureSelectorCombo = Ext.ComponentMgr.create({
            xtype: 'combo',
            name: 'cultureSelector',
            renderTo: 'cultureSelectorContainer',
            width: 150,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            store: new Ext.data.ArrayStore({
                fields: ['cultureId', 'cultureUrl', 'cultureName'],
                data: CULTURE_DATA
            }),
            valueField: 'cultureId',
            displayField: 'cultureName',
            listConfig: {
                getInnerTpl: function() {
                    return '<div class="x-combo-list-item">' +
                        '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/flag_{cultureId}_16.png" /> ' + '{cultureName}' +
                    '</div>';
                }
            },
            value: CULTURE_CURRENT,
            listeners: {
                select: function(combo, records, eOpts) {
                    document.location = records[0].data.cultureUrl;
                }
            }
        });
        cultureSelectorCombo.show();
    }

});