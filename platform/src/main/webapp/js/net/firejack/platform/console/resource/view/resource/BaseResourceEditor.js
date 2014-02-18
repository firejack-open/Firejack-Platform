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
Ext.define('OPF.console.resource.view.resource.BaseResourceEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    selectedVersion: null,
    selectedCulture: 'AMERICAN',

    resourceSaveAs: 'new',

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.formButtonAlign = 'left';

        this.resourceVersionIdField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceVersionId'
        });

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this, [ this.resourceVersionIdField ]);

        /**
         * Provides the toolbar for performing resource actions, including navigating between
         * versions of the resource, saving, and canceling changes.
         */
        this.prevVersionButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'prev',
            width: 50,
            disabled: true,
            handler: function () {
                var prevVersion = instance.selectedVersion - 1;
                instance.reloadResourceVersion(instance.selectedCulture, prevVersion);
            }
        });

        this.versionPager = Ext.ComponentMgr.create({
            xtype: 'tbtext',
            text: 'not any versions'
        });

        this.nextVersionButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'next',
            width: 50,
            disabled: true,
            handler: function () {
                var nextVersion = instance.selectedVersion + 1;
                instance.reloadResourceVersion(instance.selectedCulture, nextVersion);
            }
        });
                
        this.creareNewVersion = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'new version',
            disabled: true,
            handler: function() {
                instance.createNewResourceVersion();
            }
        });

        this.setCurrentVersion = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'set current',
            disabled: true,
            handler: function() {
                instance.createNewResourceVersion(instance.selectedVersion);
            }
        });

        this.deleteCurrentVersion = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'delete',
            disabled: true,
            width: 50,
            handler: function() {
                instance.deleteResourceVersion(instance.selectedVersion);
            }
        });

        this.versionControlsPanel = Ext.ComponentMgr.create({
            xtype: 'panel',
            flex: 1,
            region: 'south',
            height: 50,
            headerAsText: false,
            border: false,
            layout: 'hbox',
            items: [
                this.prevVersionButton,
                {
                    xtype: 'tbspacer',
                    width: 15
                },
                this.versionPager,
                {
                    xtype: 'tbspacer',
                    width: 15
                },
                this.nextVersionButton,
                {
                    xtype: 'tbspacer',
                    width: 15
                },
                this.creareNewVersion,
                {
                    xtype: 'tbspacer',
                    width: 15
                },
                this.setCurrentVersion,
                {
                    xtype: 'tbspacer',
                    width: 15
                },
                this.deleteCurrentVersion
            ]
        });

        this.fieldsPanel = Ext.ComponentMgr.create({
            xtype: 'panel',
            flex: 1,
            headerAsText: false,
            border: false,
            items: [
                this.additionalFieldSet,
                this.versionControlsPanel
            ]
        });

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.fieldsPanel
        ];

        this.resourceLanguageBar = new OPF.console.resource.view.resource.ResourceLanguageBar(instance);
        this.additionalBBar = this.resourceLanguageBar;

        this.callParent(arguments);
    },

    updateVersionPager: function(lastVersion) {
        if (OPF.isEmpty(lastVersion)) {
            this.versionPager.setText('not any versions');
        } else if (OPF.isNotEmpty(this.selectedVersion)) {
            this.versionPager.setText('version ' + this.selectedVersion + ' of ' + lastVersion);
        }
        if (lastVersion <= this.selectedVersion) {
            this.nextVersionButton.disable();
        } else {
            this.nextVersionButton.enable();
        }
        if (this.selectedVersion <= 1) {
            this.prevVersionButton.disable();
        } else {
            this.prevVersionButton.enable();
        }
    },

//    hideEditPanel: function() {
//    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData)) {
            this.setTitle(this.getPanelTitle(jsonData.name));
            if (OPF.isNotEmpty(jsonData.resourceVersion)) {
                this.resourceVersionIdField.setValue(jsonData.resourceVersion.id);
                this.resourceLanguageBar.selectCulture(jsonData.resourceVersion.culture);
                this.selectedCulture = jsonData.resourceVersion.culture;
                this.selectedVersion = jsonData.resourceVersion.version;
            }
            this.updateVersionPager(jsonData.lastVersion);
            if (OPF.isNotEmpty(jsonData.resourceVersion)) {
                this.onAfterSetSpecificValue(jsonData);
            }
            if (OPF.isNotEmpty(jsonData.id) && OPF.isNotEmpty(jsonData.lastVersion)) {
                this.creareNewVersion.enable();
                this.setCurrentVersion.enable();
                this.deleteCurrentVersion.enable();
            } else {
                this.creareNewVersion.disable();
                this.setCurrentVersion.disable();
                this.deleteCurrentVersion.disable();
            }
        }
    },

    onAfterSetSpecificValue: function(jsonData) {

    },

    onBeforeSave: function(formData) {
        if (this.selectedVersion == null) {
            this.selectedVersion = 1;
        }
        formData.resourceVersion = {
            id: formData.resourceVersionId,
            culture: this.selectedCulture,
            version: this.selectedVersion
        };
        formData.selectedVersion = this.selectedVersion;
        delete formData.resourceVersionId;
        this.onBeforeSpecificDataSave(formData);
    },

    onBeforeSpecificDataSave: function(formData) {

    },

    successSaved: function(response, action, parentNode, method) {
        var vo = Ext.decode(response.responseText);

        OPF.Msg.setAlert(true, vo.message);

        this.messagePanel.showInfo(OPF.core.validation.MessageLevel.INFO, vo.message);

        var data = vo.data[0];

        this.refreshForm(data);

        this.setTitle(this.getPanelTitle(data.name));

        this.form.getEl().unmask();

        if (method == 'POST' && this.resourceSaveAs == 'new') {
            this.nodeBasicFields.idField.setValue(data.id);
            this.selectedVersion = 1;
        }
    },

    refreshForm: function(registryJsonData) {
        this.saveAs = 'update';

        if (OPF.isNotEmpty(this.onBeforeSetValue)) {
            this.onBeforeSetValue();
        }

        this.form.getForm().setValues(registryJsonData);

        if (OPF.isNotEmpty(this.onAfterSetValue)) {
            this.onAfterSetValue(registryJsonData);
        }
    },

    reloadResourceVersion: function(culture, version) {
        var instance = this;

        this.selectedCulture = culture;

        if (isEmpty(version)) {
            version = this.selectedVersion;
        }
        if (OPF.isNotEmpty(version)) {
            var resourceId = this.nodeBasicFields.idField.getValue();
            this.resourceSaveAs = OPF.isBlank(resourceId) ? 'new' : 'update';
            var cultureVersionSuffixUrl = '/' + this.selectedCulture + '/' + version;
            var url = this.registryNodeType.generateGetUrl('by-id-culture-version/' + resourceId, cultureVersionSuffixUrl);
            Ext.Ajax.request({
                url: url,
                method: 'GET',
                jsonData: '[]',

                success: function(response, action) {
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.success) {
                        instance.refreshForm(jsonData.data[0]);
                        instance.messagePanel.cleanActiveErrors();
                    } else {
                        instance.resourceVersionIdField.setValue(null);
                        instance.onReloadResourceVersionFailure();
                        instance.deleteCurrentVersion.disable();
                        instance.refreshForm(jsonData.data[0]);
                        instance.messagePanel.showError(OPF.core.validation.MessageLevel.WARN, jsonData.message);
                    }
                },

                failure: function(response) {
                    var errorJsonData = Ext.decode(response.responseText);
                    instance.saveAs = 'new';

                    instance.resourceVersionIdField.setValue(null);
                    instance.onReloadResourceVersionFailure();
                    instance.deleteCurrentVersion.disable();
                    OPF.Msg.setAlert(false, errorJsonData.message);
                    instance.messagePanel.showError(OPF.core.validation.MessageLevel.WARN, errorJsonData.message);
                }
            });
        }
    },

    onReloadResourceVersionFailure: function() {
    },

    createNewResourceVersion: function(version) {
        var instance = this;

        var id = this.nodeBasicFields.idField.getValue();
        var url = this.registryNodeType.generatePostUrl(OPF.isEmpty(version) ? '/version/' + id : '/version2/' + id + '/' + version);
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            jsonData: '[]',

            success: function(response, action) {
                instance.saveAs = 'update';
                var registryJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, registryJsonData.message);
                instance.messagePanel.showInfo(OPF.core.validation.MessageLevel.INFO, registryJsonData.message);
                instance.showEditPanel(registryJsonData.data[0]);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
                instance.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, response.message);
            }
        });
    },

    deleteResourceVersion: function() {
        var instance = this;

        var id = this.nodeBasicFields.idField.getValue();
        var url = this.registryNodeType.generatePostUrl('/version/' + id + '/' + this.selectedVersion + '/' + this.selectedCulture);
        Ext.Ajax.request({
            url: url,
            method: 'DELETE',
            jsonData: '[]',

            success: function(response, action) {
                var registryJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, registryJsonData.message);
                instance.messagePanel.showInfo(OPF.core.validation.MessageLevel.INFO, registryJsonData.message);
                instance.resourceVersionIdField.setValue(null);
                instance.deleteCurrentVersion.disable();
                instance.onSuccessDeleteResourceVersion();
                if (registryJsonData.data[0] != null) {
                    instance.saveAs = 'update';
                    instance.showEditPanel(registryJsonData.data[0]);
                } else {
                    instance.saveAs = 'new';
                    instance.versionPager.setText('not any versions');
                    instance.creareNewVersion.disable();
                    instance.setCurrentVersion.disable();
                    instance.deleteCurrentVersion.disable();
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
                instance.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, response.message);
            }
        });
    },

    onSuccessDeleteResourceVersion: function() {
    }

});