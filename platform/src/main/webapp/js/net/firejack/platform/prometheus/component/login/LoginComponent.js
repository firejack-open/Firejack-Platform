Ext.define('OPF.prometheus.component.login.LoginComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.login-component',

    cls: 'b-login',

    submitUrl: null,
    forgotPasswordPageUrl: null,
    siteLogoLookup: null,

    initComponent: function() {
        var me = this;

        var messageCount = 0;
        var messages = '';
        Ext.each(OPF.Cfg.EXTRA_PARAMS.filterMessages, function(filterMessage) {
            messages = '<span class="' + filterMessage.type.toLowerCase() + '">' + filterMessage.message + '</span>';
            messageCount++;
        });
        this.filterMessage = Ext.ComponentMgr.create({
            xtype: 'container',
            cls: 'filter-message',
            html: messages,
            hidden: messageCount == 0
        });

        this.usernameField = Ext.ComponentMgr.create({
            xtype: 'opf-text',
            name: 'username',
            labelAlign: 'top',
            fieldLabel: 'Username',
            width: 290,
            tabIndex: 1
        });

        this.passwordEncryptedField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'encrypted',
            value: 'false'
        });

        this.visiblePasswordField = Ext.ComponentMgr.create({
            xtype: 'opf-text',
            name: 'visiblePassword',
            inputType: 'password',
            submitValue: false,
            labelAlign: 'top',
            fieldLabel: 'Password',
            width: 290,
            tabIndex: 2
        });

        this.passwordField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'password'
        });

        this.redirectUrlField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'redirect',
            value: OPF.getQueryParam('redirect')
        });

        this.forgotPasswordLink = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            html: 'Forgot your password?',
            cls: 'pass-reset',
            onClick: function() {
                if (OPF.isNotBlank(me.forgotPasswordPageUrl)) {
                    document.location = OPF.Cfg.fullUrl(me.forgotPasswordPageUrl, true);
                }
            }
        });

        this.googleLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnGoogleLogin',
            ui: 'social',
            iconCls: 'google-login-icon',
            width: 32,
            height: 32
        });

        var isFacebookLoginEnable = OPF.Cfg.EXTRA_PARAMS.facebookLoginEnable == 'true';
        this.facebookLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnFacebookLogin',
            ui: 'social',
            iconCls: 'facebook-login-icon',
            width: 32,
            height: 32,
            hidden: !isFacebookLoginEnable
        });

        var isTwitterLoginEnable = OPF.Cfg.EXTRA_PARAMS.twitterLoginEnable == 'true';
        this.twitterLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnTwitterLogin',
            ui: 'social',
            iconCls: 'twitter-login-icon',
            width: 32,
            height: 32,
            hidden: !isTwitterLoginEnable
        });

        var isLinkedinLoginEnable = OPF.Cfg.EXTRA_PARAMS.linkedinLoginEnable == 'true';
        this.linkedinLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnLinkedInLogin',
            ui: 'social',
            iconCls: 'linkedin-login-icon',
            width: 32,
            height: 32,
            hidden: !isLinkedinLoginEnable
        });

        this.loginButton = new Ext.Button({
            text: "Log In",
            ui: 'submit',
            height: 30,
            handler: function() {
                me.submit();
            }
        });

        this.loginForm = Ext.ComponentMgr.create({
            xtype: 'form',
            url: OPF.Cfg.fullUrl(this.submitUrl, true),
            monitorValid: true,
            standardSubmit: true,
            header: false,
            border: false,
            items: [
                {
                    xtype: 'container',
                    cls: 'form-login',
                    items: [
                        {
                            xtype: 'container',
                            items: [
                                {
                                    xtype: 'container',
                                    cls: 'form-leftside',
                                    items: [
                                        {
                                            xtype: 'container',
                                            items: [
                                                this.usernameField
                                            ]
                                        },
                                        {
                                            xtype: 'container',
                                            items: [
                                                //this.rememberMeField
                                            ]
                                        },
                                        {
                                            xtype: 'container',
                                            items: [
                                                this.visiblePasswordField
                                            ]
                                        },
                                        {
                                            xtype: 'container',
                                            items: [
                                                this.forgotPasswordLink
                                            ]
                                        }
                                    ]
                                },
                                {
                                    xtype: 'container',
                                    cls: 'form-rightside',
                                    hidden: !isFacebookLoginEnable &&
                                            !isTwitterLoginEnable &&
                                            !isLinkedinLoginEnable,
                                    items: [
                                        {
                                            xtype: 'container',
                                            cls: 'login-using',
                                            items: [
                                                {
                                                    xtype: 'container',
                                                    html: '<h3>or Login using social networks:</h3>'
                                                },

//                                                this.googleLoginButton,
                                                this.facebookLoginButton,
                                                this.twitterLoginButton,
                                                this.linkedinLoginButton
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        this.passwordEncryptedField,
                        this.passwordField,
                        this.redirectUrlField,
                        this.openIdField
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    cls: 'buttons',
                    items: [
                        '->',
                        this.loginButton
                    ]
                }
            ],
            listeners: {
                afterrender: function(form, options) {
                    this.keyNav = Ext.create('Ext.util.KeyNav', this.el, {
                        enter: function() {
                            me.submit();
                        },
                        scope: this
                    });
                }
            }
        });

        this.siteLogo = new OPF.core.component.resource.ImageResourceControl({
            imgResourceLookup: this.siteLogoLookup,
            componentCls: 'logo'        });

        this.items = [
            {
                xtype: 'container',
                cls: 'login-top',
                html: '<h2>Login</h2>'
            },
            this.filterMessage,
            this.siteLogo,
            this.loginForm
        ];

        this.callParent(arguments);
    },

    submit: function() {
        this.loginForm.getEl().mask();
        if (OPF.isNotBlank(OPF.Cfg.EXTRA_PARAMS.publicKey)) {
            var key = RSA.getPublicKey(OPF.Cfg.EXTRA_PARAMS.publicKey);
            var encryptedPassword = RSA.encrypt(this.visiblePasswordField.getValue(), key);
            this.passwordField.setValue(encryptedPassword);
            this.passwordEncryptedField.setValue('true');
        } else {
            this.passwordField.setValue(this.visiblePasswordField.getValue());
        }
        this.loginForm.getForm().submit();
    }

});