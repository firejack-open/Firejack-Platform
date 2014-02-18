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

Ext.define('OPF.core.component.calendar.model.CalendarEventModel', {
    extend: 'Ext.data.Model',

    fields: [
        {
            name: 'object',
            type: 'auto',
            useNull: false
        },
        {
            name: 'date',
            type: 'string',
            convert: OPF.convertDate,
            useNull: false,
            dateFormat: 'time'
        },
        {
            name: 'startTime',
            type: 'string',
            convert: OPF.convertTime,
            useNull: false,
            dateFormat: 'time'
        },
        {
            name: 'endTime',
            type: 'string',
            convert: OPF.convertTime,
            useNull: false,
            dateFormat: 'time'
        }
    ]

});