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

Ext.define('OPF.core.component.LabelContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.label-container',

    cls: 'fieldset-top-margin',
    labelCls: 'container-label-block',
    labelMargin: '5 0 15 0',

    fieldLabel: null,
    subFieldLabel: null,

    initComponent: function() {
        var instance = this;

        var layout = this.layout;
        this.layout = 'anchor';

        this.label = Ext.ComponentMgr.create({
            xtype: 'container',
            html:
                '<label class="' + this.labelCls + '">' +
                    '<span class="main-label">' + this.fieldLabel + ':</span>' +
                    '<span class="sub-label ">' + this.subFieldLabel + '</span>' +
                '</label>',
            margin: this.labelMargin
        });

        var items = this.items;

        this.items = [
            this.label,
            {
                xtype: 'fieldcontainer',
                layout: layout,
                items: items
            }
        ];

        this.callParent(arguments);
    },

    findById: function(id) {
        var me = this;
        var items = me.items.items.slice();
        var item = null;
        for (var i = 0; i < items.length; i++) {
            if (items[i].id == id || items[i].itemId == id) {
                item = items[i];
                break;
            }
        }
        return item;
    }

});
