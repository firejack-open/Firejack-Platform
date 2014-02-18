/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.construct.WizardElement;
import net.firejack.platform.core.config.meta.construct.WizardFieldElement;
import net.firejack.platform.core.config.meta.construct.WizardFormElement;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.wizard.WizardFieldModel;
import net.firejack.platform.core.model.registry.wizard.WizardModel;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IFieldStore;
import net.firejack.platform.core.store.registry.IRelationshipStore;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class WizardElementFactory extends PackageDescriptorConfigElementFactory<WizardModel, WizardElement> {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IFieldStore fieldStore;
    @Autowired
    private IRelationshipStore relationshipStore;

    public WizardElementFactory() {
        setElementClass(WizardElement.class);
        setEntityClass(WizardModel.class);
    }

    @Override
    protected void initDescriptorElementSpecific(WizardElement wizardElement, WizardModel wizard) {
        super.initDescriptorElementSpecific(wizardElement, wizard);
        List<WizardFieldModel> fields = wizard.getFields();
        if (fields != null) {
            if (Hibernate.isInitialized(fields)) {
                Map<Long, WizardFormElement> wizardFormElements = new LinkedHashMap<Long, WizardFormElement>();
                for (WizardFieldModel field : fields) {
                    WizardFieldModel form = field.getForm();
                    if (form != null) {
                        WizardFormElement wizardFormElement = wizardFormElements.get(form.getId());
                        if (wizardFormElement == null) {
                            wizardFormElement = new WizardFormElement();
                            wizardFormElement.setDisplayName(form.getDisplayName());
                            wizardFormElements.put(form.getId(), wizardFormElement);
                        }

                        List<WizardFieldElement> wizardFieldElements = wizardFormElement.getFields();
                        if (wizardFieldElements == null) {
                            wizardFieldElements = new ArrayList<WizardFieldElement>();
                            wizardFormElement.setFields(wizardFieldElements);
                        }
                        WizardFieldElement wizardField = new WizardFieldElement();
                        if (field.getField() != null) {
                            wizardField.setField(field.getField().getLookup());
                        }
                        if (field.getRelationship() != null) {
                            wizardField.setRelationship(field.getRelationship().getLookup());
                        }
                        wizardField.setDisplayName(field.getDisplayName());
                        wizardField.setEditable(field.getEditable());
                        wizardField.setDefaultValue(field.getDefaultValue());
                        wizardFieldElements.add(wizardField);
                    }
                }
                wizardElement.setForms(new ArrayList<WizardFormElement>(wizardFormElements.values()));
            }
        }
        wizardElement.setName(wizard.getName());
        wizardElement.setPath(wizard.getPath());
        wizardElement.setDescription(wizard.getDescription());
        RegistryNodeModel main = wizard.getMain();
        if (main == null) {
            logger.error("Main model information is not set for the wizard [name = " + wizard.getName() + "].");
        } else {
            wizardElement.setModel(main.getLookup());
        }
    }

    @Override
    protected void initEntitySpecific(WizardModel wizard, WizardElement wizardElement) {
        super.initEntitySpecific(wizard, wizardElement);
        List<WizardFormElement> wizardElementForms = wizardElement.getForms();
        String modelLookup = wizardElement.getModel();
        EntityModel model = entityStore.findByLookup(modelLookup);

        if (model == null)
            throw new BusinessFunctionException("Wizard model not found by lookup: " + modelLookup);

        wizard.setMain(model);

        if (wizardElementForms != null) {
            Set<String> fields = new HashSet<String>();
            Set<String> relationships = new HashSet<String>();

            for (WizardFormElement wizardFormElement : wizardElementForms) {
                List<WizardFieldElement> wizardFieldElements = wizardFormElement.getFields();
                for (WizardFieldElement wizardFieldElement : wizardFieldElements) {
                    String field = wizardFieldElement.getField();
                    if (field != null) {
                        fields.add(field);
                    }
                    String relationship = wizardFieldElement.getRelationship();
                    if (relationship != null) {
                        relationships.add(relationship);
                    }
                }
            }
            Map<String, Long> cachedFields = fieldStore.searchKeys("lookup", fields);
            Map<String, Long> cachedRelationships = relationshipStore.searchKeys("lookup", relationships);

            List<WizardFieldModel> wizardFieldModels = new ArrayList<WizardFieldModel>();
            for (int i = 0; i < wizardElementForms.size(); i++) {
                WizardFormElement wizardFormElement = wizardElementForms.get(i);
                WizardFieldModel wizardFormModel = new WizardFieldModel();
                wizardFormModel.setDisplayName(wizardFormElement.getDisplayName());
                wizardFormModel.setPosition(i);
                wizardFieldModels.add(wizardFormModel);

                List<WizardFieldElement> wizardFieldElements = wizardFormElement.getFields();
                for (int j = 0; j < wizardFieldElements.size(); j++) {
                    WizardFieldElement wizardFieldElement = wizardFieldElements.get(j);
                    WizardFieldModel wizardFieldModel = new WizardFieldModel();
                    wizardFieldModel.setDisplayName(wizardFieldElement.getDisplayName());
                    wizardFieldModel.setForm(wizardFormModel);
                    wizardFieldModel.setEditable(wizardFieldElement.isEditable());
                    wizardFieldModel.setDefaultValue(wizardFieldElement.getDefaultValue());
                    wizardFieldModel.setPosition(j);

                    String field = wizardFieldElement.getField();
                    if (field != null) {
                        Long fieldId = cachedFields.get(field);
                        if (fieldId != null) {
                            wizardFieldModel.setField(new FieldModel(fieldId));
                        }
                    }
                    String relationship = wizardFieldElement.getRelationship();
                    if (relationship != null) {
                        Long relationshipId = cachedRelationships.get(relationship);
                        if (relationshipId != null) {
                            wizardFieldModel.setRelationship(new RelationshipModel(relationshipId));
                        }
                    }

                    wizardFieldModels.add(wizardFieldModel);
                }
            }
            wizard.setFields(wizardFieldModels);
        }

        wizard.setName(wizardElement.getName());
        wizard.setPath(wizardElement.getPath());
        wizard.setDescription(wizardElement.getDescription());

        RegistryNodeModel parent = registryNodeStore.findByLookup(wizard.getPath());
        wizard.setParent(parent);
    }

}