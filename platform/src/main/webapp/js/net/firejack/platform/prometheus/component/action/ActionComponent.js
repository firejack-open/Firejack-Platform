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

Ext.define('OPF.prometheus.component.action.ActionComponent', {
    extend: 'OPF.prometheus.component.content.ContentComponent',
    alias: 'widget.prometheus.component.action-component',

    cls: 'action-panel content-panel',

    actionLookup: null,
    actions: [],

    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        OPF.prometheus.component.action.ActionComponent.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        if (OPF.isNotEmpty(this.actions)) {
            var prefixUrl = OPF.ifBlank(OPF.Cfg.DASHBOARD_PREFIX_URL, '');

            var actionButton = Ext.create('Ext.Button', Ext.apply({
                text: 'Edit',
                cls: 'edit-btn',
                listeners: {
                    click: function() {
                        document.location = OPF.generateUrlByLookup((me.actionLookup || me.entityLookup), prefixUrl);
                    }
                }
            }, this.configs.editButtonConfigs));

            this.additionalComponents = [actionButton];
        }

        this.callParent(arguments);
    }
});