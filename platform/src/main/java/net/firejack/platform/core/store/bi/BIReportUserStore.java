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

import net.firejack.platform.core.model.registry.bi.BIReportUserFieldModel;
import net.firejack.platform.core.model.registry.bi.BIReportUserModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;

/**
 * Class provides access to the data for the registry nodes of the database type
 */
@Component
public class BIReportUserStore extends BaseStore<BIReportUserModel, Long> implements IBIReportUserStore {


    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(BIReportUserModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public BIReportUserModel findById(final Long biReportUserId) {
        BIReportUserModel biReportUserModel = getHibernateTemplate().execute(new HibernateCallback<BIReportUserModel>() {
            @Override
            public BIReportUserModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(BIReportUserModel.class);
                criteria.add(Restrictions.eq("id", biReportUserId));
                criteria.createAlias("report", "biReport");
                criteria.createAlias("biReport.fields", "rfield");
                criteria.createAlias("fields", "biRUfield");
                criteria.createAlias("biRUfield.field", "biRfield");
                criteria.createAlias("biRfield.field", "field");
                criteria.createAlias("biRfield.entity", "entity");
                return (BIReportUserModel) criteria.uniqueResult();
            }
        });
        if (biReportUserModel != null) {
            Hibernate.initialize(biReportUserModel.getFields());
            Hibernate.initialize(biReportUserModel.getReport().getFields());
        }
        return biReportUserModel;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BIReportUserModel> loadReportUserByPackage(final String packageLookup, final Long userId) {
        return getHibernateTemplate().execute(new HibernateCallback<List<BIReportUserModel>>() {
            public List<BIReportUserModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.createAlias("report", "report");
                criteria.createAlias("user", "user");
                criteria.add(Restrictions.like("report.lookup", packageLookup, MatchMode.START));
                criteria.add(Restrictions.eq("user.id", userId));
                return (List<BIReportUserModel>) criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void saveOrUpdate(BIReportUserModel report) {
        if (report.getId() != null) {
            BIReportUserModel existingModel = getHibernateTemplate().get(clazz, report.getId());

            existingModel.setTitle(report.getTitle());

            existingModel.getFields().clear();
            List<BIReportUserFieldModel> fields = report.getFields();
            for (BIReportUserFieldModel field : fields) {
                field.setUserReport(existingModel);
            }
            existingModel.getFields().addAll(fields);

            existingModel.setFilter(report.getFilter());
            super.saveOrUpdate(existingModel);
        } else {
            Long userId = OPFContext.getContext().getPrincipal().getUserInfoProvider().getId();
            report.setUser(new UserModel(userId));

            List<BIReportUserFieldModel> fields = report.getFields();
            for (BIReportUserFieldModel field : fields) {
                field.setUserReport(report);
            }
            super.saveOrUpdate(report);
        }

    }
}
