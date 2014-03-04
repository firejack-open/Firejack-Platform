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


Ext.define('OPF.console.domain.view.system.ServerAssociatedPackagesFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.server-associated-pkg-fieldset',

    fieldLabel: 'Associated Packages',
    subFieldLabel: '',

    layout: 'hbox',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.system.ServerAssociatedPackagesFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {

        this.associatedPackageStore = Ext.create('OPF.console.domain.store.AssociatedPackages');

        this.associatedPackageGrid = Ext.create('Ext.grid.Panel', {
            xtype: 'grid',
            cls: 'border-radius-grid-body border-radius-grid-header',
            store: this.associatedPackageStore,
            flex: 1,
            height: 140,
            autoExpandColumn: 'description',
            stripeRows: true,
            columns: [
                OPF.Ui.populateIconColumn16(30, 'package_16.png'),
                OPF.Ui.populateColumn('name', 'Name', {width: 100}),
                OPF.Ui.populateColumn('versionName', 'Version', {width: 50}),
                OPF.Ui.populateColumn('urlPath', 'Application Url', {
                    width: 200,
                    renderer: this.getApplicationUrl
                }),
                OPF.Ui.populateColumn('description', 'Description', {flex: 1, minWidth: 150, renderer: 'htmlEncode'}),
            ]
        });

        this.items = [
            this.associatedPackageGrid
        ];

        this.callParent(arguments);
    },

    getApplicationUrl: function(value, id, record) {
        var serverName = record.get('serverName');
        var port = record.get('port');
        var sPort = '';
        var httpProtocol = 'http';
        if (port == '443') {
            httpProtocol = 'https';
        } else if (port != '80' && port != '') {
            sPort = ':' + port;
        }
        var applicationUrl = serverName + sPort + value;
        applicationUrl = httpProtocol + '://' + applicationUrl.replace(/\/+/g, '/');
        return applicationUrl;
    }

});