//@require PrototypeUtils.js
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

Ext.define('OPF.core.utils.Config', {
    alternateClassName: ['OPF.Cfg'],

    OPF_CONSOLE_URL: null,
    DEBUG_MODE: null,
    DEFAULT_LOGIN: null,
    DEFAULT_PASSWORD: null,
    CAN_EDIT_RESOURCE: null,
    PAGE_TYPE: null,
    PAGE_UID: null,

    BASE_URL: null,
    NAVIGATION_LOOKUP: null,
    PACKAGE_ID: null,
    PACKAGE_LOOKUP: null,
    CACHE_VERSION: null,

    EXTRA_PARAMS: null,

    DASHBOARD_PREFIX_URL: null,

    USER_INFO: {
        id: null,
        username: null,
        isLogged: false
    },

    fullUrl: function(urlSuffix, useBaseUrl) {
        var url;
        if (urlSuffix.startsWith('http://')) {
            url = urlSuffix;
        } else {
            url = useBaseUrl ? this.BASE_URL : this.OPF_CONSOLE_URL;
            if (!urlSuffix.startsWith('/')) {
                url += '/';
            }
            url += urlSuffix;
        }
        if (this.DEBUG_MODE == true) {
            url = this.addParameterToURL(url, 'debug', 'true');
        }
        return url;
    },

    restUrl: function(urlSuffix, useBaseUrl) {
        var url;
        if (urlSuffix.startsWith('http://')) {
            url = urlSuffix;
        } else {
            url = url = (useBaseUrl ? this.BASE_URL : this.OPF_CONSOLE_URL) + '/rest';
            if (!urlSuffix.startsWith('/')) {
                url += '/';
            }
            url += urlSuffix;
        }
//  before use it need to be sure that all urls are forming correct, special attention must be taken care of store.beforeLoad function
//        if (this.DEBUG_MODE == true) {
//            url = this.addParameterToURL(url, 'debug', 'true');
//        }
        return url;
    },

    getBaseUrl: function() {
        return OPF.ifBlank(this.BASE_URL, this.OPF_CONSOLE_URL);
    },

    randomUUID: function () {
        var s = [], itoh = '0123456789ABCDEF', i;
        for (i = 0; i < 36; i++) s[i] = Math.floor(Math.random() * 0x10);
        s[14] = 4;
        s[19] = (s[19] & 0x3) | 0x8;
        for (i = 0; i < 36; i++) s[i] = itoh[s[i]];
        s[8] = s[13] = s[18] = s[23] = '-';
        return s.join('');
    },

    noCacheLoadScriptInit: function() {
        if (this.CACHE_VERSION != undefined && this.CACHE_VERSION != null && this.CACHE_VERSION.length > 0) {
            Ext.Loader.loadScriptFile = function(url, onLoad, onError, scope, synchronous) {
                var me = this,
                    noCacheUrl,
                    fileName = url.split('/').pop(),
                    isCrossOriginRestricted = false,
                    xhr, status, onScriptError;

                if (this.getConfig('disableCaching')) {
                    noCacheUrl = url + '?' + this.getConfig('disableCachingParam') + '=' + Ext.Date.now() + '&v=' + OPF.Cfg.CACHE_VERSION;
                } else {
                    noCacheUrl = url + '?v=' + OPF.Cfg.CACHE_VERSION;
                }

                scope = scope || this;

                this.isLoading = true;

                if (!synchronous) {
                    onScriptError = function() {
                        onError.call(scope, "Failed loading '" + url + "', please verify that the file exists", synchronous);
                    };

                    if (!Ext.isReady && Ext.onDocumentReady) {
                        Ext.onDocumentReady(function() {
                            me.injectScriptElement(noCacheUrl, onLoad, onScriptError, scope);
                        });
                    }
                    else {
                        this.injectScriptElement(noCacheUrl, onLoad, onScriptError, scope);
                    }
                }
                else {
                    if (typeof XMLHttpRequest !== 'undefined') {
                        xhr = new XMLHttpRequest();
                    } else {
                        xhr = new ActiveXObject('Microsoft.XMLHTTP');
                    }

                    try {
                        xhr.open('GET', noCacheUrl, false);
                        xhr.send(null);
                    } catch (e) {
                        isCrossOriginRestricted = true;
                    }

                    status = (xhr.status === 1223) ? 204 : xhr.status;

                    if (!isCrossOriginRestricted) {
                        isCrossOriginRestricted = (status === 0);
                    }

                    if (isCrossOriginRestricted
                        ) {
                        onError.call(this, "Failed loading synchronously via XHR: '" + url + "'; It's likely that the file is either " +
                            "being loaded from a different domain or from the local file system whereby cross origin " +
                            "requests are not allowed due to security reasons. Use asynchronous loading with " +
                            "Ext.require instead.", synchronous);
                    }
                    else if (status >= 200 && status < 300
                        ) {

                        new Function(xhr.responseText + "\n//@ sourceURL=" + fileName)();

                        onLoad.call(scope);
                    }
                    else {
                        onError.call(this, "Failed loading synchronously via XHR: '" + url + "'; please " +
                            "verify that the file exists. " +
                            "XHR status code: " + status, synchronous);
                    }


                    xhr = null;
                }

            };
        }
    },

    addParameterToURL: function(url, key, value) {
        var param = key + '=' + encodeURIComponent(value);

        var sep = '&';
        if (url.indexOf('?') < 0) {
            sep = '?';
        } else {
            var lastChar = url.slice(-1);
            if (lastChar == '&') sep = '';
            if (lastChar == '?') sep = '';
        }
        url += sep + param;

        return url;
    }

});

OPF.Cfg = new OPF.core.utils.Config();
OPF.Cfg.PAGE_UID = OPF.Cfg.randomUUID();
