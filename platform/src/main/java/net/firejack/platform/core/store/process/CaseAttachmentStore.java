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
