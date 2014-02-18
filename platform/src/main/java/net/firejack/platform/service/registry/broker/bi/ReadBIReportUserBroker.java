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

import net.firejack.platform.api.registry.domain.BIReportField;
import net.firejack.platform.api.registry.domain.BIReportUser;
import net.firejack.platform.api.registry.domain.BIReportUserField;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.model.registry.bi.BIReportUserModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.bi.IBIReportUserStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component
public class ReadBIReportUserBroker extends ReadBroker<BIReportUserModel, BIReportUser> {

	@Autowired
	private IBIReportUserStore store;

	@Override
	protected IStore<BIReportUserModel, Long> getStore() {
		return store;
	}

    @Override
    protected BIReportUser convertToModel(BIReportUserModel biReportUserModel) {
        BIReportUser biReportUser = super.convertToModel(biReportUserModel);

        //set to null not necessary information otherwise response is huge

        List<BIReportField> entityBIReportFields = new ArrayList<BIReportField>();
        for (BIReportField biReportField : biReportUser.getReport().getFields()) {
            if (biReportField.getField() == null) {
                Entity entity = new Entity();
                entity.setId(biReportField.getEntity().getId());
                biReportField.setEntity(entity);
                biReportField.setReport(null);
                entityBIReportFields.add(biReportField);
            }
        }
        biReportUser.getReport().setFields(entityBIReportFields);

        for (BIReportUserField biReportUserField : biReportUser.getFields()) {
            biReportUserField.setUserReport(null);
            biReportUserField.getField().setReport(null);
        }

        return biReportUser;
    }
}
