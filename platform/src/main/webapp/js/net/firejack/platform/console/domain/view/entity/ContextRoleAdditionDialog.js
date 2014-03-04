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


Ext.define('OPF.console.domain.view.entity.ContextRoleSelectionDialog', {
    extend: 'Ext.window.Window',

    alias: 'widget.context-role-selection-dlg',

    id: 'contextRoleEditorDialog',
    title: 'Context Role Assignment',
    modal: true,
    closable: true,
    closeAction: 'hide',

    grid: null,
    rowIndex: null,

    layout: 'fit',
    width: 420,
    height: 500,
    resizable: false,

    constructor: function(grid, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.entity.ContextRoleSelectionDialog.superclass.constructor.call(this, Ext.apply({
            grid: grid
        }, cfg));
    },

    initComponent: function() {

        this.assignButton = OPF.Ui.createBtn('Assign', 60, 'select-context-role', {
            iconCls: 'silk-add',
            disabled: true
        });

        this.availableRolesStore = Ext.create('OPF.console.domain.store.ContextRoles');
        this.availableRolesGrid = Ext.create('Ext.grid.Panel', {
            multiSelect: true,
            store: this.availableRolesStore,
            columns: [
                { text: 'Role', xtype: 'templatecolumn', flex:1 ,
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                          '<div class="result context-role-item" style="color: #2A5E83">',
                             '<div><img src="' + OPF.Cfg.fullUrl('/images/default/s.gif') + '" class="tricon-empty tricon-role"/><b>{name} : {lookup}</b></div>',
                             '<span>{description}</span>',
                           '</div>',
                        '</tpl>',
                        '<div class="x-clear"></div>'
                    )}
            ],
            tbar: [
                this.assignButton
            ]
        });
        this.items = this.availableRolesGrid;
        this.callParent(arguments);
    },

    setGrid: function(grid) {
        this.grid = grid;
    },

    setIdListToSkip: function(idListToSkip) {
        var url = OPF.core.utils.RegistryNodeType.ROLE.generateUrl('/context');
        if (idListToSkip != null && Ext.isArray(idListToSkip) && idListToSkip.length > 0) {
            url += '?';
            for (var i = 0; i < idListToSkip.length; i++) {
                url += 'excludeIds=' + idListToSkip[i];
                if (i < idListToSkip.length - 1) {
                    url += '&';
                }
            }
        }
        var me = this;
        Ext.Ajax.request({
            url: url,
            method: 'GET',
            success: function(response) {
                var jsonData = Ext.decode(response.responseText);
                if (jsonData.success) {
                    if (OPF.isNotEmpty(jsonData.data) && Ext.isArray(jsonData.data)) {
                        me.availableRolesStore.loadData(jsonData.data);
                        me.assignButton.setDisabled(jsonData.data.length == 0);
                    }
                } else {
                    Ext.MessageBox.alert('Error', jsonData.message);
                }
            }
        });
    }

});