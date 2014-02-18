/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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
 *
 */
Ext.define('OPF.console.inbox.ProcessManagerView', {
    extend: 'OPF.console.PageLayout',

    alias: 'widget.process-manager-view',

    activeButtonLookup: 'net.firejack.platform.top.inbox',

    initComponent: function() {
        this.westPanel = Ext.create('OPF.console.inbox.view.task.TaskFilterPanel', this, {
            region: 'west',
            width: 220,
            border: false,
            split: true,
            collapsible: true
        });

        this.taskGridPanel = Ext.create('OPF.console.inbox.view.task.TaskGridPanel', this, true);
        this.caseGridPanel = Ext.create('OPF.console.inbox.view.task.TaskGridPanel', this, false);

        this.taskCasePanel = Ext.create('Ext.panel.Panel', {
            region: 'center',
            layout: 'card',
            border: false
        });

        this.taskDetailsPanel = Ext.create('OPF.console.inbox.view.task.TaskDetailsPanel', this, {
            region: 'south',
            split: true,
            collapsible: true,
            height: 250,
            border: false
        });

        var infoLookupPrefix = 'net.firejack.platform.process.case';
        this.navPanel = Ext.create('Ext.panel.Panel', {
            padding: 10,
            border: false,
            layout: {
                type: 'table',
                columns: 1
            },
            items: [
                {
                    xtype: 'resource-control',
                    textResourceLookup: infoLookupPrefix + '.info-text',
                    textMaxLength: 250,
                    imgResourceLookup: infoLookupPrefix + '.info-image',
                    imageWidth: 80,
                    imageHeight: 80
                },
                OPF.Ui.ySpacer(15),
                {
                    xtype: 'container',
                    height: 40,
                    cls: 'more-information',
                    html: '<h4><img src="' + OPF.Cfg.fullUrl('images/info.png') + '" border="0"/>More information</h4>'
                },
                OPF.Ui.ySpacer(15),
                this.populateNavPanel()
            ]
        });

        this.centerPanel = Ext.create('Ext.panel.Panel', {
            region: 'center',
            border: false,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'panel',
                    flex: 1,
                    layout: 'border',
                    items: [
                        this.taskCasePanel,
                        this.taskDetailsPanel
                    ]
                },
                {
                    xtype: 'container',
                    width: 220,
                    layout: 'fit',
                    buttonAlign: 'center',
                    border: false,
                    items: {
                        xtype: 'container',
                        autoScroll: true,
                        items: this.navPanel
                    }
                }
            ]
        });
        this.taskCasePanel.add(this.taskGridPanel);
        this.taskCasePanel.add(this.caseGridPanel);

        this.callParent(arguments);
    },

    populateNavPanel: function() {
        var blocks = this.taskDetailsPanel.getDetailPanels();
        var rightNavContent = '';
        Ext.each(blocks, function(block, index) {
            var isLast = index == (blocks.length - 1);
            var anchorMenuItemLabel = block.fieldLabel;
            if (OPF.isNotBlank(anchorMenuItemLabel) && !block.hidden) {
                rightNavContent +=
                    '<a href="javascript:Ext.ComponentManager.get(\'' + block.getId() + '\').makeActive();">' +
                        '<li class="' + (!isLast ? 'top' : '') + '"><img src="' + OPF.Cfg.fullUrl('images/right_nav_arrow.png') + '" border="0"/>' + anchorMenuItemLabel + '</li>' +
                        '</a>';
            }
        });
        var html = '<ul class="navlist">' + rightNavContent + '</ul>';
        return Ext.create('Ext.container.Container', {
            cls: 'right-nav',
            //width: 140,
            html: html
        })
    }

});