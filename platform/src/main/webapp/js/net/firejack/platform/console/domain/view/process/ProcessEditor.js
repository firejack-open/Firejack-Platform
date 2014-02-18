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
Ext.define('OPF.console.domain.view.process.ProcessEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.process-editor',

    title: 'PROCESS: [New]',

    infoResourceLookup: 'net.firejack.platform.process.process',

    /**
     *
     */
    initComponent: function() {
        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.statusesFieldSet = Ext.create('OPF.console.domain.view.process.StatusesFieldSet', this);

        this.activitiesStrategyFieldSet = Ext.create('OPF.console.domain.view.process.ActivitiesStrategyFieldSet');

        this.activitiesFieldSet = Ext.create('OPF.console.domain.view.process.ActivitiesFieldSet', this);

        this.explanationsFieldSet = Ext.create('OPF.console.domain.view.process.ExplanationFieldSet', this);

        this.customFieldsFieldSet = Ext.create('OPF.console.domain.view.process.CustomFieldsFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.statusesFieldSet,
            this.activitiesStrategyFieldSet,
            this.activitiesFieldSet,
            this.explanationsFieldSet,
            this.customFieldsFieldSet
        ];

        this.callParent(arguments);
    },

    onBeforeSetValue: function(jsonData) {
        if (this.saveAs == 'new') {
            this.activitiesFieldSet.activitiesGrid.cleanFieldStore();
            this.statusesFieldSet.statusesGrid.cleanFieldStore();
            this.explanationsFieldSet.explanationGrid.cleanFieldStore();
            this.customFieldsFieldSet.customFieldsGrid.cleanFieldStore();
        }
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData)) {
            if (OPF.isNotEmpty(jsonData.activities)) {
                var activityModels = [];
                Ext.each(jsonData.activities, function(activity, index) {
                    var activityModel = Ext.create('OPF.console.domain.model.ActivityModel');
                    activityModel.set('id', activity.id);
                    activityModel.set('name', activity.name);
                    activityModel.set('path', activity.path);
                    activityModel.set('description', activity.description);
                    activityModel.set('activityType', activity.activityType);
                    activityModel.set('notify', activity.notify);
                    activityModel.set('actorId', activity.actor.id);
                    activityModel.set('actorName', activity.actor.name);
                    activityModel.set('statusId', activity.status.id);
                    activityModel.set('statusName', activity.status.name);
                    activityModel.set('sortPosition', activity.sortPosition);
                    activityModel.set('created', activity.created);
                    activityModel.commit();
                    activityModels.push(activityModel);
                });
                this.activitiesFieldSet.activitiesGrid.store.loadData(activityModels);
            }
            if (OPF.isNotEmpty(jsonData.statuses)) {
                var statusModels = [];
                Ext.each(jsonData.statuses, function(status, index) {
                    var statusModel = Ext.create('OPF.console.domain.model.StatusModel', status);
                    statusModel.commit();
                    /*statusModel.set('id', status.id);
                    statusModel.set('name', status.name);
                    statusModel.set('path', status.path);
                    statusModel.set('description', status.description);*/
                    statusModels.push(statusModel);
                });
                this.statusesFieldSet.statusesGrid.store.loadData(statusModels);
            }
            if (OPF.isNotEmpty(jsonData.explanations)) {
                var explanationModels = [];
                Ext.each(jsonData.explanations, function(explanation, index) {
                    var explanationModel = Ext.create('OPF.console.domain.model.CaseExplanationModel', explanation);
                    explanationModel.commit();
                    /*explanationModel.set('id', explanation.id);
                    explanationModel.set('longDescription', explanation.longDescription);
                    explanationModel.set('shortDescription', explanation.shortDescription);*/

                    explanationModels.push(explanationModel);
                });
                this.explanationsFieldSet.explanationGrid.store.loadData(explanationModels);
            }
            if (OPF.isNotEmpty(jsonData.processFields)) {
                var processFieldModels = [];
                Ext.each(jsonData.processFields, function(processField, index) {
                    var processFieldModel = Ext.create('OPF.console.domain.model.ProcessFieldModel');
                    processFieldModel.set('id', processField.id);
                    processFieldModel.set('name', processField.name);
                    processFieldModel.set('valueType', processField.valueType);
                    processFieldModel.set('global', processField.global);
                    if (OPF.isNotEmpty(processField.field.id)) {
                        processFieldModel.set('fieldId', processField.field.id);
                    }
                    processFieldModel.set('format', processField.format);
                    processFieldModel.set('registryNodeTypeId', processField.registryNodeType.id);
                    processFieldModel.set('orderPosition', processField.orderPosition);
                    processFieldModel.commit();
                    processFieldModels.push(processFieldModel);
                });
                //this.processEntityDropPanel
                this.customFieldsFieldSet.customFieldsGrid.store.loadData(processFieldModels);
            }
        }
    },

    onBeforeSave: function(formData) {
        var instance = this;

        formData.activities = getJsonOfStore(this.activitiesFieldSet.activitiesGrid.store);
        Ext.each(formData.activities, function(activity, index){
            activity.path = instance.nodeBasicFields.lookupField.getValue();
            activity.lookup = calculateLookup(activity.path, activity.name);
            activity.actor = {
                id: activity.actorId
            };
            activity.status = {
                id: activity.statusId,
                name: activity.statusName
            };
            delete activity.actorName; // not needed to be sent
            delete activity.actorId;
            delete activity.statusId;
            delete activity.statusName;
        });

        delete formData.supportMultiActivities;
        formData.supportMultiActivities = this.activitiesStrategyFieldSet.supportMultiActivities;
        formData.statuses = getJsonOfStore(this.statusesFieldSet.statusesGrid.store);
        Ext.each(formData.statuses, function(status, index){
            status.path = instance.nodeBasicFields.lookupField.getValue();
            status.lookup = calculateLookup(status.path, status.name);
        });

        formData.explanations = getJsonOfStore(this.explanationsFieldSet.explanationGrid.store);

        formData.processFields = [];
        var gridProcessFields = getJsonOfStore(this.customFieldsFieldSet.customFieldsGrid.store);
        Ext.each(gridProcessFields, function(gridProcessField, index){
            var formSubmissionProcessField = {
                field: {
                    id: gridProcessField.fieldId
                },
                registryNodeType: {
                    id: gridProcessField.registryNodeTypeId
                },
                format: gridProcessField.format,
                global: gridProcessField.global,
                name: gridProcessField.name,
                orderPosition: gridProcessField.orderPosition,
                valueType: gridProcessField.valueType
            };

            formData.processFields.push(formSubmissionProcessField);
        });

        this.callParent(arguments);
    }

});
