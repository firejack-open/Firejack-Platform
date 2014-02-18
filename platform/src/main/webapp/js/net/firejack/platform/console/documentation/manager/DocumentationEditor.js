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

var CURRENTLY_EDITING = false;

Ext.define('OPF.console.documentation.manager.DocumentationEditor', {
    extend: 'Ext.Editor',

    editorXType: null,
    htmlEditorHeight: 150,
    textEditorHeight: 60,
    alignment: 'tl-tl',
    allowBlur: false,
    updateEl: true,
    completeOnEnter: false,
    cancelOnEsc: false,
    baseCls: 'x-doc-editor',
    shadow: false,

    documentationElement: null,

    constructor: function(editorXType, cfg) {
        cfg = cfg || {};
        OPF.console.documentation.manager.DocumentationEditor.superclass.constructor.call(this, Ext.apply({
            editorXType: editorXType,
            field: new OPF.console.documentation.manager.DocumentationEditorField(this, editorXType)
        }, cfg));
    },

    doAutoSize: function() {
        var sz = this.boundEl.getSize();
        var height = sz.height;
//        if (this.editorXType == 'simplehtmleditor') {
        if (this.editorXType == 'tinymceeditor') {
            height = sz.height < this.htmlEditorHeight ? this.htmlEditorHeight : sz.height;
        }
        height = height < this.textEditorHeight ? this.textEditorHeight : height;
        this.setSize(sz.width, height);
    },

    show: function() {
        this.callParent(arguments);
        this.doAutoSize();
        this.field.show();
    },

    startEdit: function(el, value) {
        this.hideActionButtons();
        var editorHeight = el.offsetHeight;
//        if (this.editorXType == 'simplehtmleditor') {
        if (this.editorXType == 'tinymceeditor') {
            editorHeight = (editorHeight < this.htmlEditorHeight ? this.htmlEditorHeight : editorHeight);
        } else if (this.editorXType == 'textarea') {
            editorHeight = (editorHeight < this.textEditorHeight ? this.textEditorHeight : editorHeight);
        } else if (this.editorXType == 'imageEditor') {
            editorHeight += 35;
        }
        var originalElHeight = el.height;
        el.style.height = editorHeight + 'px';
        this.boundEl = Ext.get(el);
        CURRENTLY_EDITING = true;

        this.callParent(arguments);

        if (this.editorXType == 'imageEditor') {
            if (el.tagName == 'IMG') { // for new img resources it's only a DIV with 'no image provided' text
                this.field.editorComponent.loadImageContainer(el.src, el.width, originalElHeight);
            } else {
                this.field.editorComponent.setImageContainerToNotProvided();
            }
        }

//        if (this.editorXType == 'tinymceeditor') {
//            this.field.editorComponent.onInit();
//        }
    },

    hideEdit: function(remainVisible) {
        this.showActionButtons();
        this.boundEl.dom.style.height = '';
        CURRENTLY_EDITING = false;
//        if (this.editorXType == 'tinymceeditor') {
//            this.field.editorComponent.onDestroy();
//        }
        this.callParent(arguments);
    },

    listeners: {
        startedit: function(boundEl, value) {
            this.boundEl = boundEl;
        }
    },

    setDocumentationElement: function(documentationElement) {
        this.documentationElement = documentationElement;
    },

    showActionButtons: function() {
        this.documentationElement.actionComponent.show();
    },

    hideActionButtons: function() {
        this.documentationElement.actionComponent.hide();
    },

    saveImage: function() {
        var instance = this;

        var jsonData = this.field.editorComponent.getJsonData();

        if (isEmpty(jsonData.resourceFileOriginalName)) {
            OPF.Msg.setAlert(false, 'Please upload an image.');
            return false;
        }

        jsonData.id = this.documentationElement.editableSrcData.resourceVersionId;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/documentation/image/' + jsonData.id),
            method: 'PUT',
            jsonData: {"data": jsonData},

            success: function(response, action) {
                OPF.Msg.setAlert(true, 'Image saved successfully.');

                instance.completeEdit();
                instance.hideEdit(false);

                var width = instance.field.editorComponent.imageWidth.getValue();
                var padding = 5;
                if (width > OPF.DCfg.MAX_IMAGE_WIDTH) {
                    width = OPF.DCfg.MAX_IMAGE_WIDTH;
                } else {
                    padding = Math.floor((OPF.DCfg.MAX_IMAGE_WIDTH - width) / 2 + 4);
                }

                var imageSrcData = null;
                Ext.each(instance.boundEl.dom.attributes, function(attribute, index) {
                    if (attribute.name == 'src' && isNotBlank(attribute.nodeValue)) {
                        imageSrcData = eval('(' + attribute.nodeValue + ')');
                        return false;
                    }
                });

                var imgElement =
                    '<img ' +
                        'id="'+ instance.boundEl.dom.id + '" ' +
                        'src="' + imageSrcData.imageUrl + '?" ' +
                        'class="description editable editor-image" ' +
                        'width="' + width + '" ' +
                        'style="padding-left:' + padding + 'px; padding-right:' + padding + 'px;"' +
                    '/>';
                instance.boundEl.insertHtml('afterBegin', imgElement);
            },

            failure:function(response) {
                OPF.Msg.setAlert(true, 'Error occured while saving image.');
                instance.cancelEdit();
            }
        });

    },

    save: function() {
        var instance = this;

        var src = this.documentationElement.editableSrcData;
        var regNodeId =  isNotEmpty(src.registryNodeId) ? src.registryNodeId : OPF.DCfg.REGISTRY_NODE_ID;

        var jsonData = {
            lookup: OPF.DCfg.LOOKUP,
            country: OPF.DCfg.COUNTRY,
            registryNodeId: regNodeId,
            resourceVersionId: src.resourceVersionId,
            resourceId: src.resourceId,
            lookupSuffix: src.lookupSuffix,
            value: this.getValue(),
//            resourceType: this.editorXType == 'simplehtmleditor' ? 'HTML' : 'TEXT'
            resourceType: this.editorXType == 'tinymceeditor' ? 'HTML' : 'TEXT'
        };

        var url = OPF.Cfg.restUrl('content/documentation');
        var method;
        if (isNotEmpty(src.resourceVersionId)) {
            url += '/' + src.resourceVersionId;
            method = 'PUT';
        } else {
            method = 'POST';
        }

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},

            success:function(response, action) {
                var vo = Ext.decode(response.responseText);
                var data = vo.data[0];
                if (isNotEmpty(data.resourceVersionId)) {
                    var src = '{resourceVersionId: ' + data.resourceVersionId + '}';
                    instance.documentationElement.editableSrcData.resourceVersionId = data.resourceVersionId;
                    OPF.Msg.setAlert(true, 'Description has saved success. ' + src);

                    instance.completeEdit();
                    var v = instance.getValue();
                    if(instance.updateEl && instance.boundEl) {
                        instance.boundEl.set({src: src});
                        instance.boundEl.update(v);
                    }
                    instance.hideEdit(false);
                    instance.fireEvent("complete", instance, v, instance.startValue);
                } else {
                    OPF.Msg.setAlert(true, 'Description has not saved.');
                    instance.cancelEdit();
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(true, 'Description has not saved.');
                instance.cancelEdit();
            }
        });
        return false;
    },

    cancel: function(editButton) {
        this.showActionButtons();
        this.cancelEdit();
    }

});