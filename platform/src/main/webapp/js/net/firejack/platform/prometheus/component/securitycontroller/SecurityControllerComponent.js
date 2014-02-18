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

Ext.define('OPF.prometheus.component.securitycontroller.SecurityControllerComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.security-controller-component',

    //cls: 'title-panel',
    //margin: '0 0 10 0',

    currentModelLookup: null,
    model: null,
    managerPanel: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;
        this.turnOnInstantSecurity = Ext.create('Ext.form.field.Checkbox', {
            name: 'allowSecurity',
            cls: 'opf-check-box',
            fieldLabel: 'Enable Sharing',
            labelAlign: 'right',
            labelWidth: 150,
            listeners: {
                change: function(chbox, newVal, oldVal) {
                    var securityEnabled = newVal == 'true' || newVal == 'on' || newVal == '1' || newVal;
                    Ext.Ajax.request({
                        url: OPF.Cfg.restUrl('/authority/role/enable-instant-security-on-package?entityLookup=' +
                            me.currentModelLookup + '&securityEnabled=' + securityEnabled),
                        method: 'PUT',
                        success: function(response, action) {
                            var resp = Ext.decode(response.responseText);
                            Ext.Msg.alert(resp.success ? 'Info' : 'Error', resp.message);
                            me.managerPanel.securityEnabled = securityEnabled;
                        },

                        failure: function(response) {
                            var responseStatus = Ext.decode(response.responseText);
                            var messages = [];
                            for (var i = 0; i < responseStatus.data.length; i++) {
                                var msg = responseStatus.data[i].msg;
                                messages.push(msg);
                            }
                            Ext.Msg.alert('Error', messages.join('<br/>'));
                        }
                    });
                }
            }
        });
        this.items = [
            this.turnOnInstantSecurity
        ];

        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(cnt) {
            var modelInst = Ext.create(this.model);
            var me = this;
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl("registry/entity/security-enabled-by-lookup?entityLookup=" + modelInst.self.lookup),
                method: 'GET',
                success: function(response) {
                    var json = Ext.decode(response.responseText);
                    var securityEnabled = json.success && json.data.length > 0 && json.data[0].identifier;
                    cnt.turnOnInstantSecurity.suspendEvents();
                    cnt.turnOnInstantSecurity.setValue(securityEnabled);
                    cnt.turnOnInstantSecurity.resumeEvents();
                    me.managerPanel.securityEnabled = securityEnabled;
                },
                failure: function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
            var isPackageAdmin = OPF.Cfg.USER_INFO.isPackageAdmin;
            if (isPackageAdmin) {
                cnt.show();
            } else {
                cnt.hide();
            }
        }
    }

});