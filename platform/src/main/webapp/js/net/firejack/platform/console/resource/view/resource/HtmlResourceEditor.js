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
Ext.define('OPF.console.resource.view.resource.HtmlResourceEditor', {
    extend: 'OPF.console.resource.view.resource.BaseResourceEditor',
    alias: 'widget.html-resource-editor',

    title: 'HTML: [New]',

    infoResourceLookup: 'net.firejack.platform.content.abstract-resource.resource.html-resource',

    noContentDefined: '<font title="empty-text" color="#c0c0c0">No Content Defined</font>',

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.resourceVersionHtmlField = new OPF.core.component.tinymce.TinyMCEEditor(null, {
            hideLabel: true,
            height: 200,
            name: 'resourceVersionHtml'
        });

        this.additionalFieldSet = Ext.ComponentMgr.create({
            xtype: 'label-container',
            fieldLabel: 'Html',
            subFieldLabel: '',
            layout: 'fit',
            height: 200,
            items: [
                this.resourceVersionHtmlField
            ]
        });

        this.callParent(arguments);
    },

    onAfterSetSpecificValue: function(jsonData) {
        this.resourceVersionHtmlField.setValue(jsonData.resourceVersion.html);
    },

    onBeforeSpecificDataSave: function(formData) {
        if (formData.resourceVersionHtml != this.noContentDefined) {
            formData.resourceVersion.html = formData.resourceVersionHtml;
        }
        delete formData.resourceVersionHtml;
    },

    onReloadResourceVersionFailure: function() {
        this.resourceVersionHtmlField.setValue(this.noContentDefined);
    },

    onSuccessDeleteResourceVersion: function() {
        this.resourceVersionHtmlField.setValue(this.noContentDefined);
    }

});