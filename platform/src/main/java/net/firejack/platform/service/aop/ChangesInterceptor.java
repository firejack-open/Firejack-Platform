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