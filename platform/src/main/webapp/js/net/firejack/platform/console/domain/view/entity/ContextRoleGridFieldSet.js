/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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