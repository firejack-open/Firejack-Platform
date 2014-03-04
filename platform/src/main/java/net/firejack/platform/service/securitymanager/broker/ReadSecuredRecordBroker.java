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

import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
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
@Component("readSecuredRecordBrokerEx")
public class ReadSecuredRecordBroker extends ServiceBroker
        <ServiceRequest<NamedValues>, ServiceResponse<SecuredRecord>> {

    public static final String PARAM_ENTITY_ID = "entityId";
    public static final String PARAM_ENTITY_TYPE = "entityType";

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore securedRecordStore;

    @Override
    protected ServiceResponse<SecuredRecord> perform(ServiceRequest<NamedValues> request)
		    throws Exception {
        Long entityId = (Long) request.getData().get(PARAM_ENTITY_ID);
        String entityType = (String) request.getData().get(PARAM_ENTITY_TYPE);

        SecuredRecordModel securedRecordModel =
                securedRecordStore.findByIdAndType(entityId, entityType);
        ServiceResponse<SecuredRecord> response;
        if (securedRecordModel == null) {
            response = new ServiceResponse<SecuredRecord>((SecuredRecord) null,
                    "Secured Record was not found by specified parameters", true);
        } else {
            SecuredRecord securedRecord = this.factory.convertTo(SecuredRecord.class, securedRecordModel);
            securedRecord.setRegistryNodeLookup(securedRecordModel.getRegistryNode().getLookup());//entityType
            response = new ServiceResponse<SecuredRecord>(
                    securedRecord, "Secured Record was found", true);
        }
        return response;
    }

}