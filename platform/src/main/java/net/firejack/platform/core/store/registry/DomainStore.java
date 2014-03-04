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
