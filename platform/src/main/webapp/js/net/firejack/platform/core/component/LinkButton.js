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

Ext.define('OPF.core.component.LinkButton', {
    extend: 'Ext.Component',
    alias: 'widget.linkbutton',

    autoEl: 'a',
    renderTpl: '<a href=\"javascript:;\" id="{id}-btnEl">{text}</a>',

    config: {
        text: '',
        handler: function () { }
    },

    initComponent: function () {
        var me = this;
        me.callParent(arguments);

        this.renderData = {
            text: this.getText()
        };
    },

    onRender: function(ct, position) {
        var me = this,
            btn;

        me.addChildEls('btnEl');

        me.callParent(arguments);

        btn = me.btnEl;

        me.mon(btn, 'click', me.onClick, me);
    },

    onClick: function(e) {
        var me = this;
        if (me.preventDefault || (me.disabled && me.getHref()) && e) {
            e.preventDefault();
        }
        if (e.button !== 0) {
            return;
        }
        if (!me.disabled) {
            me.fireHandler(e);
        }
    },

    fireHandler: function(e){
        var me = this,
            handler = me.handler;

        me.fireEvent('click', me, e);
        if (handler) {
            handler.call(me.scope || me, me, e);
        }
    }

});