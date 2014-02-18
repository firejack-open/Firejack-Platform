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

Ext.define('OPF.console.authority.view.ResourceLocationGridView', {
    extend: 'OPF.core.component.BaseGridView',

    title: 'RESOURCE LOCATIONS',
    iconCls: 'sm-resource-location-icon',
    iconGridUrl: '/images/icons/16/resloc_16.png',
    entityName: 'Resource Location',
    registryNodeType: OPF.core.utils.RegistryNodeType.RESOURCE_LOCATION,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                sortable: true,
                width: 30,
                renderer: function() {
                    return '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + instance.iconGridUrl + '">';
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
                dataIndex: 'urlPath',
                header: 'Url Mask',
                sortable: true,
                width: 400
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'wildcardStyle',
                header: 'Url Style',
                sortable: true,
                width: 100
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
    }
});
