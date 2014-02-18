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

import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.broker.workflow.IWorkflowHandler;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@Component
public class ReadCachedEntitiesFromUpHierarchyBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<Entity>> {

    @Override
    protected ServiceResponse<Entity> perform(ServiceRequest<SimpleIdentifier<String>> request)
            throws Exception {
        List<Entity> entityList = CacheManager.getInstance().getTypeWithSubclasses(request.getData().getIdentifier());
        return entityList == null ? new ServiceResponse<Entity>("Entity was not found.", false) :
                new ServiceResponse<Entity>(entityList, "Entity was found", true);
    }

    @Override
    protected SecurityHandler getSecurityHandler() {
        return null;
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

    @Override
    public IWorkflowHandler getWorkflowHandler() {
        return null;
    }
}