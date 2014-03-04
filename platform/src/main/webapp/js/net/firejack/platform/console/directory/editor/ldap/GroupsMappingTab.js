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

Ext.define('OPF.console.directory.editor.ldap.GroupsMappingTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.ldap-groups-tab',

    title: 'Groups Mapping',

    parentDialog: null,

    constructor: function(parentDialog, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.ldap.GroupsMappingTab.superclass.constructor.call(this, Ext.apply({
            parentDialog: parentDialog
        }, cfg));
    },

    initComponent: function() {
        var me = this;
        this.searchGroupsField = Ext.create('Ext.form.field.Text', { width: 150 });
        this.ldapGroupsStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.directory.model.Group',
            proxy: {
                type: 'memory',
                reader: { type: 'json' },
                writer: { type: 'json' }
            }
        });
        this.ldapGroupsGridView = Ext.create('Ext.grid.Panel', {
            height: 200,
            store: this.ldapGroupsStore,
            tbar: [
                OPF.Ui.xSpacer(5),
                this.searchGroupsField,
                OPF.Ui.xSpacer(5),
                Ext.create('Ext.button.Button', {
                    text: 'Search',
                    handler: function(btn) {
                        me.searchGroups(me.searchGroupsField.getValue());
                    }
                }),
                '->',
                Ext.create('Ext.button.Button', {
                    text: 'Import Group',
                    handler: function(btn) {
                        var selectedGroup = me.ldapGroupsGridView.getSelectionModel().getLastSelected();
                        me.importGroup(selectedGroup);
                    }
                })
            ],
            columns: [
                { dataIndex: 'name', text: 'Group Name', sortable: false,
                    renderer: 'htmlEncode', flex: 1, menuDisabled: true }
            ],
            listeners: {
                itemclick: function(gridView, model) {
                    if (me.editedLdapGroup != model) {
                        me.editedLdapGroup = null;
                        me.selectGroup(null);
                    }
                },
                itemdblclick: function(gridView, model) {
                    me.editedLdapGroup = me.ldapGroupsGridView.getSelectionModel().getLastSelected();
                    me.selectGroup(model.get('name'));
                }
            }
        });
        
        this.groupNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'left',
            fieldLabel: 'Group Name',
            subFieldLabel: '',
            name: 'name',
            width: 250
        });
        this.groupNameField.setReadOnly(true);

        this.availableGroupsStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.directory.model.Group',
            proxy: {
                type: 'memory',
                reader: { type: 'json' },
                writer: { type: 'json' }
            }
        });

        this.availableGroupsGrid = Ext.create('Ext.grid.Panel', {
            store: this.availableGroupsStore,
            flex: 1,
            columns: [
                { dataIndex: 'lookup', text: 'Available Groups', sortable: false,
                    renderer: 'htmlEncode', flex: 1, menuDisabled: true }
            ]
        });

        this.mappedGroupsStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.directory.model.Group',
            proxy: {
                type: 'memory',
                reader: { type: 'json' },
                writer: { type: 'json' }
            }
        });

        this.mappedGroupsGrid = Ext.create('Ext.grid.Panel', {
            store: this.mappedGroupsStore,
            flex: 1,
            columns: [
                { dataIndex: 'lookup', text: 'Mapped Groups', sortable: false,
                    renderer: 'htmlEncode', flex: 1, menuDisabled: true }
            ]
        });

        this.mapAllGroupsButton = Ext.create('Ext.button.Button', {
            text: '>>',
            width: 30,
            handler: function(btn) {
                var availableGroups = me.availableGroupsStore.getRange();
                me.mappedGroupsStore.add(availableGroups);
                me.availableGroupsStore.removeAll();
            }
        });

        this.mapSelectedGroupsButton = Ext.create('Ext.button.Button', {
            text: '>',
            width: 30,
            handler: function(btn) {
                var selectedModels = me.availableGroupsGrid.getSelectionModel().getSelection();
                if (selectedModels != null && selectedModels.length > 0) {
                    me.mappedGroupsStore.add(selectedModels);
                    me.availableGroupsStore.remove(selectedModels);
                }
            }
        });

        this.unmapSelectedGroupsButton = Ext.create('Ext.button.Button', {
            text: '<',
            width: 30,
            handler: function(btn) {
                var selectedModels = me.mappedGroupsGrid.getSelectionModel().getSelection();
                if (selectedModels != null && selectedModels.length > 0) {
                    me.availableGroupsStore.add(selectedModels);
                    me.mappedGroupsStore.remove(selectedModels);
                }
            }
        });

        this.unmapAllGroupsButton = Ext.create('Ext.button.Button', {
            text: '<<',
            width: 30,
            handler: function(btn) {
                var assignedUsers = me.mappedGroupsStore.getRange();
                me.availableGroupsStore.add(assignedUsers);
                me.mappedGroupsStore.removeAll();
            }
        });

        this.items = [
            this.ldapGroupsGridView,
            {
                //xtype: 'container',
                xtype: 'panel',
                items: [
                    {
                        xtype: 'container',
                        layout: { type: 'hbox', align: 'middle', pack: 'center' },
                        height: 50,
                        items: [
                            this.groupNameField
                        ]
                    },
                    {
                        xtype: 'container',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        height: 180,
                        items: [
                            {xtype: 'container', width: 20},
                            this.availableGroupsGrid,
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'center',
                                    pack: 'center'
                                },
                                width: 50,
                                items: [
                                    this.mapAllGroupsButton,
                                    this.mapSelectedGroupsButton,
                                    this.unmapSelectedGroupsButton,
                                    this.unmapAllGroupsButton
                                ]
                            },
                            this.mappedGroupsGrid,
                            {xtype: 'container', width: 20}
                        ]
                    },
                    { xtype: 'container', height: 20 }
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [
                            [
                                '->',
                                Ext.create('Ext.button.Button', {
                                    text: 'Save', width: 50,
                                    handler: function() {
                                        if (OPF.isNotEmpty(me.editedLdapGroup)) {
                                            var formData = [];
                                            var mappedGroupModels = me.mappedGroupsStore.getRange();
                                            if (mappedGroupModels != null && mappedGroupModels.length > 0) {
                                                for (var i = 0; i < mappedGroupModels.length; i++) {
                                                    var groupMapping = {
                                                        group : {
                                                            id: mappedGroupModels[i].get('id'),
                                                            lookup: mappedGroupModels[i].get('lookup')
                                                        }
                                                    };
                                                    formData.push(groupMapping);
                                                }
                                            }
                                            var url = OPF.Cfg.restUrl('/directory/group-mapping/by-group-dn?groupDN=');
                                            url += me.editedLdapGroup.get('distinguishedName');
                                            url += "&directoryId=" + me.parentDialog.directoryId;
                                            Ext.Ajax.request({
                                                url: url,
                                                method: 'PUT',
                                                jsonData: {"dataList": formData},
                                                success: function(response) {
                                                    var vo = Ext.decode(response.responseText);
                                                    if (vo.success) {
                                                        if (OPF.isEmpty(vo.data) || vo.data.length == 0) {
                                                            OPF.Msg.setAlert(OPF.Msg.STATUS_OK,
                                                                'No group mapping data returned from server. Server message: ' + vo.message);
                                                        } else {
                                                            Ext.MessageBox.alert('Information', vo.message);
                                                            me.selectGroup(null);
                                                        }
                                                    } else {
                                                        OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                                                    }
                                                },

                                                failure: function(response) {
                                                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                                                }
                                            });
                                        }
                                        /*var formData = {name: me.groupNameField.getValue()};
                                        var mappedGroupModels = me.mappedGroupsStore.getRange();
                                        if (mappedGroupModels != null && mappedGroupModels.length > 0) {
                                            var mappedGroups = [];
                                            for (var i = 0; i < mappedGroupModels.length; i++) {
                                                mappedGroups.push({
                                                    id: mappedGroupModels[i].get('id'),
                                                    lookup: mappedGroupModels[i].get('lookup')
                                                });
                                            }
                                            formData.mappedGroups = mappedGroups;
                                        }

                                        Ext.Ajax.request({
                                            url: OPF.Cfg.restUrl('/directory/group-mapping/by-group-dn') +
                                                formData.name + '?directoryId=' + me.parentDialog.directoryId,
                                            method: 'PUT',
                                            jsonData: {"data": formData},
                                            success: function(response) {
                                                var vo = Ext.decode(response.responseText);
                                                if (vo.success) {
                                                    if (OPF.isEmpty(vo.data) || vo.data.length == 0) {
                                                        OPF.Msg.setAlert(OPF.Msg.STATUS_OK,
                                                            'No group data returned from server. Server message: ' + vo.message);
                                                    } else {
                                                        Ext.MessageBox.alert('Information', vo.message);
                                                        me.selectGroup(null);
                                                    }
                                                } else {
                                                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                                                }
                                            },

                                            failure: function(response) {
                                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                                            }
                                        });*/
                                    }
                                }),
                                Ext.create('Ext.button.Button', {
                                    text: 'Reset', width: 55,
                                    handler: function() {
                                        me.selectGroup(null);
                                    }
                                }),
                                Ext.create('Ext.button.Button', {
                                    text: 'Cancel', width: 60,
                                    handler: function() {
                                        me.parentDialog.hide();
                                    }
                                })
                            ]
                        ]
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    listeners: {
        activate: function() {
            this.searchGroups(null);
        }
    },

    searchGroups: function(searchTerm) {
        var url = OPF.Cfg.restUrl('/directory/ldap/group/search', true);
        url += '?directoryId=' + this.parentDialog.directoryId;
        if (OPF.isNotBlank(searchTerm)) {
            url += '&term=' + searchTerm;
        }
        var me = this;
        Ext.Ajax.request({
            url: url,
            method: 'GET',

            success: function(response) {
                var vo = Ext.decode(response.responseText);
                if (vo.success) {
                    me.ldapGroupsStore.loadData(OPF.isEmpty(vo.data) || vo.data.length == 0 ? [] : vo.data);
                    me.selectGroup(null);

                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                } else {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
            }
        });
    },

    selectGroup: function(groupName) {
        var isNotBlank = OPF.isNotBlank(groupName);
        this.groupNameField.setValue('');
        this.groupNameField.disable();
        this.availableGroupsStore.loadData([]);
        this.mappedGroupsStore.loadData([]);
        this.availableGroupsGrid.disable();
        this.mappedGroupsGrid.disable();
        this.mapAllGroupsButton.disable();
        this.mapSelectedGroupsButton.disable();
        this.unmapSelectedGroupsButton.disable();
        this.unmapAllGroupsButton.disable();
        if (isNotBlank) {
            var url = OPF.Cfg.restUrl('/directory/group-mapping/load-by-ldap-group?directoryId=');
            url += this.parentDialog.directoryId;
            url += '&groupDN=' + this.editedLdapGroup.get('distinguishedName');
            var me = this;
            this.ldapGroupsGridView.disable();
            Ext.Ajax.request({
                url: url,
                method: 'GET',
                success: function(response) {
                    me.availableGroupsStore.removeAll();
                    me.mappedGroupsStore.removeAll();
                    var vo = Ext.decode(response.responseText);
                    if (vo.success) {
                        me.groupNameField.enable();
                        me.groupNameField.setValue(me.editedLdapGroup.get('name'));
                        me.availableGroupsGrid.enable();
                        me.mappedGroupsGrid.enable();
                        if (OPF.isNotEmpty(vo.data) && vo.data.length > 0) {
                            var mappedGroups = [];//OPF.isEmpty(vo.data[0].mappedGroups) ? [] : vo.data[0].roles;
                            if (vo.data.length > 1) {
                                for (var i = 1; i < vo.data.length; i++) {
                                    mappedGroups.push(vo.data[i].group);
                                }
                            }
                            //The first group mapping is a fake GroupMapping object and only the first mapping has information about available groups
                            var availableGroups = OPF.isEmpty(vo.data[0].availableGroups) ? [] : vo.data[0].availableGroups;
                            me.availableGroupsStore.loadData(availableGroups);
                            me.mappedGroupsStore.loadData(mappedGroups);
                        }
                        me.mapAllGroupsButton.enable();
                        me.mapSelectedGroupsButton.enable();
                        me.unmapSelectedGroupsButton.enable();
                        me.unmapAllGroupsButton.enable();
                    } else {
                        OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                    }
                    me.ldapGroupsGridView.enable();
                },

                failure: function(response) {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                    me.ldapGroupsGridView.enable();
                }
            });
        }
    },

    importGroup: function(selectedGroup) {
        if (OPF.isNotEmpty(selectedGroup)) {
            var groupDN = selectedGroup.get('distinguishedName');
            var url = OPF.Cfg.restUrl('/directory/group-mapping/import?groupDN=' + groupDN);
            url += "&directoryId=" + this.parentDialog.directoryId;
            var me = this;
            Ext.Ajax.request({
                url: url,
                method: 'POST',
                success: function(response) {
                    var vo = Ext.decode(response.responseText);

                    Ext.MessageBox.alert(vo.success ? 'Information' : 'Error', vo.message);
                    me.selectGroup(null);
                },

                failure: function(response) {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                }
            });

        }
    }

});
