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

Ext.define('OPF.console.security.AssignedUserStore', {
    extend: 'Ext.data.Store',

    // store configs
    autoSave: false,
    model: OPF.console.directory.model.UserModel,
    autoDestroy: true,
    securityWin: null,
    proxy : {
        type: 'ajax',
        url: OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE.generateUrl('/users/'),
        method: 'GET',
        reader: {
            type: 'json',
            totalProperty: 'total',
            successProperty: 'success',
            idProperty: 'id',
            root: 'data',
            messageProperty: 'message' // <-- New "messageProperty" meta-data
        },
        listeners: {
            exception: function(proxy, type, action, options, response) {
                OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
            }
        }
    },

    constructor: function(securityWin, cfg) {
        cfg = cfg || {};
        OPF.console.security.AssignedUserStore.superclass.constructor.call(this, Ext.apply({
            securityWin: securityWin
        }, cfg));
    },

    listeners: {
        beforeload: function(store, options) {
            var selectedNodeId = this.securityWin.getSelectedNodeId();
            var selectedNodeEntityLookup = this.securityWin.getSelectNodeEntityType();
            store.proxy.url = OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE.generateUrl('/users/') +
                selectedNodeId + '/' + selectedNodeEntityLookup;
            store.proxy.method = 'GET';
        }
    }
});