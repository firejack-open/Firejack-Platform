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

//Temporary class that are used for testing ldap authentication.
Ext.define('OPF.console.directory.editor.ldap.AuthenticationChecker', {
    extend: 'Ext.window.Window',

    alias: 'widget.ldap-auth-checker',

    title: 'Login',
    layout: 'fit',
    width: 300,
    height: 200,
    directoryId: null,

    schemaField: null,

    constructor: function(winId, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.ldap.AuthenticationChecker.superclass.constructor.call(this, Ext.apply({
            id: winId
        }, cfg));
    },

    initComponent: function() {
        var me = this;
        this.usernameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Username',
            subFieldLabel: '',
            width: 200,
            name: 'username'
        });

        this.passwordField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Password',
            subFieldLabel: '',
            width: 200,
            name: 'password',
            inputType: 'password'
        });

        this.form = Ext.create('Ext.form.Panel', {
            items: [
                this.usernameField,
                this.passwordField
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        [
                            '->',
                            {
                                xtype: 'button',
                                text: 'login',
                                handler: function() {
                                    var data = me.form.getForm().getValues();
                                    var username = data.username;
                                    var password = data.password;
                                    Ext.Ajax.request({
                                        url: OPF.Cfg.restUrl('/directory/ldap/user/sign-in?directoryId=') +
                                            me.directoryId + '&username=' + username + '&password=' + password,
                                        method: 'GET',
                                        success: function(response) {
                                            var vo = Ext.decode(response.responseText);
                                            if (vo.success) {
                                                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                                            } else {
                                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                                            }
                                        },

                                        failure: function(response) {
                                            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                                        }
                                    })
                                }
                            }
                        ]
                    ]
                }
            ]
        });
        this.items = this.form;

        this.callParent(arguments);
    },

    showDialog: function(directoryId) {
        this.directoryId = directoryId;
        this.show();
    }

});