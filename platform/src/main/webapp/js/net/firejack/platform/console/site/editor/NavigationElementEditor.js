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
Ext.define('OPF.console.site.editor.NavigationElementEditor', {
    extend: 'OPF.core.component.editor.BaseSupportedPermissionEditor',

    title: 'ELEMENT: [New]',

    infoResourceLookup: 'net.firejack.platform.site.navigation-element',

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.elementTypeField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            labelAlign: 'top',
            fieldLabel: 'Type',
            subFieldLabel: '',
            name: 'elementType',
            width: 150,
            store: '',
            emptyText: 'Page (default)',
            editable: false,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    me.updatePageUrl();
                },
                select: function(cmp, e) {
                    me.updatePageUrl();
                }
            }
        });

        this.pageUrlField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Page Url',
            subFieldLabel: '',
            anchor: '100%',
            name: 'pageUrl',
            readOnly: true,
            flex: 1
        });

        var additionalLeftPanelControls = [
            {
                xtype: 'fieldcontainer',
                layout: 'hbox',
                items: [
                    this.elementTypeField,
                    {
                        xtype: 'splitter'
                    },
                    this.pageUrlField
                ]
            }
        ];

        this.resourceFields = new OPF.core.component.editor.ResourceFieldSet(this, {
            needMethodField: false,
            additionalLeftPanelControls: additionalLeftPanelControls,
            updateResourceAccessFields: function() {
                me.elementTypeField.setReadOnly(this.editPanel.saveAs == 'update');
                me.pageUrlField.setReadOnly(this.editPanel.saveAs == 'update');
                this.updateUrlPath();
                this.updateResourceUrl();
            }
        });

        this.additionalBlocks = [
            this.resourceFields
        ];

        this.callParent(arguments);
    },

    updatePageUrl: function() {
        var elementType = this.elementTypeField.getValue();
        if ('PAGE' == elementType) {
            this.pageUrlField.setFieldLabel('Page Url');
        } else if ('WIZARD' == elementType) {
            this.pageUrlField.setFieldLabel('Wizard Lookup');
        } else {
            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Element Type: \'' + elementType + '\' doesn\'t support.');
        }
    }

});


