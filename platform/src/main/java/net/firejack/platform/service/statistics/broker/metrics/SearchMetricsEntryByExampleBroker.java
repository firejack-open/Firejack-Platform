/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("searchMetricsEntryByExampleBrokerEx")
public class SearchMetricsEntryByExampleBroker extends ServiceBroker
        <ServiceRequest<MetricsEntry>, ServiceResponse<MetricsEntry>> {

    @Autowired
    private IMetricsEntryStore metricsEntryStore;

    @Override
    protected ServiceResponse<MetricsEntry> perform(ServiceRequest<MetricsEntry> request)
		    throws Exception {
        MetricsEntry example = request.getData();
        ServiceResponse<MetricsEntry> response;
        if (example == null) {
            response = new ServiceResponse<MetricsEntry>("Metrics entry example is null.", false);
        } else {
            try {
                MetricsEntryModel modelExample = factory.convertFrom(MetricsEntryModel.class, example);
                MetricsEntryModel foundMetrics = metricsEntryStore.findByExample(modelExample);
                MetricsEntry metricsEntry = factory.convertTo(MetricsEntry.class, foundMetrics);
                List<MetricsEntry> result = new ArrayList<MetricsEntry>();
                result.add(metricsEntry);
                response = new ServiceResponse<MetricsEntry>(result);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<MetricsEntry>(e.getMessage(), false);
            }
        }
        return response;
    }

}