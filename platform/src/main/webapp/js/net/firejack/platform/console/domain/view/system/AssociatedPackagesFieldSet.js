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


Ext.define('OPF.console.domain.view.system.AssociatedPackagesFieldSet', {
    extend: 'Ext.container.Container',
    alias: 'widget.associated-pkg-fieldset',

    fieldLabel: 'Associated Packages',
    subFieldLabel: 'Manage applications and services running on this system',

    layout: 'anchor',
    cls: 'fieldset-top-margin',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.system.AssociatedPackagesFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.label = Ext.ComponentMgr.create({
            xtype: 'container',
            html:
                '<label class="container-label-block">' +
                    '<span class="main-label">' + this.fieldLabel + ':</span>' +
                    '<span class="sub-label ">' + this.subFieldLabel + '</span>' +
                '</label>',
            margin: '0 0 5 0'
        });

        this.associatedPackageGrid = Ext.create('Ext.grid.Panel', {
            store: 'AssociatedPackages',
            flex: 1,
            height: 140,
            stripeRows: true,
            cls: 'border-radius-grid-body border-radius-grid-header',
            columns: [
                OPF.Ui.populateIconColumn16(30, 'package_16.png'),
                OPF.Ui.populateColumn('name', 'Name', {width: 100}),
                OPF.Ui.populateColumn('versionName', 'Version', {width: 50}),
                OPF.Ui.populateColumn('urlPath', 'Application Url', {
                    width: 200,
                    renderer: this.getApplicationUrl
                }),
                OPF.Ui.populateColumn('description', 'Description', {flex: 1, minWidth: 150, renderer: 'htmlEncode'}),
                OPF.Ui.populateColumn('deployed', 'Actions', {
                    width: 170,
                    renderer: this.renderInstall
                })
            ]
        });

        this.items = [
            this.label,
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
    },

    renderInstall: function(value, id, record) {
        var instance = this;
        var associatedPackageId = record.get('id');

        var isDeployed = record.get('deployed');
        var extId1 = associatedPackageId + '-1-opf';
        var extId2 = associatedPackageId + '-2-opf';
        Ext.Function.defer(instance.ownerCt.createGridBtn, 100, this, ['Install', extId1, record, 'install_16.png', {
            hidden: isDeployed,
            handler: function(btn, e) {
                instance.ownerCt.showPackageInstallationDialog(associatedPackageId, extId1, extId2);
            }
        }]);

        Ext.Function.defer(instance.ownerCt.createGridBtn, 100, this, ['Uninstall', extId2, record, 'uninstall_16.png', {
            hidden: !isDeployed,
            handler: function(btn, e) {
                instance.ownerCt.showPackageUninstallationDialog(associatedPackageId, extId1, extId2);
            }
        }]);

        var extId3 = associatedPackageId + '-3-opf';
        Ext.Function.defer(instance.ownerCt.createGridBtn, 100, this, ['Remove', extId3, record, 'delete_16.png', {
            handler: function(btn, e) {
                instance.ownerCt.removeAssociationOfPackage(associatedPackageId, record);
            }
        }]);
        return('<div id="' + extId1 + '"></div><div id="' + extId2 + '"></div><div id="' + extId3 + '"></div>');
    },

    createGridBtn: function(value, id, record, icon, cfg) {
        var buttonId = 'btn-' + id;
        var gridButton = Ext.get(buttonId);
        if (OPF.isEmpty(gridButton)) {
            OPF.Ui.createGridBtn(value, id, record, icon, cfg)
        }
    },

    getPackageInstallationDialog: function() {
        var packageInstallationDialog = Ext.WindowMgr.get('packageInstallationDialog');
        if (OPF.isEmpty(packageInstallationDialog)) {
            packageInstallationDialog = Ext.create(
                'OPF.console.domain.dialog.PackageInstallationDialog', this.editPanel);
            Ext.WindowMgr.register(packageInstallationDialog);
        }
        return packageInstallationDialog;
    },

    showPackageInstallationDialog: function(associatedPackageId, installBtnId, uninstallBtnId) {
        var packageInstallationDialog = this.getPackageInstallationDialog();
        packageInstallationDialog.setAssociatedPackageId(associatedPackageId);
        packageInstallationDialog.editPanel = this.editPanel;
        packageInstallationDialog.showDialog();
        packageInstallationDialog.actionBtns = [installBtnId, uninstallBtnId];
    },

//    installPackage: function(params, actionBtns) {
//        var instance = this;
//        var url = OPF.Cfg.restUrl('registry/installation/package/install/')
//            + params.associatedPackageId
////            + '?databaseId=' + params.databaseId
//            + '?databaseAction=' + params.databaseAction;
//        Ext.Ajax.request({
//            url: url,
//            method: 'GET',
//            jsonData: {},
//
//            success:function(response, action) {
//                var resp = Ext.decode(response.responseText);
//                instance.getPackageInstallationDialog().showLogs(resp.data[0]);
//
//                Ext.get('btn-' + actionBtns[0]).hide();
//                Ext.get('btn-' + actionBtns[1]).show();
//            },
//
//            failure:function(response) {
//                OPF.Msg.setAlert(false, response.message);
//            }
//        });
//    },

    getPackageUninstallationDialog: function() {
        var packageUninstallationDialog = Ext.WindowMgr.get('packageUninstallationDialog');
        if (OPF.isEmpty(packageUninstallationDialog)) {
            packageUninstallationDialog = Ext.create(
                'OPF.console.domain.dialog.PackageUninstallationDialog', this.editPanel);
            Ext.WindowMgr.register(packageUninstallationDialog);
        }
        return packageUninstallationDialog;
    },

    showPackageUninstallationDialog: function(associatedPackageId, installBtnId, uninstallBtnId) {
        var packageUninstallationDialog = this.getPackageUninstallationDialog();
        packageUninstallationDialog.setAssociatedPackageId(associatedPackageId);
        packageUninstallationDialog.editPanel = this.editPanel;
        packageUninstallationDialog.showDialog();
        packageUninstallationDialog.uninstallButton.enable();
        packageUninstallationDialog.actionBtns = [installBtnId, uninstallBtnId];
    },

//    uninstallPackage: function(params, actionBtns) {
//        var url = OPF.Cfg.restUrl('registry/installation/package/uninstall/')
//                + params.associatedPackageId
//                + '?databaseId=' + params.databaseId
//                + '&databaseAction=' + params.databaseAction;
//        Ext.Ajax.request({
//            url: url,
//            method: 'GET',
//            jsonData: {},
//
//            success:function(response, action) {
//                var vo = Ext.decode(response.responseText);
//                OPF.Msg.setAlert(true, vo.message);
//
//                Ext.get('btn-' + actionBtns[0]).show();
//                Ext.get('btn-' + actionBtns[1]).hide();
//            },
//
//            failure:function(response) {
//                OPF.Msg.setAlert(false, response.message);
//            }
//        });
//    },

    removeAssociationOfPackage: function(associatedPackageId, record) {
        var instance = this;
        var systemId = this.editPanel.nodeBasicFields.idField.getValue();

        Ext.Msg.show({
            title:'Remove Association',
            msg: 'Are you sure?',
            buttons: Ext.Msg.YESNO,
            fn: function(buttonId) {
                if (buttonId == 'yes') {
                    Ext.Ajax.request({
                        url: OPF.core.utils.RegistryNodeType.SYSTEM.generateUrl('/installed-package/') +
                            systemId + '/' + associatedPackageId,
                        method: 'DELETE',
                        jsonData: {},

                        success:function(response, action) {
                            instance.associatedPackageGrid.store.remove(record);

                            var packageTreeNodeId = 'xnode-package-' + associatedPackageId;
                            var packageNode = instance.editPanel.managerLayout.navigationPanel.store.getNodeById(packageTreeNodeId);
                            if (OPF.isNotEmpty(packageNode)) {
                                packageNode.set('parameters', {});
                            }

                            var resp = Ext.decode(response.responseText);
                            OPF.Msg.setAlert(true, resp.message);
                        },

                        failure:function(response) {
                            OPF.Msg.setAlert(false, response.message);
                        }
                    });
                }
            },
            animEl: 'elId',
            icon: Ext.MessageBox.QUESTION
        });
    }

});