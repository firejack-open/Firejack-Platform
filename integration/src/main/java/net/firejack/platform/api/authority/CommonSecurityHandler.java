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

package net.firejack.platform.api.authority;

import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.InstantSecurity;
import net.firejack.platform.web.security.action.StandardAction;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@SuppressWarnings("unused")
@Component
public class CommonSecurityHandler implements SecurityHandler {

    private static final Logger logger = Logger.getLogger(CommonSecurityHandler.class);

    @Override
    public boolean handleSecurityBeforeRequest(ServiceRequest request) {
        boolean authorized = true;
        if (request != null && OPFContext.isInitialized()) {
            try {
                OPFContext context = OPFContext.getContext();
                if (context != null && !context.isPermissionsVerified()) {
                    Action currentAction = context.getCurrentAction();
                    if (currentAction != null && currentAction.getPermissions() != null &&
                            currentAction.getPermissions().size() > 0) {
                        AbstractDTO requestData = request.getData();
                        if (requestData != null) {
                            boolean customProcessing = false;
                            Long entityId = null;
                            String entityType = null;
                            StandardAction actionType = StandardAction.detectStandardAction(currentAction);
                            if (currentAction.getMethod() == HTTPMethod.DELETE) {
                                entityId = context.getEntityId();
                                if (entityId == null) {
                                    if (requestData instanceof SimpleIdentifier) {
                                        SimpleIdentifier simpleIdentifier = (SimpleIdentifier) requestData;
                                        Object identifier = simpleIdentifier.getIdentifier();
                                        if (identifier != null) {
                                            String lookup = null;
                                            if (identifier instanceof Long) {
                                                entityId = (Long) identifier;
                                            } else if (identifier instanceof String) {
                                                lookup = (String) identifier;
                                            }
                                            if (entityId != null) {
                                                entityType = currentAction.getPath();
                                            } else {
                                                if (lookup != null) {
                                                    logger.info("Potential lookup identifier was detected.");
                                                }
                                                customProcessing = true;
                                            }
                                        }
                                    } else {
                                        customProcessing = true;
                                    }
                                } else {
                                    entityType = currentAction.getPath();
                                }
                            } else if (currentAction.getMethod() == HTTPMethod.POST) {
                                if (actionType == StandardAction.CREATE) {
                                    if (OpenFlameSecurityConstants.isClientContext()) {
                                        Object[] parentType = InstantSecurity.getParentObjectInfo(requestData, currentAction);
                                        if (parentType != null) {
                                            entityId = (Long) parentType[0];
                                            entityType = (String) parentType[1];
                                        }
                                    } else {
                                        Object[] parentObjectInfo = getParentObjectInfo(requestData, currentAction);
                                        if (parentObjectInfo == null) {
                                            logger.warn("Failed to detect parent info.");
                                            customProcessing = true;
                                        } else {
                                            entityId = (Long) parentObjectInfo[0];
                                            entityType = (String) parentObjectInfo[1];
                                        }
                                    }
                                } else {
                                    customProcessing = true;
                                }
                            } else if (currentAction.getMethod() == HTTPMethod.PUT) {
                                if (actionType == StandardAction.UPDATE) {
                                    entityId = context.getEntityId();
                                    if (entityId == null) {
	                                    try {
		                                    entityId = ClassUtils.getPropertyValueWithSilence(requestData, "id", Long.class);
	                                    } catch (ClassCastException e) {
		                                    logger.trace(e);
	                                    }

	                                    if (entityId == null) {
                                            logger.warn("Failed to detect id of entity for update action.");
                                            customProcessing = true;
                                        } else {
                                            entityType = currentAction.getPath();
                                        }
                                    } else {
                                        entityType = currentAction.getPath();
                                    }
                                }
                            }
                            if (customProcessing) {
                                logger.warn("Could not check context permissions automatically. The Broker should take care about context permissions verification.");
                            } else if (entityId != null && StringUtils.isNotBlank(entityType)) {
                                if (context.getSecuredRecordId() == null) {
                                    SecuredRecordNode securedRecord = context.findSecuredRecord(entityId, entityType);
                                    if (securedRecord != null) {
                                        context.setSecuredRecord(securedRecord);
                                    }
                                }
                                String path = getItemPath(currentAction, requestData);
                                authorized = checkPermission(currentAction, entityType, entityId, path);
                                if (!authorized) {
                                    StringBuilder sb = new StringBuilder("User[");
                                    sb.append(context.getPrincipal().getUserInfoProvider().getUsername())
                                            .append("] unauthorized to access action [")
                                            .append(currentAction.getLookup()).append(']');
                                    logger.info(sb.toString());
                                    context.setActionAuthorized(false);
                                }
                            } else if (actionType == StandardAction.CREATE) {
                                authorized = checkPermission(currentAction, entityType, entityId, null);
                            }
                        }
                    }
                }
            } catch (ContextLookupException e) {
                logger.debug(e.getMessage());
            }
        }
        return authorized;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleSecurityAfterRequest(ServiceRequest request, ServiceResponse brokerResponse) {
        boolean authorized = true;
        if (brokerResponse != null && brokerResponse.isSuccess() && OPFContext.isInitialized()) {
            try {
                OPFContext context = OPFContext.getContext();
                if (context != null && !context.isPermissionsVerified()) {
                    Action currentAction = context.getCurrentAction();
                    if (currentAction != null && currentAction.getMethod() == HTTPMethod.GET) {
                        String type = currentAction.getPath();
                        List data = brokerResponse.getData();
                        if (data != null && !data.isEmpty()) {
                            StandardAction actionType = StandardAction.detectStandardAction(currentAction);
                            if (actionType == StandardAction.READ) {
                                AbstractDTO foundEntity = brokerResponse.getItem();
                                if (foundEntity != null) {
                                    Long id = context.getEntityId();
                                    if (id == null) {
	                                    try {
		                                    id = ClassUtils.getPropertyValueWithSilence(foundEntity, "id", Long.class);
	                                    } catch (ClassCastException e) {
		                                    logger.trace(e);
	                                    }
                                    }
                                    if (id == null) {
                                        logger.warn("Failed to detect entity id for READ action.");
                                    } else {
                                        if (context.getSecuredRecord() == null) {
                                            SecuredRecordNode securedRecord = context.findSecuredRecord(id, type);
                                            if (securedRecord != null) {
                                                logger.info("Secured record detected {" + securedRecord.getSecuredRecordId() + ": [" + id + " : " + type + "]}");
                                            }
                                            context.setEntityId(id);
                                            context.setSecuredRecord(securedRecord);
                                        }
                                        String path = getItemPath(currentAction, foundEntity);
                                        boolean itemAuthorized = checkPermission(currentAction, type, id, path);
                                        if (!itemAuthorized) {
                                            StringBuilder sb = new StringBuilder("Access to item with [ id = ");
                                            sb.append(id).append(" ] from response for action [")
                                                    .append(currentAction.getLookup())
                                                    .append("] was restricted ");
                                            String message = sb.toString();
                                            brokerResponse.setMessage(message);
                                            brokerResponse.setSuccess(false);
                                            brokerResponse.setData(null);
                                            logger.warn(message);
                                        }
                                    }
                                }
                            } else {
                                List authorizedItems = new ArrayList();
                                for (Object item : data) {
                                    if (item == null) {
                                        logger.warn("Item from response.data is null.");
                                    } else if (item instanceof AbstractDTO) {
	                                    try {
		                                    Long id = ClassUtils.getPropertyValueWithSilence((AbstractDTO) item, "id", Long.class);
		                                    if (id == null) {
			                                    logger.warn("Failed to detect id property value from read operations response.data item.");
			                                    authorizedItems.add(item);
		                                    } else {
			                                    SecuredRecordNode securedRecord = context.findSecuredRecord(id, type);
			                                    context.setSecuredRecord(securedRecord);
			                                    String path = getItemPath(currentAction, (AbstractDTO) item);
			                                    boolean itemAuthorized = checkPermission(currentAction, type, id, path);
			                                    if (itemAuthorized) {
				                                    authorizedItems.add(item);
			                                    } else {
				                                    logger.warn("Access to item with [ id = " + id + " ] from response for " +
						                                    currentAction.getLookup() + " was restricted ");
			                                    }
		                                    }
	                                    } catch (ClassCastException e) {
		                                    logger.trace(e);
	                                    }
                                    }
                                }
                                if (authorizedItems.isEmpty()) {
                                    authorized = false;
                                }
                                brokerResponse.setData(authorizedItems);
                            }
                        }
                    }
                    if (!authorized) {
                        StringBuilder sb = new StringBuilder("User[");
                        sb.append(context.getPrincipal().getUserInfoProvider().getUsername())
                                .append("] unauthorized to access action [")
                                .append(currentAction.getLookup()).append(']');
                        logger.info(sb.toString());
                        context.setActionAuthorized(false);
                    }
                }
            } catch (ContextLookupException e) {
                logger.debug(e.getMessage());
            }
        }
        return authorized;
    }

    protected boolean checkPermission(Action currentAction, String type, Long id, String path) {
        Boolean authorized = true;
        List<Permission> actionPermissions;
        if (currentAction != null && (actionPermissions = currentAction.getPermissions()) != null && !actionPermissions.isEmpty()) {
            List<UserPermission> permissions = new LinkedList<UserPermission>();
            String entityId = String.valueOf(id);
            for (Permission permission : actionPermissions) {
                permissions.add(new UserPermission(permission.getLookup(), type, entityId));
            }
            authorized = OPFContext.getContext().getPrincipal().checkUserPermission(permissions, true);
            if (!authorized && !OpenFlameSecurityConstants.isClientContext() && StringUtils.isNotBlank(path)) {//todo check client context
                String packageLookup = StringUtils.getPackageLookup(path);
                if (packageLookup != null) {
                    authorized = OPFContext.getContext().getPrincipal().checkPackageLevelPermissions(packageLookup, permissions);
                }
            }
        }
        return authorized;
    }

    protected Long getParentId(AbstractDTO dto) {
        return ClassUtils.getPropertyValueWithSilence(dto, "parentId", Long.class);
    }

    protected Object[] getParentObjectInfo(AbstractDTO source, Action currentAction) {
        return InstantSecurity.getParentObjectInfo(source, currentAction);
    }

    protected String getItemPath(Action currentAction, AbstractDTO dto) {
        String path;
        StandardAction actionType = StandardAction.detectStandardAction(currentAction);
        if (actionType == StandardAction.DELETE || OpenFlameSecurityConstants.isClientContext()) {
            path = null;
        } else {
            path = ClassUtils.getPropertyValueWithSilence(dto, "path", String.class);
        }
        return path;
    }

}