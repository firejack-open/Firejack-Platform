/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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