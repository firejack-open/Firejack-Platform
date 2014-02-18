/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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
Ext.define('OPF.console.domain.view.EntityRelationshipFieldSet', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.entity-relationship-fieldset',

    entityCombo: null,
    setSelectedEntityId: null,
    flex: 1,
    padding: 10,

    constructor: function(entityCombo, setSelectedEntityId, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.EntityRelationshipFieldSet.superclass.constructor.call(this, Ext.apply({
            entityCombo: entityCombo,
            setSelectedEntityId: setSelectedEntityId
        }, cfg));
    },

    initComponent: function() {
        this.items = [
            this.entityCombo
        ];

        this.callParent(arguments);
    },

    onRender: function(ct, position) {
        this.callParent(arguments);
        // This will make sure we only drop to the view container
        var instance = this;
        var sourcePanelDropTarget = new Ext.dd.DropTarget(this.body.dom, {
            ddGroup: 'cloudNavigatorDDGroup',
            notifyEnter: function(ddSource, e, data) {
                //Add some flare to invite drop.
                var model = data.records[0];
                var elementType = model.get('type');
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(elementType);
                if (OPF.core.utils.RegistryNodeType.ENTITY == sourceType) {
                    //instance.body.stopFx();
                    instance.body.highlight();
                } else {
                    this.lock();
                    setTimeout(function() {
                        sourcePanelDropTarget.unlock();
                    }, 200);
                }
            },
            notifyDrop: function(ddSource, e, data){
                var model = data.records[0];
                var elementType = model.get('type');
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(elementType);
                if (OPF.core.utils.RegistryNodeType.ENTITY == sourceType) {
                    instance.entityCombo.setValue();
                    var nodeId = SqGetIdFromTreeEntityId(model.get('id'));
                    instance.entityCombo.setValue(model.get('text'));
                    instance.setSelectedEntityId(nodeId);
                    OPF.Msg.setAlert(true, 'Added!!!');
                    return true;
                } else {
                    Ext.MessageBox.alert('Warning',"'" + sourceType + "' does not represent Source component");
                }
                return false;
            }
        });
    }

});

/**
 *
 */
Ext.define('OPF.console.domain.view.RelationshipDataFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.relationship-data-fieldset',

    fieldLabel: 'Relationship Data',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.RelationshipDataFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.hintField = OPF.Ui.textFormField('hint', 'Hint', {
            labelAlign: 'top',
            subFieldLabel: ''
        });

        this.relationshipTypeCombo = OPF.Ui.comboFormField('relationshipType', null, {
            labelAlign: 'top',
            fieldLabel: 'Type',
            subFieldLabel: '',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            emptyText: 'select type of relationship...',
            listeners: {
                change: function() {
                    me.refreshTargetCombo();
                }
            }
        });

        this.sourceEntityPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Source Entity (i.e. Child in "Parent/Child")',
            subFieldLabel: '',
            name: 'sourceEntity',
            registryNodeType: OPF.core.utils.RegistryNodeType.ENTITY,
            readOnly: true,
            margin: '0 0 5 0'
        });

        this.targetEntityPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Target Entity (i.e. Parent in "Parent/Child")',
            subFieldLabel: '',
            name: 'targetEntity',
            registryNodeType: OPF.core.utils.RegistryNodeType.ENTITY,
            margin: '0 0 5 0',
            allowBlank: false,
            notifyEnterValidation: function(model) {
                var lookup = model.get('lookup');
                var elementType = model.get('type');
                var entitySubType = model.get('entitySubType');
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(elementType);
                return this.registryNodeType == sourceType && !this.readOnly && !me.isDataType(entitySubType)
                    && (me.editPanel.entityLookup == null || OPF.findPackageLookup(lookup) == OPF.findPackageLookup(me.editPanel.entityLookup));
            }
        });

        this.items = [
            this.hintField,
            this.relationshipTypeCombo,
            this.sourceEntityPanel,
            this.targetEntityPanel
        ];

        this.callParent(this);
    },

    refreshTargetCombo: function() {
        var type = this.relationshipTypeCombo.getValue();
        if (type == 'TREE') {
            this.targetEntityPanel.setValue(this.sourceEntityPanel.getValue());
            this.targetEntityPanel.setData(this.sourceEntityPanel.getData());
            this.targetEntityPanel.disable();
        } else {
            this.targetEntityPanel.enable();
        }
    },

    isDataType: function(typeEntityValue) {
        return OPF.isNotBlank(typeEntityValue) && typeEntityValue == 'Data';
    }
});

Ext.define('OPF.console.domain.view.RelationshipEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.relationship-editor',

    title: 'RELATIONSHIP: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.entity.relationship',

    /**
     *
     */
    initComponent: function() {
        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);
        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);
        this.relationshipDataFields = Ext.create('OPF.console.domain.view.RelationshipDataFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.relationshipDataFields
        ];

        this.callParent(arguments);
    },

    onBeforeSave: function(formData) {
        formData.sourceEntity = {
            id: this.relationshipDataFields.sourceEntityPanel.getValue()
        };
        formData.targetEntity = {
            id: this.relationshipDataFields.targetEntityPanel.getValue()
        };
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData)) {
            if (OPF.isNotEmpty(jsonData.relationshipType)) {
                this.relationshipDataFields.refreshTargetCombo(jsonData.relationshipType);
            }

            if (OPF.isNotEmpty(jsonData.sourceEntity)) {
                this.relationshipDataFields.sourceEntityPanel.setValue(jsonData.sourceEntity.id);
                var iconCls = 'tricon-entity';
                if (jsonData.sourceEntity.typeEntity == 'Classifier') {
                    iconCls = 'tricon-abstract-entity';
                } else if (jsonData.sourceEntity.typeEntity == 'Data') {
                    iconCls = 'tricon-type-entity';
                }
                var description = cutting(jsonData.sourceEntity.description, 300);
                this.relationshipDataFields.sourceEntityPanel.renderDraggableEntity(iconCls, jsonData.sourceEntity.name, description, jsonData.sourceEntity.lookup);
            }
            if (OPF.isNotEmpty(jsonData.targetEntity)) {
                this.relationshipDataFields.targetEntityPanel.setValue(jsonData.targetEntity.id);
                var iconCls = 'tricon-entity';
                if (jsonData.targetEntity.typeEntity == 'Classifier') {
                    iconCls = 'tricon-abstract-entity';
                } else if (jsonData.targetEntity.typeEntity == 'Data') {
                    iconCls = 'tricon-type-entity';
                }
                var description = cutting(jsonData.targetEntity.description, 300);
                this.relationshipDataFields.targetEntityPanel.renderDraggableEntity(iconCls, jsonData.targetEntity.name, description, jsonData.targetEntity.lookup);
            }
        }

        var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
        if (OPF.isNotEmpty(selectedNode)) {
            var elementType = selectedNode.get('type');
            var selectedNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(elementType);
            var iconCls = selectedNode.get('iconCls');
            if (OPF.core.utils.RegistryNodeType.ENTITY == selectedNodeType && iconCls == 'tricon-entity') {
                this.relationshipDataFields.sourceEntityPanel.setValue(SqGetIdFromTreeEntityId(selectedNode.get('id')));
                this.relationshipDataFields.sourceEntityPanel.renderDraggableEntity(
                    iconCls, selectedNode.get('text'), selectedNode.get('shortDescription'), selectedNode.get('lookup'));
            }
        }
    }
});

