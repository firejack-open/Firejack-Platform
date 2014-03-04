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
import net.firejack.platform.core.domain.IdLookup;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component
public class DeleteSecuredRecordsBroker extends ServiceBroker<ServiceRequest<IdLookup>, ServiceResponse> {

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore store;

    @Override
    protected ServiceResponse perform(ServiceRequest<IdLookup> request) throws Exception {
        List<IdLookup> idLookupList = request.getDataList();
        ServiceResponse response;
        if (idLookupList == null || idLookupList.isEmpty()) {
            response = new ServiceResponse("No secured records specified for delete operation.", false);
        } else {
            List<Tuple<Long, String>> srToDelete = new ArrayList<Tuple<Long, String>>();
            for (IdLookup idLookup : idLookupList) {
                srToDelete.add(new Tuple<Long, String>(idLookup.getId(), idLookup.getLookup()));
            }
            List<SecuredRecordModel> deletedRecords = store.deleteAllByIdAndType(srToDelete);
            if (deletedRecords == null || deletedRecords.isEmpty() || srToDelete.size() > deletedRecords.size()) {
                response = new ServiceResponse(
                        "Not all secured records specified for delete operation were actually deleted.", false);
            } else {
                response = new ServiceResponse("Specified Secured Records were deleted successfully.", true);
            }
        }
        return response;
    }

    @Override
    protected void processSecuredRecords(ServiceRequest<IdLookup> request, ServiceResponse response) {
        //
    }
}