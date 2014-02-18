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
Ext.define('OPF.console.utils.GridUtils', {});

OPF.console.utils.GridUtils.populateColumn = function(dataIndex, header, cfg) {
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        dataIndex: dataIndex,
        header: header,
        sortable: true
    }, cfg));
};

OPF.console.utils.GridUtils.populateNumberColumn = function(dataIndex, header, width, cfg) {
    return Ext.create('Ext.grid.column.Number', Ext.apply({
        dataIndex: dataIndex,
        header: header,
        sortable: true,
        width: width
    }, cfg));
};

OPF.console.utils.GridUtils.populateBooleanColumn = function(dataIndex, header, cfg) {
    return Ext.create('Ext.grid.column.Boolean', Ext.apply({
        dataIndex: dataIndex,
        header: header,
        sortable: true
    }, cfg));
};

OPF.console.utils.GridUtils.populateDateColumn = function(dataIndex, header, width) {
    return Ext.create('Ext.grid.column.Date', {
        dataIndex: dataIndex,
        header: header,
        sortable: true,
        width: width,
        format: 'M j, Y g:i A'
    });
};