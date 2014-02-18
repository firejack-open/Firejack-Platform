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

package net.firejack.platform.service.registry.broker.filestore;

import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.store.registry.IFileStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.service.registry.broker.DeleteAliasableBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("deleteFilestoreBroker")
public class DeleteFilestoreBroker extends DeleteAliasableBroker<FileStoreModel> {

	@Autowired
	private IFileStore store;

	@Override
	protected String getSuccessMessage() {
		return "Filestore has deleted successfully";
	}

	@Override
	protected IRegistryNodeStore<FileStoreModel> getStore() {
		return store;
	}

}

