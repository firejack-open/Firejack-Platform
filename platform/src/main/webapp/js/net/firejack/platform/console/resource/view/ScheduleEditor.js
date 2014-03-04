/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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