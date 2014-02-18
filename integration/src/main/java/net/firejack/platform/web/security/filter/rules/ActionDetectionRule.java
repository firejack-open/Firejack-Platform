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

package net.firejack.platform.web.security.filter.rules;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.action.IActionDetector;
import net.firejack.platform.web.security.action.StandardAction;
import net.firejack.platform.web.security.filter.IFilterFlowInterceptor;
import net.firejack.platform.web.security.filter.ISecurityFilter;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ActionDetectionRule extends BaseAuthorizationRule<Action> {
    private static final Logger logger = Logger.getLogger(ActionDetectionRule.class);
    private IActionDetector actionDetector;

    /**
     * Rule constructor
     * @param actionDetector accepts instance of IActionDetector as parameter
     *
     * @see IActionDetector
     */
    public ActionDetectionRule(IActionDetector actionDetector) {
        this.actionDetector = actionDetector;
    }

    /**
     * Method returns configured action detector
     * @return configured action detector
     */
    public IActionDetector getActionDetector() {
        return actionDetector;
    }

    @Override
    protected Action detectElement(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol) {
        Action action = getActionDetector().detectAction(httpRequest, protocol);
        if (action != null) {
            OPFContext.getContext().setCurrentAction(action);
        }
        return action;
    }

    @Override
    protected void onSuccessfulAuthorization(Action element) {
        super.onSuccessfulAuthorization(element);
        OPFContext.getContext().setActionAuthorized(true);
    }

    @Override
    protected void onFailureAuthorization(
            Action element, ISecurityFilter securityFilter,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        OPFContext context = OPFContext.getContext();
        super.onFailureAuthorization(element, securityFilter, httpRequest, httpResponse);
        context.setActionAuthorized(false);
    }

    @Override
    protected void onGuestUnauthorized(
            Action element, ISecurityFilter securityFilter,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        if (MediaType.APPLICATION_JSON.equalsIgnoreCase(OPFContext.getContext().getRequestAcceptHeader())) {
            logger.info("Guest user tries to access secured action [" +
                    element.getLookup() + "]. Sending SC_FORBIDDEN response (HTTP 403)...");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for guest user.");
        } else if (MediaType.APPLICATION_OCTET_STREAM.equalsIgnoreCase(OPFContext.getContext().getRequestAcceptHeader())) {
            logger.info("Guest user tries to access secured action [" +
                    element.getLookup() + "]. Sending SC_FORBIDDEN response (HTTP 403)...");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for guest user.");
        } else {
            String apiCallMarkerHeaderValue;
            if (MediaType.APPLICATION_XML.equalsIgnoreCase(OPFContext.getContext().getRequestAcceptHeader()) &&
                    StringUtils.isNotBlank(apiCallMarkerHeaderValue = httpRequest.getHeader(
                            OpenFlameSecurityConstants.MARKER_HEADER))) {
                logger.info("Guest user tries to make API call from [" + apiCallMarkerHeaderValue + "].");
                OpenFlameSecurityConstants.printXmlErrorToResponse(
                        httpResponse, OpenFlameSecurityConstants.API_403_ERROR_RESPONSE);
            } else {
                super.onGuestUnauthorized(element, securityFilter, httpRequest, httpResponse);
            }
        }
    }

    @Override
    public Boolean authorizeAccess(
            ISecurityFilter securityFilter, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws IOException {
        Action action = getElement();
        if (action == null) {
            throw new IllegalStateException();
        }
        boolean result = true;

        OPFContext context = OPFContext.getContext();
        context.setPermissionsVerified(false);
        OpenFlamePrincipal principal = context.getPrincipal();
        Map<Long, List<UserPermission>> contextPermissionsBySecuredRecords =
                context.getPermissionContainer().loadSecuredRecordContextPermissions(
                        principal.getUserInfoProvider().getId());
        boolean contextPermissionsNotGranted = true;
        if (contextPermissionsBySecuredRecords != null && !contextPermissionsBySecuredRecords.isEmpty()) {
            for (List<UserPermission> srPermissions : contextPermissionsBySecuredRecords.values()) {
                if (srPermissions != null && !srPermissions.isEmpty()) {
                    contextPermissionsNotGranted = false;
                    break;
                }
            }
        }

        if (contextPermissionsNotGranted) {
            if (!principal.checkUserPermission(action)) {
                result = false;
            }
            context.setPermissionsVerified(true);
        }//if context permissions were granted then delegate permission check to the broker side.

        if (result) {
            onSuccessfulAuthorization(action);
        } else {
            onFailureAuthorization(action, securityFilter, httpRequest, httpResponse);
        }
        return result;
        /*boolean result;
        OPFContext context = OPFContext.getContext();
        OpenFlamePrincipal principal = context.getPrincipal();
        if (principal.checkUserPermission(action)) {
            result = true;
        } else {
            if (StandardAction.isReadAllAction(action)) {
                String entityType = action.getPath();
                SpecifiedIdsFilter idFilter = context.findSpecifiedIdsFilterByType(entityType);
                result = idFilter != null && (idFilter.hasGlobalReadPermission() || !idFilter.getNecessaryIds().isEmpty());
            } else if (context.getEntityId() != null && context.getEntityType() != null) {
                UserPermission permissionToCheck = new UserPermission(action.getLookup());
                permissionToCheck.setEntityId(context.getEntityId().toString());
                permissionToCheck.setEntityType(context.getEntityType());
                result = principal.checkUserPermission(permissionToCheck, false);
            } else {
                result = false;
            }
        }
        if (result) {
            onSuccessfulAuthorization(action);
        } else {
            onFailureAuthorization(action, securityFilter, httpRequest, httpResponse);
        }
        return result;*/
    }

    @Override
    protected void onElementDetected(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol, Action action) {
        super.onElementDetected(securityFilter, httpRequest, protocol, action);
        /*String parentIdHeader = httpRequest.getHeader(OpenFlameSecurityConstants.PARENT_ID_REQUEST_HEADER);
        Long parentId;
        if (parentIdHeader != null) {
            parentId = StringUtils.isNumeric(parentIdHeader) ? Long.valueOf(parentIdHeader) : null;
            if (parentId != null) {
                OPFContext context = OPFContext.getContext();
                context.setParentEntityId(parentId);
            }
        } else {
            parentId = null;
        }
        StandardAction standardAction = StandardAction.detectStandardAction(action);
        if (standardAction != null) {
            OPFContext context = OPFContext.getContext();
            if (standardAction == StandardAction.READ_ALL) {
                context.setEntityType(action.getPath());
            } else {
                Long entityId;
                String type;
                if (standardAction == StandardAction.CREATE) {
                    entityId = parentId;
                    type = entityId != null ? httpRequest.getHeader(
                            OpenFlameSecurityConstants.PARENT_TYPE_REQUEST_HEADER) : null;
                } else {
                    String strEntityId = httpRequest.getHeader(OpenFlameSecurityConstants.OBJECT_ID_REQUEST_HEADER);
                    entityId = StringUtils.isNumeric(strEntityId) ? Long.valueOf(strEntityId) : null;
                    entityId = entityId == null ? getIdParameter() : entityId;
                    type = entityId == null ? null : action.getPath();
                }
                if (entityId != null && StringUtils.isNotBlank(type)) {
                    Long securedRecordId = context.findSecuredRecordId(entityId, type);
                    if (securedRecordId != null) {
                        logger.info("Secured record detected {" + securedRecordId + ": [" + entityId + " : " + type + "]}");
                    }
                    context.setEntityId(entityId);
                    context.setEntityType(type);
                    context.setSecuredRecordId(securedRecordId);
                }
            }
        }*/

        OPFContext context = OPFContext.getContext();
        if (action.getMethod() == HTTPMethod.GET) {
            context.setEntityType(action.getPath());
        } else {
            StandardAction standardAction = StandardAction.detectStandardAction(action);
            Long entityId = null;
            String type = null;
            if (standardAction != StandardAction.CREATE) {
                String urlPath = OPFContext.getContext().getCurrentRequestPath();
                int pos = urlPath.lastIndexOf("/");
                if (pos >= 0 && pos != urlPath.length() - 1) {
                    String idCandidate = urlPath.substring(pos + 1);
                    entityId = StringUtils.isNumeric(idCandidate) ? Long.valueOf(idCandidate) : null;
                } else {
                    entityId = null;
                }

                if (entityId != null) {
                    logger.info("Potential entity id was detected [ id = " + entityId + "]");
                }
                type = action.getPath();
            }
            if (entityId != null && StringUtils.isNotBlank(type)) {
                SecuredRecordNode securedRecord = context.findSecuredRecord(entityId, type);
                if (securedRecord != null) {
                    logger.info("Secured record detected {" + securedRecord.getSecuredRecordId() + ": [" + entityId + " : " + type + "]}");
                }
                context.setEntityId(entityId);
                context.setEntityType(type);
                context.setSecuredRecord(securedRecord);
            }
        }

        List<IFilterFlowInterceptor> filterFlowInterceptors = securityFilter.getFilterFlowInterceptorList();
        if (filterFlowInterceptors != null) {
            for (IFilterFlowInterceptor interceptor : filterFlowInterceptors) {
                interceptor.beforeExecution(httpRequest);
            }
        }
    }

    /*private Long getIdParameter() {
        String urlPath = OPFContext.getContext().getCurrentRequestPath();
        int pos = urlPath.lastIndexOf("/");
        Long id;
        if (pos >= 0 && pos != urlPath.length() - 1) {
            String idCandidate = urlPath.substring(pos + 1);
            id = StringUtils.isNumeric(idCandidate) ? Long.valueOf(idCandidate) : null;
        } else {
            id = null;
        }
        return id;
    }*/

}