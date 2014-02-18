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

package net.firejack.platform.core.config.meta.factory;


import net.firejack.platform.core.config.meta.element.resource.CollectionElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionMemberElement;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.resource.CollectionMemberModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

public class CollectionElementFactory extends
        PackageDescriptorConfigElementFactory<CollectionModel, CollectionElement> {

    private static final Logger logger = Logger.getLogger(CollectionElementFactory.class);

    public CollectionElementFactory() {
        setEntityClass(CollectionModel.class);
        setElementClass(CollectionElement.class);
    }

    @Override
    protected void initDescriptorElementSpecific(
            CollectionElement collectionElement, CollectionModel collection) {
        super.initDescriptorElementSpecific(collectionElement, collection);
        List<CollectionMemberModel> collectionMembers = collection.getCollectionMembers();
        if (collectionMembers != null) {
            if (Hibernate.isInitialized(collectionMembers)) {
                List<CollectionMemberElement> memberList = new ArrayList<CollectionMemberElement>();
                for (CollectionMemberModel collectionMember : collectionMembers) {
                    RegistryNodeModel registryNode = collectionMember.getReference();
                    if (Hibernate.isInitialized(registryNode)) {
                        CollectionMemberElement member = new CollectionMemberElement();
                        member.setReference(registryNode.getLookup());
                        member.setOrder(collectionMember.getOrder());
                        memberList.add(member);
                    } else {
                        logger.error("Collection Member's [ id = " + collectionMember.getId() +
                                " ] reference property is not initialized.");
                        throw new BusinessFunctionException(
                                "Collection Member's reference property is not initialized.");
                    }
                }
                collectionElement.setCollectionMembers(memberList);
            } else {
                logger.error("Collection Members for collection [ id = " +
                        collection.getId() + " ] are not initialized.");
                throw new BusinessFunctionException("Collection Members are not initialized.");
            }
        }
    }

    @Override
    protected void initEntitySpecific(
            CollectionModel collection, CollectionElement collectionElement) {
        super.initEntitySpecific(collection, collectionElement);
        List<CollectionMemberElement> memberList = collectionElement.getCollectionMembers();
        if (memberList != null && !memberList.isEmpty()) {
            List<CollectionMemberModel> collectionMembers = new ArrayList<CollectionMemberModel>();
            for (CollectionMemberElement member : memberList) {
                if (StringUtils.isBlank(member.getReference())) {
                    throw new BusinessFunctionException(
                            "The reference in Collection Member Element is empty.");
                } else if (member.getOrder() >= memberList.size()) {
                    /*throw new BusinessFunctionException(
                            "Wrong order for collection member element. Should not be greater than " +
                                    memberList.size());*/
                    //todo: uncomment this block when update collection members order on delete operation will be fixed
                } else if (member.getOrder() < 0) {
                    throw new BusinessFunctionException(
                            "Wrong order for collection member element. Should not be negative.");
                }
                RegistryNodeModel registryNode = registryNodeStore.findByLookup(member.getReference());
                if (registryNode == null) {
                    String message = "Could not find record by lookup [" + member.getReference() + "] in db according Collection Member's reference property.";
                    logger.warn(message);
                    throw new BusinessFunctionException(message);
                } else {
                    RegistryNodeType type = registryNode.getType();
                    if (type == RegistryNodeType.COLLECTION || type == RegistryNodeType.HTML_RESOURCE ||
                            type == RegistryNodeType.IMAGE_RESOURCE || type == RegistryNodeType.TEXT_RESOURCE ||
                            type == RegistryNodeType.AUDIO_RESOURCE || type == RegistryNodeType.VIDEO_RESOURCE ||
                            type == RegistryNodeType.DOCUMENT_RESOURCE || type == RegistryNodeType.FILE_RESOURCE) {
                        CollectionMemberModel collectionMember = new CollectionMemberModel();
                        collectionMember.setReference(registryNode);
                        collectionMember.setOrder(member.getOrder());
                        collectionMember.setCollection(collection);
                        collectionMembers.add(collectionMember);
                    } else {
                        logger.error("Collection member references to element of type = " + type.name());
                        throw new BusinessFunctionException(
                                "Wrong Collection Member reference. Should reference to collection or to resource.");
                    }
                }
            }
            collection.setCollectionMembers(collectionMembers);
        }
    }

    @Override
    protected String getRefPath(CollectionModel collection) {
        return collection.getPath();
    }

}