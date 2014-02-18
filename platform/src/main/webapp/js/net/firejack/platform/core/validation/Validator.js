//@tag opf-prototype
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

Ext.namespace('OPF.core.validation');

OPF.core.validation.FormInitialisation = function(voId, baseUrl) {
    this.voId = voId;
    this.baseUrl = OPF.ifBlank(baseUrl, OPF.Cfg.restUrl(''));
};

OPF.core.validation.FormInitialisation.prototype.initConstraints = function(form, mapping, findById, options, mtype, suffixFieldName) {
    this.form = form;
    this.mapping = mapping;
    this.findById = OPF.isEmpty(findById) ? false : findById;
    this.mtype = mtype;
    this.suffixFieldName = suffixFieldName;

    this.fields = [];

    if (OPF.isEmpty(options)) {
        options = new OPF.core.validation.FormInitialisationOptions({
            show: false,
            asPopup: false,
            position: 'top',
            messageLevel: OPF.core.validation.MessageLevel.ERROR,
            messagePanel: null
        });
    }

    var instance = this;

    Ext.Ajax.request({
        url: this.baseUrl + 'rule?id=' + this.voId,
        method: 'GET',

        success:function(response, action) {
            var vFields = Ext.decode(response.responseText).data;
            var fields = instance.form.getForm().getFields();
            var vType;
            for (var i = 0; i < vFields.length; i++) {
                var vField = vFields[i];
                var searchFieldName = vField.name + OPF.ifBlank(instance.suffixFieldName, '');
                var field = null;
                if (instance.findById) {
                    field = Ext.getCmp(searchFieldName);
                } else {
                    if (OPF.isNotEmpty(instance.mtype)) {
                        field = fields.findBy(function(f) {
                            return (f.id === searchFieldName || f.getName() === searchFieldName) && f.mtype == instance.mtype;
                        });
                    } else {
                        field = fields.findBy(function(f) {
                            return f.id === searchFieldName || f.getName() === searchFieldName;
                        })
                    }
                    if (field == null) {
                        field = Ext.getCmp(searchFieldName);
                    }
                }
                if (field != null) {
                    instance.fields.push(field);

                    field.vConstraints = vField.constraints;
                    setComboValues(field);
//                    setGridValues(field);
                    setDefaultValue(vField, field);
                    setEditableMode(vField, field);

                    field.validator = function(field, value) {
                        var errorMessages = [];
                        var validateMessages;
                        if (OPF.isNotEmpty(field.vConstraints)) {
                            for (var i = 0; i < field.vConstraints.length; i++) {
                                validateMessages = validatorProcess(field, field.vConstraints[i], value);
                                Ext.each(validateMessages, function(validateMessage) {
                                    if (OPF.isNotBlank(field.fieldLabel)) {
                                        validateMessage = validateMessage.replace('\'' + field.name + '\'', field.fieldLabel)
                                    }
                                    errorMessages.push(validateMessage);
                                });
                            }
                        }
//                        console.log(field.fieldLabel + ': ' + errorMessages);
                        return errorMessages;
                    }
                }
            }
            instance.form.getForm().checkValidity();
        },

        failure:function(response) {
            OPF.Msg.setAlert(false, response.message);
        }
    });

    var findFields = function(form, fieldName) {
        var fElements = form.elements || (document.forms[form] || Ext.getDom(form)).elements;

        Ext.each(fElements, function(element) {
            var name = element.name;
            var type = element.type;

        });
    };

    var setComboValues = function(field) {
        if (field.xtype == 'opf-combo') {
            if (Ext.isArray(field.vConstraints)) {
                for (var j = 0; j < field.vConstraints.length; j++) {
                    var vConstraint = field.vConstraints[j];
                    if (vConstraint.name == 'EnumValue' || vConstraint.name == 'QueryValues') {
                        var fields = [
                            field.valueField,
                            field.displayField
                        ];
                        if (getParameterByKey(vConstraint.params, 'hasDescription')) {
                            field.displayField = 'title';
                            fields = [
                                field.valueField,
                                field.displayField,
                                'description'
                            ];
                            field.listConfig = {
                                getInnerTpl: function() {
                                    return '<div class="enum-item"><h3>{title}</h3>{description}</div>';
                                }
                            }
                        }
                        if (vConstraint.name == 'EnumValue' || vConstraint.name == 'AllowValue') {
                            var data = [];
                            var values = getParameterByKey(vConstraint.params, 'values');
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
                                    data: getParameterByKey(vConstraint.params, 'values')
                                });
                            } else {
                                var Record = Ext.data.Record.create([field.valueField, field.displayField]);
                                var parameters = getParameterByKey(vConstraint.params, 'values');
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
    };

    var setDefaultValue = function(fieldVO, field) {
        if (OPF.isNotEmpty(fieldVO.defaultValue) && OPF.isBlank(field.getValue())) {
            if (field.setDefaultValue) {
                field.setDefaultValue(fieldVO.defaultValue);
            } else {
                field.setValue(fieldVO.defaultValue);
            }
        }
    };

    var setEditableMode = function(fieldVO, field) {
        if (OPF.isNotEmpty(fieldVO.editable)) {
            field.setReadOnly(!fieldVO.editable);
        }
    };

    var validatorProcess = function(field, vConstraint, value) {
        if (OPF.isEmpty(vConstraint)) {
            return null;
        }
        var validateMessages = [];
        var name = vConstraint.name;
        var errorMessage = vConstraint.errorMessage;
        var params = vConstraint.params;
        switch(name) {
            case 'NotNull':
                var notNullFn = field.notNullValidatorProcessor || notNullValidatorProcessor;
                notNullFn(field, value, params, errorMessage, validateMessages);
                break;
            case 'NotBlank':
                var notBlankFn = field.notBlankValidatorProcessor || notBlankValidatorProcessor;
                notBlankFn(field, value, params, errorMessage, validateMessages);
                break;
            case 'Match':
                matchValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'NotMatch':
                notMatchValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'Length':
                lengthValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'LessThan':
                lessThanValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
        }
        return validateMessages;
    };

    var notNullValidatorProcessor = function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotEmpty(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    };

    var notBlankValidatorProcessor = function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be blank';
            validateMessages.push(msg);
        }
    };

    var matchValidatorProcessor = function(field, value, params, errorMessage, validateMessages) {
        var expression = getParameterByKey(params, 'expression');
        if (OPF.isNotEmpty(expression) && OPF.isNotBlank(value)) {
            var regexp = new RegExp(expression, "g");
            if (!regexp.test(value)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should match to regexp: [' + expression + ']';
                validateMessages.push(msg);
            }
        }
    };

    var notMatchValidatorProcessor = function(field, value, params, errorMessage, validateMessages) {
        var expression = getParameterByKey(params, 'expression');
        if (OPF.isNotEmpty(expression) && OPF.isNotBlank(value)) {
            var regexp = new RegExp(expression, "g");
            if (regexp.test(value)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should not match to regexp: [' + expression + ']';
                validateMessages.push(msg);
            }
        }

        var words = getParameterByKey(params, 'words');
        if (OPF.isNotEmpty(words) && Ext.isArray(words) && OPF.isNotBlank(value)) {
            for (var i = 0; i < words.length; i++) {
                if (value == words[i]) {
                    validateMessages.push('\'' + field.getName() + '\' should not be equal reserved word: \'' + words[i] + '\'');
                }
            }
        }
    };

    var lengthValidatorProcessor = function(field, value, params, errorMessage, validateMessages) {
        var min = getParameterByKey(params, 'min');
        var minMsg = getParameterByKey(params, 'minMsg');
        var max = getParameterByKey(params, 'max');
        var maxMsg = getParameterByKey(params, 'maxMsg');
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
    };

    var lessThanValidatorProcessor = function(field, value, params, errorMessage, validateMessages) {
        var lessThanValue = getParameterByKey(params, 'lessThanValue');
        var checkEquality = getParameterByKey(params, 'checkEquality');
        if (OPF.isNotEmpty(lessThanValue) && OPF.isNotBlank(value)) {
            if ((value == lessThanValue && !checkEquality) || (value > lessThanValue)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should less then ' + (checkEquality ? 'or equal ' : '') + lessThanValue;
                validateMessages.push(msg);
            }
        }
    };

    var getParameterByKey = function(params, key) {
        if (OPF.isNotEmpty(params) && params.length > 0) {
            for (var i = 0; i < params.length; i++) {
                if (params[i].key == key) {
                    return params[i].value;
                }
            }
        }
        return null;
    };

};

// = Ext.extend(Ext.util.Observable, {
Ext.define('OPF.core.validation.FormInitialisationOptions', {

    constructor: function(config){
        this.asPopup = config.asPopup;
        this.show = config.show;
        this.position = config.position;
        this.messageLevel = config.messageLevel;
        this.messagePanel = config.messagePanel;
        OPF.core.validation.FormInitialisationOptions.superclass.constructor.call(this, config)
    }

});

OPF.core.validation.FormInitialisation.showValidationMessages = function(resp, form, options) {
    if (OPF.isEmpty(options)) {
        options = new OPF.core.validation.FormInitialisationOptions({
            show: false,
            asPopup: false,
            position: 'top',
            messageLevel: OPF.core.validation.MessageLevel.ERROR,
            messagePanel: null
        });
    }

    if (OPF.isNotEmpty(form)) {
        form.getEl().unmask();
    }

    if (OPF.isNotEmpty(resp)) {
        var responseStatus = Ext.decode(resp.responseText);
        if (OPF.isNotEmpty(responseStatus) && OPF.isNotEmpty(responseStatus.data)) {

            if (OPF.isNotEmpty(form)) {
                form.remove('sqFormValidationMessages');
                var msg, fieldName, field;
                for (var i = 0; i < responseStatus.data.length; i++) {
                    msg = responseStatus.data[i].msg;
                    fieldName = responseStatus.data[i].name;
                    if (OPF.isNotBlank(fieldName)) {
                        field = form.getForm().findField(fieldName);
                        if (field != null) {
                            field.markInvalid(msg);
                        }
                    }
                }
            }
            if (OPF.isEmpty(form) || options.asPopup) {
                var messages = [];
                for (var i = 0; i < responseStatus.data.length; i++) {
                    msg = responseStatus.data[i].msg;
                    fieldName = responseStatus.data[i].name;
                    if (OPF.isBlank(fieldName) || options.show) {
                        messages.push(msg);
                    }
                }
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, messages);
            } else if (OPF.isNotEmpty(options.messagePanel)) {
                options.messagePanel.cleanActiveErrors();
                if (responseStatus.data.length > 0) {
                    var errors = [];
                    Ext.each(responseStatus.data, function(data) {
                        if (OPF.isNotBlank(data.msg)) {
                            errors.push({
                                level: OPF.core.validation.MessageLevel.ERROR,
                                msg: data.msg
                            });
                        }
                    });
                    options.messagePanel.setNoticeContainer(errors);
                }

            } else {
                var errorPanel = Ext.ComponentMgr.create({
                    xtype: 'panel',
                    id: 'sqFormValidationMessages',
                    flex: 1,
                    headerAsText: false,
                    frame: true,
                    defaults: {
                        bodyStyle: 'padding:15px'
                    }
                });
                var isErrorMessages = false;
                for (var i = 0; i < responseStatus.data.length; i++) {
                    msg = responseStatus.data[i].msg;
                    fieldName = responseStatus.data[i].name;
                    if (OPF.isBlank(fieldName) || options.show) {
                        var errorMessage = new Ext.Component({
                            autoEl: 'div',
                            html: msg
                        });
                        errorPanel.add(errorMessage);
                        errorPanel.doLayout();
                        isErrorMessages = true;
                    }
                }
                if (isErrorMessages) {
                    if (options.position == 'top') {
                        form.insert(0, errorPanel);
                    } else if (options.position == 'bottom') {
                        form.add(errorPanel);
                    }
                    form.doLayout();
                }
            }
        }
    }
};

OPF.core.validation.FormInitialisation.hideValidationMessages = function(form, options, clearInvalidOnly, response) {
    if (OPF.isEmpty(options)) {
        options = new OPF.core.validation.FormInitialisationOptions({
            show: false,
            asPopup: false,
            position: 'top',
            messageLevel: OPF.core.validation.MessageLevel.ERROR,
            messagePanel: null
        });
    }

    if (OPF.isNotEmpty(options.messagePanel)) {
        options.messagePanel.cleanActiveErrors();
    }

    form.getEl().unmask();
    if (clearInvalidOnly === true) {
        form.getForm().clearInvalid();
    } /*else {
        form.getForm().reset();
    }*/

    if (OPF.isNotEmpty(response)) {
        var vo = Ext.decode(response.responseText);
        if (OPF.isNotBlank(vo.message)) {
            var messageDescriptor = Ext.ComponentMgr.create({
                xtype: 'panel',
                id: 'messageDescriptor',
                cls: 'message-content',
                border: false
            });

            var errorMessage = new Ext.Toolbar.TextItem({
                xtype: 'tbtext',
                text: vo.message,
                cls: OPF.core.validation.MessageLevel.INFO.getCSS()
            });
            messageDescriptor.add(errorMessage);
            messageDescriptor.doLayout();

            options.messagePanel.add(messageDescriptor);
            options.messagePanel.doLayout();
        } else {
            form.remove('sqFormValidationMessages');
        }
    } else {
        form.remove('sqFormValidationMessages');
    }
};