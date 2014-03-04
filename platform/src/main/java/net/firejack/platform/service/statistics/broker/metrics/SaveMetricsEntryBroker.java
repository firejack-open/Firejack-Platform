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

package net.firejack.platform.service.statistics.broker.metrics;

import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.statistics.MetricsEntryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.statistics.IMetricsEntryStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("saveMetricsEntryBrokerEx")
public class SaveMetricsEntryBroker extends ServiceBroker<ServiceRequest<MetricsEntry>, ServiceResponse> {

    @Autowired
    private IMetricsEntryStore metricsEntryStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<MetricsEntry> request)
		    throws Exception {
        MetricsEntry metricsEntry = request.getData();
        ServiceResponse response;
        if (metricsEntry == null) {
            response = new ServiceResponse("Entity in request is null.", false);
        } else {
            try {
                MetricsEntryModel metricsEntryModel = factory.convertFrom(MetricsEntryModel.class, metricsEntry);
                metricsEntryStore.saveOrUpdate(metricsEntryModel);
                response = new ServiceResponse("MetricsEntry was saved successfully.", true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse(e.getMessage(), false);
            }
        }
        return response;
    }

}