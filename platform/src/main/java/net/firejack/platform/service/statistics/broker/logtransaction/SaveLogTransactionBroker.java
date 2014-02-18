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