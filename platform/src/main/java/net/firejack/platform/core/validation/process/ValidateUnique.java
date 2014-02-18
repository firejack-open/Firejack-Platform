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


import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.annotation.DomainType;
import net.firejack.platform.core.validation.annotation.Validate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidateUnique implements ApplicationContextAware {

	@Resource(name = "registryNodeStories")
	private Map<RegistryNodeType, IRegistryNodeStore<RegistryNodeModel>> registryNodeStories;
	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel>  registryNodeStore;
    private Map<DomainType,Validate> cache = new HashMap<DomainType, Validate>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Object> annotation = context.getBeansWithAnnotation(Validate.class);

        for (Object bean : annotation.values()) {
            Validate validate = bean.getClass().getAnnotation(Validate.class);
            if (validate != null) {
                cache.put(validate.type(), validate);
            }
        }
    }

    public <T extends BaseEntity> void validate(T domain, Validate validate) {
		DomainType[] unique = validate.unique();
		if (unique.length == 0)
			return;

		if (!(domain instanceof Lookup))
			throw new BusinessFunctionException("Domain not extended Lookup");

		Lookup lookup = (Lookup) domain;
		Long id = lookup.getId();
		Long parentId = lookup.getParentId();
		String name = lookup.getName();

		RegistryNodeModel nodeModel = registryNodeStore.findAllParentsById(parentId);
		String target = createPath(nodeModel);
		if (domain instanceof Entity) {
			Entity extendedEntity = ((Entity) domain).getExtendedEntity();
			if (extendedEntity != null) {
				target += extendedEntity.getId();
			}
		}

        String _package = nodeModel.getLookup().replaceAll("(([\\w\\-]+\\.){3}).*", "$1");
        checkByType(unique, target, _package, name, id);
    }

    public boolean checkName(String path, String name, DomainType type) {
        Validate validate = cache.get(type);
        if (validate != null) {
            List<RegistryNodeModel> models;
            if (DomainType.DOMAIN.equals(type)) {
                models = registryNodeStore.findAllParentsForLookup(path + ".");
            } else if (DomainType.DATABASE.equals(type)) {
                models = new ArrayList<RegistryNodeModel>();
                RegistryNodeModel registryNodeModel = registryNodeStore.findByLookup(path);
                if (registryNodeModel != null) {
                    models.add(registryNodeModel);
                }
            } else {
                models = registryNodeStore.findAllParentsForEntityLookup(path);
            }
            if (models != null && !models.isEmpty()) {
                String target = createPath(models.get(models.size() - 1));
                String _package = path.replaceAll("(([\\w\\-]+\\.){3}).*", "$1");
                try {
                    checkByType(validate.unique(), target, _package, name, null);
                } catch (BusinessFunctionException e) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkByType(DomainType[] unique, String target, String _package, String name, Long id) {
        for (DomainType type : unique) {
            RegistryNodeType nodeType = RegistryNodeType.valueOf(type.name());
            IRegistryNodeStore<RegistryNodeModel> store = registryNodeStories.get(nodeType);
            if (store == null) {
                throw new BusinessFunctionException("Store class not found by " + nodeType);
            }

            List<RegistryNodeModel> models = store.findAllByName(_package ,name);
            for (RegistryNodeModel model : models) {
                registryNodeStore.findAllParents(model);
                String path = createPath(model);
                if (!model.getId().equals(id) && path.equals(target)){
                    throw new BusinessFunctionException("Found duplicate entity located from lookup: " + model.getLookup());
                }
            }
        }
    }

	private String createPath(RegistryNodeModel model) {
		if (model.getType() == RegistryNodeType.PACKAGE) {
			PackageModel _package = (PackageModel) getTarget(model);
			return _package.getDatabase() != null ? String.valueOf(_package.getDatabase().getId()) : "";
		} else if (model.getType() ==  RegistryNodeType.DOMAIN) {
			DomainModel domain = (DomainModel) getTarget(model);
			if (domain.getDatabase() == null) {
				return createPath(domain.getParent()) + StringUtils.defaultString(domain.getPrefix(),"");
			} else {
				return String.valueOf(domain.getDatabase().getId()) + StringUtils.defaultString(domain.getPrefix(),"");
			}
		} else if (model.getType() ==  RegistryNodeType.ENTITY) {
			EntityModel entity = (EntityModel) getTarget(model);
			if (entity.getExtendedEntity() != null) {
				return createPath(entity.getParent()) + String.valueOf(entity.getExtendedEntity().getId());
			}
		} else if (model.getType() == RegistryNodeType.SYSTEM) {
            SystemModel system = (SystemModel) getTarget(model);
            return String.valueOf(system.getId());
        }
		return createPath(model.getParent());
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
