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

Ext.define('OPF.prometheus.wizard.workflow.ManageStatusesComponent', {
    extend: 'OPF.core.component.form.FieldContainer',
    alias: 'widget.prometheus.component.manage-statuses-component',

    layout: 'anchor',
    cls: 'reference-search-component',

    name: null,          //association.name + '_' + associationModel.idProperty,
    fieldLabel: null,    //association.displayName || OPF.getSimpleClassName(association.model),
    subFieldLabel: null, //association.displayDescription,
    validationName: null,

    readOnly: false,
    data: null,

    allowDelete: true,
    allowBlank: true,
    errorMessage: '{0} is required',
    emptyMessage: '<div class="x-form-empty-field">Press config button to search for statuses.</div>',

    model: 'OPF.console.domain.model.StatusModel',

    value: null,

    initComponent: function() {
        var me = this;

        this.addEvents(
            'clear'
        );

        this.initializedModel = Ext.create(this.model);
        this.itemTpl = '<b>{name}</b></br>{description}</br>';

        this.dropContainer = Ext.create('Ext.container.Container', {
            anchor: '100%',
            cls: 'reference-search-content',
            tpl: this.itemTpl,
            listeners: {
                render: function(component) {
                    component.getEl().on('click', function() {
//                        me.draggableNodeIdField.isValid();
                        me.isValid();
                    });
                }
            }
        });

        this.draggableNodeIdField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: this.validationName,
            allowBlank: this.allowBlank,
            blankText: Ext.String.format(this.errorMessage, this.fieldLabel),
            submitValue: false,
            disabled: true,
            setDefaultValue: function(defaultValue) {
                me.setDefaultValue(defaultValue);
            }
        });

        var innerContainer = [
            this.dropContainer
        ];

        var buttons = [];
        if (!this.readOnly) {
            this.configButton = Ext.create('Ext.button.Button', {
                ui:'reference-search',
                width: 16,
                height: 16,
                border: false,
                iconCls: 'config-ico',
                tooltip: 'Config',
                handler: me.showSearchDialog,
                scope: this/*,
                 hidden: true*/
            });
            buttons.push(this.configButton);
        }

        if (!this.readOnly && this.allowDelete) {
            this.cleanButton = Ext.create('Ext.button.Button', {
                ui:'reference-search',
                width: 16,
                height: 16,
                border: false,
                iconCls: 'delete-ico',
                tooltip: 'Delete',
                handler: me.clear,
                scope: this,
                hidden: true
            });
            buttons.push(this.cleanButton);
        }

        if (buttons.length > 0) {
            innerContainer.push({
                xtype: 'container',
                cls: 'buttons-panel',
                width: 52,
                items: buttons
            });
        }

        this.searchStore = Ext.create('Ext.data.Store', {
            model: this.model,
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                },
                writer: {
                    type: 'json'
                }
            },
            searchFields: this.searchFields,
            pageSize: 10
        });

        var defaultStatuses = [];
        defaultStatuses.push({
            name: 'Started',
            description: 'The very first status'
        });
        defaultStatuses.push({
            name: 'Finished',
            description: 'Final status'
        });

        defaultStatuses.push({
            name: 'Intermediate',
            description: 'Intermediate status'
        });

        this.searchStore.loadData(defaultStatuses);

        this.items = [
            {
                xtype: 'container',
                cls: 'reference-search-inner',
                items: innerContainer
            },
            this.draggableNodeIdField
        ];

        this.callParent(arguments);
    },

    listeners: {
        errorchange: function(field, error) {
            if (field.dropContainer.getEl()) {
                if (OPF.isNotBlank(error)) {
                    field.dropContainer.addCls('opf-drop-container-invalid');
                    field.dropContainer.addCls('x-form-invalid-field');
                    field.dropContainer.el.dom.setAttribute('data-errorqtip', error || '');
                } else {
                    field.dropContainer.removeCls('opf-drop-container-invalid');
                    field.dropContainer.removeCls('x-form-invalid-field');
                    field.dropContainer.el.dom.removeAttribute('data-errorqtip');
                }
            }
        }
    },

    onBlur: function() {
        this.isValid();
    },

    getValue: function() {
        return this.value;
    },

    setValue: function(model) {
        if (OPF.isEmpty(model)) {
            this.dropContainer.update(this.emptyMessage);
            this.cleanButton.hide();
            if (!this.readOnly) {
                this.configButton.show();
            }
            this.value = null;
        } else {
            this.value = model;
            this.setData(model);
        }
        return this.callParent([model]);
    },

    getRawValue: function() {
        return this.getValue();
    },

    processRawValue: function(rawValue) {
        return rawValue;
    },

    setData: function(model) {
        this.dropContainer.update(model.data);
        this.isValid();
        if (this.allowDelete && !this.readOnly) {
            this.cleanButton.show();
        }
        if (!this.readOnly) {
            this.configButton.show();
        }
    },

    setReadOnly: function(readOnly) {
        this.readOnly = readOnly;
    },

    clear: function() {
        if (this.fireEvent('clear', this) !== false) {
            this.data = null;
            this.dropContainer.update(this.emptyMessage);
//            this.draggableNodeIdField.setValue(null);
            this.value = null;
            if (this.allowDelete && !this.readOnly) {
                this.cleanButton.hide();
            }
            if (!this.readOnly) {
                this.configButton.show();
            }
            this.isValid();
        }
    },

    showSearchDialog: function() {
        var me = this;

        if (!this.searchDialog) {
            var selectedItemTpl = new Ext.XTemplate(
                '<ul class="x-boxselect-list">',
                '<tpl for=".">',
                '<li class="x-boxselect-item item-wrap">',
                '<div class="x-boxselect-item-text">',
                this.itemTpl,
                '</div>',
                '<tpl if="name != \'Started\' && name != \'Finished\'">',
                '<div class="x-tab-close-btn x-boxselect-item-close"></div>',
                '</tpl>',
                '</li>',
                '</tpl>',
                '</ul>'
            );

            this.selectedItemsView = Ext.create('Ext.view.View', {
                store: this.searchStore,
                tpl: selectedItemTpl,
                itemSelector: 'li.item-wrap',
                overItemCls: 'item-hover',
                listeners: {
                    itemclick: function (view, record, el, i, e) {
                        if (e.getTarget('.x-boxselect-item-close')) {
                            me.searchStore.remove(record);
                        }
                    },
                    itemdblclick: function(view, record, item, index, e) {
                        me.setValue(record);
                        me.searchDialog.hide();
                    }
                }
            });

            this.statusNameField = Ext.create('OPF.core.component.form.Text', {
                labelAlign: 'top',
                fieldLabel: 'Name',
                name: 'name',
                anchor: '100%',
                emptyText: 'status name',
                allowBlank: false,
                validator: function(val) {
                    var errorMessage = null;
                    if (OPF.isNotEmpty(val)) {
                        me.searchStore.each(function(status) {
                            if (status.get('name') == val) {
                                errorMessage = 'Status name is not unique.';
                                return false;
                            }
                            return true;
                        });
                    }
                    return errorMessage;
                }
            });

            this.statusDescriptionField = Ext.create('OPF.core.component.form.TextArea', {
                labelAlign: 'top',
                fieldLabel: 'Description',
                name: 'description',
                anchor: '100%',
                emptyText: 'status description'
            });

            this.statusPanel = Ext.create('Ext.panel.Panel', {
                layout: 'anchor',
                bodyPadding: 10,
                border: false,
                items: [
                    this.statusNameField,
                    this.statusDescriptionField
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [
                            '->',
                            {
                                xtype: 'button',
                                ui: 'blue',
                                text: 'Create',
                                handler: function() {
                                    if (me.statusNameField.validate()) {
                                        var record = Ext.create(me.model, {
                                            name: me.statusNameField.getValue(),
                                            description: me.statusDescriptionField.getValue()
                                        });
                                        me.searchStore.add(record);
                                        me.wrapperPanel.getLayout().setActiveItem(0);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                ui: 'grey',
                                text: 'Cancel',
                                handler: function() {
                                    me.wrapperPanel.getLayout().setActiveItem(0);
                                }
                            }
                        ]
                    }
                ]
            });

            this.wrapperPanel = Ext.create('Ext.panel.Panel', {
                autoScroll: true,
                layout: 'card',
                items: [
                    {
                        xtype: 'panel',
                        layout: 'fit',
                        items: this.selectedItemsView,
                        dockedItems: [
                            {
                                xtype: 'toolbar',
                                dock: 'top',
                                items: [
                                    {
                                        xtype: 'button',
                                        ui: 'blue',
                                        text: 'Add Status',
                                        handler: function(btn) {
                                            me.statusNameField.setValue("");
                                            me.statusDescriptionField.setValue("");
                                            me.wrapperPanel.getLayout().setActiveItem(1);
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'pagingtoolbar',
                                store: this.searchStore,
                                dock: 'bottom',
                                displayInfo: true
                            }
                        ]
                    },
                    this.statusPanel
                ]
            });

            this.searchDialog = Ext.create('Ext.window.Window', {
                ui: 'wizards',
                title: 'Select',
                height: 450,
                width: 500,
                layout: 'fit',
                closeAction: 'hide',
                resizable: false,
                items: [
                    this.wrapperPanel
                ]
            });
        }
        this.searchDialog.show();
    },

    getErrors: function(value) {
        var errors = [];
        value = value || this.getValue();
        if (OPF.isEmpty(value)) {
            errors.push('Status is required field.');
        }
        return errors;
    },

    isValid : function() {
        var me = this;
        return me.disabled || me.validateValue(me.processRawValue(me.getRawValue()));
    },

    validateValue: function(value) {
        var me = this,
            errors = me.getErrors(value),
            isValid = Ext.isEmpty(errors);
        if (!me.preventMark) {
            if (isValid) {
                me.clearInvalid();
            } else {
                me.markInvalid(errors);
            }
        }
        return isValid;
    },

    markInvalid : function(errors) {
        // Save the message and fire the 'invalid' event
        var me = this,
            oldMsg = me.getActiveError();
        me.setActiveErrors(Ext.Array.from(errors));
        if (oldMsg !== me.getActiveError()) {
            me.doComponentLayout();
        }
    },

    clearInvalid : function() {
        // Clear the message and fire the 'valid' event
        var me = this,
            hadError = me.hasActiveError();
        me.unsetActiveError();
        if (hadError) {
            me.doComponentLayout();
        }
    },

//    private methods
    getFieldsFromTemplate: function() {
        var me = this;

        var template = this.initializedModel.self.template[0];
        var fields = [];
        var matches = template.match(/\{[\w\.]+\}/gi);
        Ext.each(matches, function(match) {
            var fieldName = match.substring(1, match.length - 1);
            var field = me.findModelField(me.initializedModel, fieldName);
            if (OPF.isNotEmpty(field)) {
                fields.push(field);
            }
        });
        return fields;
    },

    findModelField: function(model, fieldName) {
        var foundField = null;
        Ext.each(model.fields.items, function(field) {
            if (field.name == fieldName) {
                foundField = field;
            }
        });
        return foundField;
    },

    getStatusByName: function(name) {
        var foundStatus = null;
        this.searchStore.each(function(statusModel) {
            if (statusModel.get('name') == name) {
                foundStatus = statusModel;
                return false;
            }
            return true;
        });
        return foundStatus;
    },

    getStartStatus: function() {
        return this.getStatusByName('Started');
    },

    getFinalStatus: function() {
        return this.getStatusByName('Finished');
    },

    getDefaultIntermediateStatus: function() {
        return this.getStatusByName('Intermediate');
    },

    isFinalStatus: function(status) {
        return OPF.isNotEmpty(status) && status.get('name') == 'Finished';
    }

});