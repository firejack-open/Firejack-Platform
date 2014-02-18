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
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@TrackDetails
@Component("readEntitiesByPackageLookupBroker")
public class ReadEntitiesByPackageLookupBroker extends ListBroker<EntityModel, Entity, SimpleIdentifier<String>> {

    @Autowired
    private IPackageStore packageStore;
    @Autowired
	private IEntityStore entityStore;
    @Autowired
    private IDomainStore domainStore;
    @Autowired
    private IRegistryNodeStore registryNodeStore;

    private String packageLookup;

    @Override
    protected List<EntityModel> getModelList(ServiceRequest<SimpleIdentifier<String>> request) throws BusinessFunctionException {
        packageLookup = request.getData().getIdentifier();
        if (packageLookup == null) {
            throw new BusinessFunctionException("Package Lookup can't be empty.");
        }

        PackageModel packageModel = packageStore.findPackage(packageLookup);
        if (packageModel == null) {
            throw new BusinessFunctionException("Could not find Package by lookup: " + packageLookup);
        }

        Class[] registryNodeClasses = {
            RegistryNodeType.ENTITY.getClazz()
        };

        return entityStore.findAllByPrefixLookupAndTypes(packageLookup, getFilter(), registryNodeClasses);
    }

    @Override
    protected List<Entity> result(List<Entity> dtoList, List<EntityModel> modelList) {
        List<Entity> entities = super.result(dtoList, modelList);

        Map<String, DatabaseModel> dataSources = domainStore.findAllWithDataSourcesByPackageLookup(packageLookup);

        Map<String, String> cacheDomainNames = new HashMap<String, String>();
        for (Entity entity : entities) {
            String entityPath = entity.getPath();
            String domainName = cacheDomainNames.get(entityPath);
            if (StringUtils.isBlank(domainName)) {
                List<RegistryNodeModel> registryNodeModels = registryNodeStore.findAllParentsForEntityLookup(entity.getLookup());
                Collections.reverse(registryNodeModels);
                for (RegistryNodeModel registryNodeModel : registryNodeModels) {
                    if (RegistryNodeType.DOMAIN.equals(registryNodeModel.getType())) {
                        domainName = registryNodeModel.getName();
                        cacheDomainNames.put(entityPath, domainName);
                    }
                }
            }
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("domainName", StringUtils.capitalize(domainName));

            String path = entity.getPath();
            DatabaseModel databaseModel = findDataSourceEntity(dataSources, path);
            if (databaseModel != null) {
                parameters.put("dataSource", databaseModel.getLookup());
            }

            entity.setParameters(parameters);
        }
        Collections.sort(entities, new Comparator<Entity>() {
            public int compare(final Entity e1, final Entity e2) {
                String domainName1 = (String) e1.getParameters().get("domainName");
                String domainName2 = (String) e2.getParameters().get("domainName");
                int domainCompare = domainName1.compareTo(domainName2);
                if (domainCompare == 0) {
                    return e1.getName().compareTo(e2.getName());
                } else {
                    return domainCompare;
                }
            }
        });
        return entities;
    }

    private DatabaseModel findDataSourceEntity(Map<String, DatabaseModel> dataSources, String lookup) {
        DatabaseModel databaseModel = dataSources.get(lookup);
        if (databaseModel == null && StringUtils.isNotEmpty(lookup)) {
            String path = DiffUtils.extractPathFromLookup(lookup);
            databaseModel = findDataSourceEntity(dataSources, path);
        }
        return databaseModel;
    }
}