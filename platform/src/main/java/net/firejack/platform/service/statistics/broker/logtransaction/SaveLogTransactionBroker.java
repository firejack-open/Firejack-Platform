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

package net.firejack.platform.service.statistics.broker.logtransaction;

import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.statistics.LogTransactionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.statistics.ILogTransactionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("saveLogTransactionBroker")
public class SaveLogTransactionBroker extends ServiceBroker<ServiceRequest<LogTransaction>, ServiceResponse> {

    @Autowired
    private ILogTransactionStore store;

    @Override
    protected ServiceResponse perform(ServiceRequest<LogTransaction> request) throws Exception {
        LogTransaction logTransaction = request.getData();
        ServiceResponse response;
        if (logTransaction == null) {
            response = new ServiceResponse("Log Transaction in request is null.", false);
        } else {
            try {
                LogTransactionModel logTransactionModel = factory.convertFrom(LogTransactionModel.class, logTransaction);

                LogTransactionModel foundLogTransactionModel = store.findByLookupAndHourPeriod(logTransaction.getPackageLookup(), logTransaction.getHourPeriod());
                if (foundLogTransactionModel != null) {
                    logTransactionModel.setTransactions(logTransactionModel.getTransactions() + foundLogTransactionModel.getTransactions());
                    logTransactionModel.setEntitiesLoaded(logTransactionModel.getEntitiesLoaded() + foundLogTransactionModel.getEntitiesLoaded());
                    logTransactionModel.setEntitiesInserted(logTransactionModel.getEntitiesInserted() + foundLogTransactionModel.getEntitiesInserted());
                    logTransactionModel.setEntitiesUpdated(logTransactionModel.getEntitiesUpdated() + foundLogTransactionModel.getEntitiesUpdated());
                    logTransactionModel.setEntitiesDeleted(logTransactionModel.getEntitiesDeleted() + foundLogTransactionModel.getEntitiesDeleted());
                    logTransactionModel.setEntitiesFetched(logTransactionModel.getEntitiesFetched() + foundLogTransactionModel.getEntitiesFetched());
                    logTransactionModel.setCollectionsLoaded(logTransactionModel.getCollectionsLoaded() + foundLogTransactionModel.getCollectionsLoaded());
                    logTransactionModel.setCollectionsRecreated(logTransactionModel.getCollectionsRecreated() + foundLogTransactionModel.getCollectionsRecreated());
                    logTransactionModel.setCollectionsUpdated(logTransactionModel.getCollectionsUpdated() + foundLogTransactionModel.getCollectionsUpdated());
                    logTransactionModel.setCollectionsRemoved(logTransactionModel.getCollectionsRemoved() + foundLogTransactionModel.getCollectionsRemoved());
                    logTransactionModel.setCollectionsFetched(logTransactionModel.getCollectionsFetched() + foundLogTransactionModel.getCollectionsFetched());
                    logTransactionModel.setMaxQueryTime(Math.max(logTransactionModel.getMaxQueryTime(), foundLogTransactionModel.getMaxQueryTime()));
                }
                store.saveOrUpdate(logTransactionModel);
                response = new ServiceResponse("Log Transaction has been saved successfully.", true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse(e.getMessage(), false);
            }
        }
        return response;
    }

}