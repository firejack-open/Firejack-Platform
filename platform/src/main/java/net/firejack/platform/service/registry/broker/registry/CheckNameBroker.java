/**
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
package net.firejack.platform.service.registry.broker.registry;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.validation.annotation.DomainType;
import net.firejack.platform.core.validation.process.ValidateUnique;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;

@Component
@TrackDetails
public class CheckNameBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

    @Autowired
   	private ValidateUnique validateUnique;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
        NamedValues data = request.getData();
        String path = (String) data.get("path");
        String name = (String) data.get("name");
        DomainType type = (DomainType) data.get("type");

        name = URLDecoder.decode(name, "UTF-8");
        if (validateUnique.checkName(path, name, type)) {
            return new ServiceResponse("Check successfully", true);
        } else {
            return new ServiceResponse("Name is not unique", false);
        }
    }
}
