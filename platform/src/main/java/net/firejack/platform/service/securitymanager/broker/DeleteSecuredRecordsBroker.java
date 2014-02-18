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

package net.firejack.platform.service.securitymanager.broker;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.IdLookup;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component
public class DeleteSecuredRecordsBroker extends ServiceBroker<ServiceRequest<IdLookup>, ServiceResponse> {

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore store;

    @Override
    protected ServiceResponse perform(ServiceRequest<IdLookup> request) throws Exception {
        List<IdLookup> idLookupList = request.getDataList();
        ServiceResponse response;
        if (idLookupList == null || idLookupList.isEmpty()) {
            response = new ServiceResponse("No secured records specified for delete operation.", false);
        } else {
            List<Tuple<Long, String>> srToDelete = new ArrayList<Tuple<Long, String>>();
            for (IdLookup idLookup : idLookupList) {
                srToDelete.add(new Tuple<Long, String>(idLookup.getId(), idLookup.getLookup()));
            }
            List<SecuredRecordModel> deletedRecords = store.deleteAllByIdAndType(srToDelete);
            if (deletedRecords == null || deletedRecords.isEmpty() || srToDelete.size() > deletedRecords.size()) {
                response = new ServiceResponse(
                        "Not all secured records specified for delete operation were actually deleted.", false);
            } else {
                response = new ServiceResponse("Specified Secured Records were deleted successfully.", true);
            }
        }
        return response;
    }

    @Override
    protected void processSecuredRecords(ServiceRequest<IdLookup> request, ServiceResponse response) {
        //
    }
}