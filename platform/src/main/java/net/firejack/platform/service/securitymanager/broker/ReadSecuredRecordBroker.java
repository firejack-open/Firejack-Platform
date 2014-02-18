/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.service.securitymanager.broker;

import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("readSecuredRecordBrokerEx")
public class ReadSecuredRecordBroker extends ServiceBroker
        <ServiceRequest<NamedValues>, ServiceResponse<SecuredRecord>> {

    public static final String PARAM_ENTITY_ID = "entityId";
    public static final String PARAM_ENTITY_TYPE = "entityType";

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore securedRecordStore;

    @Override
    protected ServiceResponse<SecuredRecord> perform(ServiceRequest<NamedValues> request)
		    throws Exception {
        Long entityId = (Long) request.getData().get(PARAM_ENTITY_ID);
        String entityType = (String) request.getData().get(PARAM_ENTITY_TYPE);

        SecuredRecordModel securedRecordModel =
                securedRecordStore.findByIdAndType(entityId, entityType);
        ServiceResponse<SecuredRecord> response;
        if (securedRecordModel == null) {
            response = new ServiceResponse<SecuredRecord>((SecuredRecord) null,
                    "Secured Record was not found by specified parameters", true);
        } else {
            SecuredRecord securedRecord = this.factory.convertTo(SecuredRecord.class, securedRecordModel);
            securedRecord.setRegistryNodeLookup(securedRecordModel.getRegistryNode().getLookup());//entityType
            response = new ServiceResponse<SecuredRecord>(
                    securedRecord, "Secured Record was found", true);
        }
        return response;
    }

}