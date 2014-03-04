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

package net.firejack.platform.service.aop;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageChangesModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageChangesStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.service.aop.annotation.Changes;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(6)
@Component
public class ChangesInterceptor {
    private static final Logger logger = Logger.getLogger(ChangesInterceptor.class);

    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    private IPackageChangesStore changesStore;

    @Around(value = "execution(* execute(..)) && args(request) && @target(changes)", argNames = "pjp,request,changes")
    public Object trackActionDetails(ProceedingJoinPoint pjp, ServiceRequest request, Changes changes) throws Throwable {
        AbstractDTO data = request.getData();
        if (data instanceof SimpleIdentifier)
            deleteProcess((Long) ((SimpleIdentifier) data).getIdentifier(), changes.value());

        ServiceResponse response = (ServiceResponse) pjp.proceed();
        if (!response.isSuccess())
            return response;

        data = response.getItem();
        if (data instanceof RegistryNodeTree)
            postProcess((RegistryNodeTree) data, changes.value());
        else if (data instanceof Lookup)
            postProcess((Lookup) data, changes.value());

        return response;
    }

    private void postProcess(RegistryNodeTree entity, String message) {
        PackageChangesModel changesModel = new PackageChangesModel();

        PackageModel packageModel = packageStore.findPackage(entity.getLookup());
        changesModel.setPackageModel(packageModel);

        changesModel.setEntity(new RegistryNodeModel(entity.getId()));

        changesModel.setDescription(ClassUtils.evaluate(message, entity));
        changesStore.saveOrUpdate(changesModel);
    }

    private void postProcess(Lookup entity, String message) {
        PackageChangesModel changesModel = new PackageChangesModel();

        PackageModel packageModel = packageStore.findPackage(entity.getLookup());
        changesModel.setPackageModel(packageModel);

        changesModel.setEntity(new RegistryNodeModel(entity.getId()));

        changesModel.setDescription(ClassUtils.evaluate(message, entity));
        changesStore.saveOrUpdate(changesModel);
    }

    private void deleteProcess(Long id, String message) {
        Long count = changesStore.countEntityChanges(id);
        if (count == 0) {
            RegistryNodeModel model = registryNodeStore.findById(id);

            PackageChangesModel changesModel = new PackageChangesModel();

            PackageModel packageModel = packageStore.findPackage(model.getLookup());
            changesModel.setPackageModel(packageModel);

            changesModel.setDescription(ClassUtils.evaluate(message, model));
            changesStore.saveOrUpdate(changesModel);
        }
    }
}