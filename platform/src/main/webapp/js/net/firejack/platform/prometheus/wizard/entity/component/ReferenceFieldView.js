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

Ext.define('OPF.prometheus.wizard.entity.component.ReferenceFieldView', {
    extend: 'Ext.view.View',
    alias : 'widget.prometheus.wizard.entity.reference-field-view',

    mixins: {
        dragSelector: 'Ext.ux.DataView.DragSelector',
        draggable   : 'Ext.ux.DataView.Draggable'
    },

    tpl: [
        '<tpl for=".">',
            '<li class="x-boxselect-item">{name}</li>',
        '</tpl>'
    ],

    itemSelector: 'li.x-boxselect-item',
    singleSelect: true,
    autoScroll: true,

    initComponent: function() {
        this.mixins.dragSelector.init(this);
        this.mixins.draggable.init(this, {
            ddConfig: {
                ddGroup: 'fieldGridDDGroup'
            },
            ghostTpl: [
                '<li class="x-boxselect-item">{name}</li>'
            ]
        });

        this.callParent();
    }
});