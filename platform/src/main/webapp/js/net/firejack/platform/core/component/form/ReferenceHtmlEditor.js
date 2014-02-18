/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
        subLabelable: 'OPF.core.component.form.SubLabelable'
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
                    var imageData = OPF.textToImage(fieldName, '21px Tahoma');

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

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
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
                var imageData = OPF.textToImage(fieldName, '21px Tahoma');
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