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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.INavigable;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
@Component("packageStore")
public class PackageStore extends RegistryNodeStore<PackageModel> implements IPackageStore {

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Autowired
	private IEntityStore entityStore;

    @Autowired
    private IActionStore actionStore;

    @Autowired
    private ISystemStore systemStore;

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;

    @Autowired
    private FileHelper helper;

    /***/
    @PostConstruct
    public void init() {
        setClazz(PackageModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PackageModel findById(Long id) {
        return super.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PackageModel findByIdWithUID(Long id) {
        PackageModel foundPackage = super.findById(id);
        if (foundPackage != null) {
            Hibernate.initialize(foundPackage.getUid());
        }
        return foundPackage;
    }

    @Override
    @Transactional(readOnly = true)
    public PackageModel findWithSystemById(Long id) {
        PackageModel packageRN = super.findById(id);
        Hibernate.initialize(packageRN.getSystem());
        return packageRN;
    }

    @Override
    @Transactional(readOnly = true)
    public PackageModel findWithSystemByLookup(String packageLookup) {
        PackageModel packageRN = super.findByLookup(packageLookup);
        if (packageRN != null) {
            Hibernate.initialize(packageRN.getSystem());
        }
        return packageRN;
    }

    @Override
    @Transactional(readOnly = true)
    public PackageModel findWithDatabaseById(Long id) {
        PackageModel packageRN = super.findById(id);
        Hibernate.initialize(packageRN.getDatabase());
        return packageRN;
    }

    @Override
    @Transactional(readOnly = true)
    public PackageModel findWithSystemAndDatabaseById(Long id) {
        PackageModel packageRN = super.findById(id);
        Hibernate.initialize(packageRN.getDatabase());
        Hibernate.initialize(packageRN.getSystem());
        return packageRN;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<RegistryNodeModel, DatabaseModel> findAllWithDatabaseById(Long id) {
        Map<RegistryNodeModel, DatabaseModel> databaseAssociations = new HashMap<RegistryNodeModel, DatabaseModel>();
        PackageModel packageModel = findWithDatabaseById(id);
        if (packageModel != null) {
            Hibernate.initialize(packageModel.getDatabase());
            if (packageModel.getDatabase() != null) {
                databaseAssociations.put(packageModel, packageModel.getDatabase());
            }
            List<Object[]> dbAssociations = findByQuery(null, null, "DataBase.findAllDataSources", "lookupPrefix", packageModel.getLookup() + ".%");

            for (Object[] dbAssociation : dbAssociations) {
                DatabaseModel databaseModel = (DatabaseModel) dbAssociation[0];
                DomainModel domainModel = (DomainModel) dbAssociation[1];
                databaseAssociations.put(domainModel, databaseModel);
            }
        }

        return databaseAssociations;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, DatabaseModel> findAllWithDatabaseByUID(String uid) {
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        PackageModel packageModel = (PackageModel) criteria.uniqueResult();
        Map<String, DatabaseModel> databaseAssociations;
        if (packageModel == null) {
            databaseAssociations = null;
        } else {
            Hibernate.initialize(packageModel.getDatabase());
            databaseAssociations = new HashMap<String, DatabaseModel>();
            if (packageModel.getDatabase() != null) {
                databaseAssociations.put(packageModel.getLookup(), packageModel.getDatabase());
            }
            List<Object[]> dbAssociations = findByQuery(null, null,
                    "DataBase.findAllDataSources", "lookupPrefix", packageModel.getLookup() + ".%");
            if (dbAssociations != null) {
                for (Object[] dbAssociation : dbAssociations) {
                    DatabaseModel databaseModel = (DatabaseModel) dbAssociation[0];
                    String domainLookup = (String) dbAssociation[1];
                    databaseAssociations.put(domainLookup, databaseModel);
                }
            }
        }
        return databaseAssociations;
    }

    @Override
    @Transactional(readOnly = true)
    public PackageModel findWithSystemByChildrenId(Long registryNodeId) {
        List<Long> registryNodeIds = new ArrayList<Long>();
        List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
        registryNodeStore.findCollectionParentIds(registryNodeIds, registryNodeId, collectionArrayIds);
        List<RegistryNodeModel> parentRegistryNodes = registryNodeStore.findByIdsWithFilter(registryNodeIds, null);

        PackageModel packageRN = null;
        for (RegistryNodeModel parentRegistryNode : parentRegistryNodes) {
            if (parentRegistryNode instanceof PackageModel) {
                packageRN = (PackageModel) parentRegistryNode;
                break;
            }
        }
        if (packageRN != null && packageRN.getSystem() != null) {
            Hibernate.initialize(packageRN.getSystem());
        }
        return packageRN;
    }

    @Override
    @Transactional
    public void save(PackageModel packageRN) {
        save(packageRN, VersionUtils.convertToNumber("0.0.1"), VersionUtils.convertToNumber("0.0.1"));
    }

    @Override
    @Transactional
    public void save(PackageModel packageRN, Integer version) {
        save(packageRN, version, null);
    }

    @Transactional
    public void save(PackageModel packageRN, Integer version, Integer databaseVersion, boolean createAutoDescription) {
        SystemModel systemModel = packageRN.getSystem();
        if (systemModel != null) {
            systemModel = systemStore.findById(systemModel.getId());
        }
        if (packageRN.getId() == null) {
            if (version != null) {
                packageRN.setVersion(version);
            }
            if (databaseVersion != null) {
                packageRN.setDatabaseVersion(databaseVersion);
            }
            packageRN.setSystem(null);
            super.save(packageRN, createAutoDescription);
            if (systemModel != null) {
                this.associate(packageRN, systemModel);
            }
            if (ConfigContainer.isAppInstalled()) {
                cacheProcessor.addNewPackage(packageRN.getLookup());
            }
        } else {
            PackageModel storedPackage = findWithSystemById(packageRN.getId());
            SystemModel storedSystem = storedPackage.getSystem();
            storedPackage.setName(packageRN.getName());
            storedPackage.setDescription(packageRN.getDescription());
            if (version != null) {
                storedPackage.setVersion(version);
            }
            if (databaseVersion != null) {
                storedPackage.setDatabaseVersion(databaseVersion);
            }

	        storedPackage.setDatabase(packageRN.getDatabase());
            storedPackage.setUrlPath(packageRN.getUrlPath());
            storedPackage.setPrefix(packageRN.getPrefix());
            packageRN.setVersion(version);
            packageRN.setChildCount(storedPackage.getChildCount());
            String oldLookup = storedPackage.getLookup();
            if (storedSystem != null && systemModel != null && !storedSystem.getId().equals(systemModel.getId())) {
                storedPackage.setSystem(null);
            }
            super.save(storedPackage, createAutoDescription);
            if (!oldLookup.equals(packageRN.getLookup())) {
                cacheProcessor.updatePackage(oldLookup, packageRN.getLookup());
            }

            if (storedSystem != null) {
                if (systemModel == null) {
                    removeAssociation(storedPackage, storedSystem);
                } else if (!storedSystem.getId().equals(systemModel.getId())) {
                    associate(storedPackage, systemModel);
                }
            } else if (systemModel != null) {
                associate(storedPackage, systemModel);
            }

            update("Package.updateParentPath", "parentPath", packageRN.getUrlPath(), "packageLookupPattern", storedPackage.getLookup() + ".%");
        }
    }

    @Override
    @Transactional
    public void save(PackageModel packageRN, Integer version, Integer databaseVersion) {
        save(packageRN, version, databaseVersion, true);
    }

    @Override
    @Transactional
    public void delete(PackageModel packageRN) {
        Long packageId = packageRN.getId();


	    OPFEngine.FileStoreService.deleteDirectory(OpenFlame.FILESTORE_BASE, helper.getVersion(), String.valueOf(packageId));

        super.saveOrUpdate(packageRN);
        super.delete(packageRN);
        cacheProcessor.removePackage(packageRN.getLookup());
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<PackageModel> findAllInstalledPackagesWithFilter(final Long systemId, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                criteria.add(Restrictions.eq("system.id", systemId));
                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void associate(PackageModel packageRN, SystemModel system) {
        lock(packageRN);
        packageRN.setSystem(system);
//        super.saveOrUpdate(packageRN);
        super.save(packageRN);

	    packageRN.setServerName(system.getServerName());
	    packageRN.setPort(system.getPort());

        List<RegistryNodeModel> registryNodes = registryNodeStore.findAllByLikeLookupPrefix(packageRN.getLookup() + ".");
        List<RegistryNodeModel> navigableNodes = new ArrayList<RegistryNodeModel>();
        for (RegistryNodeModel registryNode : registryNodes) {
            if (registryNode instanceof INavigable) {
                ((INavigable) registryNode).setServerName(system.getServerName());
                ((INavigable) registryNode).setPort(system.getPort());
                navigableNodes.add(registryNode);
            }
        }
        registryNodeStore.saveOrUpdateAll(navigableNodes);

        getCacheService().removeCacheByRegistryNode(packageRN);
        getCacheService().removeCacheByRegistryNode(system);
    }

    @Override
    @Transactional
    public void removeAssociation(PackageModel packageRN, SystemModel system) {
        lock(packageRN);
        packageRN.setSystem(null);
        packageRN.setServerName(null);
        packageRN.setPort(null);
        packageRN.setDatabase(null);
        super.save(packageRN);

        List<RegistryNodeModel> registryNodes = registryNodeStore.findAllByLikeLookupPrefix(packageRN.getLookup() + ".");
        List<RegistryNodeModel> navigableNodes = new ArrayList<RegistryNodeModel>();
        for (RegistryNodeModel registryNode : registryNodes) {
            if (registryNode instanceof INavigable) {
                ((INavigable) registryNode).setServerName(null);
                ((INavigable) registryNode).setPort(null);
                navigableNodes.add(registryNode);
            }
        }
        registryNodeStore.saveOrUpdateAll(navigableNodes);

        getCacheService().removeCacheByRegistryNode(packageRN);
        getCacheService().removeCacheByRegistryNode(system);
    }

    @Override
    @Transactional
    public void removeAllAssociations(SystemModel system) {
        List<PackageModel> associatedPackages = findAllInstalledPackagesWithFilter(system.getId(), null);
        for (PackageModel associatedPackage : associatedPackages) {
            removeAssociation(associatedPackage, system);
        }
    }

    @Override
    @Transactional
    public PackageModel updatePackageVersion(Long packageId, Integer version) {
        PackageModel packageToUpdate = null;
        if (packageId != null && version != null) {
            packageToUpdate = getHibernateTemplate().load(PackageModel.class, packageId);
            if (packageToUpdate != null) {
                packageToUpdate.setVersion(version);
                getHibernateTemplate().saveOrUpdate(packageToUpdate);
            }
        }
        return packageToUpdate;
    }

	@Override
	@Transactional(readOnly = true)
	public PackageModel findPackage(String lookup) {
		String[] split = lookup.split("(?=\\.)");
		String parent = "";
		for (int i = 0; i < Math.min(split.length, 3); i++) {
			parent += split[i];
		}
		return findByLookup(parent);
	}

}
