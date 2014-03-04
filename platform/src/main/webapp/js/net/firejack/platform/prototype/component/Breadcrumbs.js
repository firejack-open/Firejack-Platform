/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

Ext.define('OPF.prototype.component.Breadcrumbs', {
    extend: 'Ext.container.Container',
    alias : 'widget.opf.prototype.component.breadcrumbs',

    cls: 'outline breadcrumbs',

    initComponent: function() {
        var me = this;

        var selectPath = this.getSelectPath();
        var prefixUrl = OPF.ifBlank(OPF.Cfg.DASHBOARD_PREFIX_URL, '');

        this.html = '<div class="browser-path">';

        if (!selectPath.startsWith('home')) {
            this.html +=
                '<a href="' + OPF.Cfg.fullUrl(prefixUrl + '/home', true) + '">' +
                    '<span>' + Ext.String.capitalize('home') + '</span>' +
                    '<b></b>' +
                '</a>';
        }

        var url = '';
        var paths = selectPath.split('.');
        Ext.each(paths, function(path, index) {
            url += OPF.isNotBlank(path) ? '/' + path : '';
            var name = OPF.ifBlank(path, '~');
            if (index + 1 < paths.length) {
                me.html +=
                    '<a href="' + OPF.Cfg.fullUrl(prefixUrl + url, true) + '">' +
                        '<span>' + Ext.String.capitalize(name) + '</span>' +
                        '<b></b>' +
                    '</a>';
            } else {
                me.html +=
                    '<strong>' +
                        '<span>' + Ext.String.capitalize(name) + '</span>' +
                        '<b></b>' +
                    '</strong>'
            }
        });

        this.html +=
                '<div id="searchContainer"></div>'+
            '</div>';

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