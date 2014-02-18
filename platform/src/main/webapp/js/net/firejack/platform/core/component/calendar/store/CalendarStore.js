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

Ext.require([
    'OPF.core.component.calendar.model.CalendarEventModel'
]);

Ext.define('OPF.core.component.calendar.store.CalendarStore', {
    extend: 'Ext.data.Store',
    model: 'OPF.core.component.calendar.model.CalendarEventModel',

    proxy: {
        type: 'ajax',
        reader: {
            type: 'json',
            root: 'data'
        },
        writer: {
            type: 'json'
        }
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.listeners = cfg.listeners || {};
        cfg.listeners = Ext.apply(cfg.listeners, cfg.listeners, this.listeners);

        this.addEvents('preload');

        this.superclass.constructor.call(this, cfg);
    },

    load: function(options) {
        var result = this.callParent(arguments);
        if (this.loading) {
            this.fireEvent('preload', this);
        }
        return result;
    }

});