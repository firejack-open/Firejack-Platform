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