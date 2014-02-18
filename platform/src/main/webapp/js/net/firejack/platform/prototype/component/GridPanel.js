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

Ext.define('OPF.prototype.component.GridPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf-prototype-grid-panel',

    border: false,

    model: null,
    managerPanel: null,

    configs: null,

    isAdvancedSearchShown: false,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        var model = Ext.create(this.model);

        this.store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            pageSize: 10,
            model: this.model,
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: model.self.restSuffixUrl,
                reader: {
                    root: 'data',
                    totalProperty: 'total',
                    messageProperty: 'message',
                    idProperty: model.idProperty
                },
                writer: {
                    root: 'data'
                },
                simpleSortMode: true,
                startParam: 'offset',
                limitParam: 'limit',
                sortParam: 'sortColumn',
                directionParam: 'sortDirection'
            },
            listeners: {
                beforeload: function(store, operation) {
                    me.onStoreBeforeLoad(store, operation);
                }
            }
        });

        var columns = [];
        Ext.each(model.fields.items, function(field) {
            if (field.fieldType) {
                var headerName = field.displayName || field.name;
                var column = {
                    id: field.name,
                    text: headerName,
                    dataIndex: field.name,
//                    minWidth: OPF.calculateColumnWidth(headerName),
//                    flex: OPF.calculateColumnWidth(headerName),

                    minWidth: me.calculateColumnWidth(field.fieldType),
                    flex: me.calculateColumnWidth(field.fieldType),

                    sortable: true,
                    fieldType: field.fieldType
                };
                switch(field.fieldType) {
                    case 'IMAGE_FILE':
                        column.renderer = function(value) {
                            var result = '';
                            if (OPF.isNotBlank(value)) {
                                result = '<img src="' + OPF.Cfg.fullUrl('images/icons/16/image_16.png') + '"/>';
                            }
                            return result;
                        };
                        break;
                    case 'CODE':
                    case 'LABEL':
                    case 'NAME':
                    case 'DESCRIPTION':
                    case 'PASSWORD':
                    case 'SECRET_KEY':
                    case 'TINY_TEXT':
                    case 'SHORT_TEXT':
                    case 'MEDIUM_TEXT':
                    case 'LONG_TEXT':
                    case 'UNLIMITED_TEXT':
                    case 'PHONE_NUMBER':
                    case 'SSN':
                    case 'RICH_TEXT':
                        column.renderer = 'htmlEncode';
                        break;
                    case 'CURRENCY':
                        column.renderer = function(value) {
                            var cfg = {
                                allowDecimals: true,
                                alwaysDisplayDecimals: true,
                                currencySymbol: '$',
                                useThousandSeparator: true,
                                thousandSeparator: ',',
                                decimalPrecision: 2,
                                decimalSeparator: '.'
                            };
                            return OPF.core.component.NumberField.formattedValue(value, cfg);
                        };
                        break;
                }
                columns.push(column);
            }
        });

        Ext.each(model.associations.items, function(association) {
            var component;
            if (association.type == 'belongsTo') {
                var associationModel = Ext.create(association.model);
                var headerName = association.displayName || OPF.getSimpleClassName(association.model);
                columns.push({
                    id: association.name + '_' + associationModel.displayProperty,
                    text: headerName,
                    dataIndex: association.name + '.' + associationModel.displayProperty,
//                    minWidth: OPF.calculateColumnWidth(headerName),
//                    flex: OPF.calculateColumnWidth(headerName),

                    minWidth: me.calculateColumnWidth('NAME'),
                    flex: me.calculateColumnWidth('NAME'),

                    sortable: false,
                    renderer: function(value, meta, record) {
                        var associationInstance = record[association.name + 'BelongsToInstance'];
                        if (associationInstance) {
                            var associationData = associationInstance.data;
                            value = associationData[associationModel.displayProperty];
                        }
                        return value;
                    }
                });
            }
        });

        this.searchField = Ext.ComponentMgr.create({
            xtype: 'textfield',
            cls: 'search-field',
            width: 250,
            listeners: {
                specialkey:function (field, e) {
                    if (e.getKey() == e.ENTER) {
                        me.onSearchClick();
                    }
                }
            }
        });

        this.advancedSearchButton = Ext.create('Ext.button.Button', {
            text: 'Search',
            cls: 'search-button',
            iconCls: 'icon-search',
            height: 28,
            scale: 'small',
            handler: me.onSearchClick,
            scope: this
        });

        this.showAdvancedSearchButton = Ext.create('Ext.button.Button', {
            text: 'Show Advanced Search',
            cls: 'search-button',
            iconCls: 'icon-search',
            height: 28,
            scale: 'small',
            handler: me.onShowAdvancedSearchClick,
            scope: this
        });

        this.grid = Ext.create('Ext.grid.Panel', Ext.apply({
            height: 510,
            cls: 'grid-panel',
            store: this.store,
            columns: columns,

            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    padding: 3,
                    items: [
                        {
                            xtype: 'tbspacer',
                            width: 5
                        },
                        this.searchField,
                        {
                            xtype: 'tbspacer',
                            width: 10
                        },
                        this.advancedSearchButton,
                        {
                            xtype: 'tbfill'
                        },
                        this.showAdvancedSearchButton
                    ]
                },
                {
                    xtype: 'pagingtoolbar',
                    store: this.store,
                    dock: 'bottom',
                    displayInfo: true
                }
            ],
            listeners: {
                itemclick: function(grid, record) {
                    var deleteButton = me.down('button[action=delete]');
                    deleteButton.enable();
                },
                itemdblclick: function(grid, record) {
                    me.showEditor(record);
                },

                beforerender: function (cmp, eOpts) {
                    cmp.columns[0].setHeight(30);
                },

                render: function(gridView) {
                    console.log('Grid panel was just rendered');
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
                                                    url, resourceVersion.width, resourceVersion.height, '', 200, 200);
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

        this.advancedSearchPanel = Ext.create('OPF.prototype.component.AdvancedSearchPanel', {
            model: this.model,
            managerPanel: this.managerPanel,
            store: this.store,
            hidden: true
        });

        this.items = [
            this.advancedSearchPanel,
            this.grid
        ];

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
                        iconCls: 'icon-add',
                        scale: 'small',
                        scope: this,
                        handler: this.onAddClick
                    }, this.configs.addButtonConfigs),
                    Ext.apply({
                        text: 'Delete',
                        action: 'delete',
                        iconCls: 'icon-delete',
                        scale: 'small',
                        scope: this,
                        disabled: true,
                        handler: this.onDeleteClick
                    }, this.configs.deleteButtonConfigs)
                ]
            }
        ];

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
            Ext.Msg.alert('Error', 'Not selected item.');
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
                                OPF.Msg.setAlert(true, vo.message);
                                me.store.remove(record);
                                var deleteButton = me.down('button[action=delete]');
                                deleteButton.disable();
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

    onSearchClick: function() {
        this.store.searchMode = 'SIMPLE_SEARCH';
        this.store.load();
    },

    onShowAdvancedSearchClick: function() {
        if (this.isAdvancedSearchShown) {
            this.searchField.show();
            this.advancedSearchButton.show();
            this.showAdvancedSearchButton.setText('Show Advanced Search');
            this.advancedSearchPanel.hide();
        } else {
            this.searchField.hide();
            this.advancedSearchButton.hide();
            this.showAdvancedSearchButton.setText('Hide Advanced Search');
            this.advancedSearchPanel.show();
        }
        this.isAdvancedSearchShown = !this.isAdvancedSearchShown;
    },

    onStoreBeforeLoad: function(store, operation) {
        if (OPF.isEmpty(this.parentId)) {
            this.parentId = OPF.getQueryParam("parentId");
        }
        var url = store.model.restSuffixUrl;
        if (OPF.isEmpty(this.parentId)) {
            if (store.searchMode == 'SIMPLE_SEARCH') {
                var searchPhrase = this.searchField.getValue();
                if (OPF.isNotBlank(searchPhrase)) {
                    url += '/search/' + escape(searchPhrase);
                }
            } else if (store.searchMode == 'ADVANCED_SEARCH') {
                var queryParameters = this.advancedSearchPanel.getQueryParameters();
                url += '/advanced-search?queryParameters=' + Ext.encode(queryParameters);
            }
        } else {
            this.initializeParentModel();
            this.searchField.setValue('');
            this.searchField.disable();
            this.advancedSearchButton.disable();
            this.advancedSearchPanel.hide()
            var queryParameters = [];
            queryParameters.push({
                field: 'parent.id',
                operation: 'EQUALS',
                value: this.parentId
            });
            var paramValue = [];
            paramValue.push(queryParameters);
            url += '/advanced-search?queryParameters=' + Ext.encode(paramValue);
        }
        store.proxy.url = url;
    },

    showEditor: function(record) {
        if (OPF.isNotEmpty(this.managerPanel) && OPF.isNotEmpty(this.managerPanel.formPanel)) {
            this.hide();
            this.managerPanel.formPanel.parentModel = OPF.isEmpty(this.parentModel) ? null : this.parentModel;
            this.managerPanel.formPanel.showFormPanel(record || null);
        }
    },

    initializeParentModel: function() {
        var me = this;
        var model = Ext.create(this.model);
        Ext.each(model.associations.items, function(association) {
            if (association.name == 'parent' && association.type == 'belongsTo') {
                var associationModel = Ext.create(association.model);
                Ext.Ajax.request({
                    url: associationModel.self.restSuffixUrl + '/' + me.parentId,
                    method: 'GET',
                    success: function(response, action) {
                        var resp = Ext.decode(response.responseText);
                        me.parentModel = Ext.create(association.model, resp.data[0]);
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
            }
        });
    },

    calculateColumnWidth: function(fieldType) {
        var width;
        switch(fieldType) {
            case 'NUMERIC_ID':
            case 'UNIQUE_ID':
                width = 80;
                break;
            case 'INTEGER_NUMBER':
            case 'LARGE_NUMBER':
                width = 80;
                break;
            case 'DECIMAL_NUMBER':
                width = 100;
                break;
            case 'DATE':
                width = 100;
                break;
            case 'TIME':
                width = 100;
                break;
            case 'EVENT_TIME':
            case 'CREATION_TIME':
            case 'UPDATE_TIME':
                width = 120;
                break;
            case 'PASSWORD':
                width = 100;
                break;
            case 'NAME':
                width = 200;
                break;
            case 'DESCRIPTION':
            case 'MEDIUM_TEXT':
            case 'LONG_TEXT':
            case 'UNLIMITED_TEXT':
            case 'RICH_TEXT':
                width = 300;
                break;
            case 'CURRENCY':
                width = 100;
                break;
            case 'PHONE_NUMBER':
            case 'SSN':
                width = 100;
                break;
            case 'FLAG':
                width = 50;
                break;
            case 'IMAGE_FILE':
                width = 50;
                break;
            default:
                width = 100;
                break;
        }
        return width;
    }

});