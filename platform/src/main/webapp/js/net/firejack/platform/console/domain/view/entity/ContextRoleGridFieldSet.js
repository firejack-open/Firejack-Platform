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

/**
 *
 */
Ext.define('OPF.console.domain.view.entity.ContextRoleGridFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.context-role-grid-fieldset',

    layout: 'hbox',

    fieldLabel: 'Roles',
    subFieldLabel: 'Contextual Roles That Would be Available For the Type',

    initComponent: function() {

        this.assignedRolesStore = Ext.create('OPF.console.domain.store.ContextRoles');
        this.rolesGrid = Ext.create('Ext.grid.Panel', {
            height: 240,
            flex: 1,
            store: this.assignedRolesStore,
            columns: [
                { text: 'Role', xtype: 'templatecolumn', flex:1 ,
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                          '<div class="result" style="color: #2A5E83">',
                             '<div><img src="' + OPF.Cfg.fullUrl('/images/default/s.gif') + '" class="tricon-empty tricon-role"/><b>{name} : {lookup}</b></div>',
                             '<span>{description}</span>',
                           '</div>',
                        '</tpl>',
                        '<div class="x-clear"></div>'
                    )}
            ],
            tbar: [
                OPF.Ui.createBtn('Add', 50, 'add-contextual-role', {iconCls: 'silk-add'}),
                '-',
                OPF.Ui.createBtn('Delete', 60, 'delete-role', {iconCls: 'silk-delete'})
            ]
        });

        this.items = [
            this.rolesGrid
        ];

        this.callParent(arguments);
    },

    getEditorDialog: function(idListToSkip) {
        var editorDialog = Ext.WindowMgr.get('contextRoleEditorDialog');
        if (OPF.isEmpty(editorDialog)) {
            editorDialog = Ext.create('OPF.console.domain.view.entity.ContextRoleSelectionDialog', this.rolesGrid);
            Ext.WindowMgr.register(editorDialog);
        } else {
            editorDialog.setGrid(this.rolesGrid);
        }
        editorDialog.setIdListToSkip(idListToSkip);
        return editorDialog;
    },

    cleanFieldStore: function() {
        this.assignedRolesStore.removeAll();
    }

});