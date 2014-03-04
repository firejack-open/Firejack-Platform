/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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