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

Ext.define('OPF.console.home.view.Dashboard', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.home-dashboard',

    title: 'Dashboard',

    store: 'Dashboards',

    hideHeaders: true,
    multiSelect: false,
    reserveScrollOffset: true,

    columns: [
        {
            xtype: 'templatecolumn',
            flex: 1,
            tpl: '<div><b>New user created: </b>{created:date("F d, Y h:i A")}</div>' +
                 '<div>A user with the username {username} was created...</div>'
        }
    ]

});