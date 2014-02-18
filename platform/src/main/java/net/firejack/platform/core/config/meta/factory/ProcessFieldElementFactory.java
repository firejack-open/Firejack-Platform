/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.process.EntityReference;
import net.firejack.platform.core.config.meta.element.process.FieldReference;
import net.firejack.platform.core.config.meta.element.process.ProcessFieldElement;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.process.ProcessFieldModel;
import net.firejack.platform.core.store.process.IProcessFieldStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class ProcessFieldElementFactory extends PackageDescriptorConfigElementFactory
        <ProcessFieldModel, ProcessFieldElement> {

    @Autowired
    @Qualifier("entityStore")
    private IEntityStore entityStore;

    @Autowired
    @Qualifier("processFieldStore")
    private IProcessFieldStore processFieldStore;

    public ProcessFieldElementFactory() {
        setEntityClass(ProcessFieldModel.class);
        setElementClass(ProcessFieldElement.class);
    }

    @Override
    protected void initEntitySpecific(ProcessFieldModel processField, ProcessFieldElement fieldElement) {
        super.initEntitySpecific(processField, fieldElement);
        EntityReference entityReference = fieldElement.getEntityReference();
        if (entityReference == null) {
            throw new OpenFlameRuntimeException("Entity Reference should not be blank.");
        }
        FieldReference fieldReference = fieldElement.getFieldReference();
        if (fieldReference == null) {
            throw new OpenFlameRuntimeException("Field Reference should not be blank.");
        }

        EntityModel entity = entityStore.findByUID(entityReference.getEntityUid());
        if (entity == null) {
            throw new OpenFlameRuntimeException(
                    "Referenced Entity [uid = " + entityReference.getEntityUid() + "] does not exist.");
        }
        FieldModel referencedField = null;
        for (FieldModel field : entity.getFields()) {
            if (fieldReference.getFieldUid().equals(field.getUid().getUid())) {
                referencedField = field;
                break;
            }
        }
        if (referencedField == null) {
            throw new OpenFlameRuntimeException(
                    (new StringBuilder("Failed to find referenced field [uid = "))
                            .append(fieldReference.getFieldUid())
                            .append("] which supposed to be a child of Entity [uid = ")
                            .append(entityReference.getEntityUid()).append("].").toString());
        }
        processField.setRegistryNodeType(entity);
        processField.setField(referencedField);
        processField.setName(fieldElement.getDisplayName());
        processField.setFormat(fieldElement.getFormat());
        processField.setGlobal(fieldElement.getGlobal());
        processField.setOrderPosition(fieldElement.getOrderPosition());
        processField.setValueType(fieldElement.getValueType());
        PackageDescriptorConfigElementFactory.initializeModelUID(processField, fieldElement);
    }

    @Override
    protected void initDescriptorElementSpecific(ProcessFieldElement fieldElement, ProcessFieldModel processField) {
        super.initDescriptorElementSpecific(fieldElement, processField);

        fieldElement.setDisplayName(processField.getName());
        fieldElement.setFormat(processField.getFormat());
        fieldElement.setGlobal(processField.getGlobal());
        fieldElement.setOrderPosition(processField.getOrderPosition());
        fieldElement.setValueType(processField.getValueType());
        fieldElement.setPath(null);

        EntityModel entity = processField.getRegistryNodeType();
        if (Hibernate.isInitialized(entity)) {
            if (Hibernate.isInitialized(entity.getUid())) {
                EntityReference entityReference = new EntityReference();
                entityReference.setEntityUid(entity.getUid().getUid());
                fieldElement.setEntityReference(entityReference);

                if (Hibernate.isInitialized(processField.getField().getUid())) {
                    FieldReference fieldReference = new FieldReference();
                    fieldReference.setFieldUid(processField.getField().getUid().getUid());
                    fieldElement.setFieldReference(fieldReference);
                    PackageDescriptorConfigElementFactory.initializeConfigElementUID(
                            fieldElement, processField, processFieldStore);
                } else {
                    throw new OpenFlameRuntimeException(
                            "UID of field referenced by Process Field is not initialized.");
                }
            } else {
                throw new OpenFlameRuntimeException(
                        "Referenced entity has uninitialized UID property.");
            }
        } else {
            throw new OpenFlameRuntimeException("Referenced entity is not initialized.");
        }
    }

}