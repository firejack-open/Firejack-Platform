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

Ext.define('OPF.console.security.AvailableRolesStore', {
    extend: 'Ext.data.Store',
    model: OPF.console.security.AssignedRoleModel,
    securityWin: null,

    proxy: {
        type: 'ajax',//'rest'
        method: 'GET',
        url: OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE.generateUrl('/'),
        reader: {
            type: 'json',
            successProperty: 'success',
            idProperty: 'id',
            root: 'data',
            messageProperty: 'message',
            totalProperty: 'total'
        },
        writer: new Ext.data.JsonWriter({ // The new DataWriter component.
            encode: false                 // <-- don't return encoded JSON -- causes Ext.Ajax#request to send data using jsonData config rather than HTTP params
        }),                               // <-- plug a DataWriter into the store just as you would a Reader
        listeners: {
            exception: function(proxy, response, operation, eOpts) {
                OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
            }
        }
    },
    listeners: {
        beforeload: function(store, operation, eOpts) {
            store.proxy.url = OPF.console.security.AssignedRoleModel.getAssignmentRoleUrl(this.securityWin, false);
            store.proxy.method = 'GET';
        },
        update: function(store, model, operation, eOpts) {
            if (operation == Ext.data.Model.EDIT) {
                store.proxy.url = OPF.console.security.AssignedRoleModel.getAssignmentRoleUrl(this.securityWin, true);
                store.proxy.method = 'PUT';
                var assigned = model.get('assigned');
                this.securityWin.saveRoleAssignment(model, assigned);
                //store.load();
            }
        }
    },

    constructor: function(securityWin, cfg) {
        cfg = cfg || {};
        OPF.console.security.AvailableRolesStore.superclass.constructor.call(this, Ext.apply({
            securityWin: securityWin
        }, cfg));
    }
});