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

Ext.define('OPF.console.security.KnownUserStore', {
    extend: 'Ext.data.Store',

    restful: true,
    model: OPF.console.directory.model.UserModel,
    securityWin: null,
    proxy: {
        type: 'ajax',
        url: OPF.core.utils.RegistryNodeType.USER.generateUrl('/search'),
        method: 'GET',
        reader: {
            type: 'json',
            totalProperty: 'total',
            successProperty: 'success',
            idProperty: 'id',
            root: 'data',
            messageProperty: 'message' // <-- New "messageProperty" meta-data
        },
        writer: {
            //type: 'json',
            encode: false                 // <-- don't return encoded JSON -- causes Ext.Ajax#request to send data using jsonData config rather than HTTP params
        },
        listeners: {
            exception: function(proxy, type, action, options, response) {
                OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
            }
        }
    },                               // <-- plug a DataWriter into the store just as you would a Reader

    constructor: function(securityWin, cfg) {
        cfg = cfg || {};
        OPF.console.security.KnownUserStore.superclass.constructor.call(this, Ext.apply({
            securityWin: securityWin
        }, cfg));
    },

    listeners: {
        beforeload: function(store, options) {
            var urlSuffix = '';
            var searchPhrase = this.securityWin.searchField.getValue();
            var searchPhraseNotBlank = OPF.isNotBlank(searchPhrase);
            if (OPF.isEmpty(this.securityWin.selectedDirectoryId)) {
                urlSuffix = '/search';
                if (searchPhraseNotBlank) {
                    urlSuffix += '?term=' + searchPhrase;
                }
            } else {
                urlSuffix = searchPhraseNotBlank ? '/node/search/' : '/node/';
                urlSuffix += this.securityWin.selectedDirectoryId;
                if (searchPhraseNotBlank) {
                    urlSuffix += '/';
                    urlSuffix += searchPhrase;
                }
            }
            store.proxy.url = OPF.core.utils.RegistryNodeType.USER.generateUrl(urlSuffix);
            store.proxy.method = 'GET';
        }
    }
});