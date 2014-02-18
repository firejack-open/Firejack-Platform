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

package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readDomainsByPackageLookupBroker")
public class ReadDomainsByPackageLookupBroker extends ListBroker<DomainModel, Domain, SimpleIdentifier<String>> {

    @Autowired
    private IPackageStore packageStore;
    @Autowired
	private IDomainStore domainStore;

    @Override
    protected List<DomainModel> getModelList(ServiceRequest<SimpleIdentifier<String>> request) throws BusinessFunctionException {
        String packageLookup = request.getData().getIdentifier();
        if (packageLookup == null) {
            throw new BusinessFunctionException("Package Lookup can't be empty.");
        }

        PackageModel packageModel = packageStore.findPackage(packageLookup);
        if (packageModel == null) {
            throw new BusinessFunctionException("Could not find Package by lookup: " + packageLookup);
        }

        Class[] registryNodeClasses = {
            RegistryNodeType.DOMAIN.getClazz()
        };

        return domainStore.findAllByPrefixLookupAndTypes(packageLookup, getFilter(), registryNodeClasses);
    }
}