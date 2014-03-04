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
