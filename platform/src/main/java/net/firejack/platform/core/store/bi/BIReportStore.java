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

package net.firejack.platform.core.store.bi;

import net.firejack.platform.core.model.registry.RESTMethod;
import net.firejack.platform.core.model.registry.bi.BIReportFieldModel;
import net.firejack.platform.core.model.registry.bi.BIReportModel;
import net.firejack.platform.core.model.registry.bi.BIReportUserModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.store.registry.IActionStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.RegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;

/**
 * Class provides access to the data for the registry nodes of the database type
 */
@Component("bireportStore")
public class BIReportStore extends RegistryNodeStore<BIReportModel> implements IBIReportStore {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IActionStore actionStore;

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(BIReportModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public BIReportModel findByLookup(final String lookup) {
        return getHibernateTemplate().execute(new HibernateCallback<BIReportModel>() {
            public BIReportModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("lookup", lookup));
                criteria.setFetchMode("fields", FetchMode.JOIN);
                return (BIReportModel) criteria.uniqueResult();
            }
        });
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long id) {
        List<BIReportModel> models = findAllByParentIdWithFilter(id, null);
        super.deleteAll(models);

    }

    @Override
    @Transactional
    public void saveForGenerator(BIReportModel report) {
        save(report);
        actionStore.createWithPermissionByEntity(report, RESTMethod.READ);
    }

    @Override
    @Transactional
    public void save(BIReportModel report) {
        List<BIReportFieldModel> fields = report.getFields();
        for (BIReportFieldModel field : fields) {
            field.setReport(report);
        }

        EntityModel entityModel = entityStore.findById(report.getParent().getId());

        report.setServerName(entityModel.getServerName());
        report.setPort(entityModel.getPort());
        report.setParentPath(entityModel.getParentPath());
        report.setUrlPath(entityModel.getUrlPath() + "/" + StringUtils.normalize(report.getName()));
        report.setProtocol(entityModel.getProtocol());
        report.setStatus(entityModel.getStatus());

        super.save(report);
    }

    @Transactional(readOnly = true)
    public List<BIReportModel> findAllByLikeLookupPrefix(final String lookupPrefix) {
        List<BIReportModel> models = super.findAllByLikeLookupPrefix(lookupPrefix);
        for (BIReportModel model : models) {
            List<BIReportFieldModel> fields = model.getFields();
            Hibernate.initialize(fields);
            if (fields != null) {
                for (BIReportFieldModel field : fields) {
                    Hibernate.initialize(field.getEntity());
                    Hibernate.initialize(field.getField());
                }
            }
        }
        return models;
    }

    @Override
    @Transactional(readOnly = true)
    public BIReportModel findById(final Long biReportId) {
        BIReportModel biReportModel = getHibernateTemplate().execute(new HibernateCallback<BIReportModel>() {
            @Override
            public BIReportModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(BIReportModel.class);
                criteria.createCriteria("fields", "reportFields", Criteria.LEFT_JOIN);
                criteria.add(Restrictions.eq("id", biReportId));
//                criteria.setFetchMode("entity", FetchMode.JOIN);
                return (BIReportModel) criteria.uniqueResult();
            }
        });
//        Hibernate.initialize(biReportModel.getFields());
        return biReportModel;
    }
}
