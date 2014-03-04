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

Ext.define('OPF.console.domain.view.ReferenceHtmlEditor', {
    extend: 'Ext.form.field.HtmlEditor',
    alias: 'widget.opf-reference-htmleditor',

    height: 28,
    cls: 'opf-reference-htmleditor',

    enableFormat : false,
    enableFontSize : false,
    enableColors : false,
    enableAlignments : false,
    enableLists : false,
    enableSourceEdit : false,
    enableLinks : false,
    enableFont : false,
    enableKeyEvents: true,

    keyUpFn: Ext.emptyFn(),

    mixins: {
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    listeners: {
        afterrender: function(editor) {
            var formPanelDropTargetEl = editor.iframeEl.dom;

            var formPanelDropTarget = Ext.create('Ext.dd.DropTarget', formPanelDropTargetEl, {
                ddGroup: 'fieldGridDDGroup',
                notifyEnter: function(ddSource, e, data) {
                    editor.iframeEl.stopAnimation();
                    editor.iframeEl.highlight();
                },
                notifyDrop  : function(ddSource, e, data) {

                    var record = data.records[0];

                    var fieldId = record.data.id;
                    var fieldName = record.data.name;
                    var imageData = OPF.textToImage(fieldName);

                    var fieldData = {
                        fieldId: fieldId,
                        fieldName: fieldName
                    };
                    var fieldDataStr = Ext.JSON.encode(fieldData).replace(/"/g, '\'');

                    editor.activated = true;
                    editor.insertAtCursor('<img ' +
                            'src="' + imageData.base64Src + '" ' +
                            'width="' + imageData.width + '" ' +
                            'height="' + imageData.height + '" ' +
                            'style="vertical-align: middle;" ' +
                            'data="' + fieldDataStr + '"' +
                        '/>');
                    editor.keyUpFn();

                    return true;
                }
            });
        }
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.listeners = cfg.listeners || {};
        this.keyUpFn = cfg.listeners.keyup;
        cfg.listeners = Ext.apply(cfg.listeners, cfg.listeners, this.listeners);
        this.superclass.constructor.call(this, cfg);
    },

    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('subLabelableRenderTpl');
        }
        return me.callParent();
    },

    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getSubLabelableRenderData());
    },

    initEditor : function() {
        this.callParent(arguments);

        var me = this;
        var doc = me.getDoc();

        if (Ext.isGecko) {
            Ext.EventManager.on(doc, 'keyup', me.keyUpFn, me);
        }

        if (Ext.isIE || Ext.isWebKit || Ext.isOpera) {
            Ext.EventManager.on(doc, 'keyup', me.keyUpFn, me);
        }
    },

    getHtmlValue: function() {
        var me = this,
            value;
        if (!me.sourceEditMode) {
            me.syncValue();
        }
        value = me.rendered ? me.textareaEl.dom.value : me.value;
        me.value = value;
        return value;
    },

    getValue : function() {
        var value = this.callParent(arguments);
        if (OPF.isNotBlank(value)) {
            value = value.replace(/\u200B/, '');
            var re = /(<img)[^<>]*?src=[^<>]*?(data="\{[^{}]*?\}")[^<>]*?(>)/g;
            while(value.search(re) != -1) {
                value = value.replace(re, '$1 $2 $3');
            }
            value = value.replace(/<\/?br\/?>/g, '');
        }
        return value;
    },

    setValue: function(value) {
        if (OPF.isNotBlank(value)) {
            var re = /<img[^<>"]*?data="(\{[^{}]*?\})"[^<>]*?>/i;
            while(value.search(re) != -1) {
                var matchValues = value.match(re);
                var imgTag = matchValues[0];
                var imgTagData = matchValues[1];
                var fieldName = imgTagData.match(/fieldname['"]\s*:\s*['"]([^"]*?)['"]/i)[1];
                var imageData = OPF.textToImage(fieldName);
                var imageHtml =
                    '<img ' +
                        'src="' + imageData.base64Src + '" ' +
                        'width="' + imageData.width + '" ' +
                        'height="' + imageData.height + '" ' +
                        'style="vertical-align: middle;" ' +
                        'data="' + imgTagData + '"' +
                    '/>';
                value = value.replace(imgTag, imageHtml);
            }
        }
        return this.callParent(arguments);
    }

});