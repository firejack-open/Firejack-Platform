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

package net.firejack.platform.service.securitymanager.broker;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("updateSecuredRecordBroker")
public class UpdateSecuredRecordBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

    public static final String PARAM_ENTITY_ID = "id";
    public static final String PARAM_ENTITY_TYPE = "type";
    public static final String PARAM_ENTITY_NAME = "name";

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore store;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues> request)
		    throws Exception {
        Long entityId = (Long) request.getData().get(PARAM_ENTITY_ID);
        String typeLookup = (String) request.getData().get(PARAM_ENTITY_TYPE);
        String name = (String) request.getData().get(PARAM_ENTITY_NAME);

        ServiceResponse response;

        SecuredRecordModel securedRecord = store.findByIdAndType(entityId, typeLookup);
        if (securedRecord != null) {
            securedRecord.setName(name);
            store.saveOrUpdate(securedRecord);

            response = new ServiceResponse(
                    "Secured record has updated successfully", true);
        } else {
            response = new ServiceResponse(
                    "Secured record has not found by id:[" + entityId +
                    "] and type:[" + typeLookup + "]", false);
        }
        return response;
    }

}