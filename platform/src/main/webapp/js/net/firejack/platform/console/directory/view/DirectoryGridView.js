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

Ext.define('OPF.console.directory.view.DirectoryGridView', {
    extend: 'OPF.core.component.BaseGridView',


    title: 'DIRECTORIES',
    iconCls: 'sm-directory-icon',
    iconGridUrl: '/images/icons/16/directory_16.png',
    entityName: 'Directory',
    registryNodeType: OPF.core.utils.RegistryNodeType.DIRECTORY,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                sortable: true,
                align: 'center',
                width: 30,
                renderer: function() {
                    return '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/directory_16.png">';
                }
            },
            {
                xtype: 'gridcolumn',
                header: 'Name',
                dataIndex: 'name',
                sortable: true,
                width: 200,
                renderer: 'htmlEncode'
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'path',
                header: 'Path',
                sortable: true,
                width: 400
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'directoryServiceTitle',
                header: 'Directory Service',
                sortable: true,
                width: 200
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'description',
                header: 'Description',
                sortable: true,
                flex: 1,
                renderer: 'htmlEncode'
            }
        ]
    },

    getConfigPlugins: function() {
        return {
            ptype: 'gridviewdragdrop',
            dragText: 'Drag and drop to reorganize'
        }
    },

    getConfigPluginsListeners: function() {
        var instance = this;
        return {
            drop: function(node, data, overModel, dropPosition, eOpts) {
                var newPosition = instance.store.findExact('id', data.item.viewRecordId);
                instance.refreshOrderPositions(data.item.viewRecordId, newPosition + 1);
            }
        };
    },

    storeBeforeLoad: function(store, operation) {
        store.proxy.url = this.registryNodeType.generateUrl() + '/ordered';
    },

    refreshOrderPositions: function(directoryId, newPos) {
        var records = this.store.getRange();
        if (records != null && records.length != 0) {
            //var instance = this;
            var url = this.registryNodeType.generateUrl() + '/ordered/' + directoryId + '/' + newPos;
            Ext.Ajax.request({
                url: url,
                method: 'PUT',

                success:function(response, action) {
                    var vo = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(true, vo.message);
                },

                failure:function(response) {
                    OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                }
            });
            var lastIndex = records.length - 1;
            Ext.each(records, function(rec, index) {
                rec.set('sortPosition', index + 1);
            });
        }
    }

});