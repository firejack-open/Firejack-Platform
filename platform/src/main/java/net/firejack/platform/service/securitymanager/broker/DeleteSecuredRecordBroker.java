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

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("deleteSecuredRecordBroker")
public class DeleteSecuredRecordBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

    public static final String PARAM_ENTITY_ID = "entityId";
    public static final String PARAM_ENTITY_CUSTOM_ID = "entityCustomId";
    public static final String PARAM_ENTITY_TYPE = "entityType";
    public static final String PARAM_FORCE_DELETE = "forceDelete";

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore store;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues> request)
		    throws Exception {
        Long entityId = (Long) request.getData().get(PARAM_ENTITY_ID);
        String entityCustomId = (String) request.getData().get(PARAM_ENTITY_CUSTOM_ID);
        String entityType = (String) request.getData().get(PARAM_ENTITY_TYPE);
        //boolean forceDelete = (Boolean) message.get(PARAM_FORCE_DELETE);

        ServiceResponse response;
        if (StringUtils.isBlank(entityType) || (entityId == null && StringUtils.isBlank(entityCustomId))) {
            response = new ServiceResponse("Some required parameter(s) have blank values", false);
        } else {
            RegistryNodeModel registryNode = registryNodeStore.findByLookup(entityType);
            if (registryNode != null) {
                SecuredRecordModel securedRecord;
                if (entityId == null) {
                    securedRecord = store.findByExternalStringIdAndRegistryNode(entityCustomId, registryNode);
                } else {
                    securedRecord = store.findByExternalIdAndRegistryNode(entityId, registryNode);
                }
                if (securedRecord != null) {
                    store.delete(securedRecord);
                    response = new ServiceResponse("Secured record has deleted successfully", true);
                } else {
                    response = new ServiceResponse("Secured record is not found by [id: " +
                            (entityId == null ? entityCustomId : entityId) + "; type: " + entityType + "]", false);
                }
            } else {
                response = new ServiceResponse(
                        "Secured record has not found by id:[" + entityId + "] and type:[" + entityType + "]", false);
            }
        }
        return response;
    }

    @Override
    protected void processSecuredRecords(ServiceRequest<NamedValues> request, ServiceResponse response) {
        //
    }
}