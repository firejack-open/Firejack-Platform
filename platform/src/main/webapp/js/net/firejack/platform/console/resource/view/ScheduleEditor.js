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


/**
 *
 */
Ext.define('OPF.console.resource.view.ScheduleEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'Schedule: [New]',

    infoResourceLookup: 'net.firejack.platform.content.schedule',

    /**
     *
     */
    initComponent: function() {
        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.scheduleItemFields = Ext.create('OPF.console.resource.view.ScheduleItemPanel', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.scheduleItemFields
        ];

        this.callParent(arguments);
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData) && OPF.isNotEmpty(jsonData.action) && !Ext.isString(jsonData.action)) {
            this.scheduleItemFields.actionDropPanel.setValue(jsonData.action.id);
            var description = cutting(jsonData.action.description, 70);
            this.scheduleItemFields.actionDropPanel.renderDraggableEntity('tricon-action', jsonData.action.name, description, jsonData.action.lookup);
        }
    },

    onBeforeSave: function(formData) {
        var actionId = this.scheduleItemFields.actionDropPanel.getValue();
        if (OPF.isNotEmpty(actionId)) {
            formData.action = {
                id: actionId
            }
        }
    },

    hideEditPanel: function() {
        this.callParent(arguments);
        this.managerLayout.tabPanel.setActiveTab(this.managerLayout.scheduleListGrid);
    },

    onSuccessSaved: function(method, vo) {
    }

});

Ext.define('OPF.console.resource.view.ScheduleItemPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.schedule-item-panel',

    title: 'Schedule Item',
    layout: 'anchor',
    preventHeader: true,
    border: false,
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.resource.view.ScheduleItemPanel.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.cronExpressionField = OPF.Ui.textFormField('cronExpression', 'Cron Expression', {
            labelAlign: 'top',
            subFieldLabel: ''
        });

        this.emailFailureField = OPF.Ui.textFormField('emailFailure', 'Email Failure', {
            labelAlign: 'top',
            subFieldLabel: 'failure notification email'
        });

        this.activeField = OPF.Ui.formCheckBox('active', 'Turn on/off', {
            labelAlign: 'top',
            subFieldLabel: 'allow to turn scheduled task on/off',
            inputValue: true
        });

        this.actionDropPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Action which will executed by schedule',
            subFieldLabel: 'Drag and Drop from Cloud Navigator',
            name: 'action',
            registryNodeType: OPF.core.utils.RegistryNodeType.ACTION,
            setDefaultValue: function(defaultValue) {
                var jsonData = Ext.decode(defaultValue);
                me.databaseDropPanel.setValue(jsonData.id);
                var description = cutting(jsonData.description, 70);
                me.databaseDropPanel.renderDraggableEntity('tricon-action', jsonData.name, description, jsonData.lookup);
            }
        });

        this.items = [
            this.cronExpressionField,
            this.emailFailureField,
            this.activeField,
            this.actionDropPanel
        ];

        this.callParent(arguments);
    }
});