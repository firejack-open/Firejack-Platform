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

/**
 * @author Oleg Marshalenko
 * @class OPF.core.component.htmleditor.plugins.SpecialHeadingFormat
 * @extends Ext.util.Observable
 * <p>A plugin that creates a menu on the HtmlEditor for selecting a special heading formats.</p>
 */

Ext.define('OPF.core.component.htmleditor.plugins.SpecialHeadingFormat', {
    extend: 'Ext.util.Observable',

    formatInstructions: [
        {
            id: 'normal',
            labelName: 'Normal',
            tagName: null,
            className: null
        },
        {
            id: 'h1',
            labelName: 'H1',
            tagName: 'H1',
            className: null
        },
        {
            id: 'h2',
            labelName: 'H2',
            tagName: 'H2',
            className: null
        },
        {
            id: 'h3',
            labelName: 'H3',
            tagName: 'H3',
            className: null
        },
        {
            id: 'quote',
            labelName: 'Quote',
            tagName: 'DIV',
            className: 'quote'
        },
        {
            id: 'code',
            labelName: 'Code',
            tagName: 'DIV',
            className: 'code'
        },
        {
            id: 'warning',
            labelName: 'Warning',
            tagName: 'SPAN',
            className: 'warn'
        },
        {
            id: 'important',
            labelName: 'Important',
            tagName: 'SPAN',
            className: 'important'
        },
        {
            id: 'critical',
            labelName: 'Critical',
            tagName: 'SPAN',
            className: 'critical'
        },
        {
            id: 'subdued',
            labelName: 'Subdued',
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
        var btn = this.cmp.getToolbar().addItem({
            xtype: 'combo',
            displayField: 'display',
            valueField: 'value',
            name: 'headingsize',
            forceSelection: false,
            mode: 'local',
            selectOnFocus: false,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            width: 65,
            emptyText: 'Heading',
            store: {
                xtype: 'arraystore',
                autoDestroy: true,
                fields: ['value','display'],
                data: [
                    ['normal','Normal'],
                    ['h1','H1'],
                    ['h2','H2'],
                    ['h3','H3'],
                    ['quote','Quote'],
                    ['code','Code'],
                    ['warning','Warning'],
                    ['important','Important'],
                    ['critical','Critical'],
                    ['subdued','Subdued']
                ]
            },
            listeners: {
                'select': function(combo, rec) {
                    instance.applyFormatStyle(rec.get('value'));
                    combo.reset();
                },
                scope: cmp
            }
        });
    },

    applyFormatStyle: function(instructionId) {
        var formatInstruction = null;
        Ext.each(this.formatInstructions, function(instruction, index) {
            if (instruction.id == instructionId) {
                formatInstruction = instruction;    
            }
        });

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

            if (formatInstruction.id == 'normal') {
                range.deleteContents();
                range.insertNode(this.cmp.getDoc().createTextNode(txt));
            } else {
//                var node = document.createElement(formatInstruction.tagName);
                var node = this.cmp.getDoc().createElement(formatInstruction.tagName);
                node.className = formatInstruction.className;
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
            if (formatInstruction.id == 'normal') {
                range.pasteHTML(this.cmp.getDoc().selection);
            } else {
                var openTag =
                        '<' + formatInstruction.tagName +
                        (formatInstruction.className != null ? ' class="' + formatInstruction.className + '"' : '') + '>';
                var closeTag = '</' + formatInstruction.tagName + '>';
                range.pasteHTML(openTag + range.text + closeTag);
            }
        }
    }
});