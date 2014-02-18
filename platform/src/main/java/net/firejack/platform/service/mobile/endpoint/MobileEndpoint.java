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

package net.firejack.platform.service.mobile.endpoint;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.mobile.domain.MenuItem;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Class is an implementation of net.firejack.platform.service.mail.endpoint.IMobileEndpoint
 */
@Component("mobileService")
@Path("mobile")
public class MobileEndpoint implements IMobileEndpoint {

	@GET
	@Override
	@Path("/menu/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<MenuItem> loadMenu(@PathParam("lookup") String lookup) {
        return OPFEngine.MobileService.loadMenu(lookup);
    }
}