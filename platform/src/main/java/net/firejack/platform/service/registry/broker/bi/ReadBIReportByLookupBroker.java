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
