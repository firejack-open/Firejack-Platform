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

package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.CaseAttachmentModel;
import net.firejack.platform.core.store.BaseStore;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component("caseAttachmentStore")
public class CaseAttachmentStore extends BaseStore<CaseAttachmentModel, Long> implements ICaseAttachmentStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(CaseAttachmentModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseAttachmentModel> findByTaskIdAndSearchTerm(final Long taskId, String term) {
        final List<Criterion> criterions = termCriterion(term);
        List<CaseAttachmentModel> caseAttachments = getHibernateTemplate().execute(new HibernateCallback<List<CaseAttachmentModel>>() {
            @Override
            public List<CaseAttachmentModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseAttachmentModel.class);
                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }

                Criterion taskCriterion = Restrictions.eq("task.id", taskId);
                criteria.add(taskCriterion);

                return criteria.list();
            }
        });
        return caseAttachments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseAttachmentModel> findByCaseIdAndSearchTerm(final Long caseId, String term) {
        final List<Criterion> criterions = termCriterion(term);
        List<CaseAttachmentModel> caseAttachments = getHibernateTemplate().execute(new HibernateCallback<List<CaseAttachmentModel>>() {
            @Override
            public List<CaseAttachmentModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseAttachmentModel.class);
                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }

                criteria.createAlias("processCase", "case");
                Criterion taskCriterion = Restrictions.eq("case.id", caseId);
                criteria.add(taskCriterion);

                return criteria.list();
            }
        });
        return caseAttachments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseAttachmentModel> findByCaseObjectAndSearchTerm(final Long entityId, final String entityType, String term) {
        final List<Criterion> criterions = termCriterion(term);
        List<CaseAttachmentModel> caseAttachments = getHibernateTemplate().execute(new HibernateCallback<List<CaseAttachmentModel>>() {
            @Override
            public List<CaseAttachmentModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseAttachmentModel.class);
                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }
                criteria.createAlias("processCase", "case");
                criteria.createAlias("case.caseObjects", "caseObject");
                Criterion entityIdCriterion = Restrictions.eq("caseObject.entityId", entityId);
                Criterion entityTypeCriterion = Restrictions.eq("caseObject.entityType", entityType);
                criteria.add(entityIdCriterion);
                criteria.add(entityTypeCriterion);

                return criteria.list();
            }
        });
        return caseAttachments;
    }

//    @Override
//    @Transactional
//    public void saveOrUpdate(CaseAttachmentModel caseAttachment) {
//        if (caseAttachment.getTask() != null && caseAttachment.getTask().getId() != null) {
//            Task task = taskStore.findById(caseAttachment.getTask().getId());
//            caseAttachment.setProcessCase(task.getCase());
//        }
//        super.saveOrUpdate(caseAttachment);
//    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Criterion> termCriterion(String term) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        if (!StringUtils.isEmpty(term)) {
            Criterion nameCriterion = Restrictions.like("name", "%" + term + "%");
            criterions.add(nameCriterion);
        }
        return criterions;
    }

}
