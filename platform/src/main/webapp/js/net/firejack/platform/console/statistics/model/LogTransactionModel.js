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

Ext.define('OPF.console.statistics.model.LogTransactionModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: '/console/tracking',
        restSuffixUrl: '/statistic/log-transaction'
    },

    idProperty: 'id',

    fields: [
        { name: 'packageLookup', type: 'string' },

        { name: 'transactions', type: 'int' },

        { name: 'entitiesLoaded', type: 'int' },
        { name: 'entitiesUpdated', type: 'int' },
        { name: 'entitiesInserted', type: 'int' },
        { name: 'entitiesDeleted', type: 'int' },
        { name: 'entitiesFetched', type: 'int' },

        { name: 'collectionsLoaded', type: 'int' },
        { name: 'collectionsUpdated', type: 'int' },
        { name: 'collectionsRecreated', type: 'int' },
        { name: 'collectionsRemoved', type: 'int' },
        { name: 'collectionsFetched', type: 'int' },

        { name: 'maxQueryTime', type: 'int' },

        { name: 'hourPeriod', type: 'int' },
        { name: 'dayPeriod', type: 'int' },
        { name: 'weekPeriod', type: 'int' },
        { name: 'monthPeriod', type: 'int' },
        { name: 'startTime', type: 'date', dateFormat: 'time' },
        { name: 'endTime', type: 'date', dateFormat: 'time' },
        { name: 'created', type: 'date', dateFormat: 'time' }
    ]

});