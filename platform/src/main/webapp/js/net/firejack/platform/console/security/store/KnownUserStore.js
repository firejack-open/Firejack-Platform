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