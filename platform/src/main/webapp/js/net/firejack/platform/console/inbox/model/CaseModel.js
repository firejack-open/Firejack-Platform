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

var staticFields = [
    { name: 'id', type: 'int', useNull: true },
    { name: 'description', type: 'string', flex: 1 },
    { name: 'process' },
    { name: 'status' },
    { name: 'assignee' },
    { name: 'processName', type: 'string',
        convert: function(action, model) {
            return model.get('process').name;
        }
    },
    { name: 'assigneeName', type: 'string',
        convert: function(action, model) {
            return model.get('assignee').username;
        }
    },
    { name: 'statusName', type: 'string',
        convert: function(action, model) {
            return model.get('status').name;
        }
    },
    { name: 'data', type: 'string' },
    { name: 'active', type: 'boolean', useNull: true },
    { name: 'startDate', type: 'date', dateFormat: 'time' },
    { name: 'updateDate', type: 'date', dateFormat: 'time' },
    { name: 'completeDate', type: 'date', dateFormat: 'time' },
    { name: 'hasPreviousTask', type: 'boolean' },
    { name: 'userCanPerform', type: 'boolean' },

    { name: 'created', type: 'date', dateFormat: 'time' },
    { name: 'canUpdate', type: 'boolean' },
    { name: 'canDelete', type: 'boolean' }
];

Ext.define('OPF.console.inbox.model.CaseModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/case'
    },
    idProperty: 'id',
    fields: staticFields
});

OPF.console.inbox.model.CaseModel.configuredFields = staticFields;