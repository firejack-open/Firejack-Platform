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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.model.user.SystemUserModel;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;


@Component("serverStore")
public class ServerStore extends AliasableStore<ServerModel> implements IServerStore {

	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private ISystemStore systemStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(ServerModel.class);
    }

	@Override
	@Transactional(readOnly = true)
	public ServerModel findByPackage(String lookup, String name) {
		PackageModel packageModel = packageStore.findByLookup(lookup);
		if (packageModel != null && packageModel.getSystem() != null) {
			SystemModel systemModel = packageModel.getSystem();
			return findByLookup(DiffUtils.lookup(systemModel.getLookup(), name));
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServerModel> findByPackageId(Long packageId) {
		PackageModel packageModel = packageStore.findById(packageId);
		if (packageModel != null && packageModel.getSystem() != null) {
			SystemModel systemModel = packageModel.getSystem();
			return findChildrenByParentId(systemModel.getId(), null);
		}
		return null;
	}

	@Override
    @Transactional(readOnly = true)
    public ServerModel findByIdWithPackages(Long id) {
        ServerModel server = super.findById(id);
        RegistryNodeModel parent = server.getParent();
        while (parent != null) {
            Hibernate.initialize(parent);
            if (parent.getType() == RegistryNodeType.SYSTEM) {
                SystemModel system = getHibernateTemplate().get(SystemModel.class, parent.getId());
                Hibernate.initialize(system.getAssociatedPackages());
                server.setAssociatedPackages(system.getAssociatedPackages());
                break;
            } else {
                parent = parent.getParent();
            }
        }

        return server;
    }

    @Override
    @Transactional(readOnly = true)
    public SystemUserModel findSystemUserByPublicKey(String publicKey) {
        Criteria criteria = getSession().createCriteria(ServerModel.class);
        criteria.add(Restrictions.eq("publicKey", publicKey));
        ServerModel server = (ServerModel) criteria.uniqueResult();
        SystemUserModel userModel;
        if (server == null) {
            userModel = null;
        } else {
            SystemModel parentSystem = (SystemModel) server.getParent();
            Criteria systemUserCriteria = getSession().createCriteria(SystemUserModel.class);
            systemUserCriteria.add(Restrictions.eq("system", parentSystem));
            userModel = (SystemUserModel) systemUserCriteria.uniqueResult();
        }
        return userModel;
    }

	@Override
	@Transactional(readOnly = true)
	public SystemUserModel findSystemUser(ServerModel server) {
		SystemUserModel userModel = null;
		if (server != null) {
			RegistryNodeModel parent = server.getParent();
			Criteria systemUserCriteria = getSession().createCriteria(SystemUserModel.class);
			systemUserCriteria.add(Restrictions.eq("system", parent));
			userModel = (SystemUserModel) systemUserCriteria.uniqueResult();
		}
		return userModel;
	}
}
