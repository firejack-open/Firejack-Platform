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
