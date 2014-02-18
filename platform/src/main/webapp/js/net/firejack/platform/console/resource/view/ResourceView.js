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

Ext.define('OPF.console.resource.view.ResourceView', {
    extend: 'OPF.console.NavigationPageLayout',
    alias: 'widget.resource-page',

    activeButtonLookup: 'net.firejack.platform.console.resource',

    initComponent: function() {
        var instance = this;

        this.uploadCollectionArchiveDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['ofcr'],
            uploadUrl: OPF.Cfg.restUrl('content/collection/import'),
            successUploaded: function(jsonData, action) {
                this.editPanel.navigationPanel.reload();
            }
        });

        this.resourceListGrid = new OPF.console.resource.view.ResourceGridView(instance);
        this.configurationListGrid = new OPF.console.resource.view.ConfigurationGridView(instance);
        this.scheduleListGrid = new OPF.console.resource.view.ScheduleGridView(instance);

        this.additionalTabs = [
            this.resourceListGrid,
            this.configurationListGrid,
            this.scheduleListGrid
        ];

        this.rnButtons = [
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add folder',
                cls: 'folderIcon',
                iconCls: 'btn-add-folder',
                registryNodeType: OPF.core.utils.RegistryNodeType.FOLDER,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add collection',
                cls: 'collectionIcon',
                iconCls: 'btn-add-collection',
                registryNodeType: OPF.core.utils.RegistryNodeType.COLLECTION,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add text resource',
                cls: 'textResIcon',
                iconCls: 'btn-add-text',
                registryNodeType: OPF.core.utils.RegistryNodeType.TEXT_RESOURCE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add html resource',
                cls: 'htmlResIcon',
                iconCls: 'btn-add-html',
                registryNodeType: OPF.core.utils.RegistryNodeType.HTML_RESOURCE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add image resource',
                cls: 'imageResIcon',
                iconCls: 'btn-add-image',
                registryNodeType: OPF.core.utils.RegistryNodeType.IMAGE_RESOURCE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add audio resource',
                cls: 'audioResIcon',
                iconCls: 'btn-add-audio',
                registryNodeType: OPF.core.utils.RegistryNodeType.AUDIO_RESOURCE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add video resource',
                cls: 'videoResIcon',
                iconCls: 'btn-add-video',
                registryNodeType: OPF.core.utils.RegistryNodeType.VIDEO_RESOURCE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add document resource',
                cls: 'documentResIcon',
                iconCls: 'btn-add-document',
                registryNodeType: OPF.core.utils.RegistryNodeType.DOCUMENT_RESOURCE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add file resource',
                cls: 'fileResIcon',
                iconCls: 'btn-add-file',
                registryNodeType: OPF.core.utils.RegistryNodeType.FILE_RESOURCE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add schedule task',
//                cls: 'scheduleIcon',
//                iconCls: 'btn-add-schedule',
                cls: 'folderIcon',
                iconCls: 'btn-add-folder',
                registryNodeType: OPF.core.utils.RegistryNodeType.SCHEDULE,
                managerLayout: instance
            },
            {
                xtype: 'button',
                scale: 'large',
                tooltip: 'import',
                cls: 'importIcon',
                iconCls: 'btn-import-collection',
                alwaysEnable: true,
                handler: function() {
                    instance.uploadCollectionArchiveDialog.show();
                }
            },
            {
                xtype: 'button',
                scale: 'large',
                tooltip: 'export',
                cls: 'exportIcon',
                iconCls: 'btn-export-collection',
                disabled: true,
                parentRegistryNodeTypes: [
                    OPF.core.utils.RegistryNodeType.COLLECTION
                ],
                handler:function() {
                    var selectedNode = instance.navigationPanel.selectedNode;
                    if (isNotEmpty(selectedNode)) {
                        var selectedNodeId = selectedNode.get('id');
                        var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(selectedNodeId);
                        if (type == OPF.core.utils.RegistryNodeType.COLLECTION) {
                            document.location.href = OPF.Cfg.restUrl('content/collection/export/' + SqGetIdFromTreeEntityId(selectedNodeId));
                        } else {
                            OPF.Msg.setAlert(false, 'Before export a collection you should selected one in cloud navigation tree.');
                        }
                    }
                }
            }
        ];

        this.infoContent = new OPF.core.component.resource.ResourceControl({
            htmlResourceLookup: 'net.firejack.platform.content.abstract-resource.resource.info-html'
        });

        this.callParent(arguments);
    }

});