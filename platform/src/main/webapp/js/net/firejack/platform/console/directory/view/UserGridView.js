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

Ext.define('OPF.console.directory.view.UserGridView', {
    extend: 'OPF.core.component.BaseGridView',


    title: 'USERS',
    iconCls: 'sm-user-icon',
    iconGridUrl: '/images/icons/16/user_16.png',
    entityName: 'User',
    registryNodeType: OPF.core.utils.RegistryNodeType.USER,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                align: 'center',
                width: 30,
                renderer: function()
                {
                    return '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/user_16.png">';
                }
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'username',
                header: 'Username',
                flex: 2
            },
            {
                xtype: 'gridcolumn',
                header: 'First Name',
                dataIndex: 'firstName',
                flex: 3
            },
            {
                xtype: 'gridcolumn',
                header: 'Last Name',
                dataIndex: 'lastName',
                flex: 3
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'email',
                header: 'E-Mail',
                flex: 2
            }
        ]
    }

});