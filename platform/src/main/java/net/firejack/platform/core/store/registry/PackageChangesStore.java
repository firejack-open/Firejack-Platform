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
