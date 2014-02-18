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

Ext.define('OPF.console.domain.view.EntityEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.entity-editor',

    title: 'ENTITY: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.entity',

    /**
     *
     */
    initComponent: function() {
        var me = this;

        var defaultValue = 'Standard';
        this.typeCombo = Ext.create('widget.opf-combo', {
            labelAlign: 'top',
            fieldLabel: 'Type',
            subFieldLabel: '',
            anchor: '100%',
            name: 'typeEntity',
            emptyText: 'select type...',
            triggerAction: 'all',
            editable: false,
            queryMode: 'local',
            store: new Ext.data.ArrayStore({
                fields: ['typeValue', 'title', 'typeIcon', 'description'],
                data: [
                    [defaultValue, 'Standard', 'entity', 'Some description for Standard entity type.'],
                    ['Classifier', 'Classifier', 'abstract_entity', 'Some description for Classifier entity type.'],
                    ['Data', 'Data', 'type_entity', 'Some description for Data entity type.']
                ]
            }),
            valueField: 'typeValue',
            displayField: 'title',
            width: 50,
            value: defaultValue,
            listConfig: {
                getInnerTpl: function() {
                    var tpl = '<div class="x-combo-list-item">';
                    tpl += '<div class="enum-item"><h3><img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/{typeIcon}_16.png" />{title}</h3>{description}</div>';
                    tpl += '</div>';
                    return tpl;
                }
            },
            listeners: {
                change: function(combo, newValue, oldValue) {
                    if (OPF.isNotBlank(oldValue)) {
                        combo.removeCls('combo-' + oldValue.toLowerCase());
                    }
                    if (OPF.isNotBlank(newValue)) {
                        combo.addCls('combo-' + newValue.toLowerCase());
                    }
                }
            }
        });

        this.extendedEntityDropPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Extended Entity',
            subFieldLabel: 'Drag and Drop Entity from Cloud Navigator',
            name: 'extendedEntity',
            registryNodeType: me.registryNodeType,
            renderDraggableEntityFromNode: function(model) {
                var instance = this;
                this.setValue(SqGetIdFromTreeEntityId(model.get('id')));
                var description = cutting(model.get('shortDescription'), 70);
                var iconCls = cutting(model.get('iconCls'), 70);
                this.renderDraggableEntity(iconCls, model.get('text'), description, model.get('lookup'));

                var mask = new Ext.LoadMask(me.getEl(), {msg: 'Loading Main Extended Entity...'});
                mask.show();

                Ext.Ajax.request({
                    url: me.registryNodeType.generateGetUrl(this.getValue()),
                    method: 'GET',
                    async: false,

                    success:function(response, action) {
                        mask.hide();
                        var vo = Ext.decode(response.responseText);
                        OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);

                        model = me.registryNodeType.createModel(vo.data[0]);
                        var description = model.get('description');
                        instance.renderDraggableEntity(iconCls, model.get('name'), cutting(description, 400), model.get('lookup'));
                    },

                    failure:function(response) {
                        mask.hide();
                        var options = new OPF.core.validation.FormInitialisationOptions({
                            messageLevel: OPF.core.validation.MessageLevel.ERROR,
                            messagePanel: this.messagePanel
                        });
                        OPF.core.validation.FormInitialisation.showValidationMessages(response, me.form, options);
                    }
                });
                return model;
            },

            notifyEnterValidation: function(model) {
                var lookup = model.get('lookup');
                var elementType = model.get('type');
                var entitySubType = model.get('entitySubType');
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(elementType);
                return this.registryNodeType == sourceType && !this.readOnly
                    && (me.isSelectedDataEntity || me.isSelectedDataEntity == me.isDataType(entitySubType)) &&
                    (me.entityLookup == null || (lookup != me.entityLookup && OPF.findPackageLookup(lookup) == OPF.findPackageLookup(me.entityLookup)));
            }
        });

        this.enableSecurity = Ext.create('widget.opf-checkbox', {
//            labelAlign: 'top',
            fieldLabel: 'Enable Security',
            subFieldLabel: '',
            name : 'securityEnabled'
        });

        var componentFields = [
            this.typeCombo,
            this.extendedEntityDropPanel,
            this.enableSecurity
        ];

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this, componentFields);

        this.resourceFields = Ext.create('OPF.core.component.editor.ResourceFieldSet', this, {
            needMethodField: false,
            readOnlyInheritedFields: true
        });

        this.fieldGridFieldSet = Ext.create('OPF.core.component.editor.FieldGridFieldSet');

        this.referenceObjectFieldSet = Ext.create('OPF.console.domain.view.ReferenceObjectFieldSet');

        this.contextRoleGridFieldSet = Ext.create('OPF.console.domain.view.entity.ContextRoleGridFieldSet');

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.resourceFields,
            this.fieldGridFieldSet,
            this.referenceObjectFieldSet,
            this.contextRoleGridFieldSet
        ];

        this.callParent(arguments);
    },

    onBeforeSetValue: function(jsonData) {
        if (this.saveAs == 'new') {
            this.fieldGridFieldSet.cleanFieldStore();
        }
        if (OPF.isNotEmpty(jsonData)) {
            Ext.each(jsonData.fields, function(field, index) {
                field.allowedValues = Ext.isArray(field.allowedValues) ? field.allowedValues : [];
            });
        }
        this.enableSecurity.setValue(OPF.isNotEmpty(jsonData.securityEnabled) && jsonData.securityEnabled);
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData)) {
            var fields;
            var isNew = OPF.isEmpty(jsonData.id) || (Ext.isString(jsonData.id) && jsonData.id.length == 0);
            var field;
            if (isNew) {
                fields = [];
                field = Ext.create('OPF.console.domain.model.FieldModel');
                field.set('name', 'id');
                field.set('autoGenerated', true);
                field.set('required', true);
                field.set('fieldType', 'NUMERIC_ID');
                field.set('fieldTypeName', 'Numeric ID');
                field.set('path', jsonData.lookup);
                field.set('lookup', jsonData.lookup + '.id');
                field.set('allowedValues', []);
                field.commit();
                fields.push(field);
                field = Ext.create('OPF.console.domain.model.FieldModel');
                field.set('name', 'created');
                field.set('autoGenerated', true);
                field.set('required', true);
                field.set('fieldType', 'CREATION_TIME');
                field.set('fieldTypeName', 'Creation Time');
                field.set('path', jsonData.lookup);
                field.set('lookup', jsonData.lookup + '.created');
                field.set('allowedValues', []);
                fields.push(field);
                field.commit();
            } else {
                fields = jsonData.fields;

                var contextRoles = jsonData.contextRoles;
                if (contextRoles == null) {
                    this.contextRoleGridFieldSet.rolesGrid.store.loadData([]);
                } else {
                    this.contextRoleGridFieldSet.rolesGrid.store.loadData(contextRoles);
                }
            }

            this.isSelectedDataEntity = this.isDataType(jsonData.typeEntity);
            if (OPF.isNotEmpty(jsonData.extendedEntity)) {
                var newPrimaryFields = [];
                var newFields = [];
                var i;
                for (i = 0; i < fields.length; i ++) {
                    var inherited = isNew ? fields[i].get('inherited') : fields[i].inherited;
                    if (OPF.isEmpty(inherited) || !inherited) {
                        var autoGenerated = isNew ? fields[i].get('autoGenerated') : fields[i].autoGenerated;
                        if (OPF.isEmpty(autoGenerated) || !autoGenerated) {
                            newPrimaryFields.push(Ext.create('OPF.console.domain.model.FieldModel', fields[i]));
                        } else {
                            newFields.push(fields[i])
                        }
                    }
                }
                var entity = jsonData.extendedEntity;
                while (OPF.isNotEmpty(entity)) {
                    if (OPF.isNotEmpty(entity.fields)) {
                        for (i = 0; i < entity.fields.length; i++) {
//                            if (entity.fields[i].name !== 'id' && entity.fields[i].name !== 'created') {
                                field = Ext.create('OPF.console.domain.model.FieldModel', entity.fields[i]);
                                field.set('inherited', true);
                                newFields.push(field);
                                field.commit();
//                            }
                        }
                    }
                    entity = entity.extendedEntity;
                }
                for (i = 0; i < newPrimaryFields.length; i++) {
                    newFields.push(newPrimaryFields[i]);
                }
                fields = newFields;

                this.selectedExtendedEntityId = jsonData.extendedEntity.id;

                this.extendedEntityDropPanel.setValue(jsonData.extendedEntity.id);
                var description = cutting(jsonData.extendedEntity.description, 70);
                this.extendedEntityDropPanel.renderDraggableEntity('tricon-entity', jsonData.extendedEntity.name, description, jsonData.extendedEntity.lookup);
            }
            if (OPF.isNotEmpty(fields)) {
                this.fieldGridFieldSet.grid.store.loadData(fields);
            }
            if (OPF.isNotEmpty(jsonData.referenceObject)) {
                this.referenceObjectFieldSet.idField.setValue(jsonData.referenceObject.id);
                this.referenceObjectFieldSet.headingField.setValue(jsonData.referenceObject.heading);
                this.referenceObjectFieldSet.subHeadingField.setValue(jsonData.referenceObject.subHeading);
                this.referenceObjectFieldSet.descriptionField.setValue(jsonData.referenceObject.description);
                this.referenceObjectFieldSet.updateExample();
            }
        }
    },

    isDataType: function(typeEntityValue) {
        return OPF.isNotBlank(typeEntityValue) && typeEntityValue == 'Data';
    },

    onBeforeSave: function(formData) {
        var contextRoles = getJsonOfStore(this.contextRoleGridFieldSet.assignedRolesStore);
        var fields = [], fieldsFromStore = getJsonOfStore(this.fieldGridFieldSet.grid.store);
        if (OPF.isNotEmpty(fieldsFromStore)) {
            for (var i = 0; i < fieldsFromStore.length; i++) {
                if (OPF.isEmpty(fieldsFromStore[i].inherited) || !fieldsFromStore[i].inherited) {
                    fields.push(fieldsFromStore[i]);
                }
            }
        }

        formData.securityEnabled = formData.securityEnabled == "on" || formData.securityEnabled === true;

        formData.fields = fields;

        Ext.each(formData.fields, function(field, index) {
            delete field.fieldTypeName;
        });


        formData.referenceObject = {
            id: this.referenceObjectFieldSet.idField.getValue(),
            heading: this.referenceObjectFieldSet.headingField.getValue(),
            subHeading: this.referenceObjectFieldSet.subHeadingField.getValue(),
            description: this.referenceObjectFieldSet.descriptionField.getValue()
        };
        delete formData.referenceId;
        delete formData.refHeading;
        delete formData.refSubHeading;
        delete formData.refDescription;

        formData.contextRoles = contextRoles;

        if (OPF.isNotEmpty(this.selectedExtendedEntityId) && 0 !== this.selectedExtendedEntityId) {
            formData.extendedEntity = {
                id: this.selectedExtendedEntityId
            };
        } else {
            delete formData.extendedEntity;
        }
        delete formData.extendedTypeEntity;

        delete formData.fieldId;
        delete formData.fieldName;
        delete formData.fieldType;
        delete formData.fieldCustomType;
        delete formData.required;
        delete formData.defaultValue;
        delete formData.allowedValues;
    },

    showEditPanel: function(registryJsonData) {
        this.callParent(arguments);
        if (OPF.isEmpty(registryJsonData.typeEntity) || '' == registryJsonData.typeEntity) {
            var defaultValue = 'Standard';
            this.typeCombo.setValue(defaultValue);
        }
        this.typeCombo.addCls('combo-' + this.typeCombo.getValue().toLowerCase());
    }

});