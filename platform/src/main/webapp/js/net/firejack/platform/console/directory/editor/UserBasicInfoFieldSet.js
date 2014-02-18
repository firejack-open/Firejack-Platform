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


Ext.define('OPF.console.directory.editor.UserBasicInfoFieldSet', {
    extend: 'Ext.container.Container',
    
    layout: 'anchor',
    padding: 10,
    cls: 'basic-info-container',

    initComponent: function() {
        this.idField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'id'
        });

        this.registryNodeIdField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'registryNodeId',
            value: 0
        });

        this.pathField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Path',
            anchor: '100%',
            readOnly: true,
            name: 'path'
        }),

        this.directoryField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Directory',
            labelWidth: 80,
            readOnly: true,
            anchor: '100%',
            name: 'directory'
        }),

        this.usernameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            fieldLabel: 'Username',
            labelWidth: 80,
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'username-field',
            name: 'username'
        });

        this.emailField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            fieldLabel: 'Email',
            labelWidth: 80,
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'email-field',
            name: 'email'
        });

        this.items = [
            this.idField,
            this.registryNodeIdField,
            this.pathField,
            this.directoryField,
            this.usernameField,
            this.emailField
        ];

        this.callParent(arguments);
    }
});