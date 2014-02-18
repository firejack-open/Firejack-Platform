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

Ext.require([
    'OPF.prometheus.wizard.AddWizard',
    'OPF.prometheus.wizard.security.ShareDataWizard',
    'OPF.prometheus.wizard.workflow.ProcessWizard',
    'OPF.prometheus.wizard.settings.SiteSettingsWizard',
    'OPF.prometheus.utils.RedeploySite'
]);

Ext.define('OPF.prometheus.component.toolbar.ToolbarComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.toolbar-component',

    cls: 'm-admin-panel',

    logoutUrl: '/authentication/sign-out',

    mixins: {
        redeployer: 'OPF.prometheus.utils.RedeploySite'
    },

    initComponent: function() {
        var me = this;

        var toolbarItems = [];

        if (OPF.Cfg.USER_INFO.isLogged) {
            if (OPF.Cfg.USER_INFO.isPackageAdmin) {
                this.addButton = Ext.create('Ext.button.Button', {
                    ui:'link',
                    text: 'Add',
                    listeners: {
                        click: function(button) {
                            var wizard = Ext.WindowMgr.get(OPF.prometheus.wizard.AddWizard.id);
                            if (!wizard) {
                                wizard = Ext.ComponentMgr.create({
                                    xtype: 'prometheus.wizard.add-wizard'
                                });
                                Ext.WindowMgr.register(wizard);
                            }
                            wizard.show();
                            var pos = wizard.getPosition();
                            if (pos[1] < 0) {
                                wizard.setPosition(pos[0], 0);
                            }
                        }
                    }
                });

                this.shareDataButton = Ext.create('Ext.button.Button', {
                    ui:'link',
                    text: 'Share Data',
                    itemId: 'shareDataToolbarButton',
                    currentModelLookup: null,
                    currentRecord: null,
                    hidden: true,
                    listeners: {
                        click: function(btn) {
                            if (OPF.isNotEmpty(btn.currentModelLookup)) {
                                var wizard = Ext.WindowMgr.get(OPF.prometheus.wizard.security.ShareDataWizard.id);
                                if (!wizard) {
                                    wizard = Ext.ComponentMgr.create({
                                        xtype: 'prometheus.wizard.security.share-data-wizard'
                                    });
                                    Ext.WindowMgr.register(wizard);
                                }
                                wizard.setCurrentRecordInfo(btn.currentModelLookup, btn.currentRecord);
                                wizard.showWizard();
                            }
                        }
                    }
                });

                this.editButton = Ext.create('Ext.button.Button', {
                    hidden: true,
                    ui:'link',
                    text: 'Edit',
                    enableToggle: true,
                    listeners: {
                        click: function(button) {
                        }
                    }
                });

        //        this.styleButton = Ext.create('Ext.button.Button', {
        //            text: 'Style',
        //            enableToggle: true,
        //            listeners: {
        //                click: function(button) {
        //                }
        //            }
        //        });

                this.deleteButton = Ext.create('Ext.button.Button', {
                    hidden: true,
                    ui:'link',
                    text: 'Delete',
                    enableToggle: true,
                    listeners: {
                        click: function(button) {

                            // Restart progress bar
//                            var progressBar = new Ext.ProgressBar();
//
//                            var resetDialog = Ext.create('Ext.window.Window', {
//                                titleAlign: 'center',
//                                title: 'Restart Server Progress ...',
//                                modal: true,
//                                closable: false,
//                                resizable: false,
//                                cls: 'wizard-progress-bar',
//                                width: 600,
//                                height: 160,
//                                items: [
//                                    progressBar
//                                ]
//                            });
//                            resetDialog.show();
//
//                            progressBar.wait({
//                                interval: 1000,
//                                increment: 10,
//                                text: 'Restarting...'
//                            });
                        }
                    }
                });

//                this.inboxButton = Ext.create('Ext.button.Button', {
//                    ui:'link',
//                    text: 'Inbox',
//                    enableToggle: true,
//                    listeners: {
//                        click: function(button) {
//                            document.location.href = OPF.Cfg.fullUrl('/inbox', true);
//                        }
//                    }
//                });

                this.siteSettingsButton = Ext.create('Ext.button.Button', {
                    ui: 'icon',
                    width: 29,
                    height: 29,
                    tooltip: 'Site Settings',
                    iconCls: 'site-settings-btn-ico',
                    listeners: {
                        click: function(button) {
                            var wizard = Ext.WindowMgr.get(OPF.prometheus.wizard.settings.SiteSettingsWizard.id);
                            if (!wizard) {
                                wizard = Ext.ComponentMgr.create({
                                    xtype: 'prometheus.wizard.site-settings-wizard'
                                });
                                Ext.WindowMgr.register(wizard);
                            }
                            wizard.showWizard();
                        }
                    }
                });

                this.dashBoardButton = Ext.create('Ext.button.Button', {
                    ui: 'icon',
                    cls: 'dashboard-btn',
                    width: 41,
                    height: 29,
                    tooltip: 'Dashboard',
                    iconCls: 'dashboard-btn-ico',
                    listeners: {
                        afterrender: function(btn) {
                            me.initDashboardTooltip(btn);
                        }
                    }
                });

                this.notificationsButton = Ext.create('Ext.button.Button', {
                    hidden: true,
                    ui: 'icon',
                    width: 29,
                    height: 29,
                    tooltip: 'Notifications',
                    iconCls: 'notifications-btn-ico'
                });

                toolbarItems = [
                    this.addButton,
                    this.shareDataButton,
                    this.editButton,
                    this.deleteButton,
//                    '-',
//                    this.inboxButton,
                    '-',
                    this.siteSettingsButton,
                    this.dashBoardButton,
                    this.notificationsButton
                ];
            }

            this.userButton = Ext.create('Ext.button.Button', {
                cls: 'user-name',
                margin: '0 3 0 0',
                scale: 'large',
                text: OPF.Cfg.USER_INFO.username,
                icon: OPF.Cfg.fullUrl('/images/tmp/user-avatar-default.png', true),
                menu: {
                    xtype: 'menu',
                    minWidth: 130,
                    shadow:false,
                    ui: 'user-menu',
                    defaults: {
                        xtype: 'button',
                        ui: 'user-menu',
                        textAlign: 'left'
                    },
                    items: [
//                        {
//                            text: 'My Profile',
//                            iconCls: 'user-profile-ico'
//                        },
                        {
                            text: 'Logout',
                            iconCls: 'user-logout-ico',
                            href: OPF.Cfg.fullUrl(this.logoutUrl, true),
                            hrefTarget: '_self'
                        }
                    ]
                }
            });

            toolbarItems.push('->');
            toolbarItems.push(this.userButton);
        }

        this.items = [];

        if (toolbarItems.length > 0) {
            this.items.push({
                xtype: 'toolbar',
                ui: 'inner',
                border: false,
                items: toolbarItems
            });
        }

        this.callParent(arguments);
    },

    initDashboardTooltip: function(dashboardButton) {
        var me = this;

        var tplDashboardTooltip = new Ext.XTemplate(
            '<tpl for=".">',
                '<div class="dashboard-record"><i>{[this.getCreatedDate(values.created)]}</i> <b>{description}</b></div>',
            '</tpl>',
            {
                getCreatedDate: function(created) {
                    var createdDate = new Date();
                    createdDate.setTime(created);
                    return Ext.Date.format(createdDate, 'Y-m-d h:i A');
                }
            }
        );

        var messageContainer = Ext.create('Ext.container.Container', {
            html: 'Loading Deployment Changes ...',
            maxHeight: 250,
            autoScroll: true,
            flex: 1
        });

        var redeployButton = Ext.create('Ext.button.Button', {
            text: 'Deploy Changes',
            hidden: true,     //TODO - uncomment for production
            height: 50,
            handler: function() {
                me.redeploy();
            }
        });

        Ext.create('OPF.core.component.ToolTipOnClick', {
            autoHide: false,
            closable: false,
            shadow: false,
            cls: 'dashboard-tooltip',
            anchor: 'bottom',
            width: 450,
            target: dashboardButton.getEl(),
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                messageContainer,
                redeployButton
            ],
            listeners: {
                show: function(tooltip) {
                    Ext.Ajax.request({
                        url: OPF.Cfg.restUrl('/deployment/changes/' + OPF.Cfg.PACKAGE_LOOKUP, false),
                        method: 'GET',

                        success: function(response, action) {
                            var responseData = Ext.decode(response.responseText);
                            if (responseData.success) {
                                var total = responseData.data.length;
                                if (total > 0) {
                                    var content = tplDashboardTooltip.apply(responseData.data);
                                    messageContainer.update(content);
                                    dashboardButton.addCls('active');
                                    dashboardButton.setText(total);
                                    redeployButton.setVisible(true);    //TODO - uncomment for production
                                } else {
                                    messageContainer.update('<div class="tooltip-error-title">Not any changes.</div>');
                                    dashboardButton.removeCls('active');
                                    dashboardButton.setText('');
                                    redeployButton.setVisible(false);    //TODO - uncomment for production
                                }
                            } else {
                                messageContainer.update('<div class="tooltip-error-title">Occurred server error.</div>' +
                                    '<div class="tooltip-error-message">' + responseData.message + '</div>');
                                dashboardButton.removeCls('active');
                                dashboardButton.setText('');
                                redeployButton.setVisible(false);    //TODO - uncomment for production
                            }
                        },
                        failure: function(response) {
                            messageContainer.update('<div class="tooltip-error-title">Occurred server error.</div>' +
                                '<div class="tooltip-error-message">' + response.message + '</div>');
                            dashboardButton.removeCls('active');
                            dashboardButton.setText('');
                            redeployButton.setVisible(false);   //TODO - uncomment for production
                        }
                    });
                }
            }
        });

        this.refreshDashboardButton();
    },

    refreshDashboardButton: function() {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/deployment/changes/' + OPF.Cfg.PACKAGE_LOOKUP, false),
            method: 'GET',

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                if (responseData.success) {
                    var total = responseData.data.length;
                    if (total > 0) {
                        me.dashBoardButton.addCls('active');
                        me.dashBoardButton.setText(total);
                    } else {
                        me.dashBoardButton.removeCls('active');
                        me.dashBoardButton.setText('');
                    }
                } else {
                    me.dashBoardButton.removeCls('active');
                    me.dashBoardButton.setText('');
                }
            },

            failure: function(response) {
                me.dashBoardButton.removeCls('active');
                me.dashBoardButton.setText('');
            }
        });
    }

});