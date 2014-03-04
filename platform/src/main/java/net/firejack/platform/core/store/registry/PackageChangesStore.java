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

import net.firejack.platform.core.model.registry.domain.PackageChangesModel;
import net.firejack.platform.core.store.BaseStore;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Component
public class PackageChangesStore extends BaseStore<PackageChangesModel, Long> implements IPackageChangesStore {

    @PostConstruct
    public void init() {
        setClazz(PackageChangesModel.class);
    }

    @Transactional(readOnly = true)
    public Long countEntityChanges(final Long entityId) {
        return getHibernateTemplate().execute(new HibernateCallback<Long>() {
            public Long doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("entity.id", entityId));
                criteria.setProjection(Projections.count("id"));
                return (Long) criteria.uniqueResult();
            }
        });
    }

    @Transactional(readOnly = true)
    public List<PackageChangesModel> findAllPackageChange(final String packageLookup) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<PackageChangesModel>>() {
            public List<PackageChangesModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.createAlias("packageModel","pm");
                criteria.add(Restrictions.eq("pm.lookup", packageLookup));
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageChangesModel> findAllLastChanges(Long timestamp) {
        final Date date = new Date();
        date.setTime(timestamp);
        return getHibernateTemplate().executeFind(new HibernateCallback<List<PackageChangesModel>>() {
                    public List<PackageChangesModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.gt("created", date));
                criteria.setFetchMode("entity", FetchMode.JOIN);
                criteria.setFetchMode("packageModel", FetchMode.JOIN);
                return criteria.list();
            }
        });
    }

    @Transactional
    public void cleanPackageChange(Long packageId) {
        update("PackageChangesModel.cleanAllPackageChange", "packageId", packageId);
    }
}
