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

Ext.define('OPF.prometheus.component.manager.ReferenceSearchComponent', {
    extend: 'OPF.core.component.form.FieldContainer',
    alias: 'widget.prometheus.component.reference-search-component',

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
    emptyMessage: '<div class="x-form-empty-field">Press config button to search item.</div>',

    model: null,

    value: null,

    initComponent: function() {
        var me = this;

        this.addEvents(
            'clear'
        );

        this.initializedModel = Ext.create(this.model);

        if (OPF.isNotEmpty(this.initializedModel.self.template)) {
            this.itemTpl = this.initializedModel.self.template.join('');
            this.searchFields = this.getFieldsFromTemplate();
        } else {
            this.itemTpl = '{' + this.initializedModel.displayProperty + '}';
            var field = me.findModelField(this.initializedModel, this.initializedModel.displayProperty);
            this.searchFields = [field];
        }

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
//            listeners: {
//                errorchange: function(field, error) {
//                    if (me.dropContainer.getEl()) {
//                        if (OPF.isNotBlank(error)) {
//                            me.dropContainer.addCls('opf-drop-container-invalid');
//                            me.dropContainer.addCls('x-form-invalid-field');
//                            me.dropContainer.el.dom.setAttribute('data-errorqtip', error || '');
//                        } else {
//                            me.dropContainer.removeCls('opf-drop-container-invalid');
//                            me.dropContainer.removeCls('x-form-invalid-field');
//                            me.dropContainer.el.dom.removeAttribute('data-errorqtip');
//                        }
//                    }
//                }
//            },
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
//        disable: function(panel) {
////            panel.draggableNodeIdField.allowBlank = true;
////            panel.draggableNodeIdField.disable();
//            panel.allowBlank = true;
//            panel.disable();
//            panel.isValid();
//        },
//        enable: function(panel) {
////            panel.draggableNodeIdField.allowBlank = panel.allowBlank;
////            panel.draggableNodeIdField.enable();
//            panel.allowBlank = panel.allowBlank;
//            panel.enable();
//            panel.isValid();
//        },
//        hide: function(panel) {
////            panel.draggableNodeIdField.allowBlank = true;
////            panel.draggableNodeIdField.disable();
//            panel.allowBlank = true;
//            panel.disable();
//            panel.clear();
//            panel.isValid();
//        },
//        show: function(panel) {
////            panel.draggableNodeIdField.allowBlank = panel.allowBlank;
////            panel.draggableNodeIdField.enable();
//            panel.allowBlank = panel.allowBlank;
//            panel.enable();
//            panel.isValid();
//        },
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
//        return OPF.ifBlank(this.draggableNodeIdField.getValue(), null) ;
        return this.value;
    },

    setValue: function(model) {
        var id = null;
        if (OPF.isNotEmpty(model)) {
            id = model.get(this.initializedModel.idProperty);
//            this.draggableNodeIdField.setValue(id);
            this.value = id;
            this.setData(model);
        } else {
            this.dropContainer.update(this.emptyMessage);
            this.cleanButton.hide();
            if (!this.readOnly) {
                this.configButton.show();
            }
        }
        return this.callParent([id]);
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
            this.searchField = Ext.ComponentMgr.create({
                xtype: 'opf-text',
                name: "search",
                flex:1,
                enableKeyEvents: true,
                emptyText: 'Enter search phrase...',
                listeners: {
                    change: function(cmp, newValue, oldValue) {
                        me.searchStore.load();
                    },
                    keyup: function(cmp, e) {
                        me.searchStore.load();
                    },
                    buffer: 500
                }
            });

            this.searchStore = Ext.create('Ext.data.Store', {
                model: this.model,
                proxy: {
                    type: 'ajax',
                    url : this.initializedModel.self.restSuffixUrl + '/advanced-search',
                    reader: {
                        type: 'json',
                        root: 'data'
                    },
                    startParam: 'offset',
                    limitParam: 'limit'
                },
                searchFields: this.searchFields,
                pageSize: 10,
                autoLoad: true,
                listeners: {
                    beforeload: function(store) {
                        me.onBeforeLoadSearchStore(store);
                    }
                }
            });

            var selectedItemTpl = new Ext.XTemplate(
                '<ul class="x-boxselect-list">',
                    '<tpl for=".">',
                        '<li class="x-boxselect-item item-wrap">',
                            '<div class="x-boxselect-item-text">',
                                this.itemTpl,
                            '</div>',
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
                    itemdblclick: function(view, record, item, index, e) {
                        me.setValue(record);
                        me.searchDialog.hide();
                    }
                }
            });

            this.searchDialog = Ext.create('Ext.window.Window', {
                title: 'Select',
                height: 350,
                width: 500,
                layout: 'fit',
                closeAction: 'hide',
                resizable: false,
                items: [
                    {
                        xtype: 'panel',
                        autoScroll: true,
                        items: [
                            this.selectedItemsView
                        ],
                        dockedItems: [
                            {
                                xtype: 'toolbar',
                                dock: 'top',
                                items: [
                                    this.searchField
                                ]
                            },
                            {
                                xtype: 'pagingtoolbar',
                                store: this.searchStore,
                                dock: 'bottom',
                                displayInfo: true
                            }
                        ]
                    }
                ]
            });
        }
        this.searchField.setValue(null);
        this.searchDialog.show();
    },

    getErrors: function(value) {
        var errors = [];
        value = value || this.getValue();
        if(Ext.isFunction(this.draggableNodeIdField.validator)){
            var msg = this.draggableNodeIdField.validator(value);
            if (OPF.isNotEmpty(msg)) {
                errors = msg;
            }
//            this.draggableNodeIdField.isValid();
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

    onBeforeLoadSearchStore: function (store) {
        var me = this;

        var queryParameters = [];

        var queryString = this.searchField.getValue();
        if (OPF.isNotBlank(queryString)) {
            Ext.each(store.searchFields, function(searchField) {
                if (searchField.type.type == 'string') {
                    var queryParam = [{
                        field: searchField.name,
                        operation: 'LIKE',
                        value: queryString
                    }];
                    queryParameters.push(queryParam);
                }
            });
        }

        store.getProxy().extraParams = {
            queryParameters: Ext.encode(queryParameters)
        };
    }

});
