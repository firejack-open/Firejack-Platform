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
package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.CaseObjectModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.utils.Tuple;
import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class provides access to case object data
 */
@Component("caseObjectStore")
public class CaseObjectStore extends BaseStore<CaseObjectModel, Long> implements ICaseObjectStore {

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(CaseObjectModel.class);
    }

    /**
     * Finds by ID
     *
     * @param id - case object ID
     * @return found case object
     */
    @Override
    @Transactional(readOnly = true)
    public CaseObjectModel findById(Long id) {
        CaseObjectModel caseObjectModel = super.findById(id);
        if (caseObjectModel != null) {
            Hibernate.initialize(caseObjectModel.getCase());
            Hibernate.initialize(caseObjectModel.getTask());
            Hibernate.initialize(caseObjectModel.getStatus());
            Hibernate.initialize(caseObjectModel.getCreatedBy());
            Hibernate.initialize(caseObjectModel.getUpdatedBy());
            if (caseObjectModel.getCreatedBy() != null) {
                caseObjectModel.getCreatedBy().setUserRoles(null);
            }
            if (caseObjectModel.getUpdatedBy() != null) {
                caseObjectModel.getUpdatedBy().setUserRoles(null);
            }
        }
        return caseObjectModel;
    }

    /**
     * @param processLookup - lookup of the process to search by
     * @param entityId      - ID of the entity
     * @param entityType    - type of the entity
     * @return
     * @see ICaseObjectStore#findCaseByProcessAndEntity(java.lang.String, java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public CaseModel findCaseByProcessAndEntity(String processLookup, Long entityId, String entityType) {
        final List<Criterion> criterions = createCriterions(processLookup, entityId, entityType);

        List<CaseObjectModel> caseObjectModels = getHibernateTemplate().execute(new HibernateCallback<List<CaseObjectModel>>() {
            @Override
            public List<CaseObjectModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseObjectModel.class);
                criteria = criteria.createAlias("case", "cs");
                criteria = criteria.createAlias("cs.process", "proc");

                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }
                criteria.setFetchMode("case", FetchMode.JOIN);
                return criteria.list();
            }
        });

        CaseModel processCase = null;
        if (!caseObjectModels.isEmpty()) {
            processCase = caseObjectModels.get(0).getCase();
            Hibernate.initialize(processCase.getStatus());
        }

        return processCase;
    }

    /**
     * @param processLookup - lookup of the process to search by
     * @param entityIdList
     * @param entityType    - type of the entity
     * @return
     * @see ICaseObjectStore#findCaseByProcessAndEntity(java.lang.String, java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Tuple<Long, CaseModel>> findCaseByProcessAndEntity(final String processLookup, final List<Long> entityIdList, final String entityType) {
        if (entityIdList == null || entityIdList.isEmpty()) {
            return new ArrayList<Tuple<Long, CaseModel>>();
        }
        final List<Criterion> criterions = createCriterions(processLookup, entityIdList, entityType);

        return getHibernateTemplate().execute(new HibernateCallback<List<Tuple<Long, CaseModel>>>() {
            @Override
            public List<Tuple<Long, CaseModel>> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseObjectModel.class);
                criteria = criteria.createAlias("case", "cs");
                criteria = criteria.createAlias("cs.process", "proc");
                criteria = criteria.createAlias("cs.status", "case_status");

                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }
                criteria.setFetchMode("case", FetchMode.JOIN);
                List<CaseObjectModel> caseObjectModels = (List<CaseObjectModel>) criteria.list();
                ArrayList<Tuple<Long, CaseModel>> result = new ArrayList<Tuple<Long, CaseModel>>();
                if (caseObjectModels != null) {
                    Map<Long, CaseModel> caseMap = new HashMap<Long, CaseModel>();
                    for (CaseObjectModel caseObjectModel : caseObjectModels) {
                        Long id = caseObjectModel.getEntityId();
                        CaseModel processCase = caseObjectModel.getCase();
                        if (processCase != null) {
                            Hibernate.initialize(processCase.getTaskModels());
                            caseMap.put(id, processCase);
                        }
                    }
                    for (Map.Entry<Long, CaseModel> entry : caseMap.entrySet()) {
                        result.add(new Tuple<Long, CaseModel>(entry.getKey(), entry.getValue()));
                    }
                }
                return result;
            }
        });
    }

    /**
     * @param assigneeId - ID of the user assigned to the case
     * @param entityType - type of the entity
     * @param statusId   - ID of the case status
     * @return
     * @see ICaseObjectStore#findByAssignee(java.lang.Long, java.lang.String, java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseObjectModel> findByAssignee(Long assigneeId, String entityType, Long statusId) {
        final List<Criterion> criterions = createCriterions(entityType, assigneeId, statusId);
        List<CaseObjectModel> caseObjectModels = getHibernateTemplate().execute(new HibernateCallback<List<CaseObjectModel>>() {
            @Override
            public List<CaseObjectModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseObjectModel.class);
                criteria = criteria.createAlias("case", "cs");
                criteria = criteria.createAlias("cs.assignee", "asgn");
                for (Criterion restrictions : criterions) {
                    criteria.add(restrictions);
                }
                return criteria.list();
            }
        });
        return caseObjectModels;
    }

    @Override
    @Transactional(readOnly = true)
    public CaseObjectModel findByTask(final Long taskId) {
        return getHibernateTemplate().execute(new HibernateCallback<CaseObjectModel>() {
            public CaseObjectModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.eq("task.id", taskId));
                return (CaseObjectModel) criteria.uniqueResult();
            }
        });
    }

    /**
     * Creates criterions for the search
     *
     * @param processLookup - lookup of the process to search by
     * @param entityId      - ID of the entity to search by
     * @param entityType    - type of the entity to search by
     * @return list of the criterions for the search
     */
    private List<Criterion> createCriterions(String processLookup, Long entityId, String entityType) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (!StringUtils.isEmpty(processLookup)) {
            Criterion processCriterion = Restrictions.eq("proc.lookup", processLookup);
            criterions.add(processCriterion);
        }

        if (entityId != null) {
            Criterion entityCriterion = Restrictions.eq("entityId", entityId);
            criterions.add(entityCriterion);
        }

        if (!StringUtils.isEmpty(entityType)) {
            Criterion entityTypeCriterion = Restrictions.eq("entityType", entityType);
            criterions.add(entityTypeCriterion);
        }

        return criterions;
    }

    /**
     * Creates criterions for the search
     *
     * @param processLookup - lookup of the process to search by
     * @param entityIdList  - list of ithe entity IDs to search by
     * @param entityType    - type of the entity to search by
     * @return list of the criterions for the search
     */
    private List<Criterion> createCriterions(String processLookup, List<Long> entityIdList, String entityType) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (!StringUtils.isEmpty(processLookup)) {
            Criterion processCriterion = Restrictions.eq("proc.lookup", processLookup);
            criterions.add(processCriterion);
        }

        if (entityIdList != null && !entityIdList.isEmpty()) {
            Criterion entityCriterion = Restrictions.in("entityId", entityIdList);
            criterions.add(entityCriterion);
        }

        if (!StringUtils.isEmpty(entityType)) {
            Criterion entityTypeCriterion = Restrictions.eq("entityType", entityType);
            criterions.add(entityTypeCriterion);
        }

        return criterions;
    }

    /**
     * Create criterions for the search
     *
     * @param entityType - type of the entity to search by
     * @param assigneeId - ID of the assigned user to search by
     * @param statusId
     * @return list of the criterions for the search
     */
    private List<Criterion> createCriterions(String entityType, Long assigneeId, Long statusId) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (assigneeId != null) {
            Criterion assigneeCriterion = Restrictions.eq("asgn.id", assigneeId);
            criterions.add(assigneeCriterion);
        }
        if (!StringUtils.isEmpty(entityType)) {
            Criterion entityCriterion = Restrictions.eq("entityType", entityType);
            criterions.add(entityCriterion);
        }
        if (statusId != null) {
            Criterion statusCriterion = Restrictions.eq("cs.status.id", statusId);
            criterions.add(statusCriterion);
        }

        return criterions;
    }

}
