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

Ext.define('OPF.core.validation.FormValidator', {

    form: null,
    ruleId: null,
    messagePanel: null,

    suffixFieldName: null,
    findById: false,
    clearInvalidOnly: true,

    statics: {
        RULE_CACHE: new Ext.util.MixedCollection()
    },

    constructor: function(form, ruleId, messagePanel, cfg) {
        cfg = cfg || {};

        this.form = form;
        this.ruleId = ruleId;
        this.messagePanel = messagePanel;

        this.suffixFieldName = OPF.ifEmpty(cfg.suffixFieldName, this.suffixFieldName);
        this.findById = OPF.ifEmpty(cfg.findById, this.findById);
        this.clearInvalidOnly = OPF.ifEmpty(cfg.clearInvalidOnly, this.clearInvalidOnly);

        this.useBaseUrl = OPF.ifEmpty(cfg.useBaseUrl, true);

        this.initConstraints();
    },

    initConstraints: function() {
        var me = this;

        var vFields = OPF.core.validation.FormValidator.RULE_CACHE.get(this.ruleId);
        if (vFields) {
            this.initFormFields(vFields);
        } else {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('rule', this.useBaseUrl) + '?id=' + this.ruleId,
                method: 'GET',

                success: function(response) {
                    var vFields = Ext.decode(response.responseText).data;
                    OPF.core.validation.FormValidator.RULE_CACHE.add(me.ruleId, vFields);
                    me.initFormFields(vFields);
                },

                failure:function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
        }
    },

    initFormFields: function(vFields) {
        var me = this;

        if (OPF.isEmpty(this.form)) {
            return;
        }

        var fields = this.form.getForm().getFields();
        var vType;
        for (var i = 0; i < vFields.length; i++) {
            var vField = vFields[i];
            var searchFieldName = vField.name + OPF.ifBlank(this.suffixFieldName, '');
            var field = null;
            if (this.findById) {
                field = Ext.getCmp(searchFieldName);
            } else {
                if (OPF.isNotEmpty(this.mtype)) {
                    field = fields.findBy(function(f) {
                        return (f.getId() === searchFieldName || f.getName() === searchFieldName) && f.mtype == this.mtype;
                    });
                } else {
                    field = fields.findBy(function(f) {
                        return f.getId() === searchFieldName || f.getName() === searchFieldName;
                    })
                }
            }
            if (field != null) {
                field.vConstraints = vField.constraints;
                this.setComboValues(field);
                this.setDefaultValue(vField, field);
                this.setEditableMode(vField, field);

                field.validator = function(value) {
                    var field = this;
                    var errorMessages = [];
                    var validateMessages;
                    if (OPF.isNotEmpty(field.vConstraints)) {
                        for (var i = 0; i < field.vConstraints.length; i++) {
                            validateMessages = me.validatorProcess(field, field.vConstraints[i], value);
                            Ext.each(validateMessages, function(validateMessage) {
                                if (OPF.isNotBlank(field.fieldOrgLabel)) {
                                    validateMessage = validateMessage.replace('\'' + field.name + '\'', field.fieldOrgLabel)
                                } else if (OPF.isNotBlank(field.fieldLabel)) {
                                    validateMessage = validateMessage.replace('\'' + field.name + '\'', field.fieldLabel)
                                }
                                errorMessages.push(validateMessage);
                            });
                        }
                    }
                    return errorMessages;
                }
            }
        }
        this.form.getForm().checkValidity();
    },

    setComboValues: function(field) {
        if (field.xtype == 'opf-form-combo' || field.xtype == 'opf-combo') {
            if (Ext.isArray(field.vConstraints)) {
                for (var j = 0; j < field.vConstraints.length; j++) {
                    var vConstraint = field.vConstraints[j];
                    if (vConstraint.name == 'EnumValue' || vConstraint.name == 'QueryValues' || vConstraint.name == 'AllowValue') {
                        var fields = [
                            field.valueField,
                            field.displayField
                        ];
                        if (this.getParameterByKey(vConstraint.params, 'hasDescription')) {
                            field.displayField = 'title';
                            fields = [
                                field.valueField,
                                field.displayField,
                                'description'
                            ];
                            field.listConfig = {
                                cls: 'x-wizards-boundlist',
                                getInnerTpl: function() {
                                    return '<div class="enum-item"><h3>{title}</h3>{description}</div>';
                                }
                            }
                        }
                        if (vConstraint.name == 'EnumValue' || vConstraint.name == 'AllowValue') {
                            var data = [];
                            var values = this.getParameterByKey(vConstraint.params, 'values');
                            if (vConstraint.name == 'AllowValue') {
                                Ext.each(values, function(allowValue) {
                                    data.push([allowValue, allowValue]);
                                });
                            } else {
                                data = values;
                            }

                            field.typeAhead = true;
                            field.triggerAction = 'all';
                            field.lazyRender = true;
                            field.mode = 'local';
                            field.store = Ext.create('Ext.data.ArrayStore', {
                                autoDestroy: true,
                                idIndex: 0,
                                fields: fields,
                                data: data
                            });
                        } else if (vConstraint.name == 'QueryValues') {
                            field.typeAhead = OPF.ifEmpty(field.typeAhead, true);
                            field.triggerAction = OPF.ifBlank(field.triggerAction, 'all');
                            field.lazyRender = OPF.ifEmpty(field.lazyRender, true);
                            if (OPF.isEmpty(field.mode)) {
                                field.mode = 'local';
                            }
                            if (field.store == null) {
                                field.store = new Ext.data.ArrayStore({
                                    id: 0,
                                    fields: fields,
                                    data: this.getParameterByKey(vConstraint.params, 'values')
                                });
                            } else {
                                var Record = Ext.data.Record.create([field.valueField, field.displayField]);
                                var parameters = this.getParameterByKey(vConstraint.params, 'values');
                                Ext.each(parameters, function(parameter, index) {
                                    field.store.add(
                                        new Record(
                                            {
                                                id: parameter[0],
                                                name: parameter[1]
                                            },
                                            parameter[0]
                                        )
                                    );
                                })
                            }
                        }

                        if (OPF.isNotBlank(field.getValue())) {
                            field.setValue(field.getValue());
                        }
                    }
                }
            }
        }
    },

    setDefaultValue: function(fieldVO, field) {
        if (OPF.isNotEmpty(fieldVO.defaultValue) && OPF.isBlank(field.getValue())) {
            if (field.setDefaultValue) {
                field.setDefaultValue(fieldVO.defaultValue);
            } else {
                field.setValue(fieldVO.defaultValue);
            }
        }
    },

    setEditableMode: function(fieldVO, field) {
        if (OPF.isNotEmpty(fieldVO.editable)) {
            field.setReadOnly(!fieldVO.editable);
        }
    },

    validatorProcess: function(field, vConstraint, value) {
        if (OPF.isEmpty(vConstraint)) {
            return null;
        }
        var validateMessages = [];
        var name = vConstraint.name;
        var errorMessage = vConstraint.errorMessage;
        var params = vConstraint.params;
        switch(name) {
            case 'NotNull':
                var notNullFn = field.notNullValidatorProcessor || this.notNullValidatorProcessor;
                notNullFn(field, value, params, errorMessage, validateMessages);
                break;
            case 'NotBlank':
                var notBlankFn = field.notBlankValidatorProcessor || this.notBlankValidatorProcessor;
                notBlankFn(field, value, params, errorMessage, validateMessages);
                break;
            case 'Match':
                this.matchValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'NotMatch':
                this.notMatchValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'Length':
                this.lengthValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'LessThan':
                this.lessThanValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
        }
        return validateMessages;
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotEmpty(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    },

    notBlankValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value) || value.replace(/\s/g,'') == '') {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be blank';
            validateMessages.push(msg);
        }
    },

    matchValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var expression = this.getParameterByKey(params, 'expression');
        if (OPF.isNotEmpty(expression) && OPF.isNotBlank(value)) {
            var regexp = new RegExp(expression, "g");
            if (!regexp.test(value)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should match to regexp: [' + expression + ']';
                validateMessages.push(msg);
            }
        }
    },

    notMatchValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var expression = this.getParameterByKey(params, 'expression');
        if (OPF.isNotEmpty(expression) && OPF.isNotBlank(value)) {
            var regexp = new RegExp(expression, "g");
            if (regexp.test(value)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should not match to regexp: [' + expression + ']';
                validateMessages.push(msg);
            }
        }

        var words = this.getParameterByKey(params, 'words');
        if (OPF.isNotEmpty(words) && Ext.isArray(words) && OPF.isNotBlank(value)) {
            for (var i = 0; i < words.length; i++) {
                if (value == words[i]) {
                    validateMessages.push('\'' + field.getName() + '\' should not be equal reserved word: \'' + words[i] + '\'');
                }
            }
        }
    },

    lengthValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var min = this.getParameterByKey(params, 'min');
        var minMsg = this.getParameterByKey(params, 'minMsg');
        var max = this.getParameterByKey(params, 'max');
        var maxMsg = this.getParameterByKey(params, 'maxMsg');
        var msg;
        if ((OPF.isBlank(value) && min > 0) ||
            (OPF.isNotBlank(value) && value.length < min)) {
            msg = OPF.isNotBlank(minMsg) ?
                    minMsg : '\'' + field.getName() + '\' should be longer or equal then ' + min + 'characters';
            validateMessages.push(msg);
        }
        if (OPF.isNotBlank(value) && value.length > max) {
            msg = OPF.isNotBlank(maxMsg) ?
                    maxMsg : '\'' + field.getName() + '\' should shorter or equal then ' + max + 'characters';
            validateMessages.push(msg);
        }
    },

    lessThanValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var lessThanValue = this.getParameterByKey(params, 'lessThanValue');
        var checkEquality = this.getParameterByKey(params, 'checkEquality');
        if (OPF.isNotEmpty(lessThanValue) && OPF.isNotBlank(value)) {
            if ((value == lessThanValue && !checkEquality) || (value > lessThanValue)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should less then ' + (checkEquality ? 'or equal ' : '') + lessThanValue;
                validateMessages.push(msg);
            }
        }
    },

    getParameterByKey: function(params, key) {
        if (OPF.isNotEmpty(params) && params.length > 0) {
            for (var i = 0; i < params.length; i++) {
                if (params[i].key == key) {
                    return params[i].value;
                }
            }
        }
        return null;
    },

    showValidationMessages: function(response) {
        if (OPF.isNotEmpty(this.form)) {
            this.form.getEl().unmask();
        }
        if (OPF.isNotEmpty(response)) {
            var responseData = Ext.decode(response.responseText);
            if (OPF.isNotEmpty(responseData) && OPF.isNotEmpty(responseData.data)) {
                var data = responseData.data;
                if (OPF.isNotEmpty(this.form)) {
                    var msg, fieldName, field;
                    for (var i = 0; i < data.length; i++) {
                        msg = data[i].msg;
                        fieldName = data[i].name;
                        if (OPF.isNotBlank(fieldName)) {
                            field = this.form.getForm().findField(fieldName);
                            if (field != null) {
                                field.markInvalid(msg);
                            }
                        }
                    }
                }

                if (OPF.isEmpty(this.messagePanel)) {
                    this.messagePanel = Ext.ComponentMgr.create({
                        xtype: 'notice-container',
                        form: this.form
                    });
                    this.form.insert(0, this.messagePanel);
                }
                this.messagePanel.cleanActiveErrors();
                if (data.length > 0) {
                    var errors = [];
                    Ext.each(data, function(data) {
                        if (OPF.isNotBlank(data.msg)) {
                            errors.push({
                                level: OPF.core.validation.MessageLevel.ERROR,
                                msg: data.msg
                            });
                        }
                    });
                    this.messagePanel.setNoticeContainer(errors);
                }
            } else if (OPF.isNotEmpty(responseData) && OPF.isNotEmpty(responseData.success) &&
                !responseData.success && OPF.isNotEmpty(responseData.message)) {
                if (OPF.isEmpty(this.messagePanel)) {
                    this.messagePanel = Ext.ComponentMgr.create({
                        xtype: 'notice-container',
                        form: this.form
                    });
                    this.form.insert(0, this.messagePanel);
                }
                this.messagePanel.cleanActiveErrors();
                var error = [];
                error.push({
                    level: OPF.core.validation.MessageLevel.ERROR,
                    msg: responseData.message
                });
                this.messagePanel.setNoticeContainer(error);
            }
        }
    },

    hideValidationMessages: function(response) {
        if (OPF.isNotEmpty(this.messagePanel)) {
            this.messagePanel.cleanActiveErrors();
        }

        this.form.getEl().unmask();
        if (this.clearInvalidOnly === true) {
            this.form.getForm().clearInvalid();
        }

        if (OPF.isNotEmpty(response)) {
            var data = Ext.decode(response.responseText);
            if (OPF.isNotBlank(data.message)) {
                this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, data.message);
            }
        }
    }

});