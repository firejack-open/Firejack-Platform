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

//@tag opf-editor


/**
 * Almost every entity in the registry (nodes and sub-objects alike) support these
 * basic fields during input. This fieldset allows all nodes to share the basic information
 * input fields in a single class.
 */

Ext.define('OPF.core.component.editor.BasicInfoFieldSet', {
    extend: 'Ext.container.Container',

    editPanel: null,

    layout: 'anchor',
    padding: 10,
    cls: 'basic-info-container',

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.BasicInfoFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     * This initComponent method sets up the layout for the field set and allows the
     * entire form to be leveraged across multiple parent forms in the application. 
     */
    initComponent: function() {
        var instance = this;

        this.idField = Ext.ComponentMgr.create({
                xtype: 'hidden',
                name: 'id'
        });

        this.parentIdField = Ext.ComponentMgr.create({
                xtype: 'hidden',
                name: 'parentId'
        });

        this.typeField = Ext.ComponentMgr.create({
                xtype: 'hidden',
                name: 'type'
        });

        this.pathField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            name: 'path',
            fieldLabel: 'Path',
            anchor: '100%'
        });

        this.nameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'name',
            labelWidth: 80,
            fieldLabel: 'Name',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    instance.updateLookup();
                }
            }
        });

        this.lookupField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            name: 'lookup',
            fieldLabel: 'Lookup',
            anchor: '100%'
        });

        this.items = [
            this.idField,
            this.parentIdField,
            this.typeField,    
            this.pathField,
            this.nameField,
            this.lookupField
        ];

        this.callParent(arguments);
    },

    updateLookup: function(selectedNode) {
        var lookup = calculateLookup(this.pathField.getValue(), this.nameField.getValue());
        this.lookupField.setValue(lookup);

        if (isNotEmpty(this.editPanel.resourceFields)) {
            this.editPanel.resourceFields.updateResourceAccessFields();
        }
        return lookup;
    }
    
});
