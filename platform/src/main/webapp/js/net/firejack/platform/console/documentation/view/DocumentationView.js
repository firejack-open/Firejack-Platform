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

Ext.ns('OPF.console.documentation.manager');

Ext.define('OPF.console.documentation.view.DocumentationView', {

    init: function() {
        this.initCultureSelector();

        this.initCloudNavigation();

        if (OPF.isNotEmpty(OPF.console.documentation.manager.DocumentationManager)) {
            var documentationManager = new OPF.console.documentation.manager.DocumentationManager();

            documentationManager.initEditors();
            documentationManager.initAddButton();
            documentationManager.initDocumentationElements();
        }
    },

    initCloudNavigation: function() {
        Ext.ComponentMgr.create(
            {
                xtype: 'container',
                renderTo: 'left-doc-panel',
                layout: 'fit',
                width: 200,
                items: [
                    {
                        xtype: 'documentation-tree'
                    }
                ]
            }
        )
    },

    initCultureSelector: function() {
        var cultureSelectorCombo = Ext.ComponentMgr.create({
            xtype: 'combo',
            name: 'cultureSelector',
            renderTo: 'cultureSelectorContainer',
            width: 150,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            store: new Ext.data.ArrayStore({
                fields: ['cultureId', 'cultureUrl', 'cultureName'],
                data: CULTURE_DATA
            }),
            valueField: 'cultureId',
            displayField: 'cultureName',
            listConfig: {
                getInnerTpl: function() {
                    return '<div class="x-combo-list-item">' +
                        '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/flag_{cultureId}_16.png" /> ' + '{cultureName}' +
                    '</div>';
                }
            },
            value: CULTURE_CURRENT,
            listeners: {
                select: function(combo, records, eOpts) {
                    document.location = records[0].data.cultureUrl;
                }
            }
        });
        cultureSelectorCombo.show();
    }

});