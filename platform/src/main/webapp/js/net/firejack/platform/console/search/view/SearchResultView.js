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
    'OPF.console.PageLayout',
    'OPF.console.NavigationPageLayout'
]);

Ext.define('OPF.console.search.view.SearchResultView', {
    extend: 'OPF.console.NavigationPageLayout',
    alias : 'widget.search-results',
    registryNodeType: OPF.core.utils.RegistryNodeType.REGISTRY,

    initComponent: function() {
        this.activeButtonLookup = 'net.firejack.platform.console.search';

        this.assetTypeFilterCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'assetTypeFilter',
            width: 200,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            store: new Ext.data.ArrayStore({
                fields: ['assetType', 'assetTypeName', 'assetTypeIcon'],
                data: DOC_ASSET_TYPE_DATA
            }),
            valueField: 'assetType',
            displayField: 'assetTypeName',
            listConfig: {
                getInnerTpl: function() {
                    return '<div class="x-combo-list-item">' +
                        '<img src="' + OPF.Cfg.fullUrl('/images/default/s.gif') + '" class="tricon-empty tricon-empty-16 tricon-{assetTypeIcon}"/> ' + '{assetTypeName}' +
                    '</div>';
                }
            },
            value: 'All'
        });

        this.searchField = Ext.create('Ext.form.field.Text', {
            width: 350,
            enableKeyEvents: true,
            value: SEARCH_PHRASE
        });

        this.searchResultTemplate = new Ext.XTemplate(
            '<tpl for=".">',
                '<div class="result">',
                    '<h3><img src="' + OPF.Cfg.fullUrl('/images/default/s.gif') + '" class="tricon-empty tricon-search-icon {iconCls}"/>{name}</h3>',
                    '<div class="path">{path}</div>',
                    //'<div class="type-result">{type}</div>',
                    '<div class="paragraph">',
                        '<span>{description}</span>',
                        '<tpl if="isShowDocButton">',
                            '<div class="buttons">',
                                '<span class="margin">',
                                    OPF.Cfg.USER_INFO.isLogged ? '<a class="editbutton" href="{editUrl}">Edit</a>' : '',
                                    '<a href="{docUrl}" class="docbutton">Documentation</a>',
                                '</span>',
                            '</div>',
                        '</tpl>',
                    '</div>',
                '</div>',
            '</tpl>',
            '<div class="x-clear"></div>'
        );

        this.searchResultDataView = new Ext.DataView({
            store: 'SearchResult',
            tpl: this.searchResultTemplate,
            autoHeight:true,
            autoScroll: true,
            ctCls: 'search-result-data-view',
            overClass:'result-mouseover',
            itemSelector:'div.result',
            emptyText: '<h2 class="search-empty">No search results to display</h2>',

            prepareData: function(data){
                try {
                    data.description = ifBlank(data.description, 'Description has not been provided.');
                    data.iconCls = 'tricon-search-' + data.type.toLowerCase();
                    data.docUrl = OPF.Cfg.fullUrl('/console/documentation/us/' + data.lookup.replace(/\./g, "/"));

                    var type = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(data.type);
                    data.editUrl = OPF.Cfg.fullUrl('/' + type.getPageUrl() + '#' + type.getType() + '_' + data.id);

                    var isShowDocButton = false;
                    Ext.each(DOC_ASSET_TYPE_DATA, function(docAssetType) {
                        isShowDocButton |= docAssetType[0] == data.type;
                    });
                    data.isShowDocButton = isShowDocButton;
                } catch(e) {
                    Ext.Msg.alert(e);
                }
                return data;
            }
        });

        this.searchResultPaging = new Ext.PagingToolbar({
            store: 'SearchResult',
            pageSize: 10,
            displayInfo: true
        });
        this.tabPanel = Ext.create('Ext.panel.Panel', {
            title: 'Search Result',
            items: [
                this.searchResultDataView
            ],
            tbar: [
                {
                    xtype: 'tbtext',
                    text: 'Asset Type:'
                },
                this.assetTypeFilterCombo,
                {
                    xtype: 'tbspacer',
                    width: 10
                },
                this.searchField,
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'button',
                    text: 'clear',
                    action: 'clear'
                }
            ],
            bbar: this.searchResultPaging
        });

        this.callParent(arguments);
    },

    isToolBarRequired: function() {
        return false;
    }
});