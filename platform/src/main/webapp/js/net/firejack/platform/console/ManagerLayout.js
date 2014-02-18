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

Ext.define('OPF.console.ManagerLayout', {
    extend: 'Ext.container.Container',
    alias : 'widget.managerlayout',
    cls: 'wrapper',

    layout: 'fit',

    width: 1200,

    renderTo: 'content',

    constructor: function(layoutPanel, cfg) {
        cfg = cfg || {};
        OPF.console.ManagerLayout.superclass.constructor.call(this, Ext.apply({
            layoutPanel: layoutPanel
        }, cfg));
    },

    initComponent: function() {

        OPF.core.utils.RegistryNodeType.initializeAllowTypes();

        var mainPanel = {
            xtype: 'panel',
            layout: {
                type: 'vbox',
                align : 'stretch',
                pack  : 'start'
            },
            items: [
                this.layoutPanel,
                OPF.console.ManagerLayout.initFooter()
            ]
        };

        if (isNotEmpty(this.layoutPanel.activeButtonLookup)) {
            mainPanel.dockedItems = [
                OPF.console.ManagerLayout.initHeader(this.layoutPanel.activeButtonLookup, null)
            ];
        }

        this.items = [
            mainPanel
        ];

        this.callParent(arguments);

        Ext.EventManager.onWindowResize(this.fireResize, this);
        this.height = Ext.Element.getViewportHeight();
    },

    listeners: {
        afterrender: function() {
            Ext.ComponentManager.create({
                xtype: 'button',
                text: 'Show License',
                ui: 'license-ui',
                renderTo: 'licenseContainer',
                handler: this.showLicenseDialog,
                scope: this
            });
        }
    },

    fireResize : function(w, h) {
        this.setSize(this.width, h);
    },

    showLicenseDialog: function() {
        var licenseWindow = Ext.WindowMgr.get('licenseWindow');
        if (!licenseWindow) {
            licenseWindow = Ext.create('OPF.console.license.LicenseWindow', {});
            Ext.WindowMgr.register(licenseWindow);
        }
        licenseWindow.show();
    },

    statics: {
        initHeader: function(lookup, renderTo) {
            return Ext.ComponentManager.create({
                xtype: 'navigation-menu',
                dock: 'top',
                lookup: 'net.firejack.platform.console',
                buttonWidth: null,
                activeButtonLookup: lookup,
                renderTo: renderTo
            });
        },

        initFooter: function(renderTo) {
            return Ext.ComponentManager.create({
                xtype: 'container',
                cls: 'footer',
                height: 70,
                renderTo: renderTo,
                html:
                    '<div class="footer-inner">' +
                        '<div class="col left">' +
                            '<a class="logo" href="http://www.firejack.net" title="Firejack Platform">' +
                                '<span class="logo-img_w26"></span>' +
//                                '<span>Firejack Platform ' + OPF.Cfg.PLATFORM_VERSION + '</span>' +
                                '<span>Firejack Platform v.2.1</span>' +
                            '</a>' +
                            '<div class="build">' +
                                '<span>build ' + OPF.Cfg.BUILD_NUMBER + '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div class="col right">' +
                            '<div class="copyright">Copyright &copy; 2010 - 2014 - Firejack Technologies</div>' +
                            '<div id="licenseContainer" class="license"></div>' +
                        '</div>' +
                    '</div>'
            });
        }
    }

});