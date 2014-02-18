/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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
 * @author Oleg Marshalenko
 * @class OPF.core.component.htmleditor.plugins.SpecialHeadingFormats
 * @extends Ext.util.Observable
 * <p>A plugin that creates a menu on the HtmlEditor for selecting a special heading formats.</p>
 */

Ext.define('OPF.core.component.htmleditor.plugins.SpecialHeadingFormats', {
    extend: 'Ext.util.Observable',

    formatInstructions: [
        {
            id: 'normal',
            labelName: 'Normal',
            shortName: 'nor',
            tagName: null,
            className: null
        },
        {
            id: 'h1',
            labelName: 'H1',
            shortName: 'h1',
            tagName: 'H1',
            className: null
        },
        {
            id: 'h2',
            labelName: 'H2',
            shortName: 'h2',
            tagName: 'H2',
            className: null
        },
        {
            id: 'h3',
            labelName: 'H3',
            shortName: 'h3',
            tagName: 'H3',
            className: null
        },
        {
            id: 'quote',
            labelName: 'Quote',
            shortName: 'qot',
            tagName: 'DIV',
            className: 'quote'
        },
        {
            id: 'code',
            labelName: 'Code',
            shortName: 'cod',
            tagName: 'DIV',
            className: 'code'
        },
        {
            id: 'warning',
            labelName: 'Warning',
            shortName: 'wrn',
            tagName: 'SPAN',
            className: 'warn'
        },
        {
            id: 'important',
            labelName: 'Important',
            shortName: 'imp',
            tagName: 'SPAN',
            className: 'important'
        },
        {
            id: 'critical',
            labelName: 'Critical',
            shortName: 'crt',
            tagName: 'SPAN',
            className: 'critical'
        },
        {
            id: 'subdued',
            labelName: 'Subdued',
            shortName: 'sub',
            tagName: 'SPAN',
            className: 'subdued'
        }
    ],

    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },

    // private
    onRender: function(){
        var instance = this;
        var cmp = this.cmp;
        Ext.each(this.formatInstructions, function(instruction) {
            instance.cmp.getToolbar().add(
                {
                    xtype: 'button',
                    iconCls: 'editor-without-image',
    //                iconCls: 'editor-' + instruction.id,
                    tooltip: instruction.labelName,
                    text: instruction.shortName,
                    handler: function(combo, rec) {
                        instance.applyFormatStyle(instruction);
                    },
                    scope: cmp
                }
            )
        })
    },

    applyFormatStyle: function(instruction) {
        if (this.cmp.win.getSelection || this.cmp.getDoc().getSelection) {
            // FF, Chrome, Safari
            OPF.Msg.setAlert(true, 'FF');
            var sel = this.cmp.win.getSelection();
            if (!sel) {
                sel = this.cmp.getDoc().getSelection();
            }
            var range = this.cmp.win.getSelection().getRangeAt(0);
            var selDocFrag = range.cloneContents();
            var txt, hasHTML = false;
            Ext.each(selDocFrag.childNodes, function(n){
                if (n.nodeType !== 3) {
                    hasHTML = true;
                }
            });
            if (hasHTML) {
                txt = this.cmp.win.getSelection() + '';
            } else {
                txt = selDocFrag.textContent;
            }

            if (instruction.id == 'normal') {
                range.deleteContents();
                range.insertNode(this.cmp.getDoc().createTextNode(txt));
            } else {
//                var node = document.createElement(instruction.tagName);
                var node = this.cmp.getDoc().createElement(instruction.tagName);
                node.className = instruction.className;
                node.innerHTML = range;
                range.deleteContents();
                range.insertNode(node);
            }
        } else if (this.cmp.getDoc().selection) {
            // IE
            OPF.Msg.setAlert(true, 'IE');
            this.cmp.win.focus();
            range = this.cmp.getDoc().selection.createRange();
            OPF.Msg.setAlert(true, range.text);
            if (instruction.id == 'normal') {
                range.pasteHTML(this.cmp.getDoc().selection);
            } else {
                var openTag =
                        '<' + instruction.tagName +
                        (instruction.className != null ? ' class="' + instruction.className + '"' : '') + '>';
                var closeTag = '</' + instruction.tagName + '>';
                range.pasteHTML(openTag + range.text + closeTag);
            }
        }
    }
});