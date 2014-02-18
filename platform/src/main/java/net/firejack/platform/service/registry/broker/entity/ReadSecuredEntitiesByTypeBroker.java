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

package net.firejack.platform.service.registry.broker.entity;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import net.firejack.platform.api.registry.domain.SecuredEntity;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IActionStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.handler.Builder;
import net.firejack.platform.web.handler.ErrorHandler;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TrackDetails
@Component
public class ReadSecuredEntitiesByTypeBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<SecuredEntity>> {

    @Resource(name = "registryNodeStories")
    private Map<RegistryNodeType, IRegistryNodeStore<? extends RegistryNodeModel>> registryNodeStores;

    @Override
    protected ServiceResponse<SecuredEntity> perform(ServiceRequest<SimpleIdentifier<Long>> request)
            throws Exception {
        Long entityTypeId = request.getData().getIdentifier();
        ServiceResponse<SecuredEntity> response;
        if (entityTypeId == null) {
            response = new ServiceResponse<SecuredEntity>("Type lookup should not be blank.", false);
        } else {
            IEntityStore entityStore = (IEntityStore) registryNodeStores.get(RegistryNodeType.ENTITY);
            EntityModel entityModel = entityStore.findById(entityTypeId);
            if (entityModel == null) {
                response = new ServiceResponse<SecuredEntity>("Failed to load type information by spcified lookup.", false);
            } else if (entityModel.getSecurityEnabled() != null && entityModel.getSecurityEnabled()) {
                boolean openFlameType = entityModel.getLookup().startsWith("net.firejack.platform.");
                if (openFlameType) {
                    RegistryNodeType registryNodeType = RegistryNodeType.findByEntityType(entityModel.getLookup());
                    if (registryNodeType == null) {
                        response = new ServiceResponse<SecuredEntity>(
                                "Failed to find registry node type for specified lookup. Seems that incorrect entity was marked as security enabled.",
                                false);
                    } else {
                        IRegistryNodeStore<? extends LookupModel> registryNodeStore = registryNodeStores.get(registryNodeType);
                        List<? extends LookupModel> allItems = registryNodeStore.findAll();
                        List<SecuredEntity> securedEntities = new ArrayList<SecuredEntity>();
                        if (allItems != null) {
                            for (LookupModel model : allItems) {
                                SecuredEntity entity = new SecuredEntity();
                                entity.setId(model.getId());
                                entity.setName(model.getName());
                                entity.setDescription(model.getDescription());

                                securedEntities.add(entity);
                            }
                        }
                        response = new ServiceResponse<SecuredEntity>(securedEntities);
                    }
                } else {
                    IActionStore actionStore = (IActionStore) registryNodeStores.get(RegistryNodeType.ACTION);
                    ActionModel readAllAction = actionStore.findByLookup(entityModel.getLookup() + ".read-all");
                    if (readAllAction == null) {
                        response = new ServiceResponse<SecuredEntity>("Failed to find read-all action for the specified type.", false);
                    } else {
                        if (StringUtils.isBlank(readAllAction.getServerName())) {
                            response = new ServiceResponse<SecuredEntity>(
                                    "Seems that package that houses specified entity type does not associated with any system.", false);
                        } else {
                            StringBuilder sb = new StringBuilder("http://");
                            sb.append(readAllAction.getServerName());
                            if (readAllAction.getPort() != null && !readAllAction.getPort().equals(80)) {
                                sb.append(':').append(readAllAction.getPort());
                            }
                            sb.append(readAllAction.getParentPath()).append(readAllAction.getUrlPath());
                            List<SecuredEntity> securedEntities = loadGatewayItems(entityModel.getLookup(), entityModel.getName(), sb.toString());
                            if (securedEntities == null) {
                                response = new ServiceResponse<SecuredEntity>("Failed to load client application items by specified type", false);
                            } else {
                                response = new ServiceResponse<SecuredEntity>(securedEntities);
                            }
                        }
                    }
                }
            } else {
                response = new ServiceResponse<SecuredEntity>("Specified type is not marked as security enabled.", false);
            }
        }
        return response;
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

    private List<SecuredEntity> loadGatewayItems(String typeLookup, String typeName, String readAllItemsUrl) {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        WebResource webResource = Client.create(config).resource(readAllItemsUrl);
        webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 60000);
        webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, 60000);
        WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE);

        Builder proxy = ErrorHandler.getProxy(builder);
        if (OPFContext.isInitialized()) {
            try {
                String sessionToken = OPFContext.getContext().getSessionToken();
                if (sessionToken != null) {
                    Cookie cookie = new Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, sessionToken);
                    proxy.cookie(cookie);
                }
            } catch (ContextLookupException e) {
                logger.error(e.getMessage());
            }
        }
        List<SecuredEntity> result = null;
        try {
            String stringResponse = builder.get(String.class);
            if (StringUtils.isNotBlank(stringResponse)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(stringResponse);
                JsonNode successNode = rootNode.findValue("success");
                if (successNode != null && !successNode.isMissingNode() && successNode.isBoolean()) {
                    boolean success = successNode.getBooleanValue();
                    if (success) {
                        result = new ArrayList<SecuredEntity>();
                        JsonNode dataNode = rootNode.path("data");
                        if (!dataNode.isMissingNode() && dataNode.isArray()) {
                            int i = 0;
                            JsonNode dtoNode;
                            while ((dtoNode = dataNode.get(i++)) != null) {
                                if (!dtoNode.isMissingNode()) {
                                    JsonNode idNode = dtoNode.get("id");
                                    if (idNode != null) {
                                        Long id = idNode.getLongValue();
                                        if (id == null) {
                                            logger.warn("Failed to detect id property in item returned by gateway application.");
                                        } else {
                                            JsonNode displayedNameNode = dtoNode.get("name");
                                            if (displayedNameNode == null) {
                                                displayedNameNode = dtoNode.get("title");
                                            }
                                            String displayedName;
                                            if (displayedNameNode == null) {
                                                displayedName = typeName + id;
                                            } else {
                                                displayedName = displayedNameNode.getTextValue();
                                            }

                                            String description = null;
                                            JsonNode descriptionNode = dtoNode.get("description");
                                            if (descriptionNode != null) {
                                                description = descriptionNode.getTextValue();
                                            }

                                            SecuredEntity securedEntity = new SecuredEntity();
                                            securedEntity.setId(id);
                                            securedEntity.setName(displayedName);
                                            securedEntity.setDescription(description);

                                            result.add(securedEntity);
                                        }
                                    }
                                }
                            }
                            try {
                                OPFContext context = OPFContext.getContext();
                                if (context != null) {
                                    List<Long> idList = new ArrayList<Long>();
                                    for (SecuredEntity entity : result) {
                                        idList.add(entity.getId());
                                    }
                                    if (!idList.isEmpty()) {
                                        Map<Long, SecuredRecordNode> srByIdMap = context.findSecuredRecords(idList, typeLookup);
                                        if (srByIdMap != null) {
                                            for (SecuredEntity entity : result) {
                                                SecuredRecordNode securedRecord = srByIdMap.get(entity.getId());
                                                if (securedRecord != null) {
                                                    entity.setSecuredRecordId(securedRecord.getSecuredRecordId());
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (ContextLookupException e) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(e.getMessage(), e);
                                } else {
                                    logger.error(e.getMessage());
                                }
                            }

                        }
                    }
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            result = null;
        }
        return result;
    }

}