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
import net.firejack.platform.core.exception.ObjectNotFoundException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.store.registry.helper.SecuredRecordPathHelper;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("saveSecuredRecordBroker")
public class SaveSecuredRecordBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<SecuredRecord>> {

    //Long id, String name, String type, Long parentId, String parentType
    public static final String PARAM_ENTITY_ID = "id";
    public static final String PARAM_ENTITY_STRING_ID = "string_id";
    public static final String PARAM_ENTITY_NAME = "name";
    public static final String PARAM_ENTITY_TYPE = "type";
    public static final String PARAM_PARENT_ID = "parentId";
    public static final String PARAM_PARENT_TYPE = "parentType";

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore store;

    @Autowired
    @Qualifier("securedRecordPathHelper")
    private SecuredRecordPathHelper securedRecordPathHelper;

    @Override
    protected ServiceResponse<SecuredRecord> perform(ServiceRequest<NamedValues> request)
		    throws Exception {
        ServiceResponse<SecuredRecord> response;

        Long entityId = (Long) request.getData().get(PARAM_ENTITY_ID);
        String entityStringId = (String) request.getData().get(PARAM_ENTITY_STRING_ID);
        String name = (String) request.getData().get(PARAM_ENTITY_NAME);
        String lookup = (String) request.getData().get(PARAM_ENTITY_TYPE);
        Long parentId = (Long) request.getData().get(PARAM_PARENT_ID);
        String parentLookup = (String) request.getData().get(PARAM_PARENT_TYPE);
        if ((entityId == null && StringUtils.isBlank(entityStringId)) ||
                StringUtils.isBlank(name) || StringUtils.isBlank(lookup)) {
            throw new IllegalArgumentException("Required parameters(id, name, type) should not be blank.");
        }
        RegistryNodeModel registryNode = registryNodeStore.findByLookup(lookup);

        if (registryNode != null) {
            SecuredRecordModel parentSecuredRecord = null;
            if (StringUtils.isNotBlank(parentLookup)) {
                RegistryNodeModel parentRegistryNode = registryNodeStore.findByLookup(parentLookup);
                if (parentRegistryNode != null) {
                    parentSecuredRecord = store.findByExternalIdAndRegistryNode(parentId, parentRegistryNode);
                } else {
                    throw new ObjectNotFoundException();
                }
            }

            String parentPath = null;
            if (parentSecuredRecord != null) {
                parentSecuredRecord.setParentSecuredRecords(null);
                parentPath = securedRecordPathHelper.addIdToPaths(
                        parentSecuredRecord.getId(), parentSecuredRecord.getPaths());
            }

            SecuredRecordModel securedRecord;
            if (entityId == null) {
                securedRecord = store.findByExternalStringIdAndRegistryNode(entityStringId, registryNode);
            } else {
                securedRecord = store.findByExternalIdAndRegistryNode(entityId, registryNode);
            }
            if (securedRecord == null) {
                securedRecord = new SecuredRecordModel();
                if (entityId == null) {
                    securedRecord.setExternalStringId(entityStringId);
                } else {
                    securedRecord.setExternalNumberId(entityId);
                }
                securedRecord.setRegistryNode(registryNode);

                if (parentPath != null) {
                    securedRecord.setPaths(parentPath);
                } else {
                    securedRecord.setPaths(SecuredRecordPathHelper.EMPTY_PATH);
                }
            } else {
                if (parentPath == null) {
                    parentPath = SecuredRecordPathHelper.EMPTY_PATH;
                }
                if (SecuredRecordPathHelper.EMPTY_PATH.equals(securedRecord.getPaths())) {
                    securedRecord.setPaths(parentPath);
                } else {
                    String paths = securedRecordPathHelper.addPathToPaths(parentPath, securedRecord.getPaths());
                    securedRecord.setPaths(paths);
                }
            }
            securedRecord.setName(name);
            securedRecord.addParentSecuredRecords(parentSecuredRecord);

            store.saveOrUpdateRecursive(securedRecord);

            SecuredRecord sr = factory.convertTo(SecuredRecord.class, securedRecord);
            response = new ServiceResponse<SecuredRecord>(sr, "Secured record has created successfully", true);
        } else {
            response = new ServiceResponse<SecuredRecord>(
                    "Secured record has not created. Can't find RegistryNode by lookup:[" +
                            lookup + "]", false);
        }
        return response;
    }

    @Override
    protected void processSecuredRecords(ServiceRequest<NamedValues> request, ServiceResponse<SecuredRecord> response) {
        //
    }
}