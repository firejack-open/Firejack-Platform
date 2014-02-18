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

Ext.define('OPF.console.domain.store.StandardEntities', {
    extend: 'Ext.data.Store',
    restful: false,
    //remoteSort: true,

    constructor: function(cfg) {
        cfg = cfg || {};
        OPF.console.domain.store.StandardEntities.superclass.constructor.call(this, Ext.apply({
            model: 'OPF.console.domain.model.EntityModel',
            proxy: {
                type: 'ajax',
                url : OPF.core.utils.RegistryNodeType.ENTITY.generateUrl('/standard'),
                method: 'GET',
                simpleSortMode: true,
                reader: {
                    type: 'json',
                    idProperty: 'id',
                    root: 'data',

                    totalProperty: 'total',
                    successProperty: 'success',
                    messageProperty: 'message'
                },
                /*writer: {
                    type: 'json'
                }*/
                listeners: {
                    beforeload: {
                        fn: function(store, options) {
                            var instance = OPF.Ui.getCmp('entity-editor');
                            var params = isNotBlank(instance.nodeBasicFields.idField.getValue()) ?
                                '?exceptId=' + instance.nodeBasicFields.idField.getValue() : '';
                            store.proxy.url = OPF.core.utils.RegistryNodeType.ENTITY.generateUrl('/standard') + params;
                        }
                    }
                }
            }
        }, cfg));
    }
});