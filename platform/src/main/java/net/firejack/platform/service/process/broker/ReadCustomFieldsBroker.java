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

import net.firejack.platform.api.process.domain.ProcessField;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ProcessFieldModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.IProcessFieldStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving custom fields
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readCustomFieldsBroker")
public class ReadCustomFieldsBroker extends ListBroker<ProcessFieldModel, ProcessField, NamedValues<Long>> {

   @Autowired
   @Qualifier("processFieldStore")
   private IProcessFieldStore processFieldStore;

    /**
     * Invokes data access layer in order to find custom fields for a given process together with the global custom fields
     * @param namedValuesServiceRequest service request containing ID of the process
     * @return list of found fields
     * @throws BusinessFunctionException
     */
   @Override
   protected List<ProcessFieldModel> getModelList(ServiceRequest<NamedValues<Long>> namedValuesServiceRequest) throws BusinessFunctionException {
      return processFieldStore.findByProcessIdPlusGlobal(namedValuesServiceRequest.getData().get("processId"));
   }
   
}
