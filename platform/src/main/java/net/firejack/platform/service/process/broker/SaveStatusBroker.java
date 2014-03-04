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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Status;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.store.process.IStatusStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Class encapsulates the functionality of persisting a status
 */
public abstract class SaveStatusBroker extends SaveBroker<StatusModel, Status, Status> {

    @Autowired
    @Qualifier("statusStore")
    private IStatusStore statusStore;

    /**
     * Converts a status data transfer object to a status entity
     * @param status status data transfer object
     * @return status entity
     */
    @Override
    protected StatusModel convertToEntity(Status status) {
        return factory.convertFrom(StatusModel.class, status);
    }

    /**
     * Converts a status entity to a status data transfer object
     * @param entity status entity
     * @return status data transfer object
     */
    @Override
    protected Status convertToModel(StatusModel entity) {
        return factory.convertTo(Status.class, entity);
    }

    /**
     * Invokes data access layer in order to save a status
     *
     * @param statusModel status entity
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected void save(StatusModel statusModel) throws Exception {
        statusStore.save(statusModel);
    }

}
