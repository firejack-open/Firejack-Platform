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

Ext.define('OPF.console.NavigationPageLayout', {
    extend: 'OPF.console.PageLayout',

    additionalTabs: [],

    isEditorSupport: true,

    defaultEntity: null,

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.navigationPanel = new OPF.core.component.CloudNavigation(this, this.defaultEntity);

        this.defaultInfoContentTab = {
            xtype: 'panel',
            title: 'INFO',
            padding: 10,
            contentEl: 'info-content'
        };

        if (this.infoContent) {
            this.infoContentTab = {
                xtype: 'panel',
                title: 'INFO',
                padding: 10,
                border: false,
                items: [
                    this.infoContent
                ]
            }
        }

        if (isEmpty(this.tabPanel)) {
            this.tabPanel = Ext.ComponentMgr.create({
                xtype: 'tabpanel',
                border: false,
                activeTab: 0,
                itemId: 'main-tabs',
                items: [
                    this.infoContentTab || this.defaultInfoContentTab,
                    this.additionalTabs
                ],
                listeners: {
                    beforeadd: function(tabPanel, panel, index) {
                        var type = panel.registryNodeType;
                        if (isNotEmpty(type)) {
                            Ext.each(me.registryNodeButtons, function(registryNodeButton) {
                                if (type == registryNodeButton.registryNodeType) {
                                    registryNodeButton.disable();
                                }
                            });
                        }
                    }
                }
            });
        }

        if (this.isToolBarRequired() && OPF.isEmpty(this.tbar)) {
            this.addRootDomainButton = Ext.ComponentMgr.create({
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add root domain',
                cls: 'rootButton',
                iconCls: 'btn-add-root-domain',
                disabled: false,
                registryNodeType: OPF.core.utils.RegistryNodeType.ROOT_DOMAIN,
                parentRegistryNodeTypes: [
                    OPF.core.utils.RegistryNodeType.EMPTY
                ],
                managerLayout: me
            });

            this.addDomainButton = Ext.ComponentMgr.create({
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add domain',
                cls: 'domainButton',
                iconCls: 'btn-add-domain',
                registryNodeType: OPF.core.utils.RegistryNodeType.DOMAIN,
                managerLayout: me
            });

            this.deleteButton = Ext.ComponentMgr.create({
                xtype: 'button',
                scale: 'large',
                cls: 'deleteIcon',
                tooltip: 'delete',
                iconCls: 'btn-delete',
                disabled: true,
                handler: function () {
                    Ext.Msg.show({
                        title:'Delete',
                        msg: 'Are you sure?',
                        buttons: Ext.Msg.YESNO,
                        fn: function(buttonId) {
                            if (buttonId == 'yes') {
                                me.deleteEntity();
                            }
                        },
                        animEl: 'elId',
                        icon: Ext.MessageBox.QUESTION
                    });
                }
            });

            this.securityButton = Ext.ComponentMgr.create({
                xtype: 'button',
                scale: 'large',
                tooltip: 'edit security',
                cls: 'securityIcon',
                iconCls: 'btn-secure',
                disabled: true,
                handler: function () {
                    me.showSecurityWindow();
                }
            });

            this.registryNodeButtons = [
                this.addRootDomainButton,
                this.addDomainButton
            ];

            this.toolbarButtons = [
                this.addRootDomainButton,
                {
                    xtype: 'tbseparator'
                },
                this.addDomainButton,
                {
                    xtype: 'tbseparator'
                }
            ];
            Ext.each(this.rnButtons, function(button, index) {
                if (!button.hidden) {
                    var addButton = Ext.ComponentMgr.create(button);
                    me.toolbarButtons.push(
                        addButton,
                        {
                            xtype: 'tbseparator'
                        }
                    );
                    me.registryNodeButtons.push(addButton);
                }
            });

            this.toolbarButtons.push(
                this.deleteButton,
                {
                    xtype: 'tbseparator'
                },
                this.securityButton,
                {
                    xtype: 'tbseparator'
                }
            );

            this.tbar = {
                xtype: 'toolbar',
                itemId: 'main-tools',

                items: this.toolbarButtons
            };
        }

        this.westPanel = this.navigationPanel;
        this.centerPanel = {
            region: "center",
            layout: 'fit',
            border: false,
            items : [
                this.tabPanel
            ]
        };

        this.callParent(arguments);
    },

    isToolBarRequired: function() {
        return true;
    },

    isPageSupportType: function(type) {
        var isPageSupportType = false;
        if (isNotEmpty(this.registryNodeButtons)) {
            Ext.each(this.registryNodeButtons, function(registryNodeButton) {
                isPageSupportType |= registryNodeButton.registryNodeType == type;
            });
        }
        return isPageSupportType;
    },

    refreshToolbarButtons: function(deselect) {
        var me = this;
        if (this.isEditorSupport) {
            var selectedNode = this.navigationPanel.selectedNode;
            if (isNotEmpty(selectedNode)) {
                var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(selectedNode.data.id);
                if (isNotEmpty(type)) {
                    Ext.each(this.registryNodeButtons, function(registryNodeButton) {
                        if (registryNodeButton.alwaysEnable) {
                            registryNodeButton.enable();
                        } else if (isNotEmpty(registryNodeButton.registryNodeType)) {
                            var chainNodeTypes = me.navigationPanel.chainNodeTypes();
                            var contains = type.containsAllowType(registryNodeButton.registryNodeType, chainNodeTypes);
                            if (contains) {
                                var editPanel = me.tabPanel.getComponent(registryNodeButton.registryNodeType.type + 'EditPanel');
                                if (isEmpty(editPanel)) {
                                    registryNodeButton.enable();
                                }
                            } else {
                                registryNodeButton.disable();
                            }
                        } else if (isNotEmpty(registryNodeButton.parentRegistryNodeTypes)) {
                            var isContains = false;
                            Ext.each(registryNodeButton.parentRegistryNodeTypes, function(parentRegistryNodeType) {
                                isContains |= type == parentRegistryNodeType;
                            });
                            if (isContains) {
                                registryNodeButton.enable();
                            } else {
                                registryNodeButton.disable();
                            }
                        }
                    });
                }
                if (OPF.isNotEmpty(this.deleteButton)) {
                    this.deleteButton.enable();
                }
                if (OPF.isNotEmpty(this.securityButton)) {
                    this.securityButton.enable();
                }
            } else {
                if (OPF.isNotEmpty(this.deleteButton)) {
                    this.deleteButton.disable();
                }
                if (OPF.isNotEmpty(this.securityButton)) {
                    this.securityButton.disable();
                }
            }
            if (deselect) {
                Ext.each(this.registryNodeButtons, function(registryNodeButton) {
                    var isContains = registryNodeButton.alwaysEnable;
                    Ext.each(registryNodeButton.parentRegistryNodeTypes, function(parentRegistryNodeType) {
                        isContains |= OPF.core.utils.RegistryNodeType.EMPTY == parentRegistryNodeType;
                    });
                    if (isContains) {
                        registryNodeButton.enable();
                    } else {
                        registryNodeButton.disable();
                    }
                });
            }
        }
    },

    deleteEntity: function() {
        var me = this;
        var selectedNode = this.navigationPanel.selectedNode;
        if (isNotEmpty(selectedNode)) {
            if (selectedNode.data.canDelete) {
                var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(selectedNode.data.id);
                if (isNotEmpty(type)) {
                    var id = SqGetIdFromTreeEntityId(selectedNode.data.id);
                    var url = type.generateGetUrl(id);

                    var mask = new Ext.LoadMask(this.navigationPanel.getEl(), {msg: 'Deleting...'});
                    mask.show();

                    Ext.Ajax.request({
                        url: url,
                        method: 'DELETE',
                        jsonData: [],

                        success:function(response, action) {
                            mask.hide();
                            var vo = Ext.decode(response.responseText);

                            OPF.Msg.setAlert(vo.success, vo.message);
                            if (vo.success) {
                                var editPanel = me.tabPanel.getComponent(type.toString() + 'EditPanel');
                                if (isNotEmpty(editPanel)) {
                                    var displayedEntityId = editPanel.nodeBasicFields.idField.getValue();
                                    if (id == displayedEntityId) {
                                        editPanel.hideEditPanel();
                                    }
                                }
                                if (OPF.core.utils.RegistryNodeType.ROOT_DOMAIN == type) {
                                    me.navigationPanel.getRootNode().rendered = false;
                                }
                                selectedNode.parentNode.removeChild(selectedNode);

                                me.navigationPanel.selectedNode = null;
                                me.refreshToolbarButtons(true);
                            }
                        },

                        failure:function(response) {
                            mask.hide();
                            OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                        }
                    });
                }
            } else {
                OPF.Msg.setAlert(false, 'Can\'t delete node. You have not proper permissions.');
            }
        }
    },

    showSecurityWindow: function() {
        this.securityWindow = Ext.WindowMgr.get('securityWindow');
        if (isEmpty(this.securityWindow)) {
            this.securityWindow = Ext.create('OPF.console.security.SecurityWindow', this);
            Ext.WindowMgr.register(this.securityWindow);
        }
        this.securityWindow.show();
    },

    openEditor: function(editEntity, node) {
        var me = this;

        if (isNotEmpty(editEntity)) {
            var type = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(editEntity.type);

            if (isNotEmpty(type)) {
                Ext.Ajax.request({
                    url: type.generateGetUrl(editEntity.id),
                    method: 'GET',
                    jsonData: '[]',

                    success: function(response, action) {
                        var editPanel = type.createEditPanel(me);
                        editPanel.saveAs = 'update';
                        editPanel.selfNode = node;

                        var registryJsonData = Ext.decode(response.responseText);
                        if (OPF.isNotEmpty(registryJsonData.data)) {
                            editPanel.showEditPanel(registryJsonData.data[0]);
                        } else {
                            OPF.Msg.setAlert(false, registryJsonData.message);
                        }
                    },

                    failure: function(response) {
                        OPF.Msg.setAlert(false, response.message);
                    }
                });
            }
        }
        this.editEntity = null;
    }

});
