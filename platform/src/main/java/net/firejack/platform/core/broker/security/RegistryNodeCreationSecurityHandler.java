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

package net.firejack.platform.core.broker.security;

import net.firejack.platform.api.authority.CommonSecurityHandler;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.action.StandardAction;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;


public class RegistryNodeCreationSecurityHandler extends CommonSecurityHandler {

    private static final Logger logger = Logger.getLogger(RegistryNodeCreationSecurityHandler.class);
    private RegistryNodeType registryNodeType;
    private RegistryNodeProviderBroker registryNodeProviderBroker;

    public RegistryNodeCreationSecurityHandler() {
    }

    public RegistryNodeCreationSecurityHandler(RegistryNodeType registryNodeType) {
        setRegistryNodeType(registryNodeType);
    }

    public void setRegistryNodeType(RegistryNodeType registryNodeType) {
        if (registryNodeType == null) {
            throw new IllegalArgumentException("registryNodeType parameter should not be empty.");
        }
        this.registryNodeType = registryNodeType;
    }

    public RegistryNodeProviderBroker getRegistryNodeProviderBroker() {
        return registryNodeProviderBroker;
    }

    public void setRegistryNodeProviderBroker(RegistryNodeProviderBroker registryNodeProviderBroker) {
        this.registryNodeProviderBroker = registryNodeProviderBroker;
    }

    @Override
    protected Object[] getParentObjectInfo(AbstractDTO source, Action currentAction) {
        if (registryNodeType == null) {
            throw new IllegalStateException("registryNodeType property should not be empty.");
        }
        Object[] result;
        if (source == null || currentAction == null) {
            result = null;
        } else {
            Long parentId = getParentId(source);
            String parentType = this.registryNodeType.getEntityPath();
            result = new Object[]{parentId, parentType};
        }
        return result;
    }

    @Override
    protected String getItemPath(Action currentAction, AbstractDTO dto) {
        String path = super.getItemPath(currentAction, dto);
        if (StringUtils.isBlank(path) && getRegistryNodeProviderBroker() != null &&
                (StandardAction.isCreateAction(currentAction) || StandardAction.isUpdateAction(currentAction))) {
            Long parentId = getParentId(dto);
            if (parentId != null) {
                try {
                    OPFContext context = OPFContext.getContext();
                    if (context != null) {
                        SecuredRecordNode securedRecord = context.getSecuredRecord();
                        if (securedRecord != null) {
                            Long id = securedRecord.getInternalId();
                            RegistryNodeType registryNodeType =
                                    RegistryNodeType.findByEntityType(securedRecord.getType());
                            LookupModel parent =
                                    getRegistryNodeProviderBroker().findRegistryNode(id, registryNodeType);
                            if (parent != null) {
                                path = parent.getLookup();
                            }
                        }
                    }
                } catch (ContextLookupException e) {
                    logger.debug(e.getMessage(), e);
                }
            }
        }
        return path;
    }

}