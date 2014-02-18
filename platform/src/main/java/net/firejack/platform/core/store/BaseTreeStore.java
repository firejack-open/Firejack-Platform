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

package net.firejack.platform.core.store;

import net.firejack.platform.core.model.ITreeStore;
import net.firejack.platform.core.model.TreeEntityModel;
import net.firejack.platform.core.store.version.UIDStore;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;



public class BaseTreeStore<O extends TreeEntityModel, ID extends Serializable> extends UIDStore<O, ID> implements ITreeStore<O, ID> {

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<O> findEntriesByParentId(final TreeEntityModel parent, Integer offset, Integer limit) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<O>>() {
            @Override
            public List<O> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.setFetchMode("uid", FetchMode.JOIN);
                criteria.add(Restrictions.eq("parent.id", parent.getId()));
	            return (List<O>) criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<O> findAllEntries(Integer offset, Integer limit) {
        O example = instantiate();
        return findByExample(offset, limit, example);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Object[]> findAllIdAndParentId() {
        final O example = instantiate();
        return getHibernateTemplate().executeFind(new HibernateCallback<List<Object[]>>() {
            @Override
            public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                ProjectionList proList = Projections.projectionList();
                proList.add(Projections.property("id"));
                proList.add(Projections.property("parent.id"));
                criteria.setProjection(proList);
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public void changeParent(ID id, O newParent, O oldParent) {
        O o = findById(id);
        o.setParent(newParent);
        saveOrUpdate(o);

        if (newParent != null) {
            newParent.setChildCount(newParent.getChildCount() + 1);
            saveOrUpdate(newParent);
        }

        if (oldParent != null) {
            oldParent.setChildCount(oldParent.getChildCount() - 1);
            saveOrUpdate(oldParent);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public void save(O o) {
        if (o.getId() == null) {
            o.setChildCount(0);
        }
	    if (o.getId() == null) {
		    saveOrUpdate(o);
	    } else {
		    merge(o);
        }
        if (o.getParent() != null) {
            Integer childCount = countChildByParentId(o.getParent().getId());
            o.getParent().setChildCount(childCount);
//            merge((O) o.getParent());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countChildByParentId(Long parentId) {
        return count("RegistryNode.countChildByParentId", false, "parentId", parentId);
    }

    public void findCollectionParentIds(List<Long> collectionIds, Long id, List<Object[]> collectionArrayIds) {
        if (id == null) {
            return;
        }
        collectionIds.add(id);
        for (Object[] collectionArrayId : collectionArrayIds) {
            if (id.equals(collectionArrayId[0])) {
                findCollectionParentIds(collectionIds, (Long) collectionArrayId[1], collectionArrayIds);
            }
        }
    }

    public void findCollectionChildrenIds(List<Long> collectionIds, Long id, List<Object[]> collectionArrayIds) {
        for (Object[] collectionArrayId : collectionArrayIds) {
            if (id.equals(collectionArrayId[1])) {
                collectionIds.add((Long) collectionArrayId[0]);
                findCollectionChildrenIds(collectionIds, (Long) collectionArrayId[0], collectionArrayIds);
            }
        }
    }
}
