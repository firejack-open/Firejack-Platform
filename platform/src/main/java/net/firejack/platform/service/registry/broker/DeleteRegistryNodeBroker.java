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

package net.firejack.platform.service.registry.broker;

import net.firejack.platform.core.broker.DeleteLookupModelBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import net.firejack.platform.web.mina.bean.Status;
import net.firejack.platform.web.mina.bean.StatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class DeleteRegistryNodeBroker<R extends RegistryNodeModel> extends DeleteLookupModelBroker<R> {

    @Autowired
    @Qualifier("progressAspect")
    private ManuallyProgress progress;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<R> registryNodeStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        Long id = request.getData().getIdentifier();
        ServiceResponse response;
        if (id == null) {
            response = new ServiceResponse("Id parameter is blank.", false);
        } else {
            try {
                delete(id);
                response = new ServiceResponse<Status>(new Status("Starting...", 0, StatusType.STARTED), "", true);
            } catch (Throwable th) {
                logger.error(th.getMessage(), th);
                response = new ServiceResponse(th.getMessage(), false);
            }
        }
        return response;
    }

    @Override
	protected void delete(Long id) {
        DeleteProcess deleteProcess = new DeleteProcess(id);
        Thread thread = new Thread(deleteProcess);
        thread.start();
	}

    protected class DeleteProcess implements Runnable {

        private Long id;

        public DeleteProcess(Long id) {
            this.id = id;
        }

        @Override
		public void run() {
            R registryNode = registryNodeStore.findById(id);
            Integer count = registryNodeStore.findCountByLikeLookupWithFilter(registryNode.getLookup(), null);
            count++;
            if (RegistryNodeType.DOMAIN.equals(registryNode.getType()) ||
                RegistryNodeType.SUB_DOMAIN.equals(registryNode.getType()) ||
                RegistryNodeType.ENTITY.equals(registryNode.getType())) {
                Integer externalCountElements = registryNodeStore.findExternalCountByLookup(registryNode.getLookup());
                count += externalCountElements;
            }
            progress.start(count);
            IRegistryNodeStore<R> store = (IRegistryNodeStore<R>) getStore();
            store.deleteRecursiveById(id);
            progress.end(new ServiceResponse(getSuccessMessage(), true));
        }
    }

}
