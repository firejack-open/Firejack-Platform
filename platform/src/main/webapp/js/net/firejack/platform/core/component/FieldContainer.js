//@tag opf-prototype
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

Ext.define('OPF.core.component.FieldContainer', {
    extend: 'Ext.form.FieldContainer',
    alias : 'widget.opf-fieldcontainer',

    cls: 'opf-field-container',

    mixins: {
        field:'Ext.form.field.Field',
        sublabelable: 'OPF.core.component.SubLabelable'
    },

    //  configurables
    combineErrors: true,
    //  msgTarget: 'under',
    layout: 'hbox',

    readOnly: false,

    validateOnBlur: true,

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