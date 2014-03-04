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

Ext.define('OPF.prometheus.component.content.ContentComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.content-component',

    margin: '5 10 5 0',
    cls: 'content-panel',
    border: true,

    entityLookup: null,
    titleLookup: null,
    contentLookup: null,
    maxTitleLength: null,
    maxContentLength: null,

    additionalComponents: null,

    initComponent: function() {
        var me = this;

        this.titleLookup = this.titleLookup || this.entityLookup + '.panel-title';
        this.contentLookup = this.contentLookup || this.entityLookup + '.panel-text';

        this.titleControl = Ext.ComponentMgr.create({
            xtype: 'text-resource-control',
            textResourceLookup: this.titleLookup,
            cls: 'content-panel-title',
            textInnerCls: 'content-panel-title-text',
            maxTextLength: this.maxTitleLength
        });

        this.contentControl = Ext.ComponentMgr.create({
            xtype: 'text-resource-control',
            textResourceLookup: this.contentLookup,
            cls: 'content-panel-content',
            textInnerCls: 'content-panel-content-text',
            maxTextLength: this.maxContentLength
        });

        this.items = [
            this.titleControl,
            this.contentControl
        ];

        if (Ext.isArray(this.additionalComponents)) {
            Ext.each(this.additionalComponents, function(additionalComponent) {
                me.items.push(additionalComponent);
            });
        }

        this.callParent(arguments);
    }
});