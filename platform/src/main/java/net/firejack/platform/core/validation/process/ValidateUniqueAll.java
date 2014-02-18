package net.firejack.platform.core.validation.process;
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


import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.annotation.DomainType;
import net.firejack.platform.core.validation.annotation.Validate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidateUniqueAll {

    @Resource(name = "registryNodeStories")
    private Map<RegistryNodeType, IRegistryNodeStore<RegistryNodeModel>> registryNodeStories;
    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private IDomainStore domainStore;

    public void validatePackage(Package entity) {
        Long packageId = entity.getId();
        Database database = entity.getDatabase();
        Long databaseId = database != null ? database.getId() : null;

        PackageModel _package = packageStore.findWithDatabaseById(packageId);

        if (_package.getDatabase() != null && !_package.getDatabase().getId().equals(databaseId)) {
            _package.getDatabase().setId(databaseId);
            validate(_package);
        }
    }

    public void validateDomain(Domain entity) {
        Long id = entity.getId();
        String prefix = entity.getPrefix();
        Database db = entity.getDatabase();
        Long databaseId = db != null ? db.getId() : null;

        DomainModel domainModel = domainStore.findById(id);

        DatabaseModel databaseModel = null;
        if (databaseId != null) {
            databaseModel = new DatabaseModel();
            databaseModel.setId(databaseId);
        }

        domainModel.setDatabase(databaseModel);
        domainModel.setPrefix(prefix);

        validate(domainModel);
    }

    public void validate(RegistryNodeModel entity) {

        PackageModel _package;
        if (entity instanceof PackageModel) {
            _package = (PackageModel) entity;
        } else {
            _package = packageStore.findPackage(entity.getLookup());
        }

        ApplicationContext context = OpenFlameSpringContext.getContext();
        Map<String, Object> annotation = context.getBeansWithAnnotation(Validate.class);

        for (Object bean : annotation.values()) {
            Validate validate = bean.getClass().getAnnotation(Validate.class);
            DomainType type = validate.type();
            DomainType[] unique = validate.unique();

            RegistryNodeType registryNodeType = RegistryNodeType.valueOf(type.name());
            IRegistryNodeStore<RegistryNodeModel> store = registryNodeStories.get(registryNodeType);

            if (store == null) {
                throw new BusinessFunctionException("Store class not found by " + registryNodeType);
            }

            Class[] _class = new Class[unique.length];
            for (int i = 0; i < unique.length; i++) {
                DomainType domainType = unique[i];
                RegistryNodeType nodeType = RegistryNodeType.valueOf(domainType.name());
                _class[i] = nodeType.getClazz();
            }

            Map<String, RegistryNodeModel> temp = new HashMap<String, RegistryNodeModel>();
            List<String> names = store.findAllDuplicateNamesByType(_package.getLookup(), _class);

            for (String name : names) {
                List<RegistryNodeModel> models = store.findAllDuplicateEntityByType(_package.getLookup(), name, _class);
                for (RegistryNodeModel model : models) {
                    String path = createPath(model, entity);
                    if (!temp.containsKey(path)) {
                        temp.put(path, model);
                    } else {
                        RegistryNodeModel duplicate = temp.get(path);
                        throw new BusinessFunctionException("Found duplicate entity located from lookup: " + model.getLookup() + " and duplicate lookup location: " + duplicate.getLookup());
                    }
                }
                temp.clear();
            }
        }
    }

    private String createPath(RegistryNodeModel model, RegistryNodeModel variable) {
        if (model.equals(variable)) {
            variable.setParent(model.getParent());
            model = variable;
        }

        if (model.getType() == RegistryNodeType.PACKAGE) {
            PackageModel _package = (PackageModel) getTarget(model);
            return _package.getDatabase() != null ? String.valueOf(_package.getDatabase().getId()) : "";
        } else if (model.getType() == RegistryNodeType.DOMAIN) {
            DomainModel domain = (DomainModel) getTarget(model);
            if (domain.getDatabase() == null) {
                return createPath(domain.getParent(), variable) + StringUtils.defaultString(domain.getPrefix(), "");
            } else {
                return String.valueOf(domain.getDatabase().getId()) + StringUtils.defaultString(domain.getPrefix(), "");
            }
        } else if (model.getType() == RegistryNodeType.ENTITY || model.getType() == RegistryNodeType.ENTITY) {
            EntityModel entity = (EntityModel) getTarget(model);
            if (entity.getExtendedEntity() != null) {
                return createPath(entity.getParent(), variable) + String.valueOf(entity.getExtendedEntity().getId());
            }
        }

        return createPath(model.getParent(), variable) + String.valueOf(model.getId());
    }

    protected <T> T getTarget(T t) {
        if (t instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) t).getHibernateLazyInitializer();
            if (!lazyInitializer.isReadOnlySettingAvailable()) {
                t = (T) lazyInitializer.getImplementation();
            }
        }
        return t;
    }

}
