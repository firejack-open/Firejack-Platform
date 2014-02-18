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

package net.firejack.platform.web.security.model.context;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.action.container.ActionEmptyContainerFactory;
import net.firejack.platform.web.security.action.container.IActionContainer;
import net.firejack.platform.web.security.action.container.IActionContainerFactory;
import net.firejack.platform.web.security.exception.AuthenticationException;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.navigation.INavElementContainer;
import net.firejack.platform.web.security.navigation.INavElementContainerFactory;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.permission.IPermissionContainerFactory;
import net.firejack.platform.web.security.resource.IResourceLocationContainer;
import net.firejack.platform.web.security.resource.IResourceLocationContainerFactory;
import net.firejack.platform.web.security.sr.ISecuredRecordInfoContainer;
import net.firejack.platform.web.security.sr.ISecuredRecordInfoContainerFactory;
import net.firejack.platform.web.security.sr.ISpecifiedIdsFilterContainer;
import net.firejack.platform.web.security.sr.ISpecifiedIdsFilterContainerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;


@SuppressWarnings("unused")
public class OPFContext {

    //Commented this because of integration with spring security
    //which already stores it context related data in ThreadLocal
    //private static LocalContext LOCAL_CONTEXT = new LocalContext();

    /**
     * This static variable represents context holder delegate.
     * External implementation is responsible for initializing this variable.
     * (Primarily it should be performed somewhere in spring security initialization)
     */
    private static IContextContainerDelegate CONTEXT_HOLDER_DELEGATE;

    /**
     * Factory to produce permission container;
     */
    private static IPermissionContainerFactory PERMISSION_CONTAINER_FACTORY;

    private static IActionContainerFactory ACTION_CONTAINER_FACTORY;

    private static IResourceLocationContainerFactory RESOURCE_LOCATION_CONTAINER_FACTORY;

    private static INavElementContainerFactory NAV_ELEMENT_CONTAINER_FACTORY;

    private static ISecuredRecordInfoContainerFactory SECURED_RECORD_CONTAINER_FACTORY;

    private static ISpecifiedIdsFilterContainerFactory SPECIFIED_IDS_FILTER_CONTAINER_FACTORY;

    /**
     * The Principal implementation with identity and permission information
     */
    private OpenFlamePrincipal principal;

    /**
     * Permission container used to load user permissions at initialization time
     */
    private IPermissionContainer permissionContainer;

    /**
     * The action container
     */
    private IActionContainer actionContainer;

    /**
     * The masked resources container
     */
    private IResourceLocationContainer resourceLocationContainer;

    /**
     * Navigation element container
     */
    private INavElementContainer navElementContainer;

    /**
     * Secured Record element container
     */
    private ISecuredRecordInfoContainer securedRecordContainer;

    /**
     * Specified Ids element container
     */
    private ISpecifiedIdsFilterContainer specifiedIdsFilterContainer;


    /**
     * A registry lookup name for the current service action being executed
     */
    private ThreadLocal<Action> currentAction = new InheritableThreadLocal<Action>();

    private ThreadLocal<NavigationElement> currentNavigationElement = new InheritableThreadLocal<NavigationElement>();

    private SecuredRecordNode securedRecord;

    private Long parentEntityId;

    private Long entityId;

    private String entityType;

    private boolean permissionsVerified;

    /**
     * Flag that signalize that access to action was authorized
     */
    private Boolean actionAuthorized;

    /**
     * The current session token granted by an external sign-on authority, if applicable.
     * This is not to be confused with the internal server session token or id (which is
     * completely separate and accessible via other mechanisms).
     */
    private String sessionToken;

    /**
     * IP Address of client browser.
     */
    private String browserIpAddress;

    /**
     * The current request path if known.
     */
    private String currentRequestPath;

    /**
     * 'accept' header from the current request
     */
    private String requestAcceptHeader;

    //private Map<String, Serializable> parameters;

    /**
     * The private constructor ensures that only the context object itself can
     * construct a new context object. This allows the cached context pool to manage
     * the instances and control how contexts are built and associated.
     */
    private OPFContext() {

    }

    /**
     * @return current principal
     */
    public OpenFlamePrincipal getPrincipal() {
        return principal;
    }

    /**
     * @param principal current principal
     */
    public void setPrincipal(OpenFlamePrincipal principal) {
        this.principal = principal;
    }

    /**
     * @return action container
     */
    public IActionContainer getActionContainer() {
        return actionContainer;
    }

    /**
     * @return resource location container
     */
    public IResourceLocationContainer getResourceLocationContainer() {
        return resourceLocationContainer;
    }

    /**
     * @return navigation element container
     */
    public INavElementContainer getNavElementContainer() {
        return navElementContainer;
    }

    /**
     *
     * @return permission container
     */
    public IPermissionContainer getPermissionContainer() {
        return permissionContainer;
    }

    /**
     * @return current action if detected
     */
    public Action getCurrentAction() {
        return currentAction.get();
    }

    /**
     * @return lookup of the current action (if any previously detected)
     */
    public String getCurrentActionLookup() {
        Action action = currentAction.get();
        return action == null ? null : action.getLookup();
    }

    /**
     * @param currentAction set current action
     */
    public void setCurrentAction(Action currentAction) {
        if (currentAction == null) {
            this.currentAction.remove();
        } else {
            this.currentAction.set(currentAction);
        }
    }

    /**
     *
     * @return current navigation if any detected
     */
    public NavigationElement getCurrentNavigationElement() {
        return currentNavigationElement.get();
    }

    /**
     * @return lookup of the current navigation element if any detected
     */
    public String getCurrentNavigationElementLookup() {
        NavigationElement navigationElement = currentNavigationElement.get();
        return navigationElement == null ? null : navigationElement.getLookup();
    }

    public void setCurrentNavigationElement(NavigationElement currentNavigationElement) {
        if (currentNavigationElement == null) {
            this.currentNavigationElement.remove();
        } else {
            this.currentNavigationElement.set(currentNavigationElement);
        }
    }

    /**
     * @return secured record id if secured record detected from the current request
     */
    public Long getSecuredRecordId() {
        return securedRecord == null ? null : securedRecord.getSecuredRecordId();
    }

    public SecuredRecordNode getSecuredRecord() {
        return securedRecord;
    }

    public void setSecuredRecord(SecuredRecordNode securedRecord) {
        this.securedRecord = securedRecord;
    }

    /**
     * @return current parent entity id
     */
    public Long getParentEntityId() {
        return parentEntityId;
    }

    /**
     * @param parentEntityId parent entity id to set
     */
    public void setParentEntityId(Long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    /**
     * @return id of entity processed in current request(if detected from the request action)
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * @param entityId set detected entity id
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * @return detected type of the currently processed entity(if detected from the request action)
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType detected type of the currently processed entity
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public boolean isPermissionsVerified() {
        return permissionsVerified;
    }

    public void setPermissionsVerified(boolean permissionsVerified) {
        this.permissionsVerified = permissionsVerified;
    }

    /**
     * @return true if current user allowed to perform detected action, otherwise return false.
     */
    public Boolean getActionAuthorized() {
        return actionAuthorized;
    }

    /**
     * @param actionAuthorized flag that indicates that currently detected action is allowed to current user
     */
    public void setActionAuthorized(Boolean actionAuthorized) {
        this.actionAuthorized = actionAuthorized;
    }

    /**
     * @return current session token
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * @param sessionToken current session token
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getBrowserIpAddress() {
        return browserIpAddress;
    }

    public void setBrowserIpAddress(String browserIpAddress) {
        this.browserIpAddress = browserIpAddress;
    }

    /***/
    public void reset() {
        setCurrentAction(null);
        setCurrentNavigationElement(null);
        setActionAuthorized(null);
        setSecuredRecord(null);
        setEntityId(null);
        setEntityType(null);
    }

    /**
     * Initialize OPFContext with context holder delegate. Initialization is required only once somewhere in
     * external security mechanism on application startup
     *
     * @param contextContainerDelegate context holder delegate to use
     * @throws IllegalArgumentException Throws runtime exception if provided contextContainerDelegate is null
     */
    public static void initContextContainerDelegate(IContextContainerDelegate contextContainerDelegate)
            throws IllegalArgumentException {
        if (contextContainerDelegate == null) {
            throw new IllegalArgumentException(
                    "Could not initialize context holder delegate with null reference");
        }
        CONTEXT_HOLDER_DELEGATE = contextContainerDelegate;
    }

    /**
     * @param permissionContainerFactory initial permission container factory
     */
    public static void initPermissionContainerFactory(IPermissionContainerFactory permissionContainerFactory) {
        if (permissionContainerFactory == null) {
            throw new IllegalArgumentException("Permission container factory parameter should not be null");
        }
        PERMISSION_CONTAINER_FACTORY = permissionContainerFactory;
    }

    /**
     * @param actionContainerFactory initial action container factory
     */
    public static void initActionContainerFactory(IActionContainerFactory actionContainerFactory) {
        if (actionContainerFactory == null) {
            throw new IllegalArgumentException("Action container factory parameter should not be null.");
        }
        ACTION_CONTAINER_FACTORY = actionContainerFactory;
    }

    /**
     * @param resourceLocationContainerFactory initial resource location factory
     */
    public static void initResourceLocationContainerFactory(IResourceLocationContainerFactory resourceLocationContainerFactory) {
        if (resourceLocationContainerFactory == null) {
            throw new IllegalArgumentException("Masked resource container factory parameter should not be null.");
        }
        RESOURCE_LOCATION_CONTAINER_FACTORY = resourceLocationContainerFactory;
    }

    /**
     * @param navElementContainerFactory initial navigation element container factory
     */
    public static void initNavElementContainerFactory(INavElementContainerFactory navElementContainerFactory) {
        if (navElementContainerFactory == null) {
            throw new IllegalArgumentException("Secured Record container factory parameter should not be null.");
        }
        NAV_ELEMENT_CONTAINER_FACTORY = navElementContainerFactory;
    }

    /**
     * @param securedRecordInfoContainerFactory initial secured record info container factory
     *
     */
    public static void initSecuredRecordContainerFactory(ISecuredRecordInfoContainerFactory securedRecordInfoContainerFactory) {
        if (securedRecordInfoContainerFactory == null) {
            throw new IllegalArgumentException("Secured Record container factory parameter should not be null.");
        }
        SECURED_RECORD_CONTAINER_FACTORY = securedRecordInfoContainerFactory;
    }

    /**
     * @param specifiedIdsFilterContainerFactory initial id filter container factory
     *
     */
    public static void initSpecifiedIdsFilterContainerFactory(ISpecifiedIdsFilterContainerFactory specifiedIdsFilterContainerFactory) {
        if (specifiedIdsFilterContainerFactory == null) {
            throw new IllegalArgumentException("Specified Ids Filter container factory parameter should not be null.");
        }
        SPECIFIED_IDS_FILTER_CONTAINER_FACTORY = specifiedIdsFilterContainerFactory;
    }

    public static boolean isInitialized() {
        return !(CONTEXT_HOLDER_DELEGATE == null || PERMISSION_CONTAINER_FACTORY == null ||
                ACTION_CONTAINER_FACTORY == null || RESOURCE_LOCATION_CONTAINER_FACTORY == null ||
                NAV_ELEMENT_CONTAINER_FACTORY == null || SECURED_RECORD_CONTAINER_FACTORY == null ||
                SPECIFIED_IDS_FILTER_CONTAINER_FACTORY == null);
    }

    /**
     * Allows initialization of the current OPFContext using a given user principal.
     *
     * @param principal an initialized user principal object
     * @return context
     * @throws IllegalStateException  Throws runtime exception if context holder delegate was not initialized -
     *                                context holder delegate initialization is required before producing new context
     * @throws ContextLookupException Throws runtime exception if context holder delegate throws authentication exception
     */
    public static OPFContext initContext(OpenFlamePrincipal principal)
            throws IllegalStateException, ContextLookupException {
        return initContext(principal, null);
    }

    /**
     * Allows initialization of the current OPFContext using a given user principal.
     *
     * @param principal    an initialized user principal object
     * @param sessionToken user session token id
     * @return context
     * @throws IllegalStateException  Throws runtime exception if context holder delegate was not initialized -
     *                                context holder delegate initialization is required before producing new context
     * @throws ContextLookupException Throws runtime exception if context holder delegate throws authentication exception
     */
    public static OPFContext initContext(OpenFlamePrincipal principal, String sessionToken)
            throws IllegalStateException, ContextLookupException {
        if (CONTEXT_HOLDER_DELEGATE == null) {
            throw new IllegalStateException("Context holder delegate should not be null.");
        }

        if (ACTION_CONTAINER_FACTORY == null) {
            ACTION_CONTAINER_FACTORY = new ActionEmptyContainerFactory();
        }
        if (PERMISSION_CONTAINER_FACTORY == null) {
            throw new IllegalStateException("Permission loader factory should not be null.");
        }
        if (RESOURCE_LOCATION_CONTAINER_FACTORY == null) {
            throw new IllegalStateException("Masked resource container factory should not be null.");
        }
        if (NAV_ELEMENT_CONTAINER_FACTORY == null) {
            throw new IllegalStateException("Navigation element container factory should not be null.");
        }
        if (SECURED_RECORD_CONTAINER_FACTORY == null) {
            throw new IllegalStateException("Secured Record container factory should not be null.");
        }
        if (SPECIFIED_IDS_FILTER_CONTAINER_FACTORY == null) {
            throw new IllegalStateException("Specified Ids Filter container factory should not be null.");
        }

        OPFContext ctx = new OPFContext();
        ctx.setPrincipal(principal);
        ctx.sessionToken = sessionToken;
        ctx.permissionContainer = PERMISSION_CONTAINER_FACTORY.producePermissionContainer();
        ctx.actionContainer = ACTION_CONTAINER_FACTORY.produceActionContainer();
        ctx.resourceLocationContainer = RESOURCE_LOCATION_CONTAINER_FACTORY.produceMaskedResourceContainer();
        ctx.navElementContainer = NAV_ELEMENT_CONTAINER_FACTORY.produceNavElementContainer();
        ctx.securedRecordContainer = SECURED_RECORD_CONTAINER_FACTORY.produceSecuredRecordContainer();
        ctx.specifiedIdsFilterContainer = SPECIFIED_IDS_FILTER_CONTAINER_FACTORY.produceSpecifiedIdsFilter();
        //ctx.parameters = new HashMap<String, Serializable>();

        saveContext(ctx);

        principal.assignPermissionContainer(ctx.permissionContainer);

        return ctx;
    }

    /**
     * Provides any method a reference to the current {@code BusinessContext} object
     * as long as the {@code ContextFilter} has been invoked.
     *
     * @return Context the current context for the service or application invocation
     * @throws ContextLookupException Throws runtime exception if context was not retrieved from context holder delegate
     */
    public static OPFContext getContext() throws ContextLookupException {
        OPFContext ctx;
        try {
            ctx = CONTEXT_HOLDER_DELEGATE.retrieveContext();
            if (ctx == null) {
                throw new ContextLookupException();
            }
        } catch (AuthenticationException e) {
            throw new ContextLookupException(e);
        }
        return ctx;
    }

    public Map<String, Serializable> getAttributes() {
        Map<String, Serializable> attributes;
        if (StringUtils.isBlank(this.sessionToken)) {
            attributes = null;
        } else {
            CacheManager cacheManager = CacheManager.getInstance();
            attributes = cacheManager.getSessionAttributes(this.sessionToken);
        }
        return attributes;
    }

    public void setAttributes(Map<String, Serializable> attributes) {
        if (StringUtils.isNotBlank(this.sessionToken)) {
            CacheManager cacheManager = CacheManager.getInstance();
            cacheManager.setSessionAttributes(this.sessionToken, attributes);
        }
    }

    //public void setParameters

    public Serializable get(String key) {
        Serializable result;
        if (StringUtils.isBlank(key) || StringUtils.isBlank(this.sessionToken)) {
            result = null;
        } else {
            Map<String, Serializable> attributes = getAttributes();
            result = attributes == null ? null : attributes.get(key);
        }
        return result;
    }

    public void put(String key, Serializable obj) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(this.sessionToken)) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, Serializable> attributes = cacheManager.getSessionAttributes(this.sessionToken);
            if (attributes == null) {
                attributes = new HashMap<String, Serializable>();
            }
            attributes.put(key, obj);
            cacheManager.setSessionAttributes(this.sessionToken, attributes);
        }
    }

    /**
     * Get currently processed request path
     *
     * @return currently processed request path
     */
    public String getCurrentRequestPath() {
        return currentRequestPath;
    }

    /**
     * @return value of the current request's 'Accept' header
     */
    public String getRequestAcceptHeader() {
        return requestAcceptHeader;
    }

    /**
     * Set currently processed request path
     *
     * @param currentRequestPath currently processed request path
     */
    public void setCurrentRequestPath(String currentRequestPath) {
        this.currentRequestPath = currentRequestPath;
    }

    /**
     * Analyze current http request
     * @param httpRequest current http request
     */
    public void analyzeRequest(HttpServletRequest httpRequest) {
        setCurrentRequestPath(WebUtils.getRequestPath(httpRequest));
        @SuppressWarnings("unchecked")
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.equalsIgnoreCase("accept")) {
                this.requestAcceptHeader = httpRequest.getHeader(headerName);
                break;
            }
        }
    }

    /**
     * @param entityId entity id
     * @param type type of entity which could be actually a parent path of the entity
     * @return secured record id if the one was found.
     */
    public Long findSecuredRecordId(Long entityId, String type) {
        SecuredRecordNode securedRecord = findSecuredRecord(entityId, type);
        return securedRecord == null ? null : securedRecord.getSecuredRecordId();
    }

    public SecuredRecordNode findSecuredRecord(Long entityId, String type) {
        List<SecuredRecordNode> securedRecords = this.securedRecordContainer.loadSecuredRecords();
        //As soon as all entities from hierarchy stored in the same table we could assume
        //that we could find secured record mapped to subclass type(id will be unique for entities from the same inheritance hierarchy)
        ServiceResponse<Entity> response = OPFEngine.RegistryService.readEntitiesUpInHierarchyByLookup(type);
        List<String> types = new ArrayList<String>();
        if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
            for (Entity entityType : response.getData()) {
                types.add(entityType.getLookup());
            }
        } else {
            types.add(type);
        }
        SecuredRecordNode result = null;
        for (SecuredRecordNode securedRec : securedRecords) {
            if (securedRec != null && securedRec.getInternalId() != null && securedRec.getInternalId().equals(entityId) &&
                    types.contains(securedRec.getType())) {
                result = securedRec;
                break;
            }
        }
        return result;
    }

    public Map<Long, SecuredRecordNode> findSecuredRecords(List<Long> entityIdList, String type) {
        if (entityIdList == null || entityIdList.isEmpty() || StringUtils.isBlank(type)) {
            return null;
        }
        List<SecuredRecordNode> securedRecords = this.securedRecordContainer.loadSecuredRecords();
        //As soon as all entities from hierarchy stored in the same table we could assume
        //that we could find secured record mapped to subclass type(id will be unique for entities from the same inheritance hierarchy)
        ServiceResponse<Entity> response = OPFEngine.RegistryService.readEntitiesUpInHierarchyByLookup(type);
        List<String> types = new ArrayList<String>();
        if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
            for (Entity entityType : response.getData()) {
                types.add(entityType.getLookup());
            }
        } else {
            types.add(type);
        }
        Map<Long, SecuredRecordNode> result = new HashMap<Long, SecuredRecordNode>();
        for (SecuredRecordNode securedRec : securedRecords) {
            if (securedRec != null && entityIdList.contains(securedRec.getInternalId()) &&
                    types.contains(securedRec.getType())) {
                result.put(securedRec.getInternalId(), securedRec);
                break;
            }
        }
        return result;
    }

    /**
     * @return id filter for previously detected entity type
     */
    public SpecifiedIdsFilter findSpecifiedIdsFilterByType() {
        return this.specifiedIdsFilterContainer.getSpecifiedIdsFilterByType(getEntityType());
    }

    /**
     * @param type entity type
     * @return id filter for specified entity type
     */
    public SpecifiedIdsFilter findSpecifiedIdsFilterByType(String type) {
        return this.specifiedIdsFilterContainer.getSpecifiedIdsFilterByType(type);
    }

    public static void saveContext(OPFContext ctx) throws ContextLookupException {
        try {
            CONTEXT_HOLDER_DELEGATE.saveContext(ctx);
        } catch (AuthenticationException e) {
            throw new ContextLookupException(e);
        }
    }

}