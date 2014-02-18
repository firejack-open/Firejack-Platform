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
    'OPF.prometheus.wizard.AbstractWizard'
]);

Ext.define('OPF.prometheus.wizard.relationship.RelationshipWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.relationship-wizard',

    statics: {
        id: 'relationshipWizard'
    },

    id: 'relationshipWizard',
    title: 'Add a relationship',
    iconCls: 'add-relationship-icon',

    initComponent: function() {
        var me = this;

        this.nameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Name',
            name: 'name',
            anchor: '100%'
        });

        this.descriptionField = Ext.create('OPF.core.component.form.TextArea', {
            name: 'description',
            labelAlign: 'top',
            fieldLabel: 'Description',
            anchor: '100%'
        });

        this.baseForm = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            bodyPadding: 10,
            border: false,
            items: [
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
                                me.goToSelectEntitiesPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Relationship', me.messagePanel, {
                        useBaseUrl: false
                    });

//                    me.nameField.customValidator = function(value) {
//                        if (OPF.isNotBlank(value)) {
//                            me.checkUniqueEntityNameTask.delay(250);
//                        }
//                        return null;
//                    };
                }
            }
        });

        this.baseMessagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.baseForm
        });
        this.baseForm.insert(0, this.baseMessagePanel);


        this.hintField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Hint',
            name: 'hint',
            anchor: '100%'
        });

        this.relationshipTypeCombo = Ext.create('OPF.core.component.form.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Type',
            subFieldLabel: '',
            name: 'relationshipType',
            anchor: '100%',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            emptyText: 'select type of relationship...',
            displayTpl: Ext.create('Ext.XTemplate',
                '<tpl for=".">',
                    '{title}',
                '</tpl>'
            ),
            listeners: {
                change: function() {
                    me.refreshTargetCombo();
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        var fields = [
            { name: 'id', type: 'int' },
            { name: 'parameters' },
            { name: 'path' },
            { name: 'lookup' },
            { name: 'name', type: 'string',
                convert: function(value, record) {
                    return record.get('parameters').domainName + '.' + value;
                }
            }
        ];

        this.entityStore = Ext.create('Ext.data.Store', {
            autoLoad: true,
            fields: fields,
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/entities/by-lookup/' + this.packageLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                load: function(store, records) {
                    me.targetStore.loadRecords(records);
                }
            }
        });

        this.targetStore = Ext.create('Ext.data.Store', {
            fields: fields
        });

        this.sourceEntityCombo = Ext.create('OPF.core.component.form.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Source Entity',
            name: 'sourceEntity',
            anchor: '100%',
            editable: false,
            store: this.entityStore,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            listeners: {
                select: function(combo, records, eOpts) {
                    me.selectSourceCombo();
                },
                change: function(combo) {
                    me.selectSourceCombo();
                }
            }
        });



        this.targetEntityCombo = Ext.create('OPF.core.component.form.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Target Entity',
            name: 'targetEntity',
            anchor: '100%',
            editable: false,
            store: this.targetStore,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            disabled: true
        });

        this.relationshipForm = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            bodyPadding: 10,
            border: false,
            items: [
                this.hintField,
                this.relationshipTypeCombo,
                this.sourceEntityCombo,
                this.targetEntityCombo
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
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Relationship', me.relationshipMessagePanel, {
                        useBaseUrl: false
                    });
                }
            }
        });

        this.relationshipMessagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.relationshipForm
        });
        this.relationshipForm.insert(0, this.relationshipMessagePanel);

        this.items = [
            {
                title: '1. Provide Details',
                layout: 'fit',
                items: [
                    this.baseForm
                ],
                nextFrameFn: function() {
                    me.goToSelectEntitiesPanel();
                }
            },
            {
                title: '2. Select Entities',
                layout: 'fit',
                items: [
                    this.relationshipForm
                ],
                prevFrameFn: function() {
                    me.goToProvideDetailsPanel();
                },
                nextFrameFn: function() {
                    me.goToDeployPanel();
                }
            },
            {
                title: '3. Deploy Your Function',
                yesFn: function() {
                    me.createRelationship(true);
                },
                noFn: function() {
                    me.createRelationship(false);
                },
                prevFrameFn: function() {
                    me.goToSelectEntitiesPanel();
                }
            }
        ];

//        this.validationRequestCache = new Ext.util.MixedCollection();
//
//        this.checkUniqueNameTask = new Ext.util.DelayedTask(function(){
//            function changeDomainValidationStatus(success, name) {
//                if (success) {
//                    var activeErrors = me.nameField.activeErrors;
//                    if (activeErrors && activeErrors.length == 0) {
//                        me.nameField.clearInvalid();
//                    }
//                } else {
//                    me.nameField.markInvalid('Entity name is not unique.');
//                }
//            }
//
//            var name = me.nameField.getValue();
//            if (OPF.isNotBlank(name)) {
//                var parentDomainRecord = me.parentDomainCombo.findRecordByValue(me.parentDomainCombo.getValue());
//                if (parentDomainRecord) {
//                    var path = encodeURIComponent(parentDomainRecord.get('lookup'));
//                    var url = OPF.Cfg.restUrl('/registry/check/' + path + '/ENTITY', false);
//                    url = OPF.Cfg.addParameterToURL(url, 'name', name);
//                    var cachedResult = me.validationRequestCache.get(url);
//                    if (cachedResult == null) {
//                        Ext.Ajax.request({
//                            url: url,
//                            method: 'GET',
//                            success: function (response) {
//                                if (me.nameField) {
//                                    var resp = Ext.decode(response.responseText);
//                                    me.validationRequestCache.add(url, resp.success);
//                                    changeDomainValidationStatus(resp.success, name);
//                                }
//                            },
//                            failure: function () {
//                                Ext.Msg.alert('Error', 'Connection error!');
//                            }
//                        });
//                    } else {
//                        changeDomainValidationStatus(cachedResult, name);
//                    }
//                } else {
//                    me.nameField.clearInvalid();
//                }
//            }
//        });

        this.callParent();
    },

    goToSelectEntitiesPanel: function() {
        if (this.baseForm.getForm().isValid()) {
            var layout = this.getCardPanelLayout();
            layout.setActiveItem(1);
        }
    },

    goToDeployPanel: function() {
        var layout = this.getCardPanelLayout();
        if (this.baseForm.getForm().isValid()) {
            if (this.relationshipForm.getForm().isValid()) {
                layout.setActiveItem(2);
            } else {
                layout.setActiveItem(1);
            }
        } else {
            layout.setActiveItem(0);
        }
    },

    refreshTargetCombo: function() {
        var type = this.relationshipTypeCombo.getValue();
        if (type == 'TREE') {
            this.targetEntityCombo.setValue(this.sourceEntityCombo.getValue());
            this.targetEntityCombo.disable();
        } else {
            this.targetEntityCombo.enable();
        }
    },

    selectSourceCombo: function() {
        var sourceEntity = this.sourceEntityCombo.findRecordByValue(this.sourceEntityCombo.getValue());
        var selectedDataSource = sourceEntity.get('parameters').dataSource;
        this.targetStore.filterBy(function(record) {
            var dataSource = record.get('parameters').dataSource;
            return dataSource == selectedDataSource;
        });

        var type = this.relationshipTypeCombo.getValue();
        if (type == 'TREE') {
            this.targetEntityCombo.setValue(this.sourceEntityCombo.getValue());
        }
    },

    createRelationship: function(isDeploy) {
        var me = this;

        var baseFormData = this.baseForm.getForm().getValues();
        var relationshipFormData = this.relationshipForm.getForm().getValues();

        relationshipFormData = Ext.apply(baseFormData, relationshipFormData);

        relationshipFormData.parentId = relationshipFormData.sourceEntity;
        relationshipFormData.sourceEntity = {
            id: relationshipFormData.sourceEntity
        };
        relationshipFormData.targetEntity = {
            id: relationshipFormData.targetEntity
        };

        this.getEl().mask();

        this.save(OPF.Cfg.restUrl('/registry/relationship'), relationshipFormData, isDeploy);

//        Ext.Ajax.request({
//            url: url,
//            method: 'POST',
//            jsonData: {"data": relationshipFormData},
//
//            success:function(response, action) {
//                var responseData = Ext.decode(response.responseText);
//                OPF.Msg.setAlert(true, responseData.message);
//                me.getEl().unmask();
//                me.close();
//
//                if (isDeploy) {
//                    me.redeploy();
//                }
//            },
//
//            failure:function(response) {
//                me.getEl().unmask();
//                OPF.Msg.setAlert(false, response.message);
//            }
//        });

    }

});