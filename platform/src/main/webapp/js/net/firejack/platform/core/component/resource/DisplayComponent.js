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


Ext.define('OPF.core.component.resource.DisplayComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.display-component',

    maxTextLength: null,

    fullHtmlOrData: null,

    getHtmlOrData: function() {
        return this.fullHtmlOrData;
    },

    update: function(htmlOrData, loadScripts, cb) {
        var me = this;

        this.fullHtmlOrData = htmlOrData;

        if (OPF.isNotEmpty(this.maxTextLength)) {
            htmlOrData = OPF.cutting(htmlOrData, this.maxTextLength);
        }

        if (me.tpl && !Ext.isString(htmlOrData)) {
            me.data = htmlOrData;
            if (me.rendered) {
                me.tpl[me.tplWriteMode](me.getTargetEl(), htmlOrData || {});
            }
        } else {
            me.html = Ext.isObject(htmlOrData) ? Ext.DomHelper.markup(htmlOrData) : htmlOrData;
            if (me.rendered) {
                me.getTargetEl().update(me.html, loadScripts, cb);
            }
        }

        if (me.rendered) {
            me.doComponentLayout();
        }
    },

    listeners: {
        afterrender: function(me) {
            Ext.create('Ext.tip.ToolTip', {
                target: me.getId(),
                html: 'Loading...',
                dismissDelay: 0,
                listeners: {
                    beforeshow: function(tip) {
                        tip.update(me.fullHtmlOrData);
                    }
                }
            });
        }
    }

});

