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
    'OPF.prometheus.wizard.AbstractWizard',
    'OPF.prometheus.component.manager.layout.AutoFormLayout',
    'OPF.prometheus.component.manager.layout.ColumnFormLayout',
    'OPF.prometheus.component.manager.layout.WrapperFormLayout',
    'OPF.prometheus.component.manager.helper.FormHelper',
    'OPF.prometheus.component.manager.ReferenceSearchComponent'
]);

Ext.define('OPF.prometheus.wizard.BaseWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',

    mixins: {
        helper: 'OPF.prometheus.component.manager.helper.FormHelper'
    },

    initComponent: function() {
        var me = this;

        this.modelInstance = Ext.create(this.model);

        this.items = [];

        this.formPanels = [];
        this.messagePanels = [];

        this.saveAs = 'new';

        Ext.each(this.forms, function(formData, index) {
            var fields = [];
            var modelFields = [];
            Ext.each(formData.fields, function(fieldData) {
                Ext.each(me.modelInstance.fields.items, function(field) {
                    if (field.name == fieldData.name) {
                        modelFields.push(field);
                    }
                });
            });
            Ext.each(modelFields, function(field) {
                var components = me.getComponentFromModelField(field, me.modelInstance);
                fields = Ext.Array.merge(fields, components);
            });

            var modelAssociations = [];
            Ext.each(formData.fields, function(fieldData) {
                Ext.each(me.modelInstance.associations.items, function(association) {
                    if (association.name == fieldData.name) {
                        modelAssociations.push(association);
                    }
                });
            });
            Ext.each(modelAssociations, function(association) {
                var component = me.getComponentFromModelAssociation(association);
                fields.push(component);
            });

            me.formPanels[index] = Ext.create('Ext.form.Panel', {
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

                items: {
                    xtype: 'prometheus.component.manager.layout.auto-form-layout',
                    fields: fields
                },

                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [
                            {
                                xtype: 'button',
                                ui: 'blue',
                                width: 250,
                                height: 60,
                                text: 'Prev',
                                hidden: index == 0,
                                handler: function(btn) {
                                    btn.up('form').up('panel').prevFrameFn();
                                }
                            },
                            '->',
                            {
                                xtype: 'button',
                                ui: 'blue',
                                width: 250,
                                height: 60,
                                text: 'Next',
                                hidden: index == me.forms.length - 1,
                                formBind: true,
                                handler: function(btn) {
                                    btn.up('form').up('panel').nextFrameFn();
                                }
                            },
                            {
                                xtype: 'button',
                                ui: 'blue',
                                width: 250,
                                height: 60,
                                text: 'Save',
                                hidden: index != me.forms.length - 1,
                                formBind: true,
                                scope: me,
                                handler: me.onSaveClick
                            },
                            {
                                xtype: 'button',
                                ui: 'grey',
                                width: 250,
                                height: 60,
                                text: 'Cancel',
                                hidden: index != me.forms.length - 1,
                                scope: me,
                                handler: me.onCancelClick
                            }
                        ]
                    }
                ],

                listeners: {
                    afterrender: function(form) {
                        me.validator = new OPF.core.validation.FormValidator(form, me.modelInstance.self.ruleId, me.messagePanels[index]);
                    }
                }
            });

            me.messagePanels[index] = Ext.ComponentMgr.create({
                xtype: 'notice-container',
                border: true,
                form: me.formPanels[index]
            });
            me.formPanels[index].insert(0, me.messagePanels[index]);

            var item = {
                title: formData.displayName,
                layout: 'fit',
                border: false,
                items: [
                    me.formPanels[index]
                ],
                prevFrameFn: function() {
                    me.goToPrevPanel(index);
                },
                nextFrameFn: function() {
                    me.goToNextPanel(index);
                }
            };
            me.items.push(item);
        });

        this.callParent(arguments);
    },

    initDeployPanel: function() {

    },

    goToPrevPanel: function(cardPanelIndex) {
        this.validateForm(function(index, scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(index - 1);
        }, cardPanelIndex, this);
    },

    goToNextPanel: function(cardPanelIndex) {
        this.validateForm(function(index, scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(index + 1);
        }, cardPanelIndex, this);
    },

    validateForm: function(executeFn, index, scope) {
        for (var i = 0; i <= index; i++) {
            var isValid = this.formPanels[i].getForm().isValid();
            if (!isValid) {
                executeFn(i - 1, scope);
                return;
            }
        }
        executeFn(index, scope);
    },

    onSaveClick: function() {
        var result = this.prepareDataForSaveOperation();
        if (result != null) {
            this.saveFormData(result.url, result.method, result.formData, Ext.Function.bind(this.onCancelClick, this));
        }
    },

    onCancelClick: function() {
        this.close();
    },

    isFormValid: function() {
        var isValid = true;
        Ext.each(this.formPanels, function(formPanel) {
            isValid &= formPanel.getForm().isValid();
        });
        if (isValid) {
            this.formPanels[this.formPanels.length - 1].getEl().mask();
        }
        return isValid;
    },

    getFieldValues: function() {
        var fieldValues = {};
        Ext.each(this.formPanels, function(formPanel) {
            var values = formPanel.getForm().getValues();
            fieldValues = Ext.apply(fieldValues, values);
        });
        return fieldValues;
    }

});