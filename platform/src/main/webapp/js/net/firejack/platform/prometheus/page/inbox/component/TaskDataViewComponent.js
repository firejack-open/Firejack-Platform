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

Ext.require([
    'OPF.prometheus.component.manager.AdvancedSearchComponent'
]);

Ext.define('OPF.prometheus.page.inbox.component.TaskDataViewComponent', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.prometheus.component.task-dataview-component',

    border: false,

    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};

        this.callParent(arguments);
    },

    initComponent: function() {
        var me = this;

        me.addEvents(
            'storebeforeload',
            'storebeforesync',
            'storeupdate'
        );

        this.store = Ext.StoreManager.get('TaskStore');

        this.viewTemplate = new Ext.XTemplate(
            '<tpl for=".">',
                '<div class="task">' +
                    '<div class="left">' +
                        '<h3>{processCase.process.name}: {activity.name}</h3>',
                        '<div class="description">{description}</div>',
                        '<tpl if="this.isAssignee(assignee)">',
                            '<div class="assignee"><b>Assignee:</b> {assignee.lastName}, {assignee.firstName}</div>',
                        '<tpl elseif="this.isActor(activity)">',
                            '<div class="assignee"><b>Actor:</b> {activity.actor.name}</div>',
                        '</tpl>',
                        '<div class="update-date"><b>Update Date:</b> {updateDate}</div>',
                    '</div>',
                    '<div class="status"><b>Status:</b> {processCase.status.name}</div>',
                    '<div id="task_{id}_activity_{activity.id}" class="buttons"></div>',
                '</div>',
            '</tpl>',
            {
                isAssignee: function(assignee){
                   return assignee != null;
                },
                isActor: function(activity) {
                    return activity != null && activity.actor != null;
                }
            }
        );

        this.dataView = Ext.create('Ext.view.View', {
            store: this.store,
            tpl: this.viewTemplate,
            flex: 1,
            ctCls: 'task-data-view',
            overClass:'task-mouseover',
            itemSelector: 'div.task',
            emptyText: '<h2 class="search-empty">No search tasks to display</h2>'//,
        });

        this.advancedSearch = Ext.create('OPF.prometheus.component.manager.AdvancedSearchComponent', {
            searchPanel: this,
            model: 'OPF.console.inbox.model.TaskModel'
        });

        this.items = [
            {
                xtype: 'panel',
                cls: 'search-manager-component',
                border: false,
                items: [
                    this.advancedSearch
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [
                            '->',
                            {
                                xtype: 'button',
                                text: 'Search',
                                ui: 'search-button',
                                height: 28,
                                scope: this,
                                handler: this.onClickAdvancedSearchButton
                            },
                            {
                                xtype: 'button',
                                text: 'Reset',
                                ui: 'search-button',
                                height: 28,
                                scope: this,
                                handler: this.onClickResetButton
                            }
                        ]
                    }
                ]
            },
            {
                xtype: 'panel',
                title: 'Tasks to Perform',
//                height: 600,
//                autoScroll: true,
                autoHeight: true,
                items: [
                    this.dataView
                ],
                dockedItems: [
                    {
                        xtype: 'pagingtoolbar',
                        store: this.store,
                        dock: 'bottom',
                        displayInfo: true
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    executeSearch: function(searchMode) {
        this.store.load();
    },

    onClickAdvancedSearchButton: function() {
        this.advancedSearch.onClickSearchButton();
    },

    onClickResetButton: function() {
        this.advancedSearch.resetSearch();
    }

});