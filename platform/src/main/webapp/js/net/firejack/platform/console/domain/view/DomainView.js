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
    'OPF.console.PageLayout',
    'OPF.console.NavigationPageLayout'
]);

Ext.define('OPF.console.domain.view.PromptPackageVersionNumberDialog', {
    extend: 'Ext.Window',
    alias: 'widget.pkg-ver-number-dialog',

    id: 'promptPackageVersionNumberDialog',
    title: 'Package Version Dialog',
    modal: true,
    closable: true,
    closeAction: 'hide',

    layout: 'fit',
    width: 300,
    height: 140,
    resizable: false,

    packageId: null,
    okFunction: null,

    constructor: function(packageId, okFunction, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.PromptPackageVersionNumberDialog.superclass.constructor.call(this, Ext.apply({
            packageId: packageId,
            okFunction: okFunction
        }, cfg));
    },

    initComponent: function(){
        var instance = this;

        this.versionNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            id: 'versionName',
            name: 'versionName',
            anchor: '100%',
            fieldLabel: 'Version',
            allowBlank: false
        });

        this.okButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Ok',
            id: 'okButton',
            formBind : true,
            handler: function () {
                instance.setVersion();
            }
        });

        this.cancelButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Cancel',
            id: 'cancelButton',
            handler: function () {
                instance.cancel();
            }
        });

        this.form = Ext.ComponentMgr.create({
            xtype: 'form',
            flex: 1,
            padding: 10,
            border: false,
            headerAsText: false,
            layout: 'form',
            monitorValid: true,
            layoutConfig: {
                align: 'stretch'
            },
            fbar: {
                xtype: 'toolbar',
                itemId: 'footer',
                items: [
                    this.okButton,
                    this.cancelButton
                ]
            },
            items: [
                {
                    xtype: 'box',
                    autoEl: {
                        tag: 'div',
                        html: 'Please select a unique version number for this package.'
                    }
                },
                this.versionNameField
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    setVersion: function() {
        var instance = this;
        var versionName = this.versionNameField.getValue();
        Ext.Ajax.request({
			url: OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/installation/package/check/unique/version/') + this.packageId + '?versionName=' + versionName,
			method: 'GET',
			success:function(response, action) {
                var vo = Ext.decode(response.responseText);
                if (vo.success) {
                    instance.okFunction(versionName);
                    instance.hide();
                } else {
                    OPF.Msg.setAlert(false, 'Version number is not unique');
                    instance.versionNameField.markInvalid('Version number is not unique');
                }
			},
            failure: function(o) {
                OPF.Msg.setAlert(false, 'Version number is not unique');
                instance.versionNameField.markInvalid('Version number is not unique');
            }
		});
    },

    cancel: function() {
        this.hide();
    }

});

Ext.define('OPF.console.domain.view.DomainView', {
    extend: 'OPF.console.NavigationPageLayout',
    alias: 'widget.domain-page',

    activeButtonLookup: 'net.firejack.platform.console.domain',

    initComponent: function() {
        var instance = this;

        this.uploadPackageArchiveDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['ofr'],
            uploadUrl: OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/installation/package/upload'),
            successUploaded: function(jsonData, action) {
                this.editPanel.successUploadPackageArchive(jsonData.data[0]);
            },
            failureUploaded: function(action) {
                OPF.Msg.setAlert(false, 'Processed file on the server');
            }
        });

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['xml'],
            uploadFile: function() {
                var instance = this;
                var selectedNode = instance.editPanel.navigationPanel.selectedNode;
                var param = '';
                if (isNotEmpty(selectedNode)) {
                    var nodeId = selectedNode.get('id');
                    var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(nodeId);
                    if (type == OPF.core.utils.RegistryNodeType.ROOT_DOMAIN) {
                        param = '?rootDomainId=' + SqGetIdFromTreeEntityId(nodeId);
                    }
                }
                this.form.getForm().url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/system/import') + param;
                this.form.getForm().submit({
                    method : "POST",
                    success : function(fr, action) {
                        instance.hide();
                        OPF.Msg.setAlert(true, 'XML import was successfully finished!');
                        instance.editPanel.navigationPanel.reload();
                    },
                    failure: function(fr, action) {
                        OPF.Msg.setAlert(false, 'Processed file on the server');
                    }
                });
            }
        });

        this.rnButtons = [
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add entity',
                cls: 'entityIcon',
                iconCls: 'btn-add-entity',
                registryNodeType: OPF.core.utils.RegistryNodeType.ENTITY,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add sub entity',
                cls: 'entityIcon',
                iconCls: 'btn-add-entity',
                registryNodeType: OPF.core.utils.RegistryNodeType.SUB_ENTITY,
                managerLayout: instance,
                hidden: true
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add relationship',
                cls: 'relationshipIcon',
                iconCls: 'btn-add-rel',
                registryNodeType: OPF.core.utils.RegistryNodeType.RELATIONSHIP,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add action',
                cls: 'actionIcon',
                iconCls: 'btn-add-action',
                registryNodeType: OPF.core.utils.RegistryNodeType.ACTION,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add package',
                cls: 'packageIcon',
                iconCls: 'btn-add-package',
                registryNodeType: OPF.core.utils.RegistryNodeType.PACKAGE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'upload package',
                cls: 'uploadIcon',
                iconCls: 'btn-upload-package-archive',
                disabled: false,
                alwaysEnable: true,
                handler: function () {
                    instance.uploadPackageArchiveDialog.show();
                }
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add system',
                cls: 'systemIcon',
                iconCls: 'btn-add-system',
                registryNodeType: OPF.core.utils.RegistryNodeType.SYSTEM,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add server',
                cls: 'serverIcon',
                iconCls: 'btn-add-server',
                registryNodeType: OPF.core.utils.RegistryNodeType.SERVER,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add database',
                cls: 'databaseIcon',
                iconCls: 'btn-add-database',
                registryNodeType: OPF.core.utils.RegistryNodeType.DATABASE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add filestore',
                cls: 'filestoreIcon',
                iconCls: 'btn-add-filestore',
                registryNodeType: OPF.core.utils.RegistryNodeType.FILESTORE,
                managerLayout: instance
            },
            /*{
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add process',
                cls: 'processIcon',
                iconCls: 'btn-add-process',
                registryNodeType: OPF.core.utils.RegistryNodeType.PROCESS,
                managerLayout: instance
            },*/
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add actor',
                cls: 'actorIcon',
                iconCls: 'btn-add-actor',
                registryNodeType: OPF.core.utils.RegistryNodeType.ACTOR,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add report',
                cls: 'reportIcon',
                iconCls: 'btn-add-report',
                registryNodeType: OPF.core.utils.RegistryNodeType.REPORT,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add bi-report',
                cls: 'reportIcon',
                iconCls: 'btn-add-bireport',
                registryNodeType: OPF.core.utils.RegistryNodeType.BI_REPORT,
                managerLayout: instance
            },
            {
                xtype: 'button',
                scale: 'large',
                tooltip: 'import',
                cls: 'importIcon',
                iconCls: 'btn-import',
                alwaysEnable: true,
                handler: function() {
                    instance.uploadFileDialog.show();
                }
            },
            {
                xtype: 'button',
                scale: 'large',
                tooltip: 'export',
                cls: 'exportIcon',
                iconCls: 'btn-export',
                parentRegistryNodeTypes: [
                    OPF.core.utils.RegistryNodeType.EMPTY,
                    OPF.core.utils.RegistryNodeType.ROOT_DOMAIN
                ],
                handler:function() {
                    var selectedNode = instance.navigationPanel.selectedNode;
                    var param = '';
                    if (isNotEmpty(selectedNode)) {
                        var nodeId = selectedNode.get('id');
                        var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(nodeId);
                        if (type == OPF.core.utils.RegistryNodeType.ROOT_DOMAIN) {
                            param = '?rootDomainId=' + SqGetIdFromTreeEntityId(nodeId);
                        }
                    }
                    document.location.href =
                        OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/system/export/environment.xml') + param;
                }
            }
        ];

        this.infoContent = new OPF.core.component.resource.ResourceControl({
            htmlResourceLookup: 'net.firejack.platform.registry.registry-node.domain.info-html'
        });

        this.callParent(arguments);
    },

    successUploadPackageArchive: function(data) {
        var instance = this;
        if (data.status == 'PACKAGE_VERSION_HAS_INSTALLED') {
            this.navigationPanel.reload();
        } else {
            if (data.status == 'PACKAGE_VERSION_IS_ACTIVE') {
                Ext.Msg.show({
                    title:'Information',
                    msg: 'You have uploaded a version that is the same version number as the active version. Would you like to merge versions?',
                    buttons: Ext.Msg.YESNO,
                    fn: function(buttonId) {
                        if (buttonId == 'yes') {
                            instance.activatePackageVersion(data.uploadedFilename, data.versionName);
                        } else {
                            instance.promptNewPackageVersionNumber(data.packageId, data.uploadedFilename);
                        }
                    },
                    animEl: 'elId',
                    icon: Ext.MessageBox.QUESTION
                });
            } else if (data.status == 'PACKAGE_VERSION_EXISTS') {
                Ext.Msg.show({
                    title:'Information',
                    msg: 'You have uploaded a version that already exists. Would you like to overwrite the old version?',
                    buttons: Ext.Msg.YESNO,
                    fn: function(buttonId) {
                        if (buttonId == 'yes') {
                            instance.activatePackageVersion(data.uploadedFilename, data.versionName);
                        } else {
                            instance.promptNewPackageVersionNumber(data.packageId, data.uploadedFilename);
                        }
                    },
                    animEl: 'elId',
                    icon: Ext.MessageBox.QUESTION
                });

            } else if (data.status == 'PACKAGE_VERSION_NOT_EXISTS') {
                instance.activatePackageVersion(data.uploadedFilename, data.versionName);
            }
        }
    },

    promptNewPackageVersionNumber: function(packageId, uploadedFilename) {
        var instance = this;
        var dialog = Ext.WindowMgr.get('promptPackageVersionNumberDialog');
        if (isEmpty(dialog)) {
            dialog = Ext.create('OPF.console.domain.view.PromptPackageVersionNumberDialog', packageId,
                function(versionName) {
                    instance.activatePackageVersion(uploadedFilename, versionName);
                }
            );
            Ext.WindowMgr.register(dialog);
        }
        dialog.show();
    },

    activatePackageVersion: function(uploadedFilename, versionName) {
        var instance = this;
        Ext.Msg.show({
            title: 'Information',
            msg: 'Would you like to make this package the current active version?',
            buttons: Ext.Msg.YESNO,
            fn: function(buttonId) {
                if (buttonId == 'yes') {
                    Ext.Msg.show({
                        title: 'Information',
                        msg: 'Would you like to archive the current active version?',
                        buttons: Ext.Msg.YESNO,
                        fn: function(buttonId) {
                            if (buttonId == 'yes') {
                                instance.performPackageVersion(uploadedFilename, versionName, true, true);
                            } else {
                                instance.performPackageVersion(uploadedFilename, versionName, true, false);
                            }
                        },
                        animEl: 'elId',
                        icon: Ext.MessageBox.QUESTION
                    });
                } else {
                    instance.performPackageVersion(uploadedFilename, versionName, false, false);
                }
            },
            animEl: 'elId',
            icon: Ext.MessageBox.QUESTION
        });
    },

    performPackageVersion: function(uploadedFilename, versionName, doAsCurrent, doArchive) {
        var instance = this;
        var url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/installation/package/perform') +
            '?uploadedFilename=' + uploadedFilename +
            '&versionName=' + versionName +
            '&doAsCurrent=' + doAsCurrent +
            '&doArchive=' + doArchive;
        Ext.Ajax.request({
            url: url,
            method: 'GET',
            jsonData: [],

            success:function(response, action) {
                var vo = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, vo.message);
                instance.navigationPanel.reload();
            },

            failure:function(response) {
                OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
            }
        });
    }

});