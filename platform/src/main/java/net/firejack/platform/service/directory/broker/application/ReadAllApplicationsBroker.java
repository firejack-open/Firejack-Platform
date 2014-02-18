/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.service.directory.broker.application;

import net.firejack.platform.api.directory.domain.Application;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.directory.ApplicationModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IApplicationStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@Component
public class ReadAllApplicationsBroker extends ServiceBroker<ServiceRequest, ServiceResponse<Application>> {

    @Autowired
    @Qualifier("applicationStore")
    private IApplicationStore store;

    @Override
    protected ServiceResponse<Application> perform(ServiceRequest request) throws Exception {
        List<ApplicationModel> allApplicationModels = store.findAll();
        List<Application> applications = factory.convertTo(Application.class, allApplicationModels);
        return applications == null || applications.isEmpty() ?
                new ServiceResponse<Application>("No applications were found.", true) :
                new ServiceResponse<Application>(applications, "Applications loaded successfully.", true);
    }

}