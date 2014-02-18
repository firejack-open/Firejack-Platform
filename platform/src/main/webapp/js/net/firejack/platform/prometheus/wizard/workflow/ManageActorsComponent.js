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
    'OPF.prometheus.wizard.workflow.model.ActorUserModel'
]);

Ext.define('OPF.prometheus.wizard.workflow.ManageActorsComponent', {
    extend: 'OPF.core.component.form.FieldContainer',
    alias: 'widget.prometheus.component.manage-actors-component',

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
    emptyMessage: '<div class="x-form-empty-field">Press config button to search for actors.</div>',

    model: 'OPF.console.domain.model.ActorModel',

    value: null,
    parentDomainCombo: null,

    constructor: function(parentDomainCombo, cfg) {
        this.parentDomainCombo = parentDomainCombo;
        this.callParent([cfg])
    },

    initComponent: function() {
        var me = this;

        this.addEvents(
            'clear'
        );

        this.initializedModel = Ext.create(this.model);
        this.itemTpl = '<b>{name}</b></br>{description}</br>';

        this.dropContainer = Ext.ComponentMgr.create({
            xtype: 'container',
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
            this.configButton = Ext.ComponentMgr.create({
                xtype: 'button',
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
            this.cleanButton = Ext.ComponentMgr.create({
                xtype: 'button',
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
        var showOnResponse = false;
        if (!this.searchDialog) {
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
                pageSize: 10,
                autoLoad: true
            });
            showOnResponse = true;

            var selectedItemTpl = new Ext.XTemplate(
                '<ul class="x-boxselect-list">',
                '<tpl for=".">',
                '<li class="x-boxselect-item item-wrap">',
                '<div class="x-boxselect-item-text">',
                this.itemTpl,
                '</div>',
                '<div class="x-tab-close-btn x-boxselect-item-close"></div>',
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
                },
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'top',
                        items: [
                            {
                                xtype: 'button',
                                ui: 'blue',
                                text: 'Add Actor',
                                handler: function(btn) {
                                    me.actorNameField.setValue("");
                                    me.actorDescriptionField.setValue("");
                                    me.actorUsersField.setValue(null);
                                    //me.actorTabPanel.setActiveTab(me.nameAndDescriptionPanel);
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
            });

            this.actorNameField = Ext.create('OPF.core.component.form.Text', {
                name: 'name',
                anchor: '100%',
                emptyText: 'name of actor',
                allowBlank: false,
                validator: function(val) {
                    var errorMessage = null;
                    if (OPF.isNotEmpty(val)) {
                        me.searchStore.each(function(actor) {
                            if (actor.get('name') == val) {
                                errorMessage = 'Actor name is not unique.';
                                return false;
                            }
                            return true;
                        });
                    }
                    return errorMessage;
                }
            });

            this.actorNameField.customValidator = function(value) {
                var msg = null;
                if (me.lastNotUniqueName != value) {
                    if (OPF.isNotBlank(value)) {
                        me.checkUniqueActorNameTask.delay(250);
                    }
                } else {
                    msg = 'Name is not unique.';
                }
                return msg;
            };

            this.actorDescriptionField = Ext.create('OPF.core.component.form.TextArea', {
                name: 'description',
                anchor: '100%',
                emptyText: 'actor description'
            });

            this.actorUsersField = Ext.create('OPF.core.component.form.SearchComboBox', {
                anchor: '100%',
                model: 'OPF.prometheus.wizard.workflow.model.ActorUserModel',
                name: 'users',
                labelAlign: 'top',
                fieldLabel: 'Actor Users',
                subFieldLabel: '',
                validator: function(rawValue) {
                    var ids = me.actorUsersField.getValue();
                    if (ids == null || ids.length == 0) {
                        return 'One or more user actors should be selected.'
                    } else {
                        return null;
                    }
                },
                listeners: {
                    onBeforeStoreLoad: function(searchCombo, store, searchTerm) {
                        /*store.proxy.url = OPF.isBlank(searchTerm) ?
                            OPF.Cfg.restUrl('directory/user') :
                            OPF.Cfg.restUrl('directory/user/search?term=' + escape(searchTerm));*/
                        store.proxy.url = OPF.Cfg.restUrl('directory/user/advanced-search');
                    }
                }
            });

            this.actorPanel = Ext.create('Ext.panel.Panel', {
                layout: 'anchor',
                bodyPadding: 10,
                border: false,
                maxHeight: 500,
                autoScroll: true,
                items: [
                    this.actorNameField,
                    this.actorDescriptionField,
                    this.actorUsersField
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
                                    if (me.actorNameField.validate() && me.actorUsersField.searchCombo.validate()) {
                                        var selectedUserIds = me.actorUsersField.getValue();
                                        var userActors = [];
                                        Ext.each(selectedUserIds, function(selectedUserId) {
                                            var userActor = {};
                                            userActor.user = {id: selectedUserId};
                                            userActors.push(userActor);
                                        });
                                        var record = Ext.create(me.model, {
                                            name: me.actorNameField.getValue(),
                                            description: me.actorDescriptionField.getValue(),
                                            userActors: userActors
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
                                        text: 'Add Actor',
                                        handler: function(btn) {
                                            me.actorNameField.setValue("");
                                            me.actorDescriptionField.setValue("");
                                            me.actorUsersField.setValue(null);
                                            //me.actorTabPanel.setActiveTab(me.nameAndDescriptionPanel);
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
                    this.actorPanel
                ]
            });

            this.lastNotUniqueName = null;
            this.checkUniqueActorNameTask = new Ext.util.DelayedTask(function(){
                var name = me.actorNameField.getValue();
                var parentDomain = me.parentDomainCombo.findRecordByValue(me.parentDomainCombo.getValue());
                if (OPF.isNotBlank(name) && parentDomain != null) {
                    var path = parentDomain.get('lookup');
                    var url = OPF.Cfg.restUrl('/registry/check/' + path + '/ACTOR', false);
                    url = OPF.Cfg.addParameterToURL(url, 'name', name);
                    Ext.Ajax.request({
                        url: url,
                        method: 'GET',
                        success: function (response) {
                            if (me.actorNameField) {
                                var resp = Ext.decode(response.responseText);
                                if (resp.success) {
                                    var activeErrors = me.actorNameField.activeErrors;
                                    if (activeErrors && activeErrors.length == 0) {
                                        me.actorNameField.clearInvalid();
                                    }
                                } else {
                                    me.actorNameField.markInvalid(resp.message);
                                    me.lastNotUniqueName = name;
                                }
                            }
                        },
                        failure: function () {
                            Ext.Msg.alert('Error', 'Connection error!');
                        }
                    });
                }
            });

            this.searchDialog = Ext.create('Ext.window.Window', {
                ui: 'wizards',
                title: 'Select',
                width: 600,
                height: 600,
                layout: 'fit',
                closeAction: 'hide',
                resizable: false,
                //modal: true,
                items: [
                    this.wrapperPanel
                ]
            });
        }
        if (showOnResponse) {
            var parentDomain = me.parentDomainCombo.findRecordByValue(me.parentDomainCombo.getValue());
            var baseLookup = parentDomain.get('lookup') + '.';
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('process/actor/search/?baseLookup=' + baseLookup, false),
                method: 'GET',
                success: function (response) {
                    var resp = Ext.decode(response.responseText);
                    if (resp.success) {
                        var foundActors = resp.data;
                        me.searchStore.loadData(OPF.isEmpty(foundActors) ? [] : foundActors);
                    }
                    me.searchDialog.show();
                },
                failure: function () {
                    Ext.Msg.alert('Error', 'Connection error!');
                }
            });
        } else {
            this.searchDialog.show();
        }
    },

    getErrors: function(value) {
        var errors = [];
        value = value || this.getValue();
        if (OPF.isEmpty(value)) {
            errors.push('Actor is required field.');
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
    }

});