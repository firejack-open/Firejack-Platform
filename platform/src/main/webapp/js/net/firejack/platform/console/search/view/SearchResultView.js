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