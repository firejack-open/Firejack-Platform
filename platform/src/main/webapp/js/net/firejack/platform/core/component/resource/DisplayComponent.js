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


Ext.define('OPF.core.component.resource.DisplayComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.display-component',

    maxTextLength: null,

    fullHtmlOrData: null,

    getHtmlOrData: function() {
        return this.fullHtmlOrData;
    },

    update: function(htmlOrData, loadScripts, cb) {
        var me = this;

        this.fullHtmlOrData = htmlOrData;

        if (OPF.isNotEmpty(this.maxTextLength)) {
            htmlOrData = OPF.cutting(htmlOrData, this.maxTextLength);
        }

        if (me.tpl && !Ext.isString(htmlOrData)) {
            me.data = htmlOrData;
            if (me.rendered) {
                me.tpl[me.tplWriteMode](me.getTargetEl(), htmlOrData || {});
            }
        } else {
            me.html = Ext.isObject(htmlOrData) ? Ext.DomHelper.markup(htmlOrData) : htmlOrData;
            if (me.rendered) {
                me.getTargetEl().update(me.html, loadScripts, cb);
            }
        }

        if (me.rendered) {
            me.doComponentLayout();
        }
    },

    listeners: {
        afterrender: function(me) {
            Ext.create('Ext.tip.ToolTip', {
                target: me.getId(),
                html: 'Loading...',
                dismissDelay: 0,
                listeners: {
                    beforeshow: function(tip) {
                        tip.update(me.fullHtmlOrData);
                    }
                }
            });
        }
    }

});

