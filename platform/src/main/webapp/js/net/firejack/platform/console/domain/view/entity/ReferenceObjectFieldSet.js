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
Ext.define('OPF.console.domain.view.ReferenceObjectFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.reference-object-fieldset',

    layout: 'anchor',

    fieldLabel: 'Reference Object',
    subFieldLabel: '',

    initComponent: function() {
        var me = this;

        this.idField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'referenceId'
        });

        this.headingField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refHeading',
            labelWidth: 180,
            fieldLabel: 'Reference Heading',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.updateExample();
                }
            }
        });

        this.subHeadingField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refSubHeading',
            labelWidth: 180,
            fieldLabel: 'Reference Sub-Heading',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.updateExample();
                }
            }
        });

        this.descriptionField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refDescription',
            labelWidth: 180,
            fieldLabel: 'Reference Description',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.updateExample();
                }
            }
        });

        this.exampleContainer = Ext.ComponentMgr.create({
            xtype: 'label-container',
            labelWidth: 200,
            fieldLabel: 'Example',
            subFieldLabel: '',
            anchor: '100%',
            tpl:
                '<div class="reference-heading">{heading}</div>' +
                '<div class="reference-sub-heading">{subHeading}</div>' +
                '<div class="reference-description">{description}</div>'
        });

        this.items = [
            this.headingField,
            this.subHeadingField,
            this.descriptionField,
            this.exampleContainer
        ];

        this.callParent(arguments);
    },

    updateExample: function() {
        var exampleData = {
            heading: this.headingField.getHtmlValue(),
            subHeading: this.subHeadingField.getHtmlValue(),
            description: this.descriptionField.getHtmlValue()
        };
        this.exampleContainer.update(exampleData);
    }

});