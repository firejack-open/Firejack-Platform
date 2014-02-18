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


Ext.define('OPF.console.directory.editor.OAuthInfoFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'OAuth Info',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.OAuthInfoFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.consumerKeyField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Consumer Key',
            subFieldLabel: '',
            anchor: '100%',
            name: 'consumerKey',
            emptyText: 'Will be the same like system url',
            readOnly: true
        });

        this.consumerSecretField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            flex: 1,
            margin: '0 5 0 0',
            name: 'consumerSecret',
            emptyText: 'Will be generated automatically',
            readOnly: true
        });

        this.regenerateConsumerSecretButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text : 'Regenerate Consumer Secret',
            height: 28,
            handler: function() {
                var systemUserId = me.editPanel.userBasicInfoFieldSet.idField.getValue();
                var url = OPF.core.utils.RegistryNodeType.SYSTEM_USER.generateUrl('/generate_consumer_secret/' + systemUserId);
                Ext.Ajax.request({
                    url: url,
                    method: 'PUT',
                    jsonData: {},

                    success:function(response, action) {
                        var jsonData = Ext.decode(response.responseText);
                        var data = jsonData.data[0];
                        me.consumerSecretField.setValue(data.consumerSecret);
                    },

                    failure:function(response) {
                        OPF.Msg.setAlert(false, response.message);
                    }
                });
            }
        });

        this.items = [
            this.consumerKeyField,
            {
                xtype: 'label-container',
                fieldLabel: 'Consumer Secret',
                subFieldLabel: '',
                labelCls: '',
                cls: '',
                labelMargin: '0 0 5 0',
                layout: 'hbox',
                items: [
                    this.consumerSecretField,
                    OPF.Ui.xSpacer(10),
                    this.regenerateConsumerSecretButton
                ]
            }
        ];

        this.callParent(arguments);
    }
});