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

/**
 *
 */
Ext.define('OPF.console.resource.view.ResourceGridView', {
    extend: 'OPF.core.component.BaseGridView',

    title: 'RESOURCES',
    iconCls: 'sm-resource-icon',
    iconGridUrl: '/images/icons/16/resource_16.png',
    entityName: 'Resource',
    registryNodeType: OPF.core.utils.RegistryNodeType.RESOURCE,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                sortable: true,
                width: 30,
                renderer: function(value, metaData, record) {
                    var type = record.get('type');
                    var pos = type.indexOf('_');
                    type = type.substr(0, pos).toLowerCase();
                    return '<img src="' + OPF.Cfg.fullUrl('/images/icons/16/') + type + '_16.png">';
                }
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'name',
                header: 'Name',
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
                dataIndex: 'lastVersion',
                header: 'Last Version',
                sortable: true,
                width: 100,
                id: 'lastVersion'
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'selectedVersion',
                header: 'Selected Version',
                sortable: true,
                width: 100,
                id: 'selectedVersion'
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'description',
                header: 'Description',
                sortable: true,
                flex: 1,
                renderer: 'htmlEncode'
            }
        ];
    },

    onItemDblClick: function(grid, record) {
        var instance = this;

        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(record.get('type'));
        var url = registryNodeType.generateGetUrl(record.get('id'));

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {

                var editPanel = instance.managerLayout.tabPanel.getComponent(registryNodeType.type + 'EditPanel');
                if (OPF.isNotEmpty(editPanel)) {
                    if (editPanel.entityId == record.get('id')) {
                        instance.managerLayout.tabPanel.setActiveTab(editPanel);
                        return;
                    } else {
                        instance.managerLayout.tabPanel.remove(editPanel);
                    }
                }

                editPanel = registryNodeType.createEditPanel(instance.managerLayout);
                editPanel.saveAs = 'update';

                var registryJsonData = Ext.decode(response.responseText);
                editPanel.showEditPanel(registryJsonData.data[0]);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    generateDeleteUrl: function(record) {
        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(record.get('type'));
        return registryNodeType.generateGetUrl(record.get('id'));
    }

});
