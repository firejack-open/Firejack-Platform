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