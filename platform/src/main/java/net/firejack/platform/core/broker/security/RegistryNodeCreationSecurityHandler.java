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