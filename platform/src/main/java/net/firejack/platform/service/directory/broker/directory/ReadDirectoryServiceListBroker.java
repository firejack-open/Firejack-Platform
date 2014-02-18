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

package net.firejack.platform.service.directory.broker.directory;

import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.security.directory.DirectoryServiceRepository;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("readDirectoryServiceListBroker")
public class ReadDirectoryServiceListBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Directory>> {

	@Autowired
	private DirectoryServiceRepository directoryServiceRepository;

	@Override
	protected ServiceResponse<Directory> perform(ServiceRequest<NamedValues> request) throws Exception {
		List<Tuple<String, String>> directoryServiceLabels = directoryServiceRepository.getDirectoryServiceLabels();
		List<Directory> voList = new ArrayList<Directory>();
		for (Tuple<String, String> serviceLabel : directoryServiceLabels) {
			voList.add(new Directory(serviceLabel.getKey(), serviceLabel.getValue()));
		}

		return new ServiceResponse<Directory>(voList,"Read Directory Service successfully.", true);
	}
}
