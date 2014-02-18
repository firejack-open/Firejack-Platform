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

package net.firejack.platform.service.registry.broker.bi;

import net.firejack.platform.api.registry.domain.BIReport;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.bi.BIReportModel;
import net.firejack.platform.core.store.bi.IBIReportStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component
@Changes("'New BIReport '+ name +' has been created'")
public class CreateBIReportBroker extends SaveBroker<BIReportModel, BIReport, RegistryNodeTree> {

	@Autowired
	private IBIReportStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "BI Report has been created successfully.";
	}

	@Override
	protected BIReportModel convertToEntity(BIReport model) {
		return factory.convertFrom(BIReportModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(BIReportModel model) {
		return treeNodeFactory.convertTo(model);
	}

	@Override
	protected void save(BIReportModel model) throws Exception {
		store.saveForGenerator(model);
	}
}
