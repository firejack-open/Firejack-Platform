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