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

Ext.define('OPF.console.domain.model.ActivityModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/activity',
        constraintName: 'OPF.process.Activity'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        {
            name: 'name',
            type: 'string',
            fieldType: 'NAME'
        },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'sortPosition', type: 'int' },
        { name: 'activityType', type: 'string' },
        { name: 'actorId', type: 'int' },
        { name: 'actorName', mapping: 'string' },
        { name: 'statusId', mapping: 'int' },
        { name: 'statusName', mapping: 'string' },
        { name: 'notify', type: 'boolean' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'OPF.console.domain.model.ActivityActionModel',
            name: 'activityActions',
            associatedName: 'activityActions',
            associationKey: 'activityActions',
            foreignKey: 'id',
            displayName: 'Activity Actions',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ActorModel',
            name: 'actor',
            associatedName: 'actor',
            associationKey: 'actor',
            foreignKey: 'id',
            displayName: 'Actor',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.StatusModel',
            name: 'status',
            associatedName: 'status',
            associationKey: 'status',
            foreignKey: 'id',
            displayName: 'Status',
            displayDescription: ''
        }
    ]

});