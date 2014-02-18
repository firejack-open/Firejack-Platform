/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.securitymanager.broker;

import net.firejack.platform.api.securitymanager.domain.TreeNodeSecuredRecord;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.store.registry.helper.SecuredRecordPathHelper;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@TrackDetails
@Component
public class SaveSecuredRecordsBroker extends ServiceBroker<ServiceRequest<TreeNodeSecuredRecord>, ServiceResponse> {

    @Autowired
    @Qualifier("entityStore")
    private IEntityStore entityStore;

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore store;

    @Autowired
    @Qualifier("securedRecordPathHelper")
    private SecuredRecordPathHelper securedRecordPathHelper;

    @Override
    protected ServiceResponse perform(ServiceRequest<TreeNodeSecuredRecord> request)
		    throws Exception {
        ServiceResponse response;
        List<TreeNodeSecuredRecord> securedRecords = request.getDataList();
        if (securedRecords == null || securedRecords.isEmpty()) {
            response = new ServiceResponse(
                    "No secured records were created - empty input secured records parameter.", false);
        } else {
            Map<Long, String> rnIdLookupMap = new HashMap<Long, String>();
            Map<String, EntityModel> typesMap = new HashMap<String, EntityModel>();
            boolean parametersAreCorrect = false;
            for (TreeNodeSecuredRecord treeNodeSecuredRecord : securedRecords) {
                parametersAreCorrect = processRegistryNode(rnIdLookupMap, typesMap,
                        treeNodeSecuredRecord.getRegistryNodeId(), treeNodeSecuredRecord.getRegistryNodeLookup());
                if (parametersAreCorrect) {
                    if (StringUtils.isNotBlank(treeNodeSecuredRecord.getParentType())) {
                        parametersAreCorrect = processRegistryNode(
                                rnIdLookupMap, typesMap, null, treeNodeSecuredRecord.getParentType());
                        if (!parametersAreCorrect) {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            if (parametersAreCorrect) {
                List<SecuredRecordModel> securedRecordModels = new ArrayList<SecuredRecordModel>();
                for (TreeNodeSecuredRecord sr : securedRecords) {
                    SecuredRecordModel parentSecuredRecord = null;
                    if (StringUtils.isNotBlank(sr.getParentType())) {
                        EntityModel parentEntityType = typesMap.get(sr.getParentType());
                        if (parentEntityType != null) {
                            parentSecuredRecord = store.findByExternalIdAndRegistryNode(
                                    sr.getExternalParentNumberId(), parentEntityType);
                        }
                    }
                    String parentPath = null;
                    if (parentSecuredRecord != null) {
                        parentSecuredRecord.setParentSecuredRecords(null);
                        parentPath = securedRecordPathHelper.addIdToPaths(
                                parentSecuredRecord.getId(), parentSecuredRecord.getPaths());
                    }
                    EntityModel recordType;
                    if (sr.getRegistryNodeId() == null) {
                        recordType = typesMap.get(sr.getRegistryNodeLookup());
                    } else {
                        String typeLookup = rnIdLookupMap.get(sr.getRegistryNodeId());
                        recordType = typesMap.get(typeLookup);
                    }
                    SecuredRecordModel securedRecord = store.findByExternalIdAndRegistryNode(
                            sr.getExternalNumberId(), recordType);
                    if (securedRecord == null) {
                        securedRecord = new SecuredRecordModel();
                        securedRecord.setExternalNumberId(sr.getExternalNumberId());
                        securedRecord.setRegistryNode(recordType);

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
                    securedRecord.setName(sr.getName());
                    securedRecord.addParentSecuredRecords(parentSecuredRecord);

                    securedRecordModels.add(securedRecord);
                }
                store.saveOrUpdateRecursive(securedRecordModels);
                response = new ServiceResponse("Secured records has created successfully", true);
            } else {
                response = new ServiceResponse(
                        "No secured records were created - incorrect registry node information.", false);
            }
        }
        return response;
    }

    private boolean processRegistryNode(
            Map<Long, String> rnIdLookupMap, Map<String, EntityModel> typesMap,
            Long rnId, String lookup) {
        boolean parametersAreCorrect = true;
        if (rnId == null && StringUtils.isBlank(lookup)) {
            parametersAreCorrect = false;
        } else {
            EntityModel entityModel = null;
            if (rnId == null) {
                if (!typesMap.containsKey(lookup)) {
                    entityModel = entityStore.findByLookup(lookup);
                    if (entityModel == null) {
                        parametersAreCorrect = false;
                    }
                }
            } else if (!rnIdLookupMap.containsKey(rnId)) {
                entityModel = entityStore.findById(rnId);
                if (entityModel == null) {
                    parametersAreCorrect = false;
                }
            }
            if (entityModel != null) {
                rnIdLookupMap.put(entityModel.getId(), entityModel.getLookup());
                typesMap.put(entityModel.getLookup(), entityModel);
            }
        }
        return parametersAreCorrect;
    }

    @Override
    protected void processSecuredRecords(ServiceRequest<TreeNodeSecuredRecord> request, ServiceResponse response) {
        //
    }
}