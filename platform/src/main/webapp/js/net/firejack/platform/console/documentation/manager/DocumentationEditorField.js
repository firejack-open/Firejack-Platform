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


Ext.define('OPF.console.documentation.manager.DocumentationEditorField', {
    extend: 'Ext.container.Container',

    width: '100%',
    height: '100%',

    editor: null,
    editorXType: null,

    constructor: function(editor, editorXType, cfg) {
        cfg = cfg || {};
        OPF.console.documentation.manager.DocumentationEditorField.superclass.constructor.call(this, Ext.apply({
            editor: editor,
            editorXType: editorXType
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.editorComponent = Ext.ComponentMgr.create({
            xtype: this.editorXType,
            editor: this.editor
        });

        this.saveButton = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            html: 'SAVE',
            cls: 'savebutton',
            onClick: function () {
                if (instance.editorXType == 'imageEditor') {
                    instance.editor.saveImage();
                } else {
                    instance.editor.save();
                }
            }
        });

        this.cancelButton = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            html: 'CANCEL',
            cls: 'cancelbutton',
            onClick: function () {
                instance.editor.cancel();
            }
        });

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['jpg', 'jpeg', 'png', 'gif'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/image'),

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                instance.editorComponent.resourceFileOriginalName.setValue(data.orgFilename);
                instance.editorComponent.resourceFileTemporaryName.setValue(data.filename);
                var url = OPF.Cfg.restUrl('content/resource/tmp/') + data.filename;
                instance.editorComponent.imageWidth.setValue(data.width);
                instance.editorComponent.imageHeight.setValue(data.height);
                instance.editorComponent.loadImageContainer(url, data.width, data.height);
            },
            failureUploaded : function(jsonData, action) {
                OPF.Msg.setAlert(false, 'Processed file on the server');
                instance.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Processed file on the server');
            }
        });

        this.uploadButton = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            html: 'UPLOAD',
            cls: 'uploadbutton',
            onClick: function() {
                instance.uploadFileDialog.show();
            }
        });

        this.items = [
            this.editorComponent
        ];
        if (this.editorXType == 'imageEditor') {
            this.items.push(this.uploadButton);
        }
        this.items.push(this.saveButton);
        this.items.push(this.cancelButton);

        this.callParent(arguments);
    },

    show: function() {
        var component = this.callParent(arguments);
        var size = this.getSize();
        if (size.width > 0 && size.height > 0) {
            this.editorComponent.setSize(size.width, size.height);
            this.uploadButton.setPosition(size.width - 186, size.height - 25);
            this.saveButton.setPosition(size.width - 123, size.height - 25);
            this.cancelButton.setPosition(size.width - 76, size.height - 25);
        }
        return component;
    },

    focus: function(selectText, delay) {
        return this.editorComponent.focus(selectText, delay);
    },

    getValue: function() {
        return this.editorComponent.getValue();
    },

    setValue: function(value) {
        this.editorComponent.setValue(value);
    },

    reset: function() {
        this.editorComponent.reset();
    },

    isValid: function() {
        return true;
    }

});
