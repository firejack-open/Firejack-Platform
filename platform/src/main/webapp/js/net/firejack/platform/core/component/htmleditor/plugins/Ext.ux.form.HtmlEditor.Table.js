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
 * @class Ext.ux.form.HtmlEditor.Table
 * @extends Ext.util.Observable
 * <p>A plugin that creates a button on the HtmlEditor for making simple tables.</p>
 */
Ext.define('Ext.ux.form.HtmlEditor.Table', {
    extend: 'Ext.util.Observable',

    // Table language text
    langTitle       : 'Insert Table',
    langInsert      : 'Insert',
    langCancel      : 'Cancel',
    langRows        : 'Rows',
    langColumns     : 'Columns',
    langBorder      : 'Border',
    langCellLabel   : 'Label Cells',
    // private
    cmd: 'table',
    /**
     * @cfg {Boolean} showCellLocationText
     * Set true to display row and column informational text inside of newly created table cells.
     */
    showCellLocationText: true,
    /**
     * @cfg {String} cellLocationText
     * The string to display inside of newly created table cells.
     */
    cellLocationText: '{0}&nbsp;-&nbsp;{1}',
    /**
     * @cfg {Array} tableBorderOptions
     * A nested array of value/display options to present to the user for table border style. Defaults to a simple list of 5 varrying border types.
     */
    tableBorderOptions: [['none', 'None'], ['1px solid #000', 'Sold Thin'], ['2px solid #000', 'Solid Thick'], ['1px dashed #000', 'Dashed'], ['1px dotted #000', 'Dotted']],
    // private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    // private
    onRender: function(){
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-table',
            handler: function(){
                if (!this.tableWindow){
                    this.tableWindow = new Ext.Window({
                        title: this.langTitle,
                        closeAction: 'hide',
                        width: 235,
                        items: [{
                            itemId: 'insert-table',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 65,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: this.langRows,
                                name: 'row',
                                width: 60
                            }, {
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: this.langColumns,
                                name: 'col',
                                width: 60
                            }, {
                                xtype: 'combo',
                                fieldLabel: this.langBorder,
                                name: 'border',
                                forceSelection: true,
                                mode: 'local',
                                store: new Ext.data.ArrayStore({
                                    autoDestroy: true,
                                    fields: ['spec', 'val'],
                                    data: this.tableBorderOptions
                                }),
                                triggerAction: 'all',
                                value: 'none',
                                displayField: 'val',
                                valueField: 'spec',
                                anchor: '-15'
                            }, {
                            	xtype: 'checkbox',
                            	fieldLabel: this.langCellLabel,
                            	checked: this.showCellLocationText,
                            	listeners: {
                            		check: function(){
                            			this.showCellLocationText = !this.showCellLocationText;
                            		},
                            		scope: this
                            	}
                            }]
                        }],
                        buttons: [{
                            text: this.langInsert,
                            handler: function(){
                                var frm = this.tableWindow.getComponent('insert-table').getForm();
                                if (frm.isValid()) {
                                    var border = frm.findField('border').getValue();
                                    var rowcol = [frm.findField('row').getValue(), frm.findField('col').getValue()];
                                    if (rowcol.length == 2 && rowcol[0] > 0 && rowcol[1] > 0) {
                                        var colwidth = Math.floor(100/rowcol[0]);
                                        var html = "<table style='border-collapse: collapse'>";
                                        var cellText = '&nbsp;';
                                        if (this.showCellLocationText){ cellText = this.cellLocationText; }
                                        for (var row = 0; row < rowcol[0]; row++) {
                                            html += "<tr>";
                                            for (var col = 0; col < rowcol[1]; col++) {
                                                html += "<td width='" + colwidth + "%' style='border: " + border + ";'>" + String.format(cellText, (row+1), String.fromCharCode(col+65)) + "</td>";
                                            }
                                            html += "</tr>";
                                        }
                                        html += "</table>";
                                        this.cmp.insertAtCursor(html);
                                    }
                                    this.tableWindow.hide();
                                }else{
                                    if (!frm.findField('row').isValid()){
                                        frm.findField('row').getEl().frame();
                                    }else if (!frm.findField('col').isValid()){
                                        frm.findField('col').getEl().frame();
                                    }
                                }
                            },
                            scope: this
                        }, {
                            text: this.langCancel,
                            handler: function(){
                                this.tableWindow.hide();
                            },
                            scope: this
                        }]
                    });
                
                }else{
                    this.tableWindow.getEl().frame();
                }
                this.tableWindow.show();
            },
            scope: this,
            tooltip: {
                title: this.langTitle
            },
            overflowText: this.langTitle
        });
    }
});
