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


var AUTHENTICATION_OPF = 'authentication_opf';
var AUTHENTICATION_OPENID1 = 'authentication_openid1';
var AUTHENTICATION_OPENID2 = 'authentication_openid2';

Ext.define('OPF.core.utils.AuthenticationProvider', {

    constructor: function(longName, shortName, icon, url, cfg) {
        cfg = cfg || {};
        this.longName = longName;
        this.shortName = shortName;
        this.icon =  OPF.Cfg.fullUrl(icon);
        this.url = url;
        this.authenticationType = AUTHENTICATION_OPF;
    },

    getLongName: function() {
        return this.longName;
    },

    getShortName: function() {
        return this.shortName;
    },

    getIcon: function() {
        return this.icon;
    },

    getUrl: function() {
        return this.url;
    },

    getAuthenticationType: function() {
        return this.authenticationType;
    },

    prepareForm: function(formOwner) {
        formOwner.setCurrentAuthenticationProvider(this);
        formOwner.setOpenFlameForm();
    }
});

Ext.define('OPF.core.utils.OpenIdProvider', {
    extend: 'OPF.core.utils.AuthenticationProvider',

    constructor: function(longName, shortName, urlSuffix, idParameter, icon, id, url, urlPrefix, authenticationType, cfg) {
        cfg = cfg || {};
        OPF.core.utils.OpenIdProvider.superclass.constructor.call(this, longName, shortName, icon, url, cfg);
        this.urlSuffix = urlSuffix;
        this.idParameter = idParameter;
        this.urlPrefix = urlPrefix;
        this.authenticationType = authenticationType;
    },

    getUrlSuffix: function() {
        return this.urlSuffix;
    },

    getIdParameter: function() {
        return this.idParameter;
    },

    getUrlPrefix: function() {
        return this.urlPrefix;
    },

    prepareForm: function(formOwner) {
        formOwner.setCurrentAuthenticationProvider(this);
        formOwner.setOpenIdForm();
        if (this.isOpenId2Supported() && !this.isOpenId1Supported()) {
            formOwner.prepareOpenId2FieldSet(this);
        } else {
            formOwner.prepareOpenId1FieldSet(this);
        }
    }
});