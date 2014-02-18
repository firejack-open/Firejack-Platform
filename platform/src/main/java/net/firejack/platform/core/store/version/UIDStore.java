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

package net.firejack.platform.core.store.version;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.UIDModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.model.helper.FileHelper;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UIDStore<E extends UIDModel, ID extends Serializable> extends BaseStore<E, ID> implements IUIDStore<E, ID> {

    @Autowired
    protected FileHelper helper;

    @Override
    @Transactional(readOnly = true)
    public UID uidById(Long id) {
        return getHibernateTemplate().get(UID.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public UID uidById(String uid) {
        Criteria criteria = getSession().createCriteria(UID.class);
        criteria.add(Restrictions.eq("uid", uid));
        return (UID) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public E findByUIDId(Long uidId) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.eq("uid.id", uidId);
        criterions.add(registryNodeIdCriterion);
        return findByCriteria(criterions, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public E findByUID(String uid) {
        return findByUIDLocal(uid);
    }

    @Override
    @Transactional
    public void saveOrUpdate(E entity) {
        BaseEntityModel uid = createUID(entity);
        if (uid != null) {
            getHibernateTemplate().save(uid);
        }
        super.saveOrUpdate(entity);
    }

    @Override
    @Transactional
    public void saveOrUpdateAll(List<E> entities) {
        List<BaseEntityModel> uid = createUID(entities);

        if (!uid.isEmpty()) {
            getHibernateTemplate().saveOrUpdateAll(uid);
        }
        super.saveOrUpdateAll(entities);
    }

    /**
     * @param entity
     * @return
     */
    public BaseEntityModel createUID(E entity) {
        UID uid = null;
        if (entity.getId() == null) {
            uid = entity.getUid();
            if (uid == null) {
                uid = new UID(SecurityHelper.generateSecureId());
                entity.setUid(uid);
            } else if (ConfigContainer.isAppInstalled()) {
                UIDModel uidModel = findByUIDId(uid.getId());
                //todo{start}: temporary solution for diff merge
                if (uidModel == null) {
                    UID foundUid = uidById(uid.getUid());
                    if (foundUid != null) {
                        entity.setUid(foundUid);
	                    uid = null;
                    }
                }
                //todo[end]: temporary solution for diff merge

                if (uidModel != null) {
                    entity.setUid(uidModel.getUid());
                    uid = null;
                }
            }
        }
        return uid;
    }

    /**
     * @param entities
     * @return
     */
    public List<BaseEntityModel> createUID(List<E> entities) {
        List<BaseEntityModel> list = new ArrayList<BaseEntityModel>();
        for (E entity : entities) {
            BaseEntityModel uid = createUID(entity);
            if (uid != null) {
                list.add(uid);
            }
        }
        return list;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public E deleteByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        E model = findByUIDLocal(uid);
        if (model != null) {
            delete(model);
        }
        return model;
    }

    private E findByUIDLocal(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        try {
            Criteria criteria = getSession().createCriteria(getClazz());
            criteria.createAlias("uid", "uid");
            criteria.add(Restrictions.eq("uid.uid", uid));
            return (E) criteria.uniqueResult();
        } catch (HibernateException e) {
            logger.error("Query did not return a unique result by UID:[" + uid + "]");
            throw e;
        }
    }
}
