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

Ext.define('OPF.core.component.LabelContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.label-container',

    cls: 'fieldset-top-margin',
    labelCls: 'container-label-block',
    labelMargin: '5 0 15 0',

    fieldLabel: null,
    subFieldLabel: null,

    initComponent: function() {
        var instance = this;

        var layout = this.layout;
        this.layout = 'anchor';

        this.label = Ext.ComponentMgr.create({
            xtype: 'container',
            html:
                '<label class="' + this.labelCls + '">' +
                    '<span class="main-label">' + this.fieldLabel + ':</span>' +
                    '<span class="sub-label ">' + this.subFieldLabel + '</span>' +
                '</label>',
            margin: this.labelMargin
        });

        var items = this.items;

        this.items = [
            this.label,
            {
                xtype: 'fieldcontainer',
                layout: layout,
                items: items
            }
        ];

        this.callParent(arguments);
    },

    findById: function(id) {
        var me = this;
        var items = me.items.items.slice();
        var item = null;
        for (var i = 0; i < items.length; i++) {
            if (items[i].id == id || items[i].itemId == id) {
                item = items[i];
                break;
            }
        }
        return item;
    }

});
