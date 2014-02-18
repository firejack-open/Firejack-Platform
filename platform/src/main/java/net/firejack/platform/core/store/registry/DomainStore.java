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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.config.translate.sql.DefaultSqlNameResolver;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.IPrefixContainer;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("domainStore")
public class DomainStore extends RegistryNodeStore<DomainModel> implements IDomainStore, IResourceAccessFieldsStore<DomainModel> {

    @Autowired
    @Qualifier("entityStore")
    private IEntityStore entityStore;
    @Autowired
    private IPackageStore packageStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(DomainModel.class);
    }

    @Override
    public void updateResourceAccessFields(DomainModel domain) {
        if (domain.getId() != null) {
            List<EntityModel> entityModels = entityStore.findChildrenByParentId(domain.getId(), null);
            for (EntityModel entityModel : entityModels) {
                entityStore.updateResourceAccessFields(entityModel);
            }

            List<DomainModel> domains = findChildrenByParentId(domain.getId(), null);
            for (DomainModel domainModel : domains) {
                updateResourceAccessFields(domainModel);
            }
        }
    }

    private String generateEntityTableName(EntityModel entity, DomainModel updatedDomain) {
        String tableName = StringUtils.changeWhiteSpacesWithSymbol(entity.getName(), DefaultSqlNameResolver.TOKEN_UNDERSCORE).toLowerCase();
        List<RegistryNodeModel> parents = ((RegistryNodeStore) getRegistryNodeStore()).findAllParentsForEntityLookup(entity.getLookup());
        Collections.reverse(parents);
        for (RegistryNodeModel parent : parents) {
            parent = lazyInitializeIfNeed(parent);
            String tablePrefix;
            if (updatedDomain.getId().equals(parent.getId())) {
                tablePrefix = updatedDomain.getPrefix();
            } else {
                tablePrefix = ((IPrefixContainer) parent).getPrefix();
            }
            if (StringUtils.isNotBlank(tablePrefix)) {
                tableName = tablePrefix + DefaultSqlNameResolver.TOKEN_UNDERSCORE + tableName;
            }
        }
        return tableName;
    }

    @Override
    @Transactional(readOnly = true)
    public DomainModel findWithDatabaseById(Long id) {
        DomainModel domainModel = super.findById(id);
        Hibernate.initialize(domainModel.getDatabase());
        return domainModel;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, DatabaseModel> findAllWithDataSourcesByPackageUID(String packageUID) {
        Criteria criteria = getSession().createCriteria(PackageModel.class);
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", packageUID));
        PackageModel packageModel = (PackageModel) criteria.uniqueResult();
        return getDatabaseAssociations(packageModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, DatabaseModel> findAllWithDataSourcesByPackageLookup(String packageLookup) {
        PackageModel packageModel = packageStore.findByLookup(packageLookup);
        return getDatabaseAssociations(packageModel);
    }

    private Map<String, DatabaseModel> getDatabaseAssociations(PackageModel packageModel) {
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
                    "Domain.loadDatabaseAssociations", "lookupPattern", packageModel.getLookup() + ".%");
            if (dbAssociations != null) {
                for (Object[] dbAssociation : dbAssociations) {
                    String domainLookup = (String) dbAssociation[0];
                    DatabaseModel databaseModel = (DatabaseModel) dbAssociation[1];
                    databaseAssociations.put(domainLookup, databaseModel);
                }
            }
        }
        return databaseAssociations;
    }

    @Override
    @Transactional(readOnly = true)
    public String findXADomains(String lookup) {
        List<String> list = super.findByQuery(null, null, "Domain.findXADomains", "lookupPrefix", lookup + ".%");
        return StringUtils.join(list, ';');
    }
}
