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

Ext.define('OPF.console.inbox.model.CaseActionModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/action/case'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'type', type: 'string' },
        { name : 'user' },
        { name: 'userName', type: 'string', useNull: true,
            convert: function(userName, model) {
                var user = model.get('user');
                return OPF.isEmpty(user) ? null : user.username
            }
        },
        { name : 'caseNote'},
        { name: 'noteId', type: 'int', useNull: true,
            convert: function(noteId, model) {
                var note = model.get('caseNote');
                return OPF.isEmpty(note) ? null : note.id
            }
        },
        { name: 'note', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var note = model.get('caseNote');
                return OPF.isEmpty(note) ? null : note.text
            }
        },
        { name: 'caseExplanation' },
        { name: 'explanation', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var explanation = model.get('caseExplanation');
                return OPF.isEmpty(explanation) ? null : explanation.shortDescription
            }
        },
        { name: 'processCase', type: 'auto' },
        { name: 'assignee', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var processCase = model.get('processCase');
                return OPF.isEmpty(processCase) || OPF.isEmpty(processCase.assignee) ? '' : processCase.assignee.username
            }
        },
        { name: 'description', type: 'string' },
        { name: 'performedOn', type: 'date', dateFormat: 'time' }, //'U000'},

        { name: 'created', type: 'date', dateFormat: 'time' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});