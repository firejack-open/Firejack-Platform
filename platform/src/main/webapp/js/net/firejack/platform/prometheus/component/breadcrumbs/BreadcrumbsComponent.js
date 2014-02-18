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

Ext.define('OPF.prometheus.component.breadcrumbs.BreadcrumbsComponent', {
    extend: 'Ext.container.Container',
    alias : 'widget.prometheus.component.breadcrumbs-component',

    cls: 'm-breadcrumbs',

    initComponent: function() {
        var me = this;

        var selectPath = this.getSelectPath();
        var prefixUrl = OPF.ifBlank(OPF.Cfg.DASHBOARD_PREFIX_URL, '');

        this.html = '<ul class="browser-path">';

        if (!selectPath.startsWith('home')) {
            if (OPF.isBlank(selectPath)) {
                this.html +=
                    '<li>' + Ext.String.capitalize('Home') + '</li>';
            } else {
                this.html +=
                    '<li>' +
                        '<a href="' + OPF.Cfg.fullUrl(prefixUrl + '/home', true) + '">' + Ext.String.capitalize('home')+'</a>' +
                        '<span class="separator"> > </span>' +
                    '</li>';
            }
        }

        var url = '';
        var paths = selectPath.split('.');
        Ext.each(paths, function(path, index) {
            url += OPF.isNotBlank(path) ? '/' + path : '';
            var name = OPF.ifBlank(path, '');
            if (index + 1 < paths.length) {
                me.html +=
                    '<li>' +
                        '<a href="' + OPF.Cfg.fullUrl(prefixUrl + url, true) + '">' + Ext.String.capitalize(name) + '</a>' +
                        '<span class="separator"> > </span>' +
                    '</li>';
            } else {
                me.html +=
                    '<li>' + Ext.String.capitalize(name) + '</li>';
            }
        });

		this.html +=
            '</ul>';

        this.callParent(arguments);
    },

    getSelectPath: function() {
        var packageLookup = OPF.findPackageLookup(OPF.Cfg.NAVIGATION_LOOKUP);
        var gatewayLookup = packageLookup + '.gateway';
        var selectPath;
        if (OPF.Cfg.NAVIGATION_LOOKUP.startsWith(gatewayLookup)) {
            selectPath = OPF.Cfg.NAVIGATION_LOOKUP.substring(gatewayLookup.length + 1);
        } else {
            selectPath = 'home';
        }
        return selectPath;

    }

});