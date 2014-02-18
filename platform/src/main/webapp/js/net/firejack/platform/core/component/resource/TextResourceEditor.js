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


Ext.define('OPF.core.component.resource.TextResourceEditor', {
    extend: 'Ext.window.Window',

    id: 'textResourceEditorWindow',
    title: 'Edit Text Resource',
    closeAction: 'hide',
    modal: true,
    minWidth: 400,
    minHeight: 200,
    layout: 'fit',
    constrainHeader: true,

    editorXType: null,
    editingEl: null,

    constructor: function(id, editorXType, cfg) {
        cfg = cfg || {};
        var title;
        if (editorXType == 'textarea') {
            title = 'Edit Text Resource';
        } else if (editorXType == 'tinymceeditor') {
            title = 'Edit HTML Resource';
        }
        OPF.core.component.resource.TextResourceEditor.superclass.constructor.call(this, Ext.apply({
            id: id,
            editorXType: editorXType,
            title: title
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.cultureCombo = new OPF.core.component.CultureComboBox();

        this.bbar = Ext.create('Ext.toolbar.Toolbar', {
            items: [
                'Language:',
                this.cultureCombo,
                '->',
                {
                    xtype: 'button',
                    text: 'Save',
                    handler: function () {
                        instance.save();
                    }
                },
                ' ',
                {
                    xtype: 'button',
                    text: 'Close',
                    handler: function () {
                        instance.hide();
                    }
                }
            ]
        });

        this.editorComponent = Ext.ComponentMgr.create({
            xtype: this.editorXType
        });

        this.items = [
            this.editorComponent
        ];

        this.callParent(arguments);
    },

    setEditingEl: function(editingEl) {
        this.editingEl = editingEl;
        this.editorComponent.setValue(editingEl.getHtmlOrData ? editingEl.getHtmlOrData() : editingEl.el.dom.innerHTML);
    },

    setIdData: function(idData) {
        this.idData = idData;
    },

    show: function() {
        var elSize = this.editingEl.getSize();
        var winWidth = elSize.width < this.minWidth ? this.minWidth : elSize.width;
        var winHeight = elSize.height < this.minHeight ? this.minHeight : elSize.height;

        this.setSize({
            width: winWidth + 5,
            height: winHeight + 40
        });

        return OPF.core.component.resource.TextResourceEditor.superclass.show.call(this);
    },

    save: function() {
        var me = this;

        var jsonData = {};
        var urlInfix;

        if (this.editorXType == 'textarea') {
            jsonData.text = this.editorComponent.getValue();
            urlInfix = 'content/resource/text/version';
        } else if (this.editorXType == 'tinymceeditor') {
            jsonData.html = this.editorComponent.getValue();
            urlInfix = 'content/resource/html/version';
        }

        var url;
        var method;
        if (this.idData.resourceId) {
            jsonData.resourceId = this.idData.resourceId;
            jsonData.version = this.idData.version;
            url = OPF.Cfg.restUrl(urlInfix + '/' + this.idData.resourceId);
            method = 'PUT';
        } else {
            url = OPF.Cfg.restUrl(urlInfix + '/new/' + this.idData.lookup);
            method = 'POST';
        }

        jsonData.culture = this.cultureCombo.getValue();

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var vo;
                try {
                    vo = Ext.decode(response.responseText);
                } catch (e) {
                    OPF.Msg.setAlert(false, 'Error saving resource.');
                    return;
                }

                OPF.Msg.setAlert(vo.success, vo.message);

                if (vo.success) {
                    me.idData.resourceId = vo.data[0].resourceId;
                    me.idData.version = vo.data[0].version;
                    if (me.cultureCombo.getValue() == 'AMERICAN') { // TODO compare with GUI language value
                        var value = me.editorComponent.getValue();
                        me.editingEl.update(value);
                    }
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, 'Error saving resource.');
            }
        });
    }


});
