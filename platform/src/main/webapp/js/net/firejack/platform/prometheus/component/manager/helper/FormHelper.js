Ext.define('OPF.prometheus.component.manager.helper.FormHelper', {
    extend: 'Ext.Base',

    getComponentFromModelField: function(field, model) {
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
                typeAhead: false,
                valueField: 'name',
                displayField: 'display'
            };
        //} else if (field.fieldType == 'NUMERIC_ID' && field.name == model.idProperty) {
        } else if (field.name == model.idProperty) {
            component = {
                xtype: 'opf-display',
                anchor: '100%',
                itemId: 'primaryIdField',
                name: field.name + '-display',
                labelAlign: 'left',
                fieldLabel: field.displayName || field.name,
                subFieldLabel: field.displayDescription,
                cls: 'opf-text-field',
                fieldRenderer: function(isCreateMode) {
                    this.setVisible(!isCreateMode);
                }
            };
            hiddenFieldConfig = {
                xtype: 'opf-hidden',
                name: field.name,
                style: 'display:none'
            };
        } else if (field.name == 'created' && field.fieldType == 'CREATION_TIME') {
            component = {
                xtype: 'opf-display',
                anchor: '100%',
                itemId: 'createdField',
                name: field.name + '-display',
                labelAlign: 'left',
                fieldLabel: field.displayName || field.name,
                subFieldLabel: field.displayDescription,
                cls: 'opf-text-field',
                dateFormat: 'Y-m-d',
                timeFormat: 'g:i A',
                dateTimeFormat: 'Y-m-d\\TH:i:s.000O',
                fieldRenderer: function(isCreateMode) {
                    this.setVisible(!isCreateMode);
                }
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
                        xtype: 'opf-text',
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
                        xtype: 'opf-number',
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
                        xtype: 'opf-number',
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
                        xtype: 'opf-date',
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
                        xtype: 'opf-time',
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
                        xtype: 'opf-datetime',
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
                        xtype: 'opf-text',
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
                        xtype: 'opf-number',
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
                        xtype: 'opf-text',
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
                        xtype: 'opf-text',
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
//                        labelMargin: 0,
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
                        xtype: 'opf-text',
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
        if (component) {
            if (component.xtype != 'opf-display') {
                component.submitValue = field.persist;
            }
            component.hidden = field.hidden;
        }

        var components = [];
        component = this.initializeFieldCmp(component, field);
        if (component) {
            components.push(component);
        }
        if (hiddenFieldConfig != null) {
            var hiddenComponent = this.initializeFieldCmp(hiddenFieldConfig, field);
            if (hiddenComponent) {
               components.push(hiddenComponent);
           }
        }
        return components;
    },

    initializeFieldCmp: function(component, field) {
        if (component && this.fireEvent('beforecreatefield', this, field, component) !== false) {
            component.region = field.region;
            component = Ext.ComponentMgr.create(component);

            var aliases = component.alias;
            if (!Ext.isArray(aliases)) {
                aliases = [aliases];
            }
            if (Ext.Array.contains(aliases, 'widget.opf-hidden')) {
                field.hiddenField = component;
            } else {
                field.component = component;
            }
            return component;
        }
        return null;
    },

    getComponentFromModelAssociation: function(association) {
        var component;
        var associationModel = Ext.create(association.model);
        if (association.type == 'belongsTo') {
            component = {
                xtype: 'prometheus.component.reference-search-component',
                anchor: '100%',
                model: association.model,
                name: association.name + '_' + associationModel.idProperty,
                validationName: association.name,
                labelAlign: 'top',
                fieldLabel: association.displayName || OPF.getSimpleClassName(association.model),
                subFieldLabel: association.displayDescription
            };
        } else if (association.type == 'hasMany') {
            component = {
                xtype: 'opf-searchcombo',
                anchor: '100%',
                model: association.model,
                name: association.name + '_' + associationModel.idProperty,
                labelAlign: 'top',
                fieldLabel: association.displayName || OPF.getSimpleClassName(association.model),
                subFieldLabel: association.displayDescription
            };
        }
        var fieldConfig = association.formConfig || {};
        component = Ext.apply(component, fieldConfig);
        component = Ext.ComponentMgr.create(component);
        return component;
    },

    saveFormData: function(url, method, formData, successHandler, unSuccessHandler) {
        var me = this;

        if (this.configs && this.configs.onBeforeSave) {
            if (this.configs.onBeforeSave(url, method, formData) === false) {
                this.form.getEl().unmask();
                return;
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
                    } else if (OPF.isEmpty(unSuccessHandler)) {
                        me.validator.showValidationMessages(response);
                    } else {
                        unSuccessHandler(resp.message, response);
                    }
                }
            },

            failure:function(response) {
                me.validator.showValidationMessages(response);
            }
        });
    },

    prepareDataForSaveOperation: function() {
        var result;
        if (this.isFormValid()) {
            var formData = this.getFieldValues();

            result = {
                formData: {}
            };
            if (this.saveAs == 'new') {
                result['method'] = 'POST';
                result['url'] = this.modelInstance.self.restSuffixUrl;
            } else if (this.saveAs == 'update') {
                result['method'] = 'PUT';
                result['url'] = this.modelInstance.self.restSuffixUrl + '/' + formData[this.modelInstance.idProperty];
            }

            Ext.each(this.modelInstance.fields.items, function(field) {
                if (field.persist) {
                    var value = formData[field.name];
                    if (value) {
                        if (OPF.isNotEmpty(field.hiddenField) && field.fieldType == 'CREATION_TIME') {
                            result.formData[field.name] = new Date(value);
                        } else if (OPF.isNotEmpty(field.fieldType) && OPF.isNotEmpty(field.component) && Ext.isFunction(field.component.process)) {
                            result.formData[field.name] = field.component.process(formData);
                        } else {
                            switch(field.fieldType) {
                                case 'TIME':
                                    result.formData[field.name] = Ext.Date.format(Ext.Date.parse(value, 'g:i A'), 'H:i:s');
                                    break;
                                case 'CURRENCY':
                                    result.formData[field.name] = value.replace(/[\\$\\s,]/g, '');
                                    break;
                                case 'FLAG':
                                    result.formData[field.name] = value == "on" || value === true;
                                    break;
                                default:
                                    result.formData[field.name] = value;
                            }
                        }
                    }
                }
            });

            Ext.each(this.modelInstance.associations.items, function(association) {
                var associationModel = Ext.create(association.model);
                var associationValues = formData[association.name + '_' + associationModel.idProperty];
                if (association.type == 'belongsTo') {
                    if (Ext.isNumeric(associationValues)) {
                        result.formData[association.name] = {};
                        result.formData[association.name][associationModel.idProperty] = associationValues;
                    } else {
                        result.formData[association.name] = null;
                    }
                } else if (association.type == 'hasMany') {
                    result.formData[association.name] = [];
                    Ext.each(associationValues, function(associationValue) {
                        var value = {};
                        value[associationModel.idProperty] = associationValue;
                        result.formData[association.name].push(value);
                    });
                }
            });
        } else {
            result = null;
        }
        return result;
    },

    isFormValid: function() {
        var form = this.form.getForm();
        var isValid = form.isValid();
        if (isValid) {
            this.form.getEl().mask();
        }
        return isValid;
    },

    getFieldValues: function() {
        var form = this.form.getForm();
        var values = form.getValues();
        var record = form.getRecord();
        var recordValues = OPF.ModelHelper.getData(record, true);
        return Ext.apply(recordValues, values);
    }

});