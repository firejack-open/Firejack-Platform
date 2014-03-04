/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
