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

Ext.define('OPF.console.domain.model.RelationshipModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/relationship',
        editorClassName: 'OPF.console.domain.view.RelationshipEditor',
        constraintName: 'OPF.registry.Relationship'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'type', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'relationshipType', type: 'string' },
        { name: 'relationshipTypeName', type: 'string' },
        { name: 'sourceEntityId', type: 'int' },
        { name: 'sourceEntityName', type: 'string' },
        { name: 'targetEntityId', type: 'int', useNull: true },
        { name: 'targetEntityName', type: 'string', useNull: true },
        { name: 'required', type: 'boolean' },


        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],
    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.EntityModel',
            name: 'targetEntity',
            associatedName: 'targetEntity',
            associationKey: 'targetEntity',
            foreignKey: 'id'
        }
    ]
});