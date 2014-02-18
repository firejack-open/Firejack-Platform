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

Ext.define('OPF.console.home.view.Login', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.home-login',

    title: 'Login',

    initComponent: function() {
        var instance = this;

        this.loginButton = new Ext.Button({
            text: "Login",
            cls: 'login-button',
            handler: function() {
                instance.loginForm.getEl().mask();
                instance.loginForm.getForm().submit();
            }
        });

        this.chooseLoginTypeButton = new Ext.SplitButton({
            text: "Type: Default",
            cls: 'login-button',
            handler: function() {
                instance.chooseLoginTypeButton.showMenu();
            },
            menu: new Ext.menu.Menu({
                items: instance.createSignInMenuItems()
            })
        });

        this.firstFieldLabel = Ext.ComponentMgr.create({
            xtype: 'displayfield',
            value: 'Username:'
        });

        this.firstField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'login',
            value: ifBlank(OPF.Cfg.DEFAULT_LOGIN, '')
        });

        this.passwordFieldLabel = Ext.ComponentMgr.create({
            xtype: 'displayfield',
            value: 'Password:'
        });

        this.passwordField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'password',
            inputType: 'password',
            value: ifBlank(OPF.Cfg.DEFAULT_PASSWORD, '')
        });

        this.openIdFieldLabel = Ext.ComponentMgr.create({
            xtype: 'displayfield',
            value: 'Username:',
            hidden: true
        });

        this.openIdField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'openid_identifier',
            hidden: true
        });

        this.loginForm = Ext.ComponentMgr.create({
            xtype: 'form',
            url: OPF.Cfg.fullUrl('authentication'),
            monitorValid: true,
            standardSubmit: true, // do not use Ajax submit (default ExtJs)
            header: false,
            border: false,
            hideLabels: true,
            buttonAlign: 'center',
            items: [
                this.firstFieldLabel,
                this.firstField,
                this.passwordFieldLabel,
                this.passwordField,
                this.openIdFieldLabel,
                this.openIdField
            ],
            fbar: [
                this.loginButton//,
//                this.chooseLoginTypeButton
            ],
            listeners: {
                afterRender: function(form, options) {
                    this.keyNav = Ext.create('Ext.util.KeyNav', this.el, {
                        enter: function() {
                            instance.loginForm.getEl().mask();
                            instance.loginForm.getForm().submit();
                        },
                        scope: this
                    });
                }
            }
        });

        this.items = [
            this.loginForm
        ];

        this.callParent(arguments);
    },

    createSignInMenuItems: function() {
        var instance = this;

        var authenticationProviders = [
                new OPF.core.utils.AuthenticationProvider("OpenFlame", "OpenFlame", "/images/openid/opf.jpg", OPF.Cfg.fullUrl('authentication')),
                new OPF.core.utils.OpenIdProvider("myOpenID", "myOpenID", ".myopenid.com/", "username", "/images/openid/myopenID.jpg", 8, "https://www.myopenid.com/", "http://", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("Google", "Google", "", "username", "/images/openid/google.jpg", 26, "https://www.google.com/accounts/o8/id", "", AUTHENTICATION_OPENID2),
                new OPF.core.utils.OpenIdProvider("Flickr", "Flickr", "", "username", "/images/openid/flickr.jpg", 6, "http://www.flickr.com/", "http://www.flickr.com/photos/", AUTHENTICATION_OPENID2),
                new OPF.core.utils.OpenIdProvider("Yahoo!", "Yahoo!", "", "yahoo id", "/images/openid/yahoo.jpg", 15, "http://yahoo.com/", "http://me.yahoo.com/", AUTHENTICATION_OPENID2),
                new OPF.core.utils.OpenIdProvider("America Online/AIM", "AOL", "", "screenname", "/images/openid/aol.jpg", 1, "http://www.aol.com/", "http://openid.aol.com/", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("Google Blogger", "Blogger", ".blogspot.com/", "blog name", "/images/openid/blogr.jpg", 2, "http://www.blogger.com/", "http://", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("Livejournal", "Livejournal", ".livejournal.com/", "username", "/images/openid/livejrnl.jpg", 7, "http://www.livejournal.com/", "http://", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("Verisign PIP", "Verisign", ".pip.verisignlabs.com/", "username", "/images/openid/verisgn.jpg", 11, "http://pip.verisignlabs.com/", "http://", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("Vidoop", "Vidoop", ".myvidoop.com/", "username", "/images/openid/vidoop.jpg", 12, "http://www.vidoop.com/", "http://", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("claimID", "claimID", "", "username", "/images/openid/claimID.jpg", 4, "http://claimid.com/", "http://claimid.com/", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("Technorati", "Technorati", "", "username", "/images/openid/technorati.jpg", 10, "http://www.technorati.com/", "http://technorati.com/people/technorati/", AUTHENTICATION_OPENID1),
                new OPF.core.utils.OpenIdProvider("Vox", "Vox", ".vox.com/", "username", "/images/openid/vox.jpg", 13, "http://www.vox.com/", "http://", AUTHENTICATION_OPENID1)
        ];

        var menuItems = [];
        Ext.each(authenticationProviders, function(authenticationProvider){
            var menuItem = new Ext.menu.Item({
                text: authenticationProvider.getShortName(),
                icon: authenticationProvider.getIcon(),
                handler: function() {
                    if (authenticationProvider.getAuthenticationType() == AUTHENTICATION_OPF) {
                        instance.prepareOpfFieldSet();
                    } else if (authenticationProvider.getAuthenticationType() == AUTHENTICATION_OPENID1) {
                        instance.prepareOpenId1FieldSet(authenticationProvider);
                    } else if (authenticationProvider.getAuthenticationType() == AUTHENTICATION_OPENID2) {
                        instance.prepareOpenId2FieldSet(authenticationProvider);
                    }
                }
            });
            menuItems.push(menuItem);
        });
        return menuItems;
    },

    prepareOpfFieldSet: function() {
        this.loginForm.getForm().url = OPF.Cfg.fullUrl('authentication');

        this.firstFieldLabel.setValue("Username:");
        this.firstField.setValue("");

        this.firstFieldLabel.show();
        this.firstField.show();
        this.passwordFieldLabel.show();
        this.passwordField.show();
        this.openIdFieldLabel.hide();
        this.openIdField.hide();

        this.chooseLoginTypeButton.setText("Type: Default");
        this.chooseLoginTypeButton.setIcon(null);
        this.chooseLoginTypeButton.removeClass('x-btn-login-text-icon');
    },

    prepareOpenId1FieldSet: function(authenticationProvider) {
        this.loginForm.getForm().url = OPF.Cfg.fullUrl('openid-authentication');

        this.firstFieldLabel.setValue(authenticationProvider.getLongName() + " " + authenticationProvider.getIdParameter() + ":");
        this.firstField.setValue("username");
        this.openIdField.setValue(authenticationProvider.getUrlPrefix() + "username" + authenticationProvider.getUrlSuffix());

        this.firstFieldLabel.show();
        this.firstField.show();
        this.passwordFieldLabel.hide();
        this.passwordField.hide();
        this.openIdFieldLabel.show();
        this.openIdField.show();

        this.chooseLoginTypeButton.addClass('x-btn-login-text-icon');
        this.chooseLoginTypeButton.setText(authenticationProvider.getShortName());
        this.chooseLoginTypeButton.setIcon(authenticationProvider.getIcon());
    },

    prepareOpenId2FieldSet: function(authenticationProvider) {
        this.loginForm.getForm().url = OPF.Cfg.fullUrl('openid-authentication');

        this.firstField.setValue(null);
        this.openIdField.setValue(authenticationProvider.getUrl());

        this.firstFieldLabel.hide();
        this.firstField.hide();
        this.passwordFieldLabel.hide();
        this.passwordField.hide();
        this.openIdFieldLabel.show();
        this.openIdField.show();

        this.chooseLoginTypeButton.addClass('x-btn-login-text-icon');
        this.chooseLoginTypeButton.setText(authenticationProvider.getShortName());
        this.chooseLoginTypeButton.setIcon(authenticationProvider.getIcon());
    }

});