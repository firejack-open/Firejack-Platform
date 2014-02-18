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

package net.firejack.platform.service.registry.broker.package_;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.resource.FileResourceModel;
import net.firejack.platform.core.model.registry.resource.FileResourceVersionModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.reverse.ReverseEngineeringService;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@ProgressComponent(weight = 20000, showLogs = true)
@Component("reverseEngineeringBroker")
public class ReverseEngineeringBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private IDomainStore domainStore;
    @Autowired
    private FileHelper helper;
    @Autowired
    private IResourceStore<FileResourceModel> resourceStore;
    @Autowired
    private IResourceVersionStore<FileResourceVersionModel> resourceVersionStore;
    @Autowired
    private ReverseEngineeringService reverseEngineeringService;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
        Long registryNodeId = (Long) request.getData().get("registryNodeId");

        RegistryNodeModel registryNodeModel = registryNodeStore.findById(registryNodeId);
        if (registryNodeModel == null) {
            throw new BusinessFunctionException("object.not.found");
        }

        DatabaseModel databaseModel;
        if (registryNodeModel instanceof PackageModel) {
            PackageModel packageModel = packageStore.findWithDatabaseById(registryNodeModel.getId());
            databaseModel = packageModel.getDatabase();
        } else if (registryNodeModel instanceof DomainModel) {
            DomainModel domainModel = domainStore.findWithDatabaseById(registryNodeModel.getId());
            databaseModel = domainModel.getDatabase();
        } else {
            throw new BusinessFunctionException("object.not.found");
        }

        if (databaseModel == null && (registryNodeModel.getType() == RegistryNodeType.DOMAIN && StringUtils.isBlank(((DomainModel) registryNodeModel).getWsdlLocation()))) {
            throw new BusinessFunctionException("package.not.associated.with.database", registryNodeModel.getId());
        }

        if (databaseModel != null) {
            reverseEngineeringService.reverseEngineeringProcess(registryNodeModel, databaseModel);
            return new ServiceResponse("Reverse Engineering has been completed for database '" + databaseModel.getUrlPath() + "'.", true);
        } else {
            reverseEngineeringService.reverseEngineeringProcess(registryNodeModel, ((DomainModel) registryNodeModel).getWsdlLocation());
            return new ServiceResponse("Reverse Engineering has been completed.", true);
        }
    }
}
