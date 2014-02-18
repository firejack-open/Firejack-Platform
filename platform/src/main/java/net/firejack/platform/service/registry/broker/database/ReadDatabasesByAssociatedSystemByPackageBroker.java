package net.firejack.platform.service.registry.broker.database;

import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

@TrackDetails
@Component("readDatabaseListByAssociatedPackageIdBroker")
public class ReadDatabasesByAssociatedSystemByPackageBroker extends ListBroker<DatabaseModel, Database, SimpleIdentifier<Long>> {

	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private IDatabaseStore store;

	@Override
	protected List<DatabaseModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
		Long associatedPackageId = request.getData().getIdentifier();

		PackageModel packageRN = packageStore.findById(associatedPackageId);
		if (packageRN.getSystem() != null) {
			Long systemId = packageRN.getSystem().getId();
			return store.findAllByParentIdWithFilter(systemId, getFilter());
		}
		return null;
	}
}
