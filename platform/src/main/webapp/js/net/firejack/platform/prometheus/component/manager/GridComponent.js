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
    'OPF.prometheus.component.manager.AbstractGridComponent',
    'OPF.prometheus.component.manager.SearchManagerComponent',
    'OPF.prometheus.component.securitycontroller.SecurityControllerComponent'
]);

Ext.define('OPF.prometheus.component.manager.GridComponent', {
    extend: 'OPF.prometheus.component.manager.AbstractGridComponent',
    alias: 'widget.prometheus.component.grid-component',

    managerPanel: null,
    reportMode: true,

    isAdvancedSearchShown: false,
    enableSecurityControl: true,

    initComponent: function() {
        var me = this;

        this.modelInstance = this.getModel();

        this.store = this.getStore();

        this.grid = Ext.create('Ext.grid.Panel', Ext.apply({
            height: 510,
            cls: 'grid-panel',
            store: this.store,
            columns: this.getColumnsByModel(),

            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    store: this.store,
                    dock: 'bottom',
                    displayInfo: true
                }
            ],
            listeners: {
                itemclick: function(grid, record) {
                    if (!me.reportMode) {
                        var deleteButton = me.down('button[action=delete]');
                        if (deleteButton) deleteButton.enable();
                    }
                },
                itemdblclick: function(grid, record) {
                    if (!me.reportMode) {
                        me.showEditor(record);
                    }
                },

                beforerender: function (cmp, eOpts) {
                    cmp.columns[0].setHeight(30);
                },

                render: function(gridView) {
                    //create the ToolTip
                    gridView.tip = Ext.create('Ext.tip.ToolTip', {
                        // The overall target element.
                        target: gridView.el,
                        // Each grid row causes its own seperate show and hide.
                        delegate: gridView.view.cellSelector,
                        // Moving within the row should not hide the tip.
                        trackMouse: true,
                        // Render immediately so that tip.body can be referenced prior to the first show.
                        renderTo: Ext.getBody(),
                        listeners: {
                            // Change content dynamically depending on which element triggered the show.
                            beforeshow: function updateTipBody(tip) {
                                var gridColums = gridView.view.getGridColumns();
                                var column = gridColums[tip.triggerElement.cellIndex];
                                //only display the tool tip for image column
                                if (column.fieldType == 'IMAGE_FILE') {
                                    tip.update('<b>Image loading...</b>');

                                    var record = gridView.view.getRecord(tip.triggerElement.parentNode);
                                    var lookup = record.get(column.dataIndex);
                                    Ext.Ajax.request({
                                        url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + lookup),
                                        method: 'GET',

                                        success: function(response){
                                            var vo = Ext.decode(response.responseText);
//                                            OPF.Msg.setAlert(vo.success, vo.message);

                                            if (vo.success) {
                                                var resourceVersion = vo.data[0].resourceVersion;
                                                var urlSuffix = 'content/resource/image/by-filename/' + resourceVersion.resourceId +
                                                    '/' + resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
                                                var url = OPF.Cfg.restUrl(urlSuffix);
                                                var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtmlWrapper(
                                                    url, resourceVersion.width, resourceVersion.height, '', 300, 200);
                                                tip.update(imageHtml);
                                            }
                                        },
                                        failure: function(response) {
                                            var data = Ext.decode(response.responseText);
                                            OPF.Msg.setAlert(false, data.message);
                                        }
                                    });
                                } else {
                                    return false;
                                }
                            }
                        }
                    });
                }
            }
        }, this.configs.gridConfigs));

        this.search = Ext.create('OPF.prometheus.component.manager.SearchManagerComponent', Ext.apply({
            model: this.model,
            managerPanel: this.managerPanel,
            store: this.store,
            reportMode: this.reportMode
        }, this.configs.searchManagerConfigs));

        if (this.reportMode) {
            this.items = [
                this.search,
                this.grid
            ];
        } else {
            if (this.configs.enableSecurityControl) {
                this.securityControl = Ext.create('OPF.prometheus.component.securitycontroller.SecurityControllerComponent', {
                    model: this.model,
                    managerPanel: this.managerPanel,
                    currentModelLookup: this.model.lookup
                });

                this.items = [
                    this.search,
                    this.securityControl,
                    this.grid
                ];
            } else {
                this.items = [
                    this.search,
                    this.grid
                ];
            }


            this.dockedItems = [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        Ext.apply({
                            text: 'Add',
                            action: 'add',
                            ui: 'add',
                            width: 70,
                            scope: this,
                            handler: this.onAddClick
                        }, this.configs.addButtonConfigs),
                        Ext.apply({
                            text: 'Delete',
                            action: 'delete',
                            ui: 'delete',
                            width: 70,
                            scope: this,
                            disabled: true,
                            handler: this.onDeleteClick
                        }, this.configs.deleteButtonConfigs)
                    ]
                }
            ];
        }

        this.callParent();
    },

    listeners: {
        show: function(panel) {
            var deleteButton = panel.down('button[action=delete]');
            deleteButton.disable();
        }
    },

    onAddClick: function() {
        this.showEditor();
    },

    onDeleteClick: function() {
        var me = this;
        var records = this.grid.getSelectionModel().getSelection();
        if (records.length == 0) {
            Ext.Msg.alert('Error', 'Record is not selected.');
            return false;
        }
        Ext.MessageBox.confirm(
            'Deleting selected record(s)',
            'Are you sure?',
            function(btn) {
                if (btn[0] == 'y') {
                    Ext.each(records, function(record) {
                        Ext.Ajax.request({
                            url: record.self.restSuffixUrl + '/' + record.get(record.idProperty),
                            method: 'DELETE',

                            success: function(response, action) {
                                var vo = Ext.decode(response.responseText);
                                if (vo.success) {
                                    OPF.Msg.setAlert(true, vo.message);
                                    me.store.remove(record);
                                    var deleteButton = me.down('button[action=delete]');
                                    deleteButton.disable();
                                } else {
                                    Ext.Msg.alert('Error', vo.message);
                                }
                            },

                            failure: function(response) {
                                var responseStatus = Ext.decode(response.responseText);
                                var messages = [];
                                for (var i = 0; i < responseStatus.data.length; i++) {
                                    var msg = responseStatus.data[i].msg;
                                    messages.push(msg);
                                }
                                Ext.Msg.alert('Error', messages.join('<br/>'));
                            }
                        });
                    })
                }
            }
        );
    },

    onStoreBeforeLoad: function(store, operation) {
        if (OPF.isEmpty(this.parentId)) {
            this.parentId = OPF.getQueryParam("parentId");
        }
        var queryParameters;
        var url = store.model.restSuffixUrl;
        if (OPF.isEmpty(this.parentId)) {
            if (store.searchMode == 'SIMPLE_SEARCH') {
                var searchPhrase = this.search.simpleSearch.searchField.getValue();
                if (OPF.isNotBlank(searchPhrase)) {
                    url += '/search/' + encodeURIComponent(searchPhrase);
                }
            } else if (store.searchMode == 'ADVANCED_SEARCH') {
                queryParameters = this.search.advancedSearch.getQueryParameters();
                url += '/advanced-search?queryParameters=' + encodeURIComponent(Ext.encode(queryParameters));
            }
        } else {
            this.search.hide();
            queryParameters = [[{
                field: 'parent.id',
                operation: 'EQUALS',
                value: this.parentId
            }]];
            url += '/advanced-search?queryParameters=' + encodeURIComponent(Ext.encode(queryParameters));
        }
        store.proxy.url = url;
    },

    showEditor: function(record) {
        if (OPF.isNotEmpty(this.managerPanel) && OPF.isNotEmpty(this.managerPanel.formPanel)) {
            this.hide();
            this.managerPanel.formPanel.showFormPanel(record || null);
        }
    }

});