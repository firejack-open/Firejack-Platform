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
 * Tooltip on mouse click
 */


Ext.define('OPF.core.component.ToolTipOnClick', {
    extend: 'Ext.tip.ToolTip',
    alias: 'widget.tooltip-onclick',

    showDelay: 0,

    setTarget: function(target) {
        var me = this,
            t = Ext.get(target),
            tg;

        if (me.target) {
            tg = Ext.get(me.target);
            me.mun(tg, 'click', me.onTargetOver, me);
            me.mun(tg, 'mouseout', me.onTargetOut, me);
            me.mun(tg, 'mousemove', me.onMouseMove, me);
        }

        me.target = t;
        if (t) {

            me.mon(t, {
                freezeEvent: true,

                click: me.onTargetOver,
                mouseout: me.onTargetOut,
                mousemove: me.onMouseMove,
                scope: me
            });
        }
        if (me.anchor) {
            me.anchorTarget = me.target;
        }
    }
});