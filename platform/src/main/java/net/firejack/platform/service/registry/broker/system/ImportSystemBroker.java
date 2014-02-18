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
package net.firejack.platform.service.registry.broker.system;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.EnvironmentStore;
import net.firejack.platform.core.store.registry.IRootDomainStore;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.utils.EnvironmentsUtils;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("importSystemBroker")
@ProgressComponent(upload = true)
public class ImportSystemBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	@Autowired
	private IRootDomainStore rootDomainRegistryNodeStore;
	@Autowired
	private ISystemStore systemIRegistryNodeStore;
	@Autowired
	private EnvironmentStore environmentStore;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		Long id = (Long) request.getData().get("rootDomainId");
		InputStream inputStream = (InputStream) request.getData().get("stream");

		Environments environments = EnvironmentsUtils.deserialize(inputStream);
		if (environments != null && !environments.isEmpty()) {
			List<RootDomainModel> domains = new ArrayList<RootDomainModel>();
			if (id != null) {
				RootDomainModel domain = rootDomainRegistryNodeStore.findById(id);
				domains.add(domain);
			} else {
				domains = rootDomainRegistryNodeStore.findAll();
			}
			changeSystem(domains, environments);
		}

		return new ServiceResponse("Import xml successfully", true);
	}

	private void changeSystem(List<RootDomainModel> domains, Environments<Environment> environments) {
		for (Environment environment : environments.getEnvironments()) {
			for (RootDomainModel domain : domains) {
				String lookup = DiffUtils.lookup(environment.getSystem().getPath(), environment.getSystem().getName());
				if (lookup.startsWith(domain.getLookup())) {
					SystemModel system = systemIRegistryNodeStore.findByLookup(lookup);
					if (system != null) {
						systemIRegistryNodeStore.deleteRecursiveById(system.getId());
					}
					environmentStore.save(domain, environment);
				}
			}
		}
	}
}
