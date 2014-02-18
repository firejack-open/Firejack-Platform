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

Ext.define('OPF.prometheus.wizard.settings.SiteSettingsWizard', {
    extend: 'Ext.window.Window',
    alias: 'widget.prometheus.wizard.site-settings-wizard',
    ui: 'wizards',

    statics: {
        id: 'SiteSettingsWizard'
    },

    title: 'Site Settings',

    width: 600,
    draggable:false,
    shadow:false,
    resizable: false,
    modal: true,

    initComponent: function() {
        var me = this;

        this.form = Ext.ComponentManager.create({
            xtype: 'form',
            border: false,
            padding: 10,
            defaults: {
                margin: '0 0 10 0',
                padding: 10,
                frame: true,
                layout: 'anchor'
            },
            items: [
                {
                    xtype: 'container',
                    fieldDefaults: {
                        msgTarget: 'side',
                        labelWidth: 75
                    },
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype:'opf-checkbox',
                            fieldLabel: 'Facebook',
                            checked: false,
                            tooltip: 'Enable / Disable Setting',
                            configLookupKey: 'facebook-enable',
                            inputValue: 'true'
                        },
                        {
                            xtype: 'opf-text',
                            fieldLabel: 'Api Key',
                            configLookupKey: 'facebook-api-key'
                        },
                        {
                            xtype: 'opf-text',
                            fieldLabel: 'Secret Key',
                            configLookupKey: 'facebook-secret'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    fieldDefaults: {
                        msgTarget: 'side',
                        labelWidth: 75
                    },
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype:'opf-checkbox',
                            fieldLabel: 'Twitter',
                            checked: false,
                            tooltip: 'Enable / Disable Setting',
                            configLookupKey: 'twitter-enable',
                            inputValue: 'true'
                        },
                        {
                            xtype: 'opf-text',
                            fieldLabel: 'Consumer Key',
                            configLookupKey: 'twitter-consumer-key'
                        },
                        {
                            xtype: 'opf-text',
                            fieldLabel: 'Consumer Secret Key',
                            configLookupKey: 'twitter-consumer-secret'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    fieldDefaults: {
                        msgTarget: 'side',
                        labelWidth: 75
                    },
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype:'opf-checkbox',
                            fieldLabel: 'Linked In',
                            checked: false,
                            tooltip: 'Enable / Disable Setting',
                            configLookupKey: 'linkedin-enable',
                            inputValue: 'true'
                        },
                        {
                            xtype: 'opf-text',
                            fieldLabel: 'Api Key',
                            configLookupKey: 'linkedin-api-key'
                        },
                        {
                            xtype: 'opf-text',
                            fieldLabel: 'Secret Key',
                            configLookupKey: 'linkedin-secret'
                        },
                        {
                            xtype: 'opf-text',
                            fieldLabel: 'Permissions',
                            configLookupKey: 'linkedin-permissions'
                        }
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    buttonAlign : 'center',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Save',
                            handler: function() {
                                me.saveConfigs();
                            }
                        },
                        {
                            xtype: 'button',
                            ui: 'grey',
                            width: 250,
                            height: 60,
                            text: 'Cancel',
                            handler: function(btn) {
                                me.close();
                            }
                        }
                    ]
                }
            ]
        });

        this.messagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.form
        });
        this.form.insert(0, this.messagePanel);

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    showWizard: function() {
        this.show();
        var pos = this.getPosition();
        if (pos[1] < 0) {
            this.setPosition(pos[0], 0);
        }
        this.loadConfigs();
    },

    loadConfigs: function() {
        var me = this;

        this.form.getEl().mask();

        var lookupData = [];
        var form = this.form.getForm();
        var fields = form.getFields();
        fields.each(function(field) {
            if (field.configLookupKey) {
                var lookup = OPF.Cfg.PACKAGE_LOOKUP + '.' + field.configLookupKey;
                lookupData.push(lookup);
            }
        });

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/config/config/list/by-lookup'),
            method: 'POST',
            jsonData: {"data": {
                lookup: lookupData
            }},

            success: function(response, action) {
                var registryJsonData = Ext.decode(response.responseText);
                var configDataList = registryJsonData.data;
                var configDataMap = new Ext.util.MixedCollection();
                Ext.each(configDataList, function(configData) {
                    var lookup = configData.lookup;
                    configDataMap.add(lookup, configData);
                });

                fields.each(function(field) {
                    if (field.configLookupKey) {
                        var lookup = OPF.Cfg.PACKAGE_LOOKUP + '.' + field.configLookupKey;
                        var configData = configDataMap.getByKey(lookup);
                        if (configData) {
                            field.setValue(configData.value);
                            field.configData = configData;
                        } else {
                            field.setValue(null);
                            field.configData = {
                                name: field.configLookupKey,
                                value: null,
                                lookup: lookup,
                                parentId: 0
                            };
                        }
                    }
                });

                me.form.getEl().unmask();
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    saveConfigs: function() {
        var me = this;

        var form = this.form.getForm();

        var configDataList = [];
        var fields = form.getFields();
        fields.each(function(field) {
            if (field.configLookupKey) {
                var configData = field.configData;
                configData.value = field.getValue();
                configDataList.push(configData);
            }
        });

        this.form.getEl().mask();

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/config/config/batch'),
            method: 'POST',
            jsonData: {dataList: configDataList},

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, responseData.message);
                me.form.getEl().unmask();
                me.close();
            },

            failure: function(response) {
                me.form.getEl().unmask();
                me.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, response.message);
            }
        });
    }

});