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

import net.firejack.platform.core.config.meta.INamedPackageDescriptorElement;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.TreeEntityModel;
import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.UIDModel;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.version.IUIDStore;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;


//todo: apply to XmlToRegistryTranslator
public class PackageDescriptorConfigElementFactory<IE extends BaseEntityModel, PDE extends PackageDescriptorElement> {

    private Class<IE> entityClass;
    private Class<PDE> elementClass;
    protected ConfigElementFactory configElementFactory = ConfigElementFactory.getInstance();

    @Autowired
    @Qualifier("registryNodeStore")
    protected IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    /**
     * @return
     */
    public Class<IE> getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass
     */
    public void setEntityClass(Class<IE> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return
     */
    public Class<PDE> getElementClass() {
        return elementClass;
    }

    /**
     * @param elementClass
     */
    public void setElementClass(Class<PDE> elementClass) {
        this.elementClass = elementClass;
    }

    /**
     * @param entity
     * @return
     */
    public PDE getDescriptorElement(IE entity) {
        /**/
        PDE descriptorElement = populateDescriptorElement(entity);
        if (descriptorElement == null) {
            throw new IllegalStateException("Failed to instantiate registry node element.");
        }
        LookupModel<RegistryNodeModel> lookupModel = castToLookupModel(entity);
        if (lookupModel != null) {
            descriptorElement.setName(lookupModel.getName());
            descriptorElement.setDescription(lookupModel.getDescription());
        }
        String refPath = getRefPath(entity);
//            String refPath = entity.getPath();
        descriptorElement.setPath(refPath);
        //configElementFactory.normalizeName(descriptorElement);
        initDescriptorElementSpecific(descriptorElement, entity);
        initializeConfigElementUID(descriptorElement, lookupModel, registryNodeStore);
        return descriptorElement;
    }

    /**
     * @param descriptorElement
     * @return
     */
    public IE getEntity(PDE descriptorElement) {
        configElementFactory.checkElementName(descriptorElement);
        IE entity = populateEntity(descriptorElement);
        if (entity == null) {
            throw new OpenFlameRuntimeException("Failed to instantiate entity.");
        }

        LookupModel<RegistryNodeModel> lookupModel = castToLookupModel(entity);
        if (lookupModel != null) {
            lookupModel.setName(descriptorElement.getName());
            lookupModel.setDescription(descriptorElement.getDescription());
            if (StringUtils.isNotBlank(descriptorElement.getPath())) {
                lookupModel.setPath(DiffUtils.lookupByRefPath(descriptorElement.getPath()));
                RegistryNodeModel parent = registryNodeStore.findByLookup(lookupModel.getPath());
                lookupModel.setParent(parent);
            }
            lookupModel.setLookup(DiffUtils.lookup(lookupModel.getPath(), lookupModel.getName()));
        }
        initEntitySpecific(entity, descriptorElement);
        if (lookupModel != null) {
            initializeModelUID(lookupModel, descriptorElement);
        }
        return entity;
    }

    /**
     * @param entityList
     * @return
     */
    public List<PDE> getDescriptorElementList(List<IE> entityList) {
        List<PDE> descriptorElementList;
        if (entityList == null) {
            descriptorElementList = null;
        } else {
            descriptorElementList = new ArrayList<PDE>();
            for (IE entity : entityList) {
                descriptorElementList.add(getDescriptorElement(entity));
            }
        }
        return descriptorElementList;
    }

    /**
     * @param elementList
     * @return
     */
    public List<IE> getEntityList(List<PDE> elementList) {
        List<IE> entityList;
        if (elementList == null) {
            entityList = null;
        } else {
            entityList = new ArrayList<IE>();
            for (PDE descriptorElement : elementList) {
                entityList.add(getEntity(descriptorElement));
            }
        }
        return entityList;
    }

    protected String getRefPath(IE entity) {
        return getRefPathProviderStore().findRegistryNodeRefPath(entity.getId());
    }

    protected void initDescriptorElementSpecific(PDE descriptorElement, IE entity) {
    }

    protected void initEntitySpecific(IE entity, PDE descriptorElement) {
    }

    protected PDE populateDescriptorElement(IE entity) {
        return ClassUtils.populate(getElementClass());
    }

    protected IE populateEntity(PDE descriptorElement) {
        return ClassUtils.populate(getEntityClass());
    }

    protected IRegistryNodeStore<?> getRefPathProviderStore() {
        return registryNodeStore;
    }

    @SuppressWarnings("unchecked")
    protected LookupModel<RegistryNodeModel> castToLookupModel(IE entity) {
        return LookupModel.class.isAssignableFrom(getEntityClass()) ?
                (LookupModel<RegistryNodeModel>) entity : null;
    }

    /**
     * @param element
     * @param model
     * @param uidModelStore
     */
    public static void initializeConfigElementUID(
            INamedPackageDescriptorElement element, UIDModel model,
            IUIDStore<? extends UIDModel, Long> uidModelStore) {
        if (model != null && model.getUid() != null) {
            if (Hibernate.isInitialized(model.getUid())) {
                element.setUid(model.getUid().getUid());
            } else {
                UID uid = uidModelStore.uidById(model.getUid().getId());
                element.setUid(uid.getUid());
            }
        }
    }

    /**
     * @param model
     * @param element
     */
    public static void initializeModelUID(UIDModel model, INamedPackageDescriptorElement element) {
        String uid = null;
        if (StringUtils.isNotBlank(element.getUid())) {
            uid = element.getUid();
        } else if (model instanceof LookupModel) {
            TreeEntityModel parent = ((LookupModel) model).getParent();
            if (parent instanceof LookupModel) {
                String lookup = DiffUtils.lookup(
                        ((LookupModel) parent).getLookup(), element.getName());
                uid = DiffUtils.uid(lookup);
            }
        }
        if (uid == null) {
            throw new OpenFlameRuntimeException("UID should not be blank.");//todo: generate UID for generated fields.
        }
        if (model.getUid() == null) {
            model.setUid(new UID(uid));
        }
    }
}