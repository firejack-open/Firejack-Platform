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

package net.firejack.platform.service.securitymanager.endpoint;


import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.securitymanager.domain.MoveSecuredRecordInfo;
import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.TreeNodeSecuredRecord;
import net.firejack.platform.core.domain.IdLookup;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Component("securedRecordEndpoint")
@Path("authority/secured-record")
public class SecuredRecordEndpoint implements ISecuredRecordEndpoint {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<SecuredRecord> readSecuredRecordByEntityIdAndType(
             @QueryParam("entityId") Long entityId, @QueryParam("typeLookup") String typeLookup) {
        return OPFEngine.SecurityManagerService.getSecuredRecordInfo(entityId, typeLookup);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<SecuredRecord> createSecuredRecord(
             @QueryParam("parentId") Long parentId, @QueryParam("parentType") String parentType,
            ServiceRequest<SecuredRecord> request) {
        SecuredRecord data = request.getData();
        if (data.getExternalNumberId() == null) {
            return OPFEngine.SecurityManagerService.createSecuredRecord(
                    data.getExternalStringId(), data.getName(), data.getRegistryNodeLookup(), parentId, parentType);
        } else {
            return OPFEngine.SecurityManagerService.createSecuredRecord(
                    data.getExternalNumberId(), data.getName(), data.getRegistryNodeLookup(), parentId, parentType);
        }
    }

    @POST
    @Path("/many")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse createSecuredRecords(ServiceRequest<TreeNodeSecuredRecord> request) {
        return OPFEngine.SecurityManagerService.createSecuredRecords(request.getDataList());
    }

    @GET
    @Path("/all-nodes")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<SecuredRecordNode> readAllSecuredRecords() {
        return OPFEngine.SecurityManagerService.loadAllSecureRecordNodes();
    }

    @POST
    @Path("/move")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<SecuredRecord> moveSecuredRecord(ServiceRequest<MoveSecuredRecordInfo> request) {
        MoveSecuredRecordInfo requestData = request.getData();
        return OPFEngine.SecurityManagerService.moveSecuredRecord(
                requestData.getId(), requestData.getLookup(), requestData.getParent(), requestData.getOldParents());
    }

    @PUT
    //@Path("/{securedRecordId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse updateSecuredRecord(ServiceRequest<SecuredRecord> request) {
        SecuredRecord securedRecord = request.getData();
        return OPFEngine.SecurityManagerService.updateSecuredRecord(
                securedRecord.getExternalNumberId(), securedRecord.getRegistryNodeLookup(),
                securedRecord.getName());
    }

    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteSecuredRecord(
             @QueryParam("entityId") Long entityId, @QueryParam("typeLookup") String typeLookup,
             @QueryParam("entityCustomId") String entityCustomId) {
        if (entityId == null) {
            return OPFEngine.SecurityManagerService.removeSecuredRecord(entityCustomId, typeLookup);
        } else {
            return OPFEngine.SecurityManagerService.removeSecuredRecord(entityId, typeLookup);
        }
    }

    @DELETE
    @Path("/many")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteSecuredRecords(ServiceRequest<IdLookup> request) {
        return OPFEngine.SecurityManagerService.removeSecuredRecords(request.getDataList());
    }

}