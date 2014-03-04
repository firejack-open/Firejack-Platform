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

package net.firejack.platform.service.securitymanager.broker;

import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.IdLookup;
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
@Component("moveSecuredRecordBroker")
public class MoveSecuredRecordBroker extends ServiceBroker
        <ServiceRequest<NamedValues>, ServiceResponse<SecuredRecord>> {

    public static final String PARAM_ENTITY_ID = "entityId";
    public static final String PARAM_ENTITY_LOOKUP = "entityLookup";
    public static final String PARAM_PARENT_ID = "parentId";
    public static final String PARAM_PARENT_LOOKUP = "parentLookup";
    public static final String PARAM_OLD_PARENTS = "oldParents";

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
        Long entityId = (Long) request.getData().get(PARAM_ENTITY_ID);
        String entityLookup = (String) request.getData().get(PARAM_ENTITY_LOOKUP);
        Long parentId = (Long) request.getData().get(PARAM_PARENT_ID);
        String parentLookup = (String) request.getData().get(PARAM_PARENT_LOOKUP);
        IdLookup[] oldParents = (IdLookup[]) request.getData().get(PARAM_OLD_PARENTS);

        //MoveEntityDataVO vo = message.getVO();
        RegistryNodeModel registryNode = registryNodeStore.findByLookup(entityLookup);

        SecuredRecordModel parentSecuredRecord = null;
        if (StringUtils.isNotBlank(parentLookup)) {
            RegistryNodeModel parentRegistryNode = registryNodeStore.findByLookup(parentLookup);
            if (parentRegistryNode != null) {
                parentSecuredRecord = store.findByExternalIdAndRegistryNode(parentId, parentRegistryNode);
            } else {
                throw new ObjectNotFoundException();
            }
        }

        SecuredRecordModel securedRecord = store.findByExternalIdAndRegistryNode(entityId, registryNode);
        if (securedRecord == null) {
            throw new ObjectNotFoundException();
        }

        for (IdLookup idLookup: oldParents) {
            Long oldParentId = idLookup.getId();
            String oldParentLookup = idLookup.getLookup();

            SecuredRecordModel oldParentSecuredRecord;

            RegistryNodeModel oldParentRegistryNode = registryNodeStore.findByLookup(oldParentLookup);
            if (oldParentRegistryNode != null) {
                oldParentSecuredRecord =
                        store.findByExternalIdAndRegistryNode(oldParentId, oldParentRegistryNode);

                if (oldParentSecuredRecord != null) {
                    String paths = securedRecordPathHelper.removeIdFromPaths(
                            securedRecord.getPaths(), oldParentSecuredRecord);
                    securedRecord.setPaths(paths);

                    securedRecord.removeParentSecuredRecords(oldParentSecuredRecord);
                }
            } else {
                throw new ObjectNotFoundException();
            }
        }

//        if (securedRecord != null && parentSecuredRecord != null && oldParentSecuredRecord != null) {
//            String paths = securedRecordPathHelper.switchPathToPaths(
//                    securedRecord.getPaths(), parentSecuredRecord, oldParentSecuredRecord);
//            securedRecord.setPaths(paths);
//        } else {
//            throw new ObjectNotFoundException();
//        }
//
//        securedRecord.removeParentSecuredRecords(oldParentSecuredRecord);

        if (parentSecuredRecord != null) {
            String paths = securedRecordPathHelper.addIdToPaths(securedRecord.getPaths(), parentSecuredRecord);
            securedRecord.setPaths(paths);
            securedRecord.addParentSecuredRecords(parentSecuredRecord);
        }

        store.saveOrUpdateRecursive(securedRecord);

        if (securedRecord.getRegistryNode() != null) {
            securedRecord.getRegistryNode().setParent(null);
        }
        securedRecord.setParentSecuredRecords(null);

        SecuredRecord securedRecordForClient =
                this.factory.convertTo(SecuredRecord.class, securedRecord);
        return new ServiceResponse<SecuredRecord>(
                securedRecordForClient, "Secured record has created successfully", true);
    }

}