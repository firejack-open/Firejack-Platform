/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.service.content.broker.resource;

import net.firejack.platform.api.content.domain.AbstractResource;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.model.helper.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class AbstractSaveResourceBroker<R extends AbstractResourceModel, RDTO extends AbstractResource>
        extends OPFSaveBroker<R, RDTO, RDTO> {

    @Autowired
    protected FileHelper helper;

    protected abstract IResourceStore<R> getResourceStore();

    @Override
    protected R convertToEntity(RDTO dto) {
        Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[0];
        return factory.convertFrom((Class<R>) type, dto);
    }

    @Override
    protected RDTO convertToModel(R model) {
        Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[1];
        return factory.convertTo((Class<RDTO>) type, model);
    }

    @Override
    protected void save(R model) throws Exception {
        getResourceStore().save(model);
    }

}