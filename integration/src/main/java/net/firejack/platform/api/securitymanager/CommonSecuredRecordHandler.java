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

package net.firejack.platform.api.securitymanager;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.CustomPK;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.validation.exception.PropertyNotFoundException;
import net.firejack.platform.web.security.InstantSecurity;
import net.firejack.platform.web.security.action.StandardAction;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@SuppressWarnings("unused")
@Component
public class CommonSecuredRecordHandler implements ISecuredRecordHandler {

    private static final Logger logger = Logger.getLogger(CommonSecuredRecordHandler.class);
    protected static final String DEFAULT_DISPLAY_NAME = "Not Detected";
    protected static final String PROP_NAME1 = "name";
    protected static final String PROP_NAME2 = "title";

    @Override
    public void handleSecuredRecord(ServiceRequest request, ServiceResponse brokerResponse) {
        if (brokerResponse != null && brokerResponse.isSuccess() && OPFContext.isInitialized()) {
            try {
                OPFContext context = OPFContext.getContext();
                if (context != null) {
                    Action currentAction = context.getCurrentAction();
                    if (currentAction != null) {
                        StandardAction actionType = StandardAction.detectStandardAction(currentAction);
                        boolean isCreate = actionType == StandardAction.CREATE;
                        if (isCreate || actionType == StandardAction.DELETE) {
                            String typeLookup = currentAction.getPath();
                            boolean securityEnabled = InstantSecurity.securityEnabledForType(typeLookup);
                            if (securityEnabled) {
                                AbstractDTO dto = isCreate ? brokerResponse.getItem() : request.getData();
                                Object id = getId(dto, isCreate);
                                if (id == null) {
                                    StringBuilder sb = new StringBuilder(
                                            "Secured Record handler is not able to detect id info [ class = ");
                                    if (dto != null) {
                                        sb.append(dto.getClass().getName());
                                    }
                                    sb.append(" ].");
                                    logger.warn(sb.toString());
                                } else if (isCreate) {
                                    ServiceResponse<SecuredRecord> response;
                                    String modelDisplayInfo = getDisplayInfo(dto);
                                    if (id instanceof Long) {
                                        Long numberId = (Long) id;
                                        Object[] parentObjectInfo = InstantSecurity.getParentObjectInfo(dto, currentAction);
                                        Long parentId = parentObjectInfo == null ? null : (Long) parentObjectInfo[0];
                                        String parentType = parentObjectInfo == null ? null : (String) parentObjectInfo[1];
                                        response = OPFEngine.SecurityManagerService.createSecuredRecord(
                                                numberId, modelDisplayInfo, typeLookup, parentId, parentType);
                                        if (OpenFlameSecurityConstants.isClientContext()) {
                                            SecuredRecord securedRecord = response.getItem();
                                            InstantSecurity.forceDefaultPermissionsCreation(typeLookup, securedRecord.getId(), numberId);
                                        }
                                    } else if (id instanceof String || id instanceof CustomPK) {
                                        String stringId ;
                                        if (id instanceof String) {
                                            stringId = (String) id;
                                        } else {
                                            CustomPK customId = (CustomPK) id;
                                            stringId = customId.toString();
                                        }
                                        response = OPFEngine.SecurityManagerService.createSecuredRecord(
                                                stringId, modelDisplayInfo, typeLookup, null, null);
                                        if (OpenFlameSecurityConstants.isClientContext()) {
                                            SecuredRecord securedRecord = response.getItem();
                                            InstantSecurity.forceDefaultPermissionsCreation(typeLookup, securedRecord.getId(), stringId);
                                        }
                                    } else {
                                        response = null;
                                        logger.warn("PK info is not recognized.");
                                    }
                                    if (response != null && !response.isSuccess()) {
                                        logger.warn("Failed to create secured record. Reason: " + response.getMessage());
                                    }
                                } else {
                                    ServiceResponse response;
                                    //we should also handle recursive deletion somehow.
                                    if (id instanceof Long) {
                                        Long numberId = (Long) id;
                                        response = OPFEngine.SecurityManagerService.removeSecuredRecord(numberId, typeLookup);
                                        if (!response.isSuccess()) {
                                            logger.error("Delete Secured Record Operation returned failure status. Message: " + response.getMessage());
                                        }
                                        if (OpenFlameSecurityConstants.isClientContext()) {
                                            InstantSecurity.forceReleaseAllContextPermissions(typeLookup, numberId);
                                        }
                                    } else if (id instanceof String || id instanceof CustomPK) {
                                        String stringId ;
                                        if (id instanceof String) {
                                            stringId = (String) id;
                                        } else {
                                            CustomPK customId = (CustomPK) id;
                                            stringId = customId.toString();
                                        }
                                        response = OPFEngine.SecurityManagerService.removeSecuredRecord(stringId, typeLookup);
                                        if (!response.isSuccess()) {
                                            logger.error("Delete Secured Record Operation returned failure status. Message: " + response.getMessage());
                                        }
                                        if (OpenFlameSecurityConstants.isClientContext()) {
                                            InstantSecurity.forceReleaseAllContextPermissions(typeLookup, stringId);
                                        }
                                    } else {
                                        response = null;
                                        logger.warn("PK info is not recognized.");
                                    }
                                    if (response != null && !response.isSuccess()) {
                                        logger.warn("Failed to delete secured record. Reason: " + response.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ContextLookupException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    protected String getDisplayInfo(AbstractDTO dto) {
        String displayInfo;
        PropertyDescriptor[] descriptors = ClassUtils.getPropertyDescriptors(dto.getClass());
        if (descriptors == null || descriptors.length == 0) {
            displayInfo = DEFAULT_DISPLAY_NAME;
        } else {
            displayInfo = null;
            for (PropertyDescriptor descriptor : descriptors) {
                if (descriptor.getPropertyType() == String.class) {
                    String propertyName = descriptor.getName();
                    if (PROP_NAME1.equalsIgnoreCase(propertyName) || PROP_NAME2.equalsIgnoreCase(propertyName)) {
                        Method readMethod = descriptor.getReadMethod();
                        try {
                            displayInfo = (String) readMethod.invoke(dto);
                            break;
                        } catch (IllegalAccessException e) {
                            //
                        } catch (InvocationTargetException e) {
                            //
                        }
                    }
                }
            }
            if (displayInfo == null) {
                for (PropertyDescriptor descriptor : descriptors) {
                    if (descriptor.getPropertyType() == String.class) {
                        Method readMethod = descriptor.getReadMethod();
                        try {
                            displayInfo = (String) readMethod.invoke(dto);
                            break;
                        } catch (IllegalAccessException e) {
                            //
                        } catch (InvocationTargetException e) {
                            //
                        }
                    }
                }
            }
            displayInfo = displayInfo == null ? DEFAULT_DISPLAY_NAME : displayInfo;
        }
        return displayInfo;
    }

    protected Serializable getId(AbstractDTO dto, boolean create) {
        Serializable id;
        if (dto == null) {
            id = null;
        } else {
            if (create) {
                id = getPropertyValue(dto, "id", Long.class); //TODO: refactor
            } else {
                if (dto instanceof SimpleIdentifier) {
                    SimpleIdentifier idHolder = (SimpleIdentifier) dto;
                    id = (Serializable) idHolder.getIdentifier();
                } else if (dto instanceof NamedValues) {
                    NamedValues namedValues = (NamedValues) dto;
                    id = (Serializable) namedValues.get("id");
                } else {
                    logger.warn("The id info is not retrieved.");
                    id = null;
                }
            }
        }
        return id;
    }

    private <T> T getPropertyValue(AbstractDTO dto, String propertyName, Class<T> clazz) {
        T result;
        try {
            result = ClassUtils.getPropertyValue(dto, propertyName, clazz);
        } catch (PropertyNotFoundException e) {
            result = null;
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        return result;
    }

}