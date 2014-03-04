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

Ext.define('OPF.core.component.editor.DescriptionInfoFieldSet', {
    extend: 'Ext.container.Container',

    fieldLabel: 'Main Information',

    editPanel: null,
    componentFields: [],
    layout: 'anchor',
    border: false,

    constructor: function(editPanel, componentFields, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.BasicInfoFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel,
            componentFields: isNotEmpty(componentFields) ? componentFields : []
        }, cfg));
    },

    /**
     * This initComponent method sets up the layout for the field set and allows the
     * entire form to be leveraged across multiple parent forms in the application. 
     */
    initComponent: function() {
        var instance = this;

        this.descriptionField = Ext.ComponentMgr.create({
            xtype: 'opf-htmleditor',
            name: 'description',
            anchor: '100%',
            labelAlign: 'top',
            fieldLabel: 'Description',
            subFieldLabel: 'Enter a basic description of this asset',
            allowBlank: true
        });

        this.items = [
            this.descriptionField,
            this.componentFields
        ];

        this.callParent(arguments);
    }
    
});
