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
    'Ext.ux.CheckColumn',
    'OPF.console.domain.model.EntityModel',
    'OPF.console.domain.model.ReferenceObjectModel',
    'OPF.prometheus.wizard.AbstractWizard',
    'OPF.prometheus.wizard.entity.FieldWizard',
    'OPF.prometheus.wizard.entity.model.FieldModel',
    'OPF.prometheus.wizard.entity.model.RelationshipModel',
    'OPF.prometheus.wizard.entity.component.ReferenceFieldView'
]);

Ext.define('OPF.prometheus.wizard.entity.EntityWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.entity-wizard',

    statics: {
        id: 'entityWizard'
    },
    height: 950,
    title: 'Add a Form',
    iconCls: 'add-form-icon',

    parentWizard: null,
    domainId: null,
    editingRecord: null,

    initComponent: function() {
        var me = this;

        Ext.apply(Ext.form.field.VTypes, {
            domainname: function (v) {
                return Ext.form.field.VTypes.domainnameRegex.test(v);
            },
            domainnameRegex: /^[a-z][a-z0-9]*$/,
            domainnameText: 'Domain names must be all lower case and contain no spaces, numbers, or special characters.'
        });

        this.parentDomainStore = Ext.create('Ext.data.Store', {
            autoLoad: true,
            model: 'OPF.console.domain.model.DomainModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/domains/by-lookup/' + this.packageLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                load: function(store, records) {
                    if (me.domainId) {
                        me.parentDomainCombo.setValue(me.domainId);
                        me.parentDomainCombo.setDisabled(true);
                        me.domainId = null;
                    }
                }
            }
        });

        this.parentDomainCombo = Ext.create('OPF.core.component.form.ComboBox', {
            name: 'parentId',
            labelAlign: 'top',
            fieldLabel: 'Select Domain',
            anchor: '100%',
            store: this.parentDomainStore,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            typeAhead: true,
            isFieldValid: true,
            checkChangeBuffer: 250,
            enableKeyEvents: true,
            listConfig: {
                cls: 'x-wizards-boundlist'
            },
            listeners: {
                keyup: function(cmp, e) {
                    var value = cmp.getValue();
                    var lValue = OPF.ifBlank(value, '').toLowerCase();
                    if (Ext.isString(value) && value != lValue) {
                        cmp.setValue(lValue);
                    }
                },
                change: function() {
                    me.entitySearchPanel.hide();
                    me.entitySearchField.setValue(null);
                }
            },
            vtype: 'domainname'
        });

        this.nameField = Ext.create('OPF.core.component.form.Text', {
            name: 'name',
            labelAlign: 'top',
            fieldLabel: 'Name',
            anchor: '100%',
            isFieldValid: true,
            checkChangeBuffer: 250
        });

        this.descriptionField = Ext.create('OPF.core.component.form.TextArea', {
            name: 'description',
            labelAlign: 'top',
            fieldLabel: 'Description',
            anchor: '100%'
        });

        this.form = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            bodyPadding: 10,
            border: false,
            items: [
                this.parentDomainCombo,
                this.nameField,
                this.descriptionField
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
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToSelectFieldsPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Entity', me.messagePanel, {
                        useBaseUrl: false
                    });

                    me.nameField.customValidator = function(value) {
                        if (OPF.isNotBlank(value)) {
                            me.checkUniqueEntityName();
                        }
                        return !this.isFieldValid ? ['Entity name is not unique.'] : null;
                    };

                    me.parentDomainCombo.customValidator = function(value) {
                        if (OPF.isNotBlank(value)) {
                            me.checkUniqueDomainName();
                        }
                        return !this.isFieldValid ? ['Domain name is not unique.'] : null;
                    };
                }
            }
        });

        this.messagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.form
        });
        this.form.insert(0, this.messagePanel);

        this.fieldStore = Ext.create('Ext.data.Store', {
            model: 'OPF.prometheus.wizard.entity.model.FieldModel',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    idProperty: 'id',
                    root: 'fields'
                },
                writer: {
                    type: 'json'
                }
            }
        });

        this.fieldRowEditor = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToEdit: 2,
            listeners: {
                beforeedit: function(rowEditing, context) {
                    var autoGenerated = context.record.get('autoGenerated');
                    var inherited = context.record.get('inherited');
                    if (!autoGenerated && !inherited) {
                        var form = {
                            getForm: function() {
                                return rowEditing.editor.form;
                            }
                        };
                        me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Field', null, {
                            useBaseUrl: false
                        });

                        var nameFieldFocusTask = new Ext.util.DelayedTask(function(){
                            var textField = rowEditing.editor.form.getFields().findBy(function(field) {
                                return field.name == 'name';
                            });
                            textField.focus();
                        });
                        nameFieldFocusTask.delay(250);

                        return true;
                    } else {
                        return false;
                    }
                },
                edit: function(rowEditing, context) {
                    var fieldTypeField = rowEditing.editor.form.findField('fieldType');
                    var fieldTypeRecord = fieldTypeField.findRecordByValue(fieldTypeField.getValue());
                    var record = context.record;
                    record.set('fieldTypeName', fieldTypeRecord.get('title'));
                    if (record.isNew !== true) {
                        record.set('searchable', true);
                        record.set('allowedValues', []);
                        record.isNew = true;
                    }
                    record.commit();
                    me.validateFields();
                },
                canceledit: function(rowEditing, context) {
                    var record = context.record;
                    if (record && record.phantom) {
                        context.store.remove(record);
                    }
                }
            }
        });

        this.fieldRowToolbar = Ext.create('OPF.core.component.grid.RowToolbarGridPlugin', {
            buttons: [
                {
                    name: 'edit'
                },
                {
                    name: 'delete'
                }
            ],
            editFn: me.editField,
            deleteFn: me.deleteField,
            showFn: function(btn, e) {
                var rowSize = this.rowElement.getSize();
                this.rowCmp.setWidth(rowSize.width);
                this.showRowToolbarButton.hide();

                var autoGenerated = this.currentRecord.get('autoGenerated');
                var inherited = this.currentRecord.get('inherited');
                if (!autoGenerated && !inherited) {
                    this.editRecordButton.show();
                    this.deleteRecordButton.show();
                }
            },
            scope: this
        });

        this.addFieldButton = Ext.create('Ext.button.Button', {
            ui: 'add-entity',
            width: 60,
            height: 60,
            tooltip: 'Add Field',
            menu: {
                xtype: 'menu',
                width: 270,
                shadow: false,
                baseCls: 'group-field-type-menu',
                items: []
            }
        });

        var fieldReadOnlyProcessEvent = function (type, view, cell, recordIndex, cellIndex, e) {
            var record = view.panel.store.getAt(recordIndex);
            var autoGenerated = record.get('autoGenerated');
            var inherited = record.get('inherited');
            if (!autoGenerated && !inherited) {
                var dataIndex = this.dataIndex;
                var checked = !record.get(dataIndex);
                record.set(dataIndex, checked);
                this.fireEvent('checkchange', this, recordIndex, checked);
            }
        };

        this.fieldGrid = Ext.create('Ext.grid.Panel', {
            store: this.fieldStore,
            flex: 2,
            border: false,
            bodyBorder: false,
            columns: [
                {
                    xtype: 'gridcolumn',
                    text: '!',
                    dataIndex: 'inherited',
                    sortable: true,
                    align: 'center',
                    tdCls: 'cell-img',
                    width: 40,
                    renderer: function(inherited, metaData, record) {
                        var iconName = 'table_';
                        var autoGenerated = record.get('autoGenerated');
                        if (autoGenerated) {
                            iconName += 'lock';
                        } else if (inherited) {
                            iconName += 'inherited';
                        } else {
                            iconName += 'own';
                        }
                        return '<img class="field-type-icon" src="' + OPF.Ui.icon16(iconName + '_16.png') + '">';
                    },
                    editor: {
                        xtype: 'displayfield',
                        name: 'inherited',
                        cls: 'cell-img'
                    }
                },
                {
                    dataIndex: 'name',
                    text: 'Name',
                    sortable: true,
                    width: 200,
                    editor: {
                        xtype: 'opf-text',
                        name: 'name'
                    },
                    renderer: function(value, metaData, record ) {
                        var nameValidMessage = record.get('nameValidMessage');
                        if (OPF.isNotBlank(nameValidMessage)) {
                            metaData.tdCls = 'cell-invalid';
                            metaData.tdAttr = 'data-qtip="' + nameValidMessage + '"';
                        } else {
                            metaData.tdCls = metaData.tdAttr = null;
                        }
                        return Ext.util.Format.htmlEncode(value);
                    }
                },
                {
                    dataIndex: 'displayName',
                    text: 'Display Name',
                    sortable: true,
                    flex: 1,
                    editor: {
                        xtype: 'opf-text',
                        name: 'displayName'
                    }
                },
                {
                    dataIndex: 'fieldType',
                    text: 'Type',
                    sortable: true,
                    width: 200,
                    renderer: function(value, metaData, record) {
                        var fieldTypeValidMessage = record.get('fieldTypeValidMessage');
                        if (OPF.isNotBlank(fieldTypeValidMessage)) {
                            metaData.tdCls = 'cell-invalid';
                            metaData.tdAttr = 'data-qtip="' + fieldTypeValidMessage + '"';
                        } else {
                            metaData.tdCls = metaData.tdAttr = null;
                        }
                        var fieldTypeName = record.get('fieldTypeName');
                        return OPF.ifBlank(fieldTypeName, null);
                    },
                    editor: {
                        xtype: 'opf-combo',
                        name: 'fieldType',
                        editable: false,
                        valueField: 'value',
                        displayField: 'title'
                    }
                },
                {
                    xtype: 'checkcolumn',
                    cls: 'required-ico',
                    tooltip: 'Required',
                    align: 'center',
                    dataIndex: 'required',
                    width: 50,
                    resizable: false,
                    editor: {
                        xtype: 'opf-checkbox',
                        name: 'required'
                    },
                    processCheckEvent: fieldReadOnlyProcessEvent
                },
                {
                    xtype: 'checkcolumn',
                    cls: 'searchable-ico',
                    tooltip: 'Searchable',
                    align: 'center',
                    dataIndex: 'searchable',
                    width: 50,
                    resizable: false,
                    editor: {
                        xtype: 'opf-checkbox',
                        name: 'searchable'
                    },
                    processCheckEvent: fieldReadOnlyProcessEvent
                }
            ],
            selType: 'rowmodel',
            plugins: [
                this.fieldRowEditor,
                this.fieldRowToolbar
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'left',
                    ui: 'left-grid',
                    items: [
                        this.addFieldButton
                    ]
                }
            ]
        });

        this.relationshipRowEditor = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToEdit: 2,
            listeners: {
                beforeedit: function(rowEditing, context) {
                    var form = {
                        getForm: function() {
                            return rowEditing.editor.form;
                        }
                    };
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Relationship', null, {
                        useBaseUrl: false
                    });
                    return true;
                },
                edit: function(rowEditing, context) {
                    var relationshipTypeField = rowEditing.editor.form.findField('relationshipType');
                    var relationshipTypeRecord = relationshipTypeField.findRecordByValue(relationshipTypeField.getValue());
                    var record = context.record;
                    record.set('relationshipTypeName', relationshipTypeRecord.get('title'));
                    record.commit();
                    me.validateRelationships();
                }
            }
        });

        this.relationshipRowToolbar = Ext.create('OPF.core.component.grid.RowToolbarGridPlugin', {
            buttons: [
                {
                    name: 'edit'
                },
                {
                    name: 'delete'
                }
            ],
            editFn: me.editRelationship,
            deleteFn: me.deleteRelationship,
            showFn: function(btn, e) {
                var rowSize = this.rowElement.getSize();
                this.rowCmp.setWidth(rowSize.width);
                this.showRowToolbarButton.hide();
                this.deleteRecordButton.show();

                var targetEntityId = this.currentRecord.get('targetEntityId');
                if (targetEntityId < 0) {
                    this.editRecordButton.show();
                }
            },
            scope: this
        });

        this.addRelationshipButton = Ext.create('Ext.button.Button', {
            ui: 'add-entity',
            width: 60,
            height: 60,
            tooltip: 'Add Relationship',
            listeners: {
                click: function(btn) {
                    var btnPos = btn.getPosition();
                    var btnSize = btn.getSize();
                    if (me.entitySearchPanel.isVisible()) {
                        me.entitySearchPanel.hide();
                    } else {
                        me.entitySearchPanel.showAt(0,  btnPos[1] + btnSize.height);
                    }
                    me.entitySearchField.setValue(null);
                    me.entitySearchStore.load();
                }
            }
        });

        this.relationshipStore = Ext.create('Ext.data.Store', {
            model: 'OPF.prometheus.wizard.entity.model.RelationshipModel'
        });

        this.relationshipGrid = Ext.create('Ext.grid.Panel', {
            store: this.relationshipStore,
            flex: 1,
            border: false,
            bodyBorder: false,
            hidden: OPF.isNotEmpty(this.parentWizard),
            columns: [
                {
                    xtype: 'gridcolumn',
                    text: '!',
                    dataIndex: 'inherited',
                    sortable: true,
                    align: 'center',
                    width: 40,
                    renderer: function(inherited, metaData, record) {
                        metaData.tdCls = 'row-icon';
                        var iconName = 'table_';
                        var autoGenerated = record.get('autoGenerated');
                        if (autoGenerated) {
                            iconName += 'lock';
                        } else if (inherited) {
                            iconName += 'inherited';
                        } else {
                            iconName += 'own';
                        }
                        return '<img class="field-type-icon" src="' + OPF.Ui.icon16(iconName + '_16.png') + '">';
                    },
                    editor: {
                        xtype: 'displayfield',
                        name: 'inherited',
                        cls: 'icon-record'
                    }
                },
                {
                    dataIndex: 'targetEntityName',
                    text: 'Target Entity (i.e. Parent in "Parent/Child")',
                    flex: 1,
                    editor: {
                        xtype: 'displayfield',
                        name: 'targetEntityName',
                        cls: 'target-entity-name-record'
                    }
                },
                {
                    dataIndex: 'relationshipType',
                    text: 'Type',
                    flex: 1,
                    renderer: function(value, metaData, record) {
                        var relationshipTypeValidMessage = record.get('relationshipTypeValidMessage');
                        if (OPF.isNotBlank(relationshipTypeValidMessage)) {
                            metaData.tdCls = 'cell-invalid';
                            metaData.tdAttr = 'data-qtip="' + relationshipTypeValidMessage + '"';
                        } else {
                            metaData.tdCls = metaData.tdAttr = null;
                        }
                        var relationshipTypeName = record.get('relationshipTypeName');
                        return OPF.ifBlank(relationshipTypeName, null);
                    },
                    editor: {
                        xtype: 'opf-combo',
                        name: 'relationshipType',
                        editable: false,
                        valueField: 'value',
                        displayField: 'title'
                    }
                }
            ],
            selType: 'rowmodel',
            plugins: [
                this.relationshipRowEditor,
                this.relationshipRowToolbar
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'left',
                    ui: 'left-grid',
                    items: [
                        this.addRelationshipButton
                    ]
                }
            ]
        });

        this.entitySearchField = Ext.create('OPF.core.component.form.Text', {
            anchor: '100%',
            height: 60,
            emptyText: 'Search...',
            name: 'search',
            enableKeyEvents: true,
            listeners: {
                keyup: function(field, event) {
                    if (event.keyCode == 13) {
                        me.entitySearchStore.load();
                    }
                }
            }
        });

        this.entitySearchStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.domain.model.EntityModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/entity/search'),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                beforeload: function(store, operation) {
                    var url = OPF.Cfg.restUrl('/registry/entity/search');
                    var entitySearch = me.entitySearchField.getValue();
                    url += '?packageLookup=' + OPF.Cfg.PACKAGE_LOOKUP;
                    url += OPF.isNotBlank(entitySearch) ? '&terms=' + entitySearch : '';
                    var domainId = me.parentDomainCombo.getValue();
                    if (Ext.isNumber(domainId)) {
                        url += '&domainId=' + domainId;
                    }
                    store.proxy.url = url;
                    return true;
                }
            }
        });

        this.entitySearchPanel = Ext.create('Ext.window.Window', {
            ui: 'wizards',
            cls: 'add-relationship-window',
            constrain: true,
            width: 270,
            height: 270,
            shadow: false,
            draggable: false,
            closable: false,
            resizable: false,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'anchor',
                    cls: 'entity-search-panel',
                    items: [
                        this.entitySearchField,
                        {
                            xtype: 'button',
                            ui: 'entity-search',
                            width: 26,
                            height: 26,
                            iconCls: 'entity-search-btn',
                            tooltip: 'Entity Search',
                            listeners: {
                                click: function() {
                                    me.entitySearchStore.load();
                                }
                            }
                        }
                    ]
                },
                {
                    xtype: 'dataview',
                    cls: 'entity-list',
                    autoScroll: true,
                    flex: 1,
                    store: this.entitySearchStore,
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                            '<div class="entity-record"><b>{name}</b><br>{description}</div>',
                        '</tpl>'
                    ),
                    itemSelector: 'div.entity-record',
                    emptyText: 'No entities available',
                    listeners: {
                        itemdblclick: function(view, record, item, index, e) {
                            var relName = me.nameField.getValue() + ' ' + record.get('name');
                            relName = me.checkUniqueRelationshipName(me.relationshipStore, relName, 0);
                            var relationshipRecord = Ext.create('OPF.prometheus.wizard.entity.model.RelationshipModel', {
                                name: relName,
                                description: 'Added from Entity Wizard',
                                relationshipType: 'TYPE',
                                relationshipTypeName: 'Type',
                                targetEntityId: record.get('id'),
                                targetEntityName: record.get('name')
                            });
                            me.relationshipStore.add(relationshipRecord);
                            me.entitySearchPanel.hide();
                        }
                    }
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    border: false,
                    layout:'fit',
                    items: [
                        {
                            xtype: 'button',
                            ui: 'blue',
                            text: 'New',
                            height: 60,
                            handler: function() {
                                me.showEntityWizard();
                            }
                        }
                    ]
                }
            ]
        });

        this.selectFieldsPanel = Ext.create('Ext.panel.Panel', {
            border: false,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                this.fieldGrid,
                this.relationshipGrid
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
                            width: 250,
                            height: 60,
                            text: 'Next',
                            handler: function() {
                                me.goToReferenceObjectPanel();
                            }
                        }
                    ]
                }
            ]
        });

        this.referenceFieldView = Ext.create('OPF.prometheus.wizard.entity.component.ReferenceFieldView', {
            cls: 'x-boxselect-list',
            autoEl: 'ul',
            store: this.fieldStore
        });

        this.referenceHeadingField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refHeading',
            height: 83,
            labelAlign: 'top',
            fieldLabel: 'Reference Heading',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.referenceObjectUpdateExample();
                }
            }
        });

        this.referenceSubHeadingField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refSubHeading',
            height: 83,
            labelAlign: 'top',
            fieldLabel: 'Reference Sub-Heading',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.referenceObjectUpdateExample();
                }
            }
        });

        this.referenceDescriptionField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refDescription',
            height: 83,
            labelAlign: 'top',
            fieldLabel: 'Reference Description',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.referenceObjectUpdateExample();
                }
            }
        });

        this.referenceObjectExampleContainer = Ext.ComponentMgr.create({
            xtype: 'label-container',
            cls: 'x-form-item-label',
            labelAlign: 'top',
            fieldLabel: 'Example',
            subFieldLabel: '',
            anchor: '100%',
            tpl:
                '<div class="reference-search-inner">' +
                    '<div class="reference-heading">{heading}</div>' +
                    '<div class="reference-sub-heading">{subHeading}</div>' +
                    '<div class="reference-description">{description}</div>' +
                '</div>'
        });

        this.referenceObjectForm = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            bodyPadding: 10,
            border: false,
            items: [
                this.referenceFieldView,
                this.referenceHeadingField,
                this.referenceSubHeadingField,
                this.referenceDescriptionField,
                this.referenceObjectExampleContainer
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
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToDeployPanel();
                            }
                        }
                    ]
                }
            ]
        });

        this.items = [
            {
                title: '1. Provide Details',
                layout: 'fit',
                items: [
                    this.form
                ],
                nextFrameFn: function() {
                    me.goToSelectFieldsPanel();
                }
            },
            {
                title: '2. Select Fields',
                layout: 'fit',
                items: [
                    this.selectFieldsPanel,
                    this.entitySearchPanel
                ],
                prevFrameFn: function() {
                    me.goToProvideDetailsPanel();
                },
                nextFrameFn: function() {
                    me.goToReferenceObjectPanel();
                }
            },
            {
                title: '3. Reference Object',
                layout: 'fit',
                items: [
                    this.referenceObjectForm
                ],
                prevFrameFn: function() {
                    me.goToSelectFieldsPanel();
                },
                nextFrameFn: function() {
                    me.goToDeployPanel();
                }
            },
            {
                title: '4. Deploy Your Function',
                hidden: OPF.isNotEmpty(this.parentWizard),
                yesFn: function() {
                    me.createEntity(true);
                },
                noFn: function() {
                    me.createEntity(false);
                },
                prevFrameFn: function() {
                    me.goToReferenceObjectPanel();
                }
            }
        ];

        this.validationRequestCache = new Ext.util.MixedCollection();

        this.checkUniqueEntityName = function() {
            var entityName = me.nameField.getValue();
            var isTargetEntityNameValid = true;
            if (me.parentWizard) {
                if (me.parentWizard.nameField.getValue() == entityName) {
                    isTargetEntityNameValid = false;
                } else {
                    var relationshipRecords = me.parentWizard.relationshipStore.getRange();
                    Ext.each(relationshipRecords, function(relationshipRecord) {
                        var targetEntityName = relationshipRecord.get('targetEntityName');
                        if (targetEntityName == entityName && (!me.editingRecord || relationshipRecord != me.editingRecord.record)) {
                            isTargetEntityNameValid = false;
                        }
                    });
                }
            }

            if (isTargetEntityNameValid) {
                if (OPF.isNotBlank(entityName) && OPF.isNotEmpty(me.parentDomainCombo.store)) {
                    var parentDomainRecord = me.parentDomainCombo.findRecordByValue(me.parentDomainCombo.getValue());
                    if (parentDomainRecord) {
                        var path = encodeURIComponent(parentDomainRecord.get('lookup'));
                        var url = OPF.Cfg.restUrl('/registry/check/' + path + '/ENTITY', false);
                        url = OPF.Cfg.addParameterToURL(url, 'name', entityName);
                        var cachedResult = me.validationRequestCache.get(url);
                        if (cachedResult == null) {
                            me.validationRequestCache.add(url, {success: null});
                            Ext.Ajax.request({
                                url: url,
                                method: 'GET',
                                success: function (response) {
                                    if (me.nameField) {
                                        var resp = Ext.decode(response.responseText);
                                        me.validationRequestCache.add(url, {success: resp.success});
                                        me.nameField.isFieldValid = resp.success;
                                        me.form.getForm().isValid();
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.alert('Error', 'Connection error!');
                                }
                            });
                        } else if (cachedResult != null && cachedResult.success != null) {
                            me.nameField.isFieldValid = cachedResult.success;
                        }
                    } else {
                        me.nameField.isFieldValid = true;
                    }
                }
            } else {
                me.nameField.isFieldValid = false;
            }
        };

        this.checkUniqueDomainName = function() {
            var domainName = null;
            var domainData = me.parentDomainCombo.getValue();
            var parentDomainRecord = null;
            if (Ext.isObject(domainData)) {
                parentDomainRecord = domainData;
            } else if (Ext.isNumber(domainData)) {
                parentDomainRecord = me.parentDomainCombo.findRecordByValue(domainData);
            } else if (Ext.isString(domainData)) {
                domainName = domainData;
                parentDomainRecord = me.parentDomainCombo.findRecordByDisplay(domainData);
            }

            function changeEntityValidationStatus(success) {
                me.checkUniqueEntityName();
                me.parentDomainCombo.isFieldValid = success;
            }

            if (parentDomainRecord) {
                me.parentDomainCombo.select(parentDomainRecord);
                changeEntityValidationStatus(true);
            } else {
                if (OPF.isNotBlank(domainName)) {
                    var url = OPF.Cfg.restUrl('/registry/check/' + OPF.Cfg.PACKAGE_LOOKUP + '/DOMAIN', false);
                    url = OPF.Cfg.addParameterToURL(url, 'name', domainName);
                    var cachedResult = me.validationRequestCache.get(url);
                    if (cachedResult == null) {
                        me.validationRequestCache.add(url, {success: null});
                        Ext.Ajax.request({
                            url: url,
                            method: 'GET',
                            success: function (response) {
                                if (me.parentDomainCombo) {
                                    var resp = Ext.decode(response.responseText);
                                    me.validationRequestCache.add(url, {success: resp.success});
                                    changeEntityValidationStatus(resp.success);
                                    me.form.getForm().isValid();
                                }
                            },
                            failure: function () {
                                Ext.Msg.alert('Error', 'Connection error!');
                            }
                        });
                    } else if (cachedResult != null && cachedResult.success != null) {
                        changeEntityValidationStatus(cachedResult);
                    }
                }
            }
        };

        this.callParent(arguments);

        this.initEditMode();

        this.initFieldMenu();
    },

    referenceObjectUpdateExample: function() {
        var exampleData = {
            heading: this.referenceHeadingField.getHtmlValue(),
            subHeading: this.referenceSubHeadingField.getHtmlValue(),
            description: this.referenceDescriptionField.getHtmlValue()
        };
        this.referenceObjectExampleContainer.update(exampleData);
    },

    goToSelectFieldsPanel: function() {
        this.validateForm(function(scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(1);
            scope.addPredefinedFields();
        }, this);
    },

    goToReferenceObjectPanel: function() {
        this.validateForm(function(scope) {
            if (scope.validateFields() && scope.validateRelationships()) {
                var layout = scope.getCardPanelLayout();
                layout.setActiveItem(2);
            }
        }, this);
    },

    goToDeployPanel: function() {
        this.validateForm(function(scope) {
            if (scope.validateFields() && scope.validateRelationships()) {
                if (OPF.isEmpty(scope.parentWizard)) {
                    var layout = scope.getCardPanelLayout();
                    layout.setActiveItem(3);
                } else {
                    scope.createRelatedEntity();
                }
            }
        }, this);
    },

    validateForm: function(executeFn, scope) {
        var parentId = null;
        var domainName = this.parentDomainCombo.getValue();
        var parentDomainRecord = this.parentDomainCombo.findRecordByValue(this.parentDomainCombo.getValue());
        if (parentDomainRecord) {
            parentId = parentDomainRecord.get('id');
        }
        if ((OPF.isNotEmpty(parentId) && Ext.isNumeric(parentId)) || OPF.isNotBlank(domainName)) {
            if (this.form.getForm().isValid()) {
                executeFn(scope);
            }
        } else {
            this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Parent has not been selected.');
        }
    },

    validateFields: function() {
        var fields = this.fieldStore.getRange();

        function isFieldNameUnique(fields, checkedField) {
            var fieldNames = [];
            Ext.each(fields, function(field) {
                if (field != checkedField) {
                    var fieldName = field.get('name');
                    if (!Ext.Array.contains(fieldNames, fieldName)) {
                        fieldNames.push(fieldName);
                    }
                }
            });
            return Ext.Array.contains(fieldNames, checkedField.get('name'));
        }

        var isValid = true;
        Ext.each(fields, function(field, index) {
            var autoGenerated = field.get('autoGenerated');
            var inherited = field.get('inherited');
            if (!autoGenerated && !inherited) {
                var fieldName = field.get('name');
                if (OPF.isBlank(fieldName)) {
                    field.set('nameValidMessage', 'Field Name should not be empty.');
                    isValid &= false;
                } else if (isFieldNameUnique(fields, field)) {
                    field.set('nameValidMessage', 'Field Name is not unique.');
                    isValid &= false;
                } else {
                    field.set('nameValidMessage', null);
                }

                var fieldType = field.get('fieldType');
                if (OPF.isBlank(fieldName)) {
                    field.set('fieldTypeValidMessage', 'Field Type should not be empty.');
                    isValid &= false;
                } else if ('NUMERIC_ID' == fieldType) {
                    field.set('fieldTypeValidMessage', 'Only one field can be Numeric ID');
                    isValid &= false;
                } else {
                    field.set('fieldTypeValidMessage', null);
                }
                field.commit();
            }
        });
        return isValid;
    },

    validateRelationships: function() {
        var relationships = this.relationshipStore.getRange();

        function isParentRelationshipType(relationships, checkedRelationship) {
            var isParentRelationshipType = false;
            Ext.each(relationships, function(relationship) {
                if (relationship != checkedRelationship) {
                    var relationshipType = relationship.get('relationshipType');
                    isParentRelationshipType |= relationshipType == 'TREE' || relationshipType == 'PARENT_CHILD';
                }
            });
            return isParentRelationshipType;
        }

        var isValid = true;
        Ext.each(relationships, function(relationship, index) {
            var relationshipType = relationship.get('relationshipType');
            if ((relationshipType == 'TREE' || relationshipType == 'PARENT_CHILD') && isParentRelationshipType(relationships, relationship)) {
                relationship.set('relationshipTypeValidMessage', 'Entity can\'t has both relationships like Tree and ParentChild.');
                isValid &= false;
            } else {
                relationship.set('relationshipTypeValidMessage', null);
            }
            relationship.commit();
        });
        return isValid;
    },

    addPredefinedFields: function() {
        if (this.fieldStore.find('name', 'id') == -1) {
            var idField = OPF.ModelHelper.createModelFromData('OPF.console.domain.model.FieldModel', {
                name: 'id',
                autoGenerated: true,
                required: true,
                searchable: false,
                fieldType: 'NUMERIC_ID',
                fieldTypeName: 'Numeric ID',
                allowedValues: []
            });
            this.fieldStore.add(idField);
        }

        if (this.fieldStore.find('name', 'created') == -1) {
            var createdField = OPF.ModelHelper.createModelFromData('OPF.console.domain.model.FieldModel', {
                name: 'created',
                autoGenerated: true,
                required: false,
                searchable: false,
                fieldType: 'CREATION_TIME',
                fieldTypeName: 'Creation Time',
                allowedValues: []
            });
            this.fieldStore.add(createdField);
        }
    },

    initEditMode: function() {
        var me = this;

        if (this.editingRecord) {
            var relationshipRecord = this.editingRecord.record;
            var entityRecord = OPF.ModelHelper.getFk(relationshipRecord, 'targetEntity');
            this.form.getForm().setValues({
                name: entityRecord.get('name'),
                description: entityRecord.get('description'),
                parentId: entityRecord.get('parentId')
            });

            var referenceObjectRecord = OPF.ModelHelper.getFk(entityRecord, 'referenceObject');
            this.referenceObjectForm.getForm().setValues({
                refHeading: referenceObjectRecord.get('heading'),
                refSubHeading: referenceObjectRecord.get('subHeading'),
                refDescription: referenceObjectRecord.get('description')
            });

            var fieldRecords = entityRecord.fieldModelsStore.getRange();
            Ext.each(fieldRecords, function(fieldRecord) {
                var fieldModel = me.fieldStore.createModel(fieldRecord.getData());
                me.fieldStore.add(fieldModel);
            });
        }
    },

    initFieldMenu: function() {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('rule', this.useBaseUrl) + '?id=OPF.registry.Field',
            method: 'GET',

            success: function(response) {
                var vFields = Ext.decode(response.responseText).data;
                OPF.core.validation.FormValidator.RULE_CACHE.add('OPF.registry.Field', vFields);

                var fieldTypeData = null;
                Ext.each(vFields, function(vField) {
                    if (vField.name == 'fieldType') {
                        Ext.each(vField.constraints, function(constraint) {
                            if (constraint.name == 'EnumValue') {
                                Ext.each(constraint.params, function(param) {
                                    if (param.key == 'values') {
                                        fieldTypeData = param.value;
                                    }
                                });
                            }
                        });
                    }
                });

                if (fieldTypeData) {
                    var groupFieldTypes = {
                        ID: {
                            name: 'Id',
                            types: ['UNIQUE_ID', 'NUMERIC_ID']
                        },
                        STRING: {
                            name: 'String',
                            types: ['CODE', 'LABEL', 'NAME', 'DESCRIPTION', 'TINY_TEXT', 'SHORT_TEXT', 'MEDIUM_TEXT', 'LONG_TEXT', 'UNLIMITED_TEXT', 'RICH_TEXT', 'URL']
                        },
                        SECRET: {
                            name: 'Security',
                            types: ['SECRET_KEY', 'PASSWORD']
                        },
                        DATETIME: {
                            name: 'Date & Time',
                            types: ['DATE', 'TIME', 'EVENT_TIME', 'CREATION_TIME', 'UPDATE_TIME']
                        },
                        NUMBER: {
                            name: 'Numbers',
                            types: ['INTEGER_NUMBER', 'LARGE_NUMBER', 'DECIMAL_NUMBER', 'CURRENCY']
                        },
                        OBJECT: {
                            name: 'Objects',
                            types: ['IMAGE_FILE']
                        }
                    };

                    var groupFieldTypeItems = [];
                    for (var key in groupFieldTypes) {
                        var groupFieldType = groupFieldTypes[key];

                        var groupFieldTypeSubItems = [];
                        Ext.each(groupFieldType.types, function(type) {
                            var fieldTypeDataItem = null;
                            Ext.each(fieldTypeData, function(item) {
                                if (item[0] == type) {
                                    fieldTypeDataItem = item;
                                }
                            });
                            if (fieldTypeDataItem) {
                                var groupFieldTypeSubItem = {
                                    xtype: 'opf-menuitem',
                                    text: fieldTypeDataItem[1],
                                    description: fieldTypeDataItem[2],
                                    handler: function() {
                                        me.addField(fieldTypeDataItem[0]);
                                    }
                                };
                                groupFieldTypeSubItems.push(groupFieldTypeSubItem);
                            }
                        });

                        var groupFieldTypeItem = {
                            text: groupFieldType.name,
                            menu: {
                                xtype: 'menu',
                                width: 420,
                                shadow: false,
                                baseCls: 'field-type-menu',
                                items: groupFieldTypeSubItems
                            }
                        };
                        groupFieldTypeItems.push(groupFieldTypeItem);
                    }
                    me.addFieldButton.menu.add(groupFieldTypeItems);
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    addField: function(fieldType) {
        var fieldModel = this.fieldStore.createModel({fieldType: fieldType});
        this.fieldStore.add(fieldModel);
        this.fieldGrid.editingPlugin.startEdit(fieldModel, this.fieldGrid.columns[0]);
    },

    editField: function(btn, e, options) {
        var me = this;

        var grid = options[0];
        var record = options[1];
        var index = options[2];
        var autoGenerated = record.get('autoGenerated');
        var inherited = record.get('inherited');
        if (!autoGenerated && !inherited) {
            var wizard = Ext.WindowMgr.get(OPF.prometheus.wizard.entity.FieldWizard.id);
            if (!wizard) {
                wizard = Ext.ComponentMgr.create({
                    xtype: 'prometheus.wizard.entity-field-wizard',
                    listeners: {
                        aftersave: function() {
                            me.validateFields();
                        }
                    }
                });
                Ext.WindowMgr.register(wizard);
            }
            wizard.initWizardBeforeShow(grid.store, record, index);
            wizard.show();
            var pos = wizard.getPosition();
            wizard.setPosition(pos[0], 0);
        }
    },

    deleteField: function(btn, e, options) {
        var grid = options[0];
        var record = options[1];
        var autoGenerated = record.get('autoGenerated');
        var inherited = record.get('inherited');
        if ((OPF.isEmpty(autoGenerated) || !autoGenerated) && (OPF.isEmpty(inherited) || !inherited)) {
            grid.getStore().remove(record);
        }
        grid.fireEvent('itemmouseleave', grid, options);
    },

    editRelationship: function(btn, e, options) {
        var record = options[1];
        var targetEntityId = record.get('targetEntityId');
        if (targetEntityId < 0) {
            this.showEntityWizard({
                record: record,
                index: options[2]
            });
        } else {
            btn.hide();
        }
    },

    deleteRelationship: function(btn, e, options) {
        var grid = options[0];
        var record = options[1];
        grid.getStore().remove(record);
        grid.fireEvent('itemmouseleave', grid, options);
    },

    showEntityWizard: function(editingRecord) {
        var wizard = Ext.ComponentMgr.create({
            xtype: 'prometheus.wizard.entity-wizard',
            parentWizard: this,
            domainId: this.parentDomainCombo.getValue(),
            editingRecord: editingRecord
        });
        this.entitySearchPanel.hide();
        this.hide();
        wizard.show();
        var pos = wizard.getPosition();
        wizard.setPosition(pos[0], 0);
    },

    createEntity: function(isDeploy) {
        var me = this;

        var mainEntityData = this.form.getForm().getValues();
        mainEntityData.type = 'ENTITY';
        mainEntityData.typeEntity = 'Standard';
        mainEntityData.status = 'UNKNOWN';

        mainEntityData.fields = OPF.ModelHelper.getModelDataFromStore(this.fieldStore);

        var createdFieldsCount = 0;
        var lastCreatedFieldName = "";
        Ext.each(mainEntityData.fields, function(field) {
            if(field.fieldType === 'CREATION_TIME') {
                createdFieldsCount++;
                lastCreatedFieldName = field.name;
            }
        });

        if(createdFieldsCount > 1) {
            this.deployMessagePanel.setNoticeContainer([{
                level: OPF.core.validation.MessageLevel.ERROR,
                msg: 'It is now allowed if form has two or more Creation Time field. Please remove \'' + lastCreatedFieldName + '\' field.'
            }]);
            return;
        }

        Ext.each(mainEntityData.fields, function(field) {
            delete field.fieldTypeName;
            delete field.nameValidMessage;
            delete field.fieldTypeValidMessage;
        });

        var mainReferenceObjectData = this.referenceObjectForm.getForm().getValues();
        mainEntityData.referenceObject = {
            heading: mainReferenceObjectData.refHeading,
            subHeading: mainReferenceObjectData.refSubHeading,
            description: mainReferenceObjectData.refDescription
        };

        var formData = {
            entities: [mainEntityData],
            relationships: []
        };

        if (!Ext.isNumber(mainEntityData.parentId)) {
            formData.domain = {
                name: mainEntityData.parentId,
                path: OPF.Cfg.PACKAGE_LOOKUP,
                dataSource: false,
                parentId: 0
            };
            mainEntityData.parentId = 0;
        }

        var relationshipRecords = this.relationshipStore.getRange();
        Ext.each(relationshipRecords, function(relationshipRecord) {
            var entityRecord = OPF.ModelHelper.getFk(relationshipRecord, 'targetEntity');
            if (entityRecord && entityRecord.get('id') < 0) {
                var entityData = entityRecord.getData(true);
                entityData.parentId = mainEntityData.parentId;
                entityData.type = 'ENTITY';
                entityData.typeEntity = 'Standard';
                entityData.status = 'UNKNOWN';
                entityData.fields = entityData.fieldModels;
                Ext.each(entityData.fields, function(field) {
                    delete field.fieldTypeName;
                    delete field.nameValidMessage;
                    delete field.fieldTypeValidMessage;
                });
                delete entityData.fieldModels;

                formData.entities.push(entityData);
            }

            var relationshipData = relationshipRecord.getData();
            relationshipData.parentId = 0;
            relationshipData.sourceEntity = {};
            relationshipData.targetEntity = {
                id: relationshipData.targetEntityId
            };
            delete relationshipData.sourceEntityId;
            delete relationshipData.sourceEntityName;
            delete relationshipData.targetEntityId;
            delete relationshipData.targetEntityName;
            delete relationshipData.relationshipTypeName;
            delete relationshipData.relationshipTypeValidMessage;

            formData.relationships.push(relationshipData);
        });

        this.save(OPF.Cfg.restUrl('/registry/form'), formData, isDeploy);
    },

    createRelatedEntity: function() {
        var me = this;
        var relName;
        var relationshipRecord;
        var entityData = this.form.getForm().getValues();
        var referenceObjectData = this.referenceObjectForm.getForm().getValues();
        if (this.editingRecord) {
            relationshipRecord = this.editingRecord.record;
            var entityRecord = OPF.ModelHelper.getFk(relationshipRecord, 'targetEntity');

            relName = this.parentWizard.nameField.getValue() + ' ' + entityData.name;
            relName = this.checkUniqueRelationshipName(this.parentWizard.relationshipStore, relName, 0);

            relationshipRecord.beginEdit();
            relationshipRecord.set('name', relName);
            relationshipRecord.set('targetEntityName', entityData.name);
            entityRecord.set('name', entityData.name);
            entityRecord.set('description', entityData.description);

            var referenceObjectRecord = OPF.ModelHelper.getFk(entityRecord, 'referenceObject');
            referenceObjectRecord.set('heading', referenceObjectData.refHeading);
            referenceObjectRecord.set('subHeading', referenceObjectData.refSubHeading);
            referenceObjectRecord.set('description', referenceObjectData.refDescription);

            entityRecord.fieldModelsStore.removeAll();
            var fieldRecords = this.fieldStore.getRange();
            Ext.each(fieldRecords, function(fieldRecord) {
                var fieldModel = entityRecord.fieldModelsStore.createModel(fieldRecord.getData());
                entityRecord.fieldModelsStore.add(fieldModel);
            });
            relationshipRecord.endEdit();
        } else {
            var uniqueEntityId = - Math.floor(Math.random() * 1000000000);
            entityData.id = uniqueEntityId;
            entityData.type = 'ENTITY';
            entityData.typeEntity = 'Standard';
            entityData.status = 'UNKNOWN';
            entityData.fieldModels = OPF.ModelHelper.getModelDataFromStore(this.fieldStore);

            entityData.referenceObject = {
                heading: referenceObjectData.refHeading,
                subHeading: referenceObjectData.refSubHeading,
                description: referenceObjectData.refDescription
            };

            relName = this.parentWizard.nameField.getValue() + ' ' + entityData.name;
            relName = this.checkUniqueRelationshipName(this.parentWizard.relationshipStore, relName, 0);

            relationshipRecord = OPF.ModelHelper.createModelFromData('OPF.console.domain.model.RelationshipModel', {
                name: relName,
                description: 'Added from Entity Wizard',
                relationshipType: 'TYPE',
                relationshipTypeName: 'Type',
                targetEntityId: uniqueEntityId,
                targetEntityName: entityData.name,
                targetEntity: entityData
            });

            this.parentWizard.relationshipStore.add(relationshipRecord);
        }

        this.close();
        this.parentWizard.show();
    },

    checkUniqueRelationshipName: function(store, relName, index) {
        var suffix = index > 0 ? ' ' + index : '';
        var record = store.find('name', relName + suffix);
        if (record >= 0) {
            relName = this.checkUniqueRelationshipName(store, relName, ++index);
        } else {
            relName = relName + suffix;
        }
        return relName;
    }

});