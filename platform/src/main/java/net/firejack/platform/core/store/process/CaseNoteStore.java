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

import net.firejack.platform.core.model.registry.process.CaseNoteModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.store.BaseStore;
import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component("caseNoteStore")
public class CaseNoteStore extends BaseStore<CaseNoteModel, Long> implements ICaseNoteStore {

    @Autowired
    private ICaseStore caseStore;

    @Autowired
    private ITaskStore taskStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(CaseNoteModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseNoteModel findById(Long aLong) {
        CaseNoteModel caseNoteModel = super.findById(aLong);
        if (caseNoteModel != null) {
            Hibernate.initialize(caseNoteModel.getProcessCase());
            Hibernate.initialize(caseNoteModel.getUser());
        }
        return caseNoteModel;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseNoteModel> findByTaskIdAndSearchTerm(final Long taskId, String term) {
        TaskModel taskModel = taskStore.findById(taskId);
        return findByCaseIdAndSearchTerm(taskModel.getCase().getId(), term);
//        final List<Criterion> criterions = termCriterion(term);
//        List<CaseNoteModel> caseNotes = getHibernateTemplate().execute(new HibernateCallback<List<CaseNoteModel>>() {
//            @Override
//            public List<CaseNoteModel> doInHibernate(Session session) throws HibernateException, SQLException {
//                Criteria criteria = session.createCriteria(CaseNoteModel.class);
//                for (Criterion restrictions : criterions) {
//                    criteria.add(restrictions);
//                }
//
//                Criterion taskCriterion = Restrictions.eq("task.id", taskId);
//                criteria.add(taskCriterion);
//
//                criteria.setFetchMode("user", FetchMode.JOIN);
//
//                return criteria.list();
//            }
//        });
//        return caseNotes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseNoteModel> findByCaseIdAndSearchTerm(final Long caseId, String term) {
        final List<Criterion> criterions = termCriterion(term);
        List<CaseNoteModel> caseNotes = getHibernateTemplate().execute(new HibernateCallback<List<CaseNoteModel>>() {
            @Override
            public List<CaseNoteModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseNoteModel.class);
                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }

                criteria.createAlias("processCase", "case");
                Criterion taskCriterion = Restrictions.eq("case.id", caseId);
                criteria.add(taskCriterion);

                criteria.setFetchMode("user", FetchMode.JOIN);

                return criteria.list();
            }
        });
        return caseNotes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseNoteModel> findByCaseObjectAndSearchTerm(final Long entityId, final String entityType, String term) {
        final List<Criterion> criterions = termCriterion(term);
        List<CaseNoteModel> caseNotes = getHibernateTemplate().execute(new HibernateCallback<List<CaseNoteModel>>() {
            @Override
            public List<CaseNoteModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseNoteModel.class);
                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }
                criteria.createAlias("processCase", "case");
                criteria.createAlias("case.caseObjectModels", "caseObject");
                Criterion entityIdCriterion = Restrictions.eq("caseObject.entityId", entityId);
                Criterion entityTypeCriterion = Restrictions.eq("caseObject.entityType", entityType);
                criteria.add(entityIdCriterion);
                criteria.add(entityTypeCriterion);

                criteria.setFetchMode("user", FetchMode.JOIN);

                return criteria.list();
            }
        });
        return caseNotes;
    }

    @Override
    @Transactional
    public void saveOrUpdate(CaseNoteModel caseNote) {
        if (caseNote.getTaskModel() != null && caseNote.getTaskModel().getId() != null) {
            TaskModel taskModel = taskStore.findById(caseNote.getTaskModel().getId());
            caseNote.setProcessCase(taskModel.getCase());
        }
        super.saveOrUpdate(caseNote);
        getHibernateTemplate().refresh(caseNote);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Criterion> termCriterion(String term) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        if (!StringUtils.isEmpty(term)) {
            Criterion nameCriterion = Restrictions.like("text", "%" + term + "%");
            criterions.add(nameCriterion);
        }
        return criterions;
    }

}
