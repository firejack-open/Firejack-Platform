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

package net.firejack.platform.web.progress;

import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.mina.aop.ProgressReadStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("progress")
public class ProgressEndpoint {


    @Autowired
	private ProgressReadStatus progressReadStatus;

    /**
	 * Read progress status
	 *
	 * @param uid  uid
	 * @return founded progress statuses
	 */
	@GET
	@Path("/status")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse progress(@HeaderParam("Page-UID") String  uid) {
		List progress = progressReadStatus.getProgress(uid);
		return new ServiceResponse(progress, "Load progress", true);
	}

}
