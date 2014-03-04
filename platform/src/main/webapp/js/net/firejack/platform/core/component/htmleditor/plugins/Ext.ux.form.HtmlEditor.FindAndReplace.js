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
 * @author Shea Frederick - http://www.vinylfox.com
 * @contributor Ronald van Raaphorst - Twensoc
 * @class Ext.ux.form.HtmlEditor.FindReplace
 * @extends Ext.util.Observable
 * <p>A plugin that provides search and replace functionality in source edit mode. Incomplete.</p>
 */
Ext.define('Ext.ux.form.HtmlEditor.FindAndReplace', {
    extend: 'Ext.util.Observable',

	// Find and Replace language text
	langTitle: 'Find/Replace',
	langFind: 'Find',
	langReplace: 'Replace',
	langReplaceWith: 'Replace with',
	langClose: 'Close',
    // private
    cmd: 'findandreplace',
    // private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            'editmodechange': this.editModeChange,
            scope: this
        });
        this.lastSelectionStart=-1;
    },
    editModeChange: function(t, m){
        if (this.btn && m){
            this.btn.setDisabled(false);
        }
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-findandreplace',
            sourceEditEnabled:true,
            handler: function(){
                
                if (!this.farWindow){
                
                    this.farWindow = new Ext.Window({
                        title: this.langTitle,
                        closeAction: 'hide',
                        width: 270,
                        items: [{
                            itemId: 'findandreplace',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 80,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'textfield',
                                allowBlank: false,
                                fieldLabel: this.langFind,
                                name: 'find',
                                width: 145
                            }, {
                                xtype: 'textfield',
                                allowBlank: true,
                                fieldLabel: this.langReplaceWith,
                                name: 'replace',
                                width: 145
                            }]
                        }],
                        buttons: [{
                            text: this.langFind,
                            handler: this.doFind,
                            scope: this
                        }, {
                            text: this.langReplace,
                            handler: this.doReplace,
                            scope: this
                        }, {
                            text: this.langClose,
                            handler: function(){
                                this.farWindow.hide();
                            },
                            scope: this
                        }]
                    });
                
                }else{
                    
                    this.farWindow.getEl().frame();
                    
                }
                
                this.farWindow.show();
                
            },
            scope: this,
            tooltip: {
                title: this.langTitle
            },
            overflowText: this.langTitle
        });
        
    },
    doFind: function(){
        
        var frm = this.farWindow.getComponent('findandreplace').getForm();
        if (!frm.isValid()) {
            return '';
        }
        
        var findValue = frm.findField('find').getValue();
        var replaceValue = frm.findField('replace').getValue();
        if(this.cmp.sourceEditMode) {
            // source edit mode
            var textarea = this.cmp.el.dom; 
            var startPos = textarea.selectionStart===this.lastSelectionStart ? textarea.selectionStart+1 : textarea.selectionStart;
            var txt = textarea.value.substring(startPos);
            
            var regexp = new RegExp(findValue);
            var r = txt.search(regexp);
            if(r==-1) {
                return;                                    
            }
            this.lastSelectionStart = startPos + r;
            if(Ext.isGecko) {
                textarea.setSelectionRange(this.lastSelectionStart , this.lastSelectionStart + findValue.length);
                this.cmp.scrollIntoView(startPos);
                this.cmp.focus(false, true);
            }
            return;
        }
        // design mode
        //var doc = this.cmp.getEditorBody();
        //var txt = doc.innerHTML;
        // Should we search/replace in the source, and push the result back to the design?
        
    },
    doReplace: function(){
        
        var frm = this.farWindow.getComponent('findandreplace').getForm();
        if (!frm.isValid()) {
            return '';
        }
        
        var findValue = frm.findField('find').getValue();
        var replaceValue = frm.findField('replace').getValue();
        if(this.cmp.sourceEditMode) {
            var textarea = this.cmp.el.dom;
            var startPos = textarea.selectionStart;
            var endPos = textarea.selectionEnd;
            var txt = textarea.value;
            
            //cmp.execCmd('delete', null);
            //cmp.focus(false, false);
            //cmp.insertAtCursor(replaceValue);

            if(Ext.isGecko) {
                // TODO: Scroll into view
                var scrollPosition = textarea.scrollTop;
                textarea.value = txt.substring(0,startPos) + replaceValue + txt.substring(endPos);
                textarea.setSelectionRange(startPos,startPos + replaceValue.length);
                textarea.scrollTop = scrollPosition;
                this.cmp.focus(false, true);
            }
            return;
        }
        return;
        
    }
});