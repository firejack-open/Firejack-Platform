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

Ext.define('OPF.core.component.LinkButton', {
    extend: 'Ext.Component',
    alias: 'widget.linkbutton',

    autoEl: 'a',
    renderTpl: '<a href=\"javascript:;\" id="{id}-btnEl">{text}</a>',

    config: {
        text: '',
        handler: function () { }
    },

    initComponent: function () {
        var me = this;
        me.callParent(arguments);

        this.renderData = {
            text: this.getText()
        };
    },

    onRender: function(ct, position) {
        var me = this,
            btn;

        me.addChildEls('btnEl');

        me.callParent(arguments);

        btn = me.btnEl;

        me.mon(btn, 'click', me.onClick, me);
    },

    onClick: function(e) {
        var me = this;
        if (me.preventDefault || (me.disabled && me.getHref()) && e) {
            e.preventDefault();
        }
        if (e.button !== 0) {
            return;
        }
        if (!me.disabled) {
            me.fireHandler(e);
        }
    },

    fireHandler: function(e){
        var me = this,
            handler = me.handler;

        me.fireEvent('click', me, e);
        if (handler) {
            handler.call(me.scope || me, me, e);
        }
    }

});