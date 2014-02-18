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
    'OPF.prototype.layout.form.AutoFormLayout',
    'OPF.prototype.layout.form.ColumnFormLayout',
    'OPF.prototype.layout.form.WrapperFormLayout'
]);

Ext.define('OPF.prototype.component.FormPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf-prototype-form-panel',

    border: false,

    model: null,
    managerPanel: null,
    saveAs: null,

    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        me.addEvents(
            'beforecreatefield',
            'beforeformlayout'
        );

        var model = Ext.create(this.model);
        var fields = [];
        Ext.each(model.fields.items, function(field) {
            var component;
            var hiddenFieldConfig = null;
            if (field.customXType) {
                component = {
                    xtype: field.customXType,
                    anchor: '100%',
                    name: field.name,
                    labelAlign: 'top',
                    fieldLabel: field.displayName || field.name,
                    subFieldLabel: field.displayDescription
                };
            } else if (field.hasAllowValues) {
                component = {
                    xtype: 'opf-combo',
                    anchor: '100%',
                    name: field.name,
                    labelAlign: 'top',
                    cls: 'opf-combo-field',
                    fieldLabel: field.displayName || field.name,
                    subFieldLabel: field.displayDescription,
                    editable: false,
                    valueField: 'name',
                    displayField: 'display'
                };
            //} else if (field.fieldType == 'NUMERIC_ID' && field.name == model.idProperty) {
            } else if (field.name == model.idProperty) {
                component = {
                    xtype: 'opf-displayfield',
                    anchor: '100%',
                    itemId: 'primaryIdField',
                    name: field.name + '-display',
                    labelAlign: 'left',
                    fieldLabel: field.displayName || field.name,
                    subFieldLabel: field.displayDescription,
                    cls: 'opf-text-field'
                };
                hiddenFieldConfig = {
                    xtype: 'opf-hidden',
                    name: field.name,
                    style: 'display:none'
                };
            } else if (field.name == 'created' && field.fieldType == 'CREATION_TIME') {
                component = {
                    xtype: 'opf-displayfield',
                    anchor: '100%',
                    itemId: 'createdField',
                    name: field.name + '-display',
                    labelAlign: 'left',
                    fieldLabel: field.displayName || field.name,
                    subFieldLabel: field.displayDescription,
                    cls: 'opf-text-field',
                    dateFormat: 'Y-m-d',
                    timeFormat: 'g:i A',
                    dateTimeFormat: 'Y-m-d\\TH:i:s.000O'
                };
                hiddenFieldConfig = {
                    xtype: 'opf-hidden',
                    name: field.name,
                    style: 'display:none'
                };
            } else if (field.fieldType) {
                switch(field.fieldType) {
                    case 'NUMERIC_ID':
                    case 'UNIQUE_ID':
                        component = {
                            xtype: 'opf-textfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-text-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription
                        };
                        break;
                    case 'INTEGER_NUMBER':
                    case 'LARGE_NUMBER':
                        component = {
                            xtype: 'opf-numberfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-number-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            useThousandSeparator: false,
                            allowDecimals: false
                        };
                        break;
                    case 'DECIMAL_NUMBER':
                        component = {
                            xtype: 'opf-numberfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-number-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            useThousandSeparator: false,
                            decimalPrecision: 10
                        };
                        break;
                    case 'DATE':
                        component = {
                            xtype: 'opf-datefield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-date-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            format: 'Y-m-d'
                        };
                        break;
                    case 'TIME':
                        component = {
                            xtype: 'opf-timefield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-time-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            format: 'g:i A'
                        };
                        break;
                    case 'EVENT_TIME':
                    case 'CREATION_TIME':
                    case 'UPDATE_TIME':
                        component = {
                            xtype: 'opf-datetimefield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-date-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            dateFormat: 'Y-m-d',
                            timeFormat: 'g:i A',
                            dateTimeFormat: 'Y-m-d\\TH:i:s.000O'
                        };
                        break;
                    case 'PASSWORD':
                        component = {
                            xtype: 'opf-textfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-text-field',
                            inputType: 'password',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription
                        };
                        break;
                    case 'DESCRIPTION':
                    case 'MEDIUM_TEXT':
                    case 'LONG_TEXT':
                    case 'UNLIMITED_TEXT':
                        component = {
                            xtype: 'opf-textarea',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-text-area',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription
                        };
                        break;
                    case 'RICH_TEXT':
                        component = {
                            xtype: 'opf-htmleditor',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-html',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription
                        };
                        break;
                    case 'CURRENCY':
                        component = {
                            xtype: 'opf-numberfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-number-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            currencySymbol: '$',
                            alwaysDisplayDecimals: true
                        };
                        break;
                    case 'PHONE_NUMBER':
                        component = {
                            xtype: 'opf-textfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-text-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            plugins: [ new OPF.core.component.utils.ui.InputTextMask('(999) 999-9999') ]
                        };
                        break;
                    case 'SSN':
                        component = {
                            xtype: 'opf-textfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-text-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            plugins: [ new OPF.core.component.utils.ui.InputTextMask('999-99-9999') ]
                        };
                        break;
                    case 'FLAG':
                        component = {
                            xtype: 'opf-checkbox',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-check-box',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription
                        };
                        break;
                    case 'IMAGE_FILE':
                        component = {
                            xtype: 'opf-image',
                            anchor: '100%',
                            id: field.name + 'ContainerId',
                            name: field.name,
                            cls: 'x-form-item-label-top x-field',
                            labelMargin: 0,
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription,
                            imageWidth: 300,
                            imageHeight: 200,
                            idData: {
                                path: model.self.lookup + '.opf-resource.' + field.name
                            }
                        };
                        break;
                    default:
                        component = {
                            xtype: 'opf-textfield',
                            anchor: '100%',
                            name: field.name,
                            labelAlign: 'top',
                            cls: 'opf-text-field',
                            fieldLabel: field.displayName || field.name,
                            subFieldLabel: field.displayDescription
                        };
                        break;
                }
            }

            me.initializeFieldCmp(component, field, fields);
            if (hiddenFieldConfig != null) {
                me.initializeFieldCmp(hiddenFieldConfig, field, fields);
            }
        });

        var parentEntityCombo = null;
        Ext.each(model.associations.items, function(association) {
            var component;
            var associationModel = Ext.create(association.model);
            var store = Ext.create('Ext.data.Store', {
                model: association.model,
                proxy: {
                    type: 'ajax',
                    url : associationModel.self.restSuffixUrl,
                    reader: {
                        type: 'json',
                        root: 'data'
                    },
                    startParam: 'offset',
                    limitParam: 'limit'
                },
                pageSize: 10,
                autoLoad: false
            });

            var queryCaching = me.model != association.model;
            component = {
                xtype: 'opf-combo',
                store: store,
                queryCaching: queryCaching,
                anchor: '100%',
                name: association.name + '_' + associationModel.idProperty,
                labelAlign: 'top',
                cls: 'opf-combo-field',
                fieldLabel: association.displayName || OPF.getSimpleClassName(association.model),
                subFieldLabel: association.displayDescription,
                valueField: associationModel.idProperty,
                displayField: associationModel.displayProperty,
                pageSize: 10
            };
            if (association.type == 'belongsTo') {
                component.multiSelect = false;
                if (association.name == 'parent') {
                    me.parentAssociation = association;
                }
            } else if (association.type == 'hasMany') {
                component.multiSelect = true;
            }
            component = Ext.ComponentMgr.create(component);
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
        Ext.each(model.children, function(child) {
            nestedChildrenButtons.push({
                text: 'Add ' + child.displayName,
                formBind : true,
                handler: function(b) {
                    var data = me.prepareDataForSaveOperation();
                    me.saveFormData(data.url, data.method, data.formData, function(message, respData){
                        if (Ext.isArray(respData) && respData.length > 0) {
                            var savedEntityId = respData[0].id;
                            /*var savedEntityName = OPF.isEmpty(respData[0].name) ?
                                '' + savedEntityId : respData[0].name;*/
                            var savedEntityName = OPF.isEmpty(respData[0][model.displayProperty]) ?
                                '' + savedEntityId : respData[0][model.displayProperty];
                            var urlToRedirect = OPF.Cfg.BASE_URL + child.navigationUrl;
                            var currentEntityPageUrl = me.getCurrentEntityPageUrl(savedEntityId);
                            document.location = urlToRedirect + '?parentId=' + savedEntityId +
                                '&link=' + currentEntityPageUrl + '&parentName=' +
                                encodeURIComponent(savedEntityName);
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

        this.form = Ext.create('Ext.form.Panel', Ext.apply({
            flex: 1,
            border: false,
            bodyPadding: 5,
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
                    items: [
                        '->',
                        Ext.apply({
                            text: 'Save',
                            iconCls: 'icon-save',
                            scale: 'small',
                            itemId: 'save',
                            formBind: true,
                            scope: this,
                            handler: this.onSaveClick
                        }, this.configs.saveButtonConfigs),
                        Ext.apply({
                            text: 'Cancel',
                            iconCls: 'icon-cancel',
                            scale: 'small',
                            scope: this,
                            handler: this.onCancelClick
                        }, this.configs.cancelButtonConfigs)
                    ]
                }
            ],

            listeners: {
                afterrender: function(form) {
                    var model = Ext.create(me.model);
                    var constraints = new OPF.core.validation.FormInitialisation(model.self.ruleId, OPF.Cfg.restUrl('', true));
                    constraints.initConstraints(form, null);
                }
            }
        }, this.configs.formConfigs));

        this.messagePanel = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'notice-container',
            form: this.form
        }, this.configs.errorContainerConfigs));

        this.items = [
            this.messagePanel,
            this.form
        ];

        this.callParent(arguments);
    },

    applyFormLayout: function(fields) {
        this.fireEvent('beforeformlayout', this, fields);
        return Ext.ComponentMgr.create(Ext.apply({
            xtype: 'opf-auto-form-layout',
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

        var options = new OPF.core.validation.FormInitialisationOptions({
            messageLevel: OPF.core.validation.MessageLevel.ERROR,
            messagePanel: this.messagePanel
        });
        OPF.core.validation.FormInitialisation.hideValidationMessages(this.form, options);

        this.form.getForm().checkValidity();

        this.refreshParentInfo();

        var primaryIdField = this.down("#primaryIdField");
        var createdField = this.down("#createdField");

        if (record) {
            primaryIdField.show();
            if (OPF.isNotEmpty(createdField)) {
                createdField.show();
            }

            Ext.Ajax.request({
                url: record.self.restSuffixUrl + '/' + record.get(record.idProperty),
                method: 'GET',

                success: function(response, action) {
                    var registryJsonData = Ext.decode(response.responseText);
                    var jsonData = registryJsonData.data[0];
                    me.setFormValues(jsonData);
                },

                failure: function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
        } else {
            primaryIdField.hide();
            if (OPF.isNotEmpty(createdField)) {
                createdField.hide();
            }
            var model = Ext.create(this.model);
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
        if (!this.isGridPanel()) {
            this.down('#save').disable();
        }
    },

    setFormValues: function(jsonData) {
        var me = this;
        var form = this.form.getForm();

        var model = Ext.create(this.model, jsonData);
        Ext.each(model.fields.items, function(field) {
            if (field.hasAllowValues) {
                var object = model.get(field.name);
                if (object != null) {
                    var value = object.name;
                    model.set(field.name, value);
                }
            }
        });

        Ext.each(model.fields.items, function(field) {
            if (OPF.isNotEmpty(field.fieldType) && OPF.isNotEmpty(field.component) && Ext.isFunction(field.component.load)) {
                var value = model.get(field.name);
                field.component.load(value, jsonData);
            } else if (OPF.isNotEmpty(field.hiddenField)) {
                field.component.setValue(model.get(field.hiddenField.name))
            } else {
                switch(field.fieldType) {
                    case 'TIME':
                        model.set(field.name, Ext.Date.format(Ext.Date.parse(model.get(field.name), 'H:i:s'), 'g:i A'));
                        break;
                    case 'FLAG':
                        model.set(field.name, model.get(field.name) == "on" || model.get(field.name) === true);
                        break;
                }
            }
        });

        form.loadRecord(model);

        Ext.each(model.associations.items, function(association) {
            var associationModel = Ext.create(association.model);
            var fieldName = association.name + '_' + associationModel.idProperty;
            var field = form.findField(fieldName);
            if (field) {
                var associationData = jsonData[association.name];
                if (associationData) {
                    if (association.type == 'belongsTo') {
                        associationModel = Ext.create(association.model, associationData);
                        field.setValue(associationModel);
                        if (OPF.isNotEmpty(me.parentModel) && association.name == 'parent') {
                            field.setReadOnly(true);
                        }
                    } else if (association.type == 'hasMany') {
                        var associationModels = [];
                        Ext.each(associationData, function(associationItem) {
                            var associationModel = Ext.create(association.model, associationItem);
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
        }
    },

    isGridPanel: function() {
        return OPF.isNotEmpty(this.managerPanel) && OPF.isNotEmpty(this.managerPanel.gridPanel);
    },

    refreshParentInfo: function() {
        if (OPF.isNotEmpty(this.parentModel) && OPF.isNotEmpty(this.parentAssociation)) {
            var parentName = OPF.getQueryParam('parentName');
            var parentUrl = OPF.getQueryParam('link');
            if (OPF.isNotBlank(parentName) && OPF.isNotBlank(parentUrl)) {
                parentUrl = decodeURIComponent(parentUrl);
                var parentInfoHtml = '<a class="parent-page-url" href="' + parentUrl + '">';
                parentInfoHtml += 'Return to ' + this.parentAssociation.displayName + ' ' + parentName;
                parentInfoHtml += '</a>';
                this.parentLinksContainer.update(parentInfoHtml);
            }
        }
    },

    getCurrentEntityPageUrl: function(id) {
        var entityListPageUrl = document.location.href.split("?")[0];
        return encodeURIComponent(entityListPageUrl + '?entityId=' + id);
    },

    saveFormData: function(url, method, formData, successHandler) {
        var me = this;

        if (this.configs.onBeforeSave) {
            if (this.configs.onBeforeSave(url, method, formData) === false) {
                this.form.getEl().unmask();
                return false;
            }
        }

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": formData},

            success:function(response, action) {
                if (OPF.isNotEmpty(successHandler)) {
                    var resp = Ext.decode(response.responseText);
                    if (resp.success) {
                        successHandler(resp.message, resp.data);
                    } else {
                        var options = new OPF.core.validation.FormInitialisationOptions({
                            messageLevel: OPF.core.validation.MessageLevel.ERROR,
                            messagePanel: me.messagePanel
                        });
                        OPF.core.validation.FormInitialisation.showValidationMessages(response, me.form, options);
                    }
                }
            },

            failure:function(response) {
                var options = new OPF.core.validation.FormInitialisationOptions({
                    messageLevel: OPF.core.validation.MessageLevel.ERROR,
                    messagePanel: me.messagePanel
                });
                OPF.core.validation.FormInitialisation.showValidationMessages(response, me.form, options);
            }
        });
    },

    prepareDataForSaveOperation: function() {
        var result;
        var form = this.form.getForm();
        if (form.isValid()) {
            result = {};
            this.form.getEl().mask();
            var record = form.getRecord();
            result.method = null;
            result.url = null;
            if (this.saveAs == 'new') {
                result.method = 'POST';
                result.url = record.self.restSuffixUrl;
            } else if (this.saveAs == 'update') {
                result.method = 'PUT';
                result.url = record.self.restSuffixUrl + '/' + record.get(record.idProperty);
            }

            result.formData = form.getValues();

            Ext.each(record.fields.items, function(field) {
                if (OPF.isNotEmpty(field.hiddenField) && field.fieldType == 'CREATION_TIME') {
                    var record = form.getRecord();
                    result.formData[field.name] = record.data[field.name];
                } else if (OPF.isNotEmpty(field.fieldType) && OPF.isNotEmpty(field.component) && Ext.isFunction(field.component.process)) {
                    result.formData[field.name] = field.component.process(result.formData);
                } else {
                    switch(field.fieldType) {
                        case 'TIME':
                            result.formData[field.name] = Ext.Date.format(Ext.Date.parse(result.formData[field.name], 'g:i A'), 'H:i:s');
                            break;
                        case 'CURRENCY':
                            result.formData[field.name] = result.formData[field.name].replace(/[\\$\\s,]/g, '');
                            break;
                        case 'FLAG':
                            result.formData[field.name] = result.formData[field.name] == "on" || result.formData[field.name] === true;
                            break;
                    }
                }
            });

            Ext.each(record.associations.items, function(association) {
                var associationModel = Ext.create(association.model);
                if (association.type == 'belongsTo') {
                    var associationValue = result.formData[association.name + '_' + associationModel.idProperty];
                    if (Ext.isNumeric(associationValue)) {
                        result.formData[association.name] = result.formData[association.name] || {};
                        result.formData[association.name][associationModel.idProperty] = associationValue;
                    } else {
                        result.formData[association.name] = null;
                    }
                    delete result.formData[association.name + '_' + associationModel.idProperty];
                } else if (association.type == 'hasMany') {
                    var associationValues = result.formData[association.name + '_' + associationModel.idProperty];
                    result.formData[association.name] = result.formData[association.name] || [];
                    Ext.each(associationValues, function(associationValue) {
                        var value = {};
                        value[associationModel.idProperty] = associationValue;
                        result.formData[association.name].push(value);
                    });
                    delete result.formData[association.name + '_' + associationModel.idProperty];
                }
            });
        } else {
            result = null;
        }
        return result;
    },

    initializeFieldCmp: function(component, field, fields) {
        if (component && this.fireEvent('beforecreatefield', this, field, component) !== false) {
            component.region = field.region;
            component = Ext.ComponentMgr.create(component);
            if (component.alias == 'widget.opf-hidden') {
                field.hiddenField = component;
            } else {
                field.component = component;
            }
            fields.push(component);
        }
    }

});
