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
 * @class Ext.ux.form.HtmlEditor.Font
 * @extends Ext.util.Observable
 * <p>A plugin that creates a menu on the HtmlEditor for selecting a font. Uses the HtmlEditors default font settings which can be overriden on that component to change the list of fonts or default font.</p>
 */
Ext.define('Ext.ux.form.HtmlEditor.Font', {
    extend: 'Ext.util.Observable',

    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    // private
    onRender: function(){
        var cmp = this.cmp;
        var fonts = function(){
            var fnts = [];
            Ext.each(cmp.fontFamilies, function(itm){
                fnts.push([itm.toLowerCase(),itm]);
            });
            return fnts;
        }(); 
        var btn = this.cmp.getToolbar().addItem({
            xtype: 'combo',
            displayField: 'display',
            valueField: 'value',
            name: 'fontfamily',
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            width: 80,
            emptyText: 'Font',
            tpl: '<tpl for="."><div class="x-combo-list-item" style="font-family:{value};">{display}</div></tpl>',
            store: {
                xtype: 'arraystore',
                autoDestroy: true,
                fields: ['value','display'],
                data: fonts
            },
            listeners: {
                'select': function(combo,rec){
                    this.relayCmd('fontname', rec.get('value'));
                    this.deferFocus();
                    combo.reset();
                },
                scope: cmp
            }
        });
    }
});