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