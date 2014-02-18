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

/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.MidasCommand
 * @extends Ext.util.Observable
 * <p>A base plugin for extending to create standard Midas command buttons.</p>
 * http://msdn.microsoft.com/en-us/library/ms533049%28v=VS.85%29.aspx
 * http://www.mozilla.org/editor/midas-spec.html
 */
Ext.ns('Ext.ux.form.HtmlEditor');

if (!Ext.isObject) {
    Ext.isObject = function(v){
        return v && typeof v == "object";
    };
}

Ext.override(Ext.form.HtmlEditor, {
    getSelectedText: function(clip){
        var doc = this.getDoc(), selDocFrag;
        var txt = '', hasHTML = false, selNodes = [], ret, html = '';
        if (this.win.getSelection || doc.getSelection) {
            // FF, Chrome, Safari
            var sel = this.win.getSelection();
            if (!sel) {
                sel = doc.getSelection();
            }
            var range;
            if (clip) {
                range = sel.getRangeAt(0);
                selDocFrag = range.extractContents();
            } else {
                range = this.win.getSelection().getRangeAt(0);
                selDocFrag = range.cloneContents();
            }
            Ext.each(selDocFrag.childNodes, function(n){
                if (n.nodeType !== 3) {
                    hasHTML = true;
                }
            });
            if (hasHTML) {
                var div = document.createElement('div');
                div.appendChild(selDocFrag);
                html = div.innerHTML;
                txt = this.win.getSelection() + '';
            } else {
                html = txt = selDocFrag.textContent;
            }
            ret = {
                textContent: txt,
                hasHTML: hasHTML,
                html: html,
                range: range,
                anchorNode: sel.anchorNode,
                startOffset: range.startOffset,
                endOffset: range.endOffset
            };
        } else if (doc.selection) {
            // IE
            this.win.focus();
            txt = doc.selection.createRange();
            var range = doc.selection.getRangeAt(0);
            if (txt.text !== txt.htmlText) {
                hasHTML = true;
            }
            ret = {
                textContent: txt.text,
                hasHTML: hasHTML,
                html: txt.htmlText,
                anchorNode: sel.anchorNode,
                startOffset: range.startOffset,
                endOffset: range.endOffset
            };
        } else {
            return {
                textContent: ''
            };
        }
        
        return ret;
    }
});

Ext.define('Ext.ux.form.HtmlEditor.MidasCommand', {
    extend: 'Ext.util.Observable',

    // private
    init: function(cmp){
        this.cmp = cmp;
        this.btns = [];
        this.cmp.on('render', this.onRender, this);
        this.cmp.on('initialize', this.onInit, this, {
            delay: 100,
            single: true
        });
    },
    // private
    onInit: function(){
        Ext.EventManager.on(this.cmp.getDoc(), {
            'mousedown': this.onEditorEvent,
            'dblclick': this.onEditorEvent,
            'click': this.onEditorEvent,
            'keyup': this.onEditorEvent,
            buffer: 100,
            scope: this
        });
    },
    // private
    onRender: function(){
        var midasCmdButton, tb = this.cmp.getToolbar(), btn, iconCls;
        Ext.each(this.midasBtns, function(b){
            if (Ext.isObject(b)) {
                iconCls = (b.iconCls) ? b.iconCls : 'x-edit-' + b.cmd;
                if (b.value) { iconCls = iconCls+'-'+b.value.replace(/[<>\/]/g,''); }
                midasCmdButton = {
                    iconCls: iconCls,
                    handler: function(){
                        this.cmp.relayCmd(b.cmd, b.value);
                    },
                    scope: this,
                    tooltip: b.tooltip ||
                    {
                        title: b.title
                    },
                    overflowText: b.overflowText || b.title
                };
            } else {
                midasCmdButton = new Ext.Toolbar.Separator();
            }
            btn = tb.addButton(midasCmdButton);
            if (b.enableOnSelection) {
                btn.disable();
            }
            this.btns.push(btn);
        }, this);
    },
    // private
    onEditorEvent: function(){
        var doc = this.cmp.getDoc();
        Ext.each(this.btns, function(b, i){
            if (this.midasBtns[i].enableOnSelection || this.midasBtns[i].disableOnSelection) {
                if (doc.getSelection) {
                    if ((this.midasBtns[i].enableOnSelection && doc.getSelection() !== '') || (this.midasBtns[i].disableOnSelection && doc.getSelection() === '')) {
                        b.enable();
                    } else {
                        b.disable();
                    }
                } else if (doc.selection) {
                    if ((this.midasBtns[i].enableOnSelection && doc.selection.createRange().text !== '') || (this.midasBtns[i].disableOnSelection && doc.selection.createRange().text === '')) {
                        b.enable();
                    } else {
                        b.disable();
                    }
                }
            }
            if (this.midasBtns[i].monitorCmdState) {
                b.toggle(doc.queryCommandState(this.midasBtns[i].cmd));
            }
        }, this);
    }
});
