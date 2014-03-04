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

Ext.require([
    'OPF.core.component.calendar.model.CalendarEventModel'
]);

Ext.define('OPF.core.component.calendar.store.CalendarStore', {
    extend: 'Ext.data.Store',
    model: 'OPF.core.component.calendar.model.CalendarEventModel',

    proxy: {
        type: 'ajax',
        reader: {
            type: 'json',
            root: 'data'
        },
        writer: {
            type: 'json'
        }
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.listeners = cfg.listeners || {};
        cfg.listeners = Ext.apply(cfg.listeners, cfg.listeners, this.listeners);

        this.addEvents('preload');

        this.superclass.constructor.call(this, cfg);
    },

    load: function(options) {
        var result = this.callParent(arguments);
        if (this.loading) {
            this.fireEvent('preload', this);
        }
        return result;
    }

});