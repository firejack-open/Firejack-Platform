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

package net.firejack.platform.service.registry.broker.bi;

import net.firejack.platform.api.registry.domain.BIReport;
import net.firejack.platform.api.registry.domain.BIReportField;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.bi.BIReportModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.bi.IBIReportStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component
public class ReadBIReportByLookupBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<BIReport>> {

    @Autowired
    private IBIReportStore store;

    @Override
    protected ServiceResponse<BIReport> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {

        String packageLookup = request.getData().getIdentifier();

        BIReportModel biReportModel = store.findByLookup(packageLookup);
        BIReport biReport = factory.convertTo(BIReport.class, biReportModel);

        for (BIReportField biReportField : biReport.getFields()) {
            BIReport report = biReportField.getReport();
            report.setFields(null);
        }
        return new ServiceResponse<BIReport>(biReport, "Loaded Bi Report", true);
    }

}
