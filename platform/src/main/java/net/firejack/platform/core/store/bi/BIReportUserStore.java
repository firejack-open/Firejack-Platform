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
