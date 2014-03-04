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

Ext.define('OPF.prometheus.component.manager.layout.WrapperFormLayout', {
    extend: 'OPF.prometheus.component.manager.layout.AutoFormLayout',
    alias: 'widget.prometheus.component.manager.layout.default-form-layout',

    layout: 'auto',
    fields: [],

    initComponent: function() {

        var wrapperContainer = Ext.create('Ext.container.Container', Ext.apply({
            xtype: 'container',
            items: this.fields
        }, this.config));

        this.items = [
            wrapperContainer
        ];

        this.callParent(arguments);

    }

});