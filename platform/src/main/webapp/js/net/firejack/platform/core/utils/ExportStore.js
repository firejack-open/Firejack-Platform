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

Ext.override(Ext.data.Store, {
    exportExcel: function (options) {
        var me = this;

        options = options || {};

        Ext.applyIf(options, {
            groupers: me.groupers.items,
            page: me.currentPage,
            start: (me.currentPage - 1) * me.pageSize,
            limit: me.pageSize,
            addRecords: false,
            action : 'read',
            filters: me.filters.items,
            sorters: me.getSorters()
        });

        Ext.apply(options.headers, me.proxy.headers);
        Ext.apply(options.headers, {
            'Accept': 'application/xls'
        });

        var operation = Ext.create('Ext.data.Operation', options);

        me.proxy.processResponse = function(success, operation, request, response, callback, scope){
            var proxy = this;
            me.unmask();
            if (typeof callback == 'function') {
                callback(operation, success, response);
            }

            proxy.afterRequest(request, success);
        };

        if (me.fireEvent('beforeload', me, operation) !== false) {
            me.loading = true;
            me.masked = true;
            me.proxy.headers = options.headers;
            me.proxy.read(operation, options.callback, me);

//            Ext.Ajax.request({
//                url: me.proxy.url,
//                method: 'GET',
//                headers: options.headers,
//                callback: function(opts, success, response) {
//                    me.unmask();
//                    options.callback(opts, success, response);
//                }
//            });
        }

        return me;
    }

});