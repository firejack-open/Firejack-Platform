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
 *
 */
Ext.define('OPF.console.inbox.ProcessManagerView', {
    extend: 'OPF.console.PageLayout',

    alias: 'widget.process-manager-view',

    activeButtonLookup: 'net.firejack.platform.top.inbox',

    initComponent: function() {
        this.westPanel = Ext.create('OPF.console.inbox.view.task.TaskFilterPanel', this, {
            region: 'west',
            width: 220,
            border: false,
            split: true,
            collapsible: true
        });

        this.taskGridPanel = Ext.create('OPF.console.inbox.view.task.TaskGridPanel', this, true);
        this.caseGridPanel = Ext.create('OPF.console.inbox.view.task.TaskGridPanel', this, false);

        this.taskCasePanel = Ext.create('Ext.panel.Panel', {
            region: 'center',
            layout: 'card',
            border: false
        });

        this.taskDetailsPanel = Ext.create('OPF.console.inbox.view.task.TaskDetailsPanel', this, {
            region: 'south',
            split: true,
            collapsible: true,
            height: 250,
            border: false
        });

        var infoLookupPrefix = 'net.firejack.platform.process.case';
        this.navPanel = Ext.create('Ext.panel.Panel', {
            padding: 10,
            border: false,
            layout: {
                type: 'table',
                columns: 1
            },
            items: [
                {
                    xtype: 'resource-control',
                    textResourceLookup: infoLookupPrefix + '.info-text',
                    textMaxLength: 250,
                    imgResourceLookup: infoLookupPrefix + '.info-image',
                    imageWidth: 80,
                    imageHeight: 80
                },
                OPF.Ui.ySpacer(15),
                {
                    xtype: 'container',
                    height: 40,
                    cls: 'more-information',
                    html: '<h4><img src="' + OPF.Cfg.fullUrl('images/info.png') + '" border="0"/>More information</h4>'
                },
                OPF.Ui.ySpacer(15),
                this.populateNavPanel()
            ]
        });

        this.centerPanel = Ext.create('Ext.panel.Panel', {
            region: 'center',
            border: false,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'panel',
                    flex: 1,
                    layout: 'border',
                    items: [
                        this.taskCasePanel,
                        this.taskDetailsPanel
                    ]
                },
                {
                    xtype: 'container',
                    width: 220,
                    layout: 'fit',
                    buttonAlign: 'center',
                    border: false,
                    items: {
                        xtype: 'container',
                        autoScroll: true,
                        items: this.navPanel
                    }
                }
            ]
        });
        this.taskCasePanel.add(this.taskGridPanel);
        this.taskCasePanel.add(this.caseGridPanel);

        this.callParent(arguments);
    },

    populateNavPanel: function() {
        var blocks = this.taskDetailsPanel.getDetailPanels();
        var rightNavContent = '';
        Ext.each(blocks, function(block, index) {
            var isLast = index == (blocks.length - 1);
            var anchorMenuItemLabel = block.fieldLabel;
            if (OPF.isNotBlank(anchorMenuItemLabel) && !block.hidden) {
                rightNavContent +=
                    '<a href="javascript:Ext.ComponentManager.get(\'' + block.getId() + '\').makeActive();">' +
                        '<li class="' + (!isLast ? 'top' : '') + '"><img src="' + OPF.Cfg.fullUrl('images/right_nav_arrow.png') + '" border="0"/>' + anchorMenuItemLabel + '</li>' +
                        '</a>';
            }
        });
        var html = '<ul class="navlist">' + rightNavContent + '</ul>';
        return Ext.create('Ext.container.Container', {
            cls: 'right-nav',
            //width: 140,
            html: html
        })
    }

});