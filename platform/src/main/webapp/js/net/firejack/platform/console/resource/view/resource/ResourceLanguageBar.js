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


/**
 * Provides the toolbar for selecting language versions of the resource and
 * allowing each to be edited and maintained separately.
 */
Ext.define('OPF.console.resource.view.resource.ResourceLanguageBar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.resource-language-bar',

    dock: 'bottom',
    height: 28,

    editPanel: null,
    selectedCulture: null,
    cultureButtons: [],

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.resource.view.resource.ResourceLanguageBar.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.addCountryButtons();

        this.callParent(arguments);
    },

    selectCulture: function(culture) {
        if (this.cultureButtons.length > 0) {
            this.selectedCulture = null;
            Ext.each(this.cultureButtons, function(cultureButton, index) {
                if (cultureButton.culture == culture) {
                    if (!cultureButton.pressed) cultureButton.toggle();
                }
            });
        } else {
            this.selectedCulture = culture;
        }
    },

    addCountryButtons: function() {
        var instance = this;

        this.cultureButtons = [];

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/culture'),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var culturesData = Ext.decode(response.responseText).data;
                Ext.each(culturesData, function(culture, index) {
                    var cultureButton = Ext.ComponentMgr.create({
                        xtype: 'button',
                        text: culture.country,
                        culture: culture.culture,
                        iconCls: 'cult-' + culture.culture.toLowerCase(),
                        enableToggle: true,
                        toggleGroup: 'culture',
                        handler: function () {
                            instance.editPanel.reloadResourceVersion(culture.culture);
                        }
                    });
                    instance.cultureButtons.push(cultureButton);
                    var spacerComponent = Ext.ComponentMgr.create({
                        xtype: 'tbspacer',
                        width: 5
                    });
                    instance.insert(index * 2, cultureButton);
                    instance.insert(index * 2 + 1, spacerComponent);
                });
                instance.doLayout();
                if (isNotEmpty(instance.selectedCulture)) {
                    instance.selectCulture(instance.selectedCulture);
                }
            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                App.setAlert(false, errorJsonData.message);
            }
        });
    }

});