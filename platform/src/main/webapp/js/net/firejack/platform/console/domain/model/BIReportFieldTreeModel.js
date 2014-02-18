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

Ext.define('OPF.console.domain.model.BIReportFieldTreeModel', {
    extend: 'Ext.data.Model',


    fields: [
        { name: 'id', type: 'int' },
        { name: 'entityId', type: 'int' },
        { name: 'title', type: 'string'},
        { name: 'type', type: 'string'},
        { name: 'expanded', type: 'boolean'},
        { name: 'alwaysExpanded', type: 'boolean'},
        { name: 'enabled', type: 'boolean'},
        { name: 'order_position', type: 'int' },
        { name: 'leaf', type: 'boolean'},
        { name: 'children', useNull: true}
    ]

});