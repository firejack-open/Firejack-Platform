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

import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.store.registry.helper.SecuredRecordPathHelper;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SecuredRecordStore extends BaseStore<SecuredRecordModel, Long> implements ISecuredRecordStore {

    @Autowired
    @Qualifier("securedRecordPathHelper")
    private SecuredRecordPathHelper securedRecordPathHelper;

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;

    /***/
    @PostConstruct
    public void init() {
        setClazz(SecuredRecordModel.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public SecuredRecordModel findByExternalIdAndRegistryNode(final Long externalNumberId, final RegistryNodeModel registryNode) {
        SecuredRecordModel sr;
        if (externalNumberId == null || registryNode == null) {
            sr = null;
        } else {
            Criteria criteria = getSession().createCriteria(getClazz());
            criteria.setFetchMode("registryNode", FetchMode.JOIN);
            criteria.setFetchMode("parentSecuredRecords", FetchMode.JOIN);
            criteria.add(Restrictions.eq("externalNumberId", externalNumberId));
            criteria.add(Restrictions.eq("registryNode.id", registryNode.getId()));
            List<SecuredRecordModel> srList = (List<SecuredRecordModel>) criteria.list();
            sr = srList.isEmpty() ? null : srList.get(0);
        }
        return sr;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public SecuredRecordModel findByExternalStringIdAndRegistryNode(String externalStringId, RegistryNodeModel registryNode) {
        SecuredRecordModel sr;
        if (StringUtils.isBlank(externalStringId) || registryNode == null) {
            sr = null;
        } else {
            Criteria criteria = getSession().createCriteria(getClazz());
            criteria.setFetchMode("registryNode", FetchMode.JOIN);
            criteria.setFetchMode("parentSecuredRecords", FetchMode.JOIN);
            criteria.add(Restrictions.eq("externalStringId", externalStringId));
            criteria.add(Restrictions.eq("registryNode.id", registryNode.getId()));
            List<SecuredRecordModel> srList = (List<SecuredRecordModel>) criteria.list();
            sr = srList.isEmpty() ? null : srList.get(0);
        }
        return sr;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<SecuredRecordModel> findChildrenByParentId(final Long parentId) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<SecuredRecordModel>>() {
            @Override
            public List<SecuredRecordModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.createAlias("parentSecuredRecords", "parent");
                criteria.add(Restrictions.eq("parent.id", parentId));
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public SecuredRecordModel findByIdAndType(final Long id, final String typeLookup) {
        return getHibernateTemplate().execute(new HibernateCallback<SecuredRecordModel>() {
            @Override
            public SecuredRecordModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("externalNumberId", id));
                criteria.createAlias("registryNode", "rn");
                criteria.add(Restrictions.eq("rn.lookup", typeLookup));
                return (SecuredRecordModel) criteria.uniqueResult();
            }
        });
    }

//    @Override
//    @SuppressWarnings("unchecked")
//    @Transactional(readOnly = true)
//    public List<SecuredRecordModel> findParentByParentId(final Long parentId) {
//        return getHibernateTemplate().executeFind(new HibernateCallback<List<SecuredRecordModel>>() {
//            @Override
//            public List<SecuredRecordModel> doInHibernate(Session session) throws HibernateException, SQLException {
//                Criteria criteria = session.createCriteria(getClazz());
//                criteria. createAlias("parentSecuredRecords", "parent");
//                criteria.add(Restrictions.eq("parent.id", parentId));
//                return criteria.list();
//            }
//        });
//    }

    @Override
    @Transactional
    public void saveOrUpdateRecursive(SecuredRecordModel securedRecord) {
        super.saveOrUpdate(securedRecord);
        List<SecuredRecordModel> securedRecordChild = findChildrenByParentId(securedRecord.getId());
        for (SecuredRecordModel securedRecordChildren : securedRecordChild) {
            String paths = securedRecordPathHelper.addIdToPaths(securedRecord.getId(), securedRecord.getPaths());
            securedRecordChildren.setPaths(paths);
            saveOrUpdateRecursive(securedRecordChildren);
        }
        SecuredRecordNode srNode = AuthorizationVOFactory.convert(securedRecord);
        cacheProcessor.addSecuredRecord(srNode);
    }

    @Override
    @Transactional
    public void saveOrUpdateRecursive(List<SecuredRecordModel> securedRecordList) {
        if (securedRecordList != null) {
            for (SecuredRecordModel securedRecord : securedRecordList) {
                super.saveOrUpdate(securedRecord);
                List<SecuredRecordModel> securedRecordChild = findChildrenByParentId(securedRecord.getId());
                for (SecuredRecordModel securedRecordChildren : securedRecordChild) {
                    String paths = securedRecordPathHelper.addIdToPaths(securedRecord.getId(), securedRecord.getPaths());
                    securedRecordChildren.setPaths(paths);
                    saveOrUpdateRecursive(securedRecordChildren);
                }
            }
        }
        Map<Long, SecuredRecordNode> srMap = AuthorizationVOFactory.convertSecuredRecords(securedRecordList);
        if (srMap != null && !srMap.isEmpty()) {
            List<SecuredRecordNode> srList = new ArrayList<SecuredRecordNode>(srMap.values());
            cacheProcessor.addSecuredRecords(srList);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<SecuredRecordModel> findAllWithLoadedRegistryNode() {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<SecuredRecordModel>>() {
            @Override
            public List<SecuredRecordModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.setFetchMode("registryNode", FetchMode.JOIN);
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void delete(SecuredRecordModel securedRecord) {
        getHibernateTemplate().lock(securedRecord, LockMode.NONE);
        deleteRecursive(securedRecord, false);
        cacheProcessor.deleteSecuredRecord(securedRecord.getId());
    }

    @Override
    @Transactional
    public List<SecuredRecordModel> deleteAllByIdAndType(List<Tuple<Long, String>> idTypeData) {
        List<SecuredRecordModel> result = new ArrayList<SecuredRecordModel>();
        if (idTypeData != null && !idTypeData.isEmpty()) {
            for (Tuple<Long, String> idType : idTypeData) {
                if (idType.getKey() != null && StringUtils.isNotBlank(idType.getValue())) {
                    SecuredRecordModel securedRecordModel = findByIdAndType(idType.getKey(), idType.getValue());
                    if (securedRecordModel != null) {
                        result.add(securedRecordModel);
                    }
                }
            }
        }
        deleteAll(result);
        return result;
    }

    @Override
    @Transactional
    public void deleteAll(List<SecuredRecordModel> srList) {
        if (srList != null && !srList.isEmpty()) {
            List<Long> idList = new ArrayList<Long>();
            for (SecuredRecordModel sr : srList) {
                deleteRecursive(sr, false);
                idList.add(sr.getId());
            }
            cacheProcessor.deleteSecuredRecords(idList);
        }
    }

    private void deleteRecursive(SecuredRecordModel securedRecord, boolean multiParent) {
        List<SecuredRecordModel> securedRecordChildren = findChildrenByParentId(securedRecord.getId());
        if (!securedRecordChildren.isEmpty()) {
            for (SecuredRecordModel securedRecordChild : securedRecordChildren) {
                List<SecuredRecordModel> securedRecordParents = securedRecordChild.getParentSecuredRecords();
                boolean isMultiParent = securedRecordParents.size() > 1;
                if (isMultiParent) {
                    securedRecordChild.getParentSecuredRecords().remove(securedRecord);
                    String paths = securedRecordPathHelper.removeIdFromPaths(securedRecordChild.getPaths(), securedRecord);
                    securedRecordChild.setPaths(paths);
                    saveOrUpdateRecursive(securedRecordChild);
                } else {
                    deleteRecursive(securedRecordChild, isMultiParent);
                }
            }
        }
        if (!multiParent) {
            super.delete(securedRecord);
        }
    }
}
