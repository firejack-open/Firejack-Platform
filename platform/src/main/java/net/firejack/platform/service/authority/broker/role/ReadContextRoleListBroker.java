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

package net.firejack.platform.service.authority.broker.role;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.core.broker.FilteredListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component
public class ReadContextRoleListBroker extends FilteredListBroker<RoleModel, Role, SimpleIdentifier<List<Long>>> {

    @Autowired
    private IRoleStore roleStore;

    @Override
    protected ILookupStore<RoleModel, Long> getStore() {
        return roleStore;
    }

    @Override
    protected List<RoleModel> getModelList(
            ServiceRequest<SimpleIdentifier<List<Long>>> roleServiceRequest)
            throws BusinessFunctionException {
        List<Long> exceptIds = roleServiceRequest.getData().getIdentifier();
        return roleStore.findContextRoles(exceptIds);
    }

}