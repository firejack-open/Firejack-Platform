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
 * @class Ext.ux.form.HtmlEditor.Link
 * @extends Ext.util.Observable
 * <p>A plugin that creates a button on the HtmlEditor for inserting a link.</p>
 */
Ext.define('Ext.ux.form.HtmlEditor.Link', {
    extend: 'Ext.util.Observable',

    // Link language text
    langTitle   : 'Insert Link',
    langInsert  : 'Insert',
    langCancel  : 'Cancel',
    langTarget  : 'Target',
    langURL     : 'URL',
    langText    : 'Text',
    // private
    linkTargetOptions: [['_self', 'Default'], ['_blank', 'New Window'], ['_parent', 'Parent Window'], ['_top', 'Entire Window']],
    init: function(cmp){
        cmp.enableLinks = false;
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-createlink',
            handler: function(){
                var sel = this.cmp.getSelectedText();
                if (!this.linkWindow) {
                    this.linkWindow = new Ext.Window({
                        title: this.langTitle,
                        closeAction: 'hide',
                        width: 250,
                        height: 160,
                        items: [{
                            xtype: 'form',
                            itemId: 'insert-link',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 40,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'textfield',
                                fieldLabel: this.langText,
                                name: 'text',
                                anchor: '100%',
                                value: '' 
                            }, {
                                xtype: 'textfield',
                                fieldLabel: this.langURL,
                                vtype: 'url',
                                name: 'url',
                                anchor: '100%',
                                value: 'http://'
                            }, {
                                xtype: 'combo',
                                fieldLabel: this.langTarget,
                                name: 'target',
                                forceSelection: true,
                                mode: 'local',
                                store: new Ext.data.ArrayStore({
                                    autoDestroy: true,
                                    fields: ['spec', 'val'],
                                    data: this.linkTargetOptions
                                }),
                                triggerAction: 'all',
                                value: '_self',
                                displayField: 'val',
                                valueField: 'spec',
                                anchor: '100%'
                            }]
                        }],
                        buttons: [{
                            text: this.langInsert,
                            handler: function(){
                                var frm = this.linkWindow.getComponent('insert-link').getForm();
                                if (frm.isValid()) {
                                    var afterSpace = '', sel = this.cmp.getSelectedText(true), text = frm.findField('text').getValue(), url = frm.findField('url').getValue(), target = frm.findField('target').getValue();
                                    if (text.length && text[text.length - 1] == ' ') {
                                        text = text.substr(0, text.length - 1);
                                        afterSpace = ' ';
                                    }
                                    if (sel.hasHTML) {
                                        text = sel.html;
                                    }
                                    var html = '<a href="' + url + '" target="' + target + '">' + text + '</a>' + afterSpace;
                                    this.cmp.insertAtCursor(html);
                                    this.linkWindow.hide();
                                } else {
                                    if (!frm.findField('url').isValid()) {
                                        frm.findField('url').getEl().frame();
                                    } else if (!frm.findField('target').isValid()) {
                                        frm.findField('target').getEl().frame();
                                    }
                                }
                                
                            },
                            scope: this
                        }, {
                            text: this.langCancel,
                            handler: function(){
                                this.linkWindow.close();
                            },
                            scope: this
                        }],
                        listeners: {
                            show: {
                                fn: function(){
                                    var frm = this.linkWindow.getComponent('insert-link').getForm();
                                    frm.findField('text').setValue(sel.textContent).setDisabled(sel.hasHTML);
                                    frm.findField('url').reset().focus(true, 50);
                                },
                                scope: this,
                                defer: 350
                            }
                        }
                    });
                    this.linkWindow.show();
                } else {
                    this.linkWindow.show();
                    this.linkWindow.getEl().frame();
                }
            },
            scope: this,
            tooltip: this.langTitle
        });
    }
});
