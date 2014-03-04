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

import net.firejack.platform.api.registry.domain.BIReportUser;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.bi.BIReportUserModel;
import net.firejack.platform.core.store.bi.IBIReportUserStore;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("updateBIReportUserBroker")
@Changes("'Report '+ name +' has been updated'")
public class UpdateBIReportUserBroker extends SaveBroker<BIReportUserModel, BIReportUser, BIReportUser> {

    @Autowired
    private IBIReportUserStore store;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Database has been updated successfully.";
	}

	@Override
	protected BIReportUserModel convertToEntity(BIReportUser model) {
		return factory.convertFrom(BIReportUserModel.class, model);
	}

    @Override
    protected BIReportUser convertToModel(BIReportUserModel entity) {
        return factory.convertTo(BIReportUser.class, entity);
    }

	@Override
	protected void save(BIReportUserModel model) throws Exception {
		store.saveOrUpdate(model);
	}
}
