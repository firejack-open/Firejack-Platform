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

Ext.define('OPF.prototype.component.TitlePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf.prototype.component.title-panel',

    margin: 10,
    cls: 'title-panel',
    border: false,

    entityLookup: null,
    titleLookup: null,
    maxTitleLength: null,

    initComponent: function() {

        this.titleLookup = this.titleLookup || this.entityLookup + '.page-title';

        this.items = [
            {
                xtype: 'text-resource-control',
                textResourceLookup: this.titleLookup,
                cls: 'title-panel-title',
                textInnerCls: 'title-panel-title-text',
                maxTextLength: this.maxTitleLength,
                autoInit: true
            }
        ];

        this.callParent(arguments);
    }
});