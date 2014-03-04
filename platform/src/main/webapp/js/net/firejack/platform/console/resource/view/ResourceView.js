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