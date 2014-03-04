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


/**
 * Provides the toolbar for selecting language versions of the resource and
 * allowing each to be edited and maintained separately.
 */
Ext.define('OPF.console.resource.view.resource.ResourceLanguageBar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.resource-language-bar',

    dock: 'bottom',
    height: 28,

    editPanel: null,
    selectedCulture: null,
    cultureButtons: [],

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.resource.view.resource.ResourceLanguageBar.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.addCountryButtons();

        this.callParent(arguments);
    },

    selectCulture: function(culture) {
        if (this.cultureButtons.length > 0) {
            this.selectedCulture = null;
            Ext.each(this.cultureButtons, function(cultureButton, index) {
                if (cultureButton.culture == culture) {
                    if (!cultureButton.pressed) cultureButton.toggle();
                }
            });
        } else {
            this.selectedCulture = culture;
        }
    },

    addCountryButtons: function() {
        var instance = this;

        this.cultureButtons = [];

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/culture'),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var culturesData = Ext.decode(response.responseText).data;
                Ext.each(culturesData, function(culture, index) {
                    var cultureButton = Ext.ComponentMgr.create({
                        xtype: 'button',
                        text: culture.country,
                        culture: culture.culture,
                        iconCls: 'cult-' + culture.culture.toLowerCase(),
                        enableToggle: true,
                        toggleGroup: 'culture',
                        handler: function () {
                            instance.editPanel.reloadResourceVersion(culture.culture);
                        }
                    });
                    instance.cultureButtons.push(cultureButton);
                    var spacerComponent = Ext.ComponentMgr.create({
                        xtype: 'tbspacer',
                        width: 5
                    });
                    instance.insert(index * 2, cultureButton);
                    instance.insert(index * 2 + 1, spacerComponent);
                });
                instance.doLayout();
                if (isNotEmpty(instance.selectedCulture)) {
                    instance.selectCulture(instance.selectedCulture);
                }
            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                App.setAlert(false, errorJsonData.message);
            }
        });
    }

});