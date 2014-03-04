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