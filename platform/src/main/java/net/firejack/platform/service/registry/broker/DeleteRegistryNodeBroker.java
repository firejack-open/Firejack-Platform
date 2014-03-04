/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
