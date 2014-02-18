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

package net.firejack.platform.service.registry.broker.report;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.Report;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.report.ReportModel;
import net.firejack.platform.core.store.registry.IReportStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createReportBroker")
@Changes("'New Report '+ name +' has been created'")
public class CreateReportBroker extends SaveBroker<ReportModel, Report, RegistryNodeTree> {

	@Autowired
	private IReportStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Report has been created successfully.";
	}

	@Override
	protected ReportModel convertToEntity(Report model) {
		return factory.convertFrom(ReportModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(ReportModel model) {
		return treeNodeFactory.convertTo(model);
	}

	@Override
	protected void save(ReportModel model) throws Exception {
		store.saveForGenerator(model);
	}
}
