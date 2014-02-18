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

Ext.define('OPF.console.domain.model.ReportFieldModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/report',
        constraintName: 'OPF.registry.ReportField'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'displayName', type: 'string' },
//        { name: 'function', type: 'string' },

        { name: 'visible', type: 'boolean' },
        { name: 'searchable', type: 'boolean' },

        { name: 'relationships', type: 'auto' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ReportModel',
            name: 'report',
            associatedName: 'report',
            associatedKey: 'report',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.FieldModel',
            name: 'field',
            associatedName: 'field',
            associatedKey: 'field',
            foreignKey: 'id'
        },
        {
            type: 'hasMany',
            model: 'OPF.console.domain.model.RelationshipModel',
            name: 'relationships',
            associatedName: 'relationships',
            associatedKey: 'relationships',
            foreignKey: 'id'
        }
    ]

});