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

Ext.require([
    'OPF.prometheus.component.manager.layout.AutoFormLayout',
    'OPF.prometheus.component.manager.layout.ColumnFormLayout',
    'OPF.prometheus.component.manager.layout.WrapperFormLayout',
    'OPF.prometheus.component.manager.helper.FormHelper',
    'OPF.prometheus.component.manager.ReferenceSearchComponent',
    'OPF.prometheus.component.manager.WorkflowRecordComponent',
    'OPF.prometheus.component.socialshare.SocialShareComponent'
]);

Ext.define('OPF.prometheus.component.manager.FormComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.form-component',

    mixins: {
        helper: 'OPF.prometheus.component.manager.helper.FormHelper'
    },

    border: false,

    model: null,
    managerPanel: null,
    saveAs: null,

    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        this.callParent(arguments);
    },

    initComponent: function() {
        var me = this;

        me.addEvents(
            'beforecreatefield',
            'beforeformlayout'
        );

        this.modelInstance = Ext.create(this.model);

        var fields = this.prepareFormFields();

        this.socialShareComponent = Ext.create('OPF.prometheus.component.socialshare.SocialShareComponent', {
            hidden: true,
            listeners: {
                beforeshare: function(component, socialDataItem) {
                    var result = false;
                    if (me.saveAs == 'update') {
                        var record = me.form.getRecord();
                        socialDataItem.sharedRecord = record.getData(true);

                        var referenceObjectTpl = me.modelInstance.self.template;
                        if (referenceObjectTpl) {
                            socialDataItem.sharedHeading = new Ext.XTemplate(me.modelInstance.self.template[0]).apply(record.getData(true));
                            socialDataItem.sharedSubHeading = new Ext.XTemplate(me.modelInstance.self.template[1]).apply(record.getData(true));
                            socialDataItem.sharedDescription = new Ext.XTemplate(me.modelInstance.self.template[2]).apply(record.getData(true));
                        } else {
                            socialDataItem.sharedHeading = new Ext.XTemplate('{' + me.modelInstance.displayProperty + '}').apply(record.getData(true));
                            socialDataItem.sharedSubHeading = null;
                            socialDataItem.sharedDescription = null;
                        }
                        result = true;
                    }
                    return result;
                }
            }
        });

        this.form = Ext.create('Ext.form.Panel', Ext.apply({
            flex: 1,
            border: false,
            cls: 'form-panel',
            autoScroll: true,
            monitorValid: true,

            fieldDefaults: {
                anchor: '100%',
                labelAlign: 'right'
            },

            items: this.applyFormLayout(fields),

            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: Ext.Array.merge(
                        [ this.socialShareComponent, '->' ],
                        this.prepareFormButtons()
                    )
                }
            ],

            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, me.modelInstance.self.ruleId, me.messagePanel);
                }
            }
        }, this.configs.formConfigs));

        this.messagePanel = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'notice-container',
            border: true,
            form: this.form
        }, this.configs.errorContainerConfigs));

        this.items = [
            this.messagePanel,
            this.form
        ];

        this.initWorkflowPanel();

        this.callParent(arguments);
    },

    initWorkflowPanel: function() {
        this.workflowRecordPanel = Ext.ComponentMgr.create({
            xtype: 'prometheus.component.workflow-record-component',
            formComponent: this,
            hidden: true
        });

        this.items = Ext.Array.merge([this.workflowRecordPanel], this.items);
    },

    prepareFormFields: function () {
        var me = this;

        var fields = [];
        Ext.each(this.modelInstance.fields.items, function (field) {
            var components = me.getComponentFromModelField(field, me.modelInstance);
            fields = Ext.Array.merge(fields, components);
        });

        var parentEntityCombo = null;
        Ext.each(this.modelInstance.associations.items, function (association) {
            var component = me.getComponentFromModelAssociation(association);
            var parentIdParam = OPF.getQueryParam('parentId');
            if (OPF.isNotEmpty(parentIdParam) && association.type == 'belongsTo' && association.name == 'parent') {
                parentEntityCombo = component;
            } else {
                fields.push(component);
            }
        });

        if (parentEntityCombo != null) {
            fields.splice(0, 0, parentEntityCombo);
        }

        this.parentLinksContainer = Ext.create('Ext.container.Container', {
            html: ''
        });

        fields.splice(0, 0, this.parentLinksContainer);

        var nestedChildrenButtons = [];
        Ext.each(this.modelInstance.children, function (child) {
            nestedChildrenButtons.push({
                text: 'Add ' + child.displayName,
                formBind: true,
                handler: function (b) {
                    var data = me.prepareDataForSaveOperation();
                    me.saveFormData(data.url, data.method, data.formData, function (message, respData) {
                        if (Ext.isArray(respData) && respData.length > 0) {
                            me.redirectToChildPage(respData[0], me.modelInstance, child);
                        }
                    }, function (message, sourceResponse) {
                        var parentEntityId;
                        if (OPF.isNotEmpty(data.formData) && OPF.isNotEmpty(parentEntityId = data.formData.id)) {
                            me.redirectToChildPage(data.formData, me.modelInstance, child);
                        } else {
                            me.validator.showValidationMessages(sourceResponse);
                        }
                    });
                }
            });
        });

        if (nestedChildrenButtons.length > 0) {
            nestedChildrenButtons.push('->');
            fields.push(Ext.create('Ext.toolbar.Toolbar', {
                ui: 'footer',
                items: nestedChildrenButtons
            }));
        }
        return fields;
    },

    prepareFormButtons: function() {
        this.saveButton = Ext.apply({
            text: 'Save',
            ui: 'save',
            itemId: 'save',
            formBind: true,
            scope: this,
            handler: this.onSaveClick
        }, this.configs.saveButtonConfigs);

        this.cancelButton = Ext.apply({
            text: 'Cancel',
            ui: 'cancel',
            scope: this,
            handler: this.onCancelClick
        }, this.configs.cancelButtonConfigs);

        return [this.saveButton, this.cancelButton];
    },

    redirectToChildPage: function(recordData, model, child) {
        var itemId = recordData.id;
        var itemName = OPF.isEmpty(recordData[model.displayProperty]) ?
            '' + itemId : recordData[model.displayProperty];
        var urlToRedirect = OPF.Cfg.BASE_URL + child.navigationUrl;
        var currentEntityPageUrl = this.getCurrentEntityPageUrl(itemId);
        document.location = urlToRedirect + '?parentId=' + itemId +
            '&link=' + currentEntityPageUrl + '&parentName=' + encodeURIComponent(itemName);
    },

    applyFormLayout: function(fields) {
        this.fireEvent('beforeformlayout', this, fields);
        return Ext.ComponentMgr.create(Ext.apply({
            xtype: 'prometheus.component.manager.layout.auto-form-layout',
            fields: fields
        }, this.configs.formLayoutConfigs));
    },

    showFormPanel: function(record) {
        var me = this;
        var form = this.form.getForm();
        form.reset();
        this.form.getEl().unmask();

        this.saveAs = OPF.isNotEmpty(record) ? 'update' : 'new';
        this.show();

        if (record) {
            this.socialShareComponent.show();
            this.socialShareComponent.setDisabled(true);
        } else {
            this.socialShareComponent.hide();
        }

        this.validator.hideValidationMessages();

        this.form.getForm().checkValidity();

        var primaryIdField = this.down("#primaryIdField");
        var createdField = this.down("#createdField");

        var model = Ext.create(this.model);
        if (record) {
            if (primaryIdField) {
                primaryIdField.show();
            }
            if (createdField) {
                createdField.show();
            }

            var recordId = record.get(record.idProperty);

            if (this.workflowRecordPanel) {
                this.workflowRecordPanel.loadWorkflowData(recordId);
            }

            Ext.each(model.fields.items, function(field) {
                if (field.component && Ext.isFunction(field.component.renderer)) {
                    field.component.renderer(false);
                }
            });

            me.setFormValues(record);
            me.socialShareComponent.setDisabled(false);

            if (me.managerPanel.securityEnabled) {
                var userRoleData = {};
                userRoleData.typeLookup = record.self.lookup;

                var compoundKey = false;
                Ext.each(model.fields.items, function(field) {
                    if (field.name == record.idProperty && !field.persist) {
                        compoundKey = true;
                    }
                });

                var id = record.get('id');
                if (compoundKey) {
                    userRoleData.complexPK = id;
                } else {
                    userRoleData.modelId = id;
                }

                Ext.Ajax.request({
                    url: OPF.Cfg.restUrl("authority/user-role/is-owner"),
                    method: 'POST',
                    jsonData: {"data": userRoleData},
                    success: function(response, action) {
                        var json = Ext.decode(response.responseText);
                        if (json.success && json.data.length > 0) {
                            me.showShareButton(json.data[0].identifier ? record : null);
                        }
                    },

                    failure: function(response) {
                        OPF.Msg.setAlert(false, response.message);
                    }
                });
            }
        } else {
            if (primaryIdField) {
                primaryIdField.hide();
            }
            if (createdField) {
                createdField.hide();
            }

            Ext.each(model.fields.items, function(field) {
                if (field.hasAllowValues) {
                    var value = field.defaultValue.trim().toUpperCase().replace(/[^\w\s]/g, '').replace(/\s+/g, '_');
                    model.set(field.name, value);
                }
            });

            Ext.each(model.fields.items, function(field) {
                if (OPF.isNotEmpty(field.fieldType) && OPF.isNotEmpty(field.component) && Ext.isFunction(field.component.clean)) {
                    field.component.clean();
                }
                if (field.component && Ext.isFunction(field.component.renderer)) {
                    field.component.fieldRenderer(true);
                }
            });

            if (OPF.isNotEmpty(this.parentModel)) {
                Ext.each(model.associations.items, function(association) {
                    if (association.name == 'parent' && association.type == 'belongsTo') {
                        var fieldName = association.name + '_' + me.parentModel.idProperty;
                        var field = form.findField(fieldName);
                        if (field) {
                            field.setValue(me.parentModel);
                            field.setReadOnly(true);
                        }
                    }
                });
            }

            form.loadRecord(model);
        }
    },

    setFormValues: function(record) {
        var me = this;
        var form = this.form.getForm();

//        var jsonData = record.getData(true);
        var jsonData = record.raw;

        Ext.each(record.fields.items, function(field) {
            if (OPF.isNotEmpty(field.fieldType) && OPF.isNotEmpty(field.component) && Ext.isFunction(field.component.load)) {
                var value = record.get(field.name);
                field.component.load(value, jsonData);
            } else if (OPF.isNotEmpty(field.hiddenField)) {
                field.component.setValue(record.get(field.hiddenField.name))
            } else {
                switch(field.fieldType) {
//                    case 'TIME':
//                        record.set(field.name, Ext.Date.format(Ext.Date.parse(record.get(field.name), 'H:i:s'), 'g:i A'));
//                        break;
                    case 'FLAG':
                        record.set(field.name, record.get(field.name) == "on" || record.get(field.name) === true);
                        break;
                }
            }
        });

        form.loadRecord(record);

        Ext.each(record.associations.items, function(association) {
            var associationModel = Ext.create(association.model);
            var fieldName = association.name + '_' + associationModel.idProperty;
            var field = form.findField(fieldName);
            if (field) {
                var associationData = jsonData[association.name];
                if (associationData) {
                    if (association.type == 'belongsTo') {
                        associationModel = OPF.ModelHelper.createModelFromData(association.model, associationData);
                        field.setValue(associationModel);
                        if (OPF.isNotEmpty(me.parentModel) && association.name == 'parent') {
                            field.setReadOnly(true);
                        }
                    } else if (association.type == 'hasMany') {
                        var associationModels = [];
                        Ext.each(associationData, function(associationItem) {
                            var associationModel = OPF.ModelHelper.createModelFromData(association.model, associationItem);
                            associationModels.push(associationModel);
                        });
                        field.setValue(associationModels);
                    }
                }
            }
        });
    },

    onSaveClick: function() {
        var result = this.prepareDataForSaveOperation();
        if (result != null) {
            this.saveRecord(result.url, result.method, result.formData);
        }
    },

    saveRecord: function(url, method, formData) {
        this.saveFormData(url, method, formData, Ext.Function.bind(this.onCancelClick, this));
    },

    onCancelClick: function() {
        this.form.getForm().reset();
        this.form.getEl().unmask();
        this.showGrid();
    },

    showGrid: function() {
        if (this.isGridPanel()) {
            this.hide();
            this.managerPanel.gridPanel.store.load();
            this.managerPanel.gridPanel.show();
            this.showShareButton(null);
        }
    },

    showShareButton: function(record) {
        var shareDataButtons = Ext.ComponentQuery.query("button[itemId='shareDataToolbarButton']");
        if (shareDataButtons != null && shareDataButtons.length > 0) {
            var shareDataBtn = shareDataButtons[0];
            if (record) {
                shareDataBtn.currentModelLookup = this.modelInstance.self.lookup;
                shareDataBtn.currentRecord = record;
                shareDataBtn.show();
            } else {
                shareDataBtn.hide();
            }
        }
    },

    isGridPanel: function() {
        return OPF.isNotEmpty(this.managerPanel) && OPF.isNotEmpty(this.managerPanel.gridPanel);
    },

    getCurrentEntityPageUrl: function(id) {
        var entityListPageUrl = document.location.href.split("?")[0];
        return encodeURIComponent(entityListPageUrl + '?entityId=' + id);
    }
});
