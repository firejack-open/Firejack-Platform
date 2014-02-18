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

Ext.define('OPF.console.search.controller.SearchController', {
    extend: 'Ext.app.Controller',

    views: ['SearchResultView'],

    stores: ['SearchResult'],

    models: ['SearchModel'],

    init: function() {
        var searchResultStore = this.getSearchResultStore();
        searchResultStore.addListener('beforeload', this.onBeforeStoreLoad, this);

        var instance = this;

        this.control(
            {
                'search-results': {
                    activate: this.onActivate,
                    afterrender: this.refreshResults
                },
                'search-results cloud-navigation': {
                    itemclick: this.refreshResults
                },
                'search-results combo': {
                    select: this.refreshResults
                },
                'search-results textfield': {
                    keyup: {
                        fn: function(cmp, e) {
                            instance.refreshResults();
                        },
                        buffer: 500
                    }
                },
                'search-results button[action=clear]': {
                    click: function(button) {
                        var searchResultsView = this.getCmp('search-results');
                        searchResultsView.searchField.setValue('');
                        instance.refreshResults();
                    }
                }
            }
        )
    },
    onActivate: function() {
        var searchResultStore = this.getSearchResultStore();
        searchResultStore.reload();
    },
    onBeforeStoreLoad: function(store, options) {
        var url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/search');

        var searchResultsView = this.getCmp('search-results');
        var searchPhrase = searchResultsView.searchField.getValue();
        var isFirstParam = true;
        if (isNotBlank(searchPhrase)) {
            url = this.param(url, 'term', escape(searchPhrase), isFirstParam);
            isFirstParam = false;
        }

        var selectedNode = searchResultsView.navigationPanel.getSelectedNode();
        if (isNotEmpty(selectedNode) && isNotEmpty(selectedNode.data) &&
            isNotEmpty(selectedNode.data.lookup)) {
            url = this.param(url, 'lookup', selectedNode.data.lookup, isFirstParam);
            isFirstParam = false;
        }

        var assetType = searchResultsView.assetTypeFilterCombo.getValue();
        if (isNotBlank(assetType) && assetType != 'ALL') {
            url = this.param(url, 'assetType', assetType, isFirstParam);
            isFirstParam = false;
        }

        url = this.param(url, 'start', isEmpty(options.start) ? 0 : options.start, isFirstParam);
        isFirstParam = false;
        url = this.param(url, 'limit', isEmpty(options.limit) ? 0 : options.limit, isFirstParam);
        store.proxy.url = url;
    },

    refreshResults: function() {
        var options = {
            start: 0,
            limit: 10
        };
        var searchResultStore = this.getSearchResultStore();
        searchResultStore.load(options);
    },

    getCmp: function(query) {
        var cmpArray = Ext.ComponentQuery.query(query);
        return cmpArray == null ? null : cmpArray[0];
    },

    param: function(urlPrefix, paramName, paramValue, isFirstParameter) {
        return urlPrefix += (isFirstParameter ? '?' : '&') + paramName + '=' + paramValue;
    }

});