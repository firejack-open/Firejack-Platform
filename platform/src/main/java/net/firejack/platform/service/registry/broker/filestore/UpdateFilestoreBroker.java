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

import net.firejack.platform.api.registry.domain.Filestore;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.store.registry.IFileStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.service.registry.broker.SaveAliasableBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("updateFilestoreBroker")
public class UpdateFilestoreBroker extends SaveAliasableBroker<FileStoreModel, Filestore, RegistryNodeTree> {

	@Autowired
	private IFileStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Filestore has been updated successfully.";
	}

	@Override
	protected FileStoreModel convertToEntity(Filestore model) {
		return factory.convertFrom(FileStoreModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(FileStoreModel filestoreModel) {
		return treeNodeFactory.convertTo(filestoreModel);
	}

	@Override
	protected void save(FileStoreModel model) throws Exception {
		store.save(model);
	}
}
