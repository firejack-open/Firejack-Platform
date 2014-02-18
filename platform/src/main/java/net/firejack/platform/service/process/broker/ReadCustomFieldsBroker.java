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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.ProcessField;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ProcessFieldModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.IProcessFieldStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving custom fields
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readCustomFieldsBroker")
public class ReadCustomFieldsBroker extends ListBroker<ProcessFieldModel, ProcessField, NamedValues<Long>> {

   @Autowired
   @Qualifier("processFieldStore")
   private IProcessFieldStore processFieldStore;

    /**
     * Invokes data access layer in order to find custom fields for a given process together with the global custom fields
     * @param namedValuesServiceRequest service request containing ID of the process
     * @return list of found fields
     * @throws BusinessFunctionException
     */
   @Override
   protected List<ProcessFieldModel> getModelList(ServiceRequest<NamedValues<Long>> namedValuesServiceRequest) throws BusinessFunctionException {
      return processFieldStore.findByProcessIdPlusGlobal(namedValuesServiceRequest.getData().get("processId"));
   }
   
}
