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

Ext.define('OPF.console.license.LicenseWindow', {
    extend: 'Ext.window.Window',

    title: 'License',
    id: 'licenseWindow',
    width: 300,
    height: 140,
    layout: {
        type: 'vbox',
        align: 'stretch',
        padding: 5
    },
    modal: true,

    initComponent: function() {
        var me = this;

        this.errorContainer = Ext.ComponentManager.create({
            xtype: 'container',
            height: 72,
            style: 'color: #CC0404;',
            hidden: true
        });

        this.nameField = Ext.ComponentManager.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'License Name',
            labelWidth: 120
        });

        this.sessionField = Ext.ComponentManager.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Sessions',
            labelWidth: 120
        });

        this.expiredField = Ext.ComponentManager.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Expired',
            labelWidth: 120
        });

        this.uploadLicenseDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['xml'],
            uploadUrl: OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/license'),

            successUploaded: function(jsonData, action) {
                me.loadLicenseData();
            },

            failureUploaded: function(action) {
                var response = action.response;
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, resp.message);

                me.uploadLicenseDialog.hide();
                me.showLicenseData(false);
                me.errorContainer.update(resp.message);
            }
        });

        this.items = [
            this.errorContainer,
            this.nameField,
            this.sessionField,
            this.expiredField,
            {
                xtype: 'button',
                text: 'Change License',
                handler: function() {
                    me.uploadLicenseDialog.show();
                }
            }
        ];

        this.callParent(arguments);

        this.loadLicenseData();
    },

    loadLicenseData: function() {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/registry/license'),
            method: 'GET',

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, resp.message);
                me.showLicenseData(true);
                if (resp.data && resp.data.length > 0 && resp.data[0]) {
                    var data = resp.data[0];
                    me.nameField.setValue(data.name);
                    me.sessionField.setValue(data.session ? data.session : 'unlimited');

                    var date = new Date();
                    date.setTime(data.expired);
                    var colorStyle = (new Date().valueOf() > date.valueOf()) ? 'color: red' : 'color: black;';
                    me.expiredField.setFieldStyle( colorStyle );
                    me.expiredField.setValue(Ext.Date.format(date, 'Y-m-d G:i:s'));
                } else {
                    me.nameField.setValue('Trial Version');
                    me.sessionField.setValue('3');
                    me.expiredField.setValue('none');
                }
            },

            failure: function(response) {
                me.nameField.setValue('Trial Version');
                me.sessionField.setValue('3');
                me.expiredField.setValue('none');
            }
        });

    },

    showLicenseData: function(isShow) {
        this.nameField.setVisible(isShow);
        this.sessionField.setVisible(isShow);
        this.expiredField.setVisible(isShow);
        this.errorContainer.setVisible(!isShow);
    }

});
