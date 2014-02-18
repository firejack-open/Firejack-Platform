package net.firejack.platform.core.store.process;

import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO;
import net.firejack.platform.api.process.model.CaseActionType;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.ProcessFieldType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.model.registry.process.ProcessFieldCaseValue;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.db.QueryCreator;
import net.firejack.platform.web.security.model.context.ContextManager;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.*;

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

/**
 * Class provides access to case data
 */
@Component("caseStore")
public class CaseStore extends BaseStore<CaseModel, Long> implements ICaseStore {

    @Autowired
    protected ICaseObjectStore caseObjectStore;
    @Autowired
    protected IProcessStore processStore;
    @Autowired
    @Qualifier("userStore")
    protected IUserStore userStore;
    @Autowired
    protected ITaskStore taskStore;
    @Autowired
    protected ICaseActionStore caseActionStore;
    @Autowired
    protected IActorStore actorStore;
    @Autowired
    protected IUserActorStore userActorStore;
    @Autowired
    protected ICaseNoteStore caseNoteStore;
    @Autowired
    protected ICaseExplanationStore caseExplanationStore;
    @Autowired
    protected IProcessFieldStore processFieldStore;
    @Autowired
    private IActivityStore activityStore;
    @Autowired
    private IActivityActionStore activityActionStore;

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(CaseModel.class);
    }

    /**
     * @param id - case ID to search by
     * @return
     * @see ICaseStore#findByIdNoNulls(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public CaseModel findByIdNoNulls(Long id) {
        return super.findById(id);
    }

    /**
     * Finds case by ID
     *
     * @param id - ID of the case
     * @return found case
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CaseModel findById(Long id) {
        CaseModel processCase = super.findById(id);
        if (processCase != null) {
            Hibernate.initialize(processCase.getAssignee());
            if (processCase.getAssignee() != null) {
                processCase.getAssignee().setUserRoles(null);
            }
            Hibernate.initialize(processCase.getStatus());
            Hibernate.initialize(processCase.getCaseObjectModels());
        }
        return processCase;
    }

    /**
     * Finds case with task ny ID
     *
     * @param id case ID to search by
     * @return found case with tasks
     */
    @Override
    @Transactional(readOnly = true)
    public CaseModel findByIdWithTasks(Long id) {
        CaseModel processCase = super.findById(id);
        if (processCase != null) {
            processCase.setProcess(null);
            Hibernate.initialize(processCase.getAssignee());
            if (processCase.getAssignee() != null) {
                processCase.getAssignee().setUserRoles(null);
            }
            Hibernate.initialize(processCase.getStatus());
            Hibernate.initialize(processCase.getCaseObjectModels());
            Hibernate.initialize(processCase.getTaskModels());
        }
        return processCase;
    }

    /**
     * Helper class for making CaseModel select & count queries that OUTER JOIN active task (if any) and processFieldCaseValues.
     */
    private abstract class QueryCreatorForCustomFieldsJoin extends QueryCreator {

        private List<ProcessFieldModel> processFields;

        /**
         * Just slightly different signature, but works the same as {@link #QueryCreatorForCustomFieldsJoin(AbstractPaginatedSortableSearchTermVO, boolean, Long)}
         * 
         * @param offset offset for paging
         * @param limit limit for paging
         * @param sortColumn sortColumn for paging
         * @param sortDirection sortDirection for paging
         * @param isCount true to generate select count(*) query, false to generate normal select object query
         * @param processId the id of process for which the processFields will be retrieved
         */
        protected QueryCreatorForCustomFieldsJoin(Integer offset, Integer limit, String sortColumn, String sortDirection, boolean isCount, Long processId) {
            this(new AbstractPaginatedSortableSearchTermVO(offset, limit, sortColumn, sortDirection), isCount, processId);
        }

        /**
         * Helper constructor to set properties needed for all the queries. processFields will be initialized based on the specified processId. These processFields
         * are used in the other methods to create OUTER JOINs to processFieldCaseValues, ORDER BY clause and init of CaseModel objects with ProcessFields in case
         * the field value doesn't exist (i.e. when OUTER JOIN returns null)
         *
         * @param paging offset, limit, sortColumn & sortDirection to use for the select object query (irrelevant when isCount = true)
         * @param isCount true to generate select count(*) query, false to generate normal select object query
         * @param processId the id of process for which the processFields will be retrieved
         */
        protected QueryCreatorForCustomFieldsJoin(AbstractPaginatedSortableSearchTermVO paging, boolean isCount, Long processId) {
            super(paging, isCount);

            List<String> additionalColumns = new ArrayList<String>();
            additionalColumns.add("c.process.name");
            additionalColumns.add("c.assignee.username");
            additionalColumns.add("c.status.name");
            setAdditionalSelectColumns(additionalColumns);

            this.processFields = processFieldStore.findByProcessIdPlusGlobal(processId);
        }

        /**
         * Make the query with as many outer joins to the processFieldCaseValues as there are process fields;
         * each join will be 1-1 because of the processField.id condition. Select case, task and all joined
         * process fields, if isCount==false or just count(*), if isCount==true: <br/>
         * <br/>
         * <code>
         *       SELECT c, t, cf_1, cf_2 <br/>
         *       FROM CaseModel c <br/>
         *       LEFT OUTER JOIN c.taskModels t WITH t.active = true <br/>
         *       LEFT OUTER JOIN c.processFieldCaseValues cf_1 WITH cf_1.processField.id = :cfId_1 <br/>
         *       LEFT OUTER JOIN c.processFieldCaseValues cf_2 WITH cf_2.processField.id = :cfId_2 <br/>
         * </code>
         */
        protected void appendCustomFieldsSelectAndJoin() {

            if (isCount) {
                appendQuery("SELECT count(*)");
            } else {
                appendQuery("SELECT c, t");
                if (additionalSelectColumns != null) {
                    for (String selectColumn : additionalSelectColumns) {
                        appendQuery(", ");
                        appendQuery(selectColumn);
                    }
                }
                if (processFields != null) {
                    for (ProcessFieldModel processField : processFields) {
                        appendQuery(", cf_");
                        appendQuery(processField.getId());
                    }
                }
            }
            appendQuery(" FROM CaseModel c");
            appendQuery(" LEFT OUTER JOIN c.taskModels t WITH t.active = true");
            if (processFields != null) {
                for (ProcessFieldModel processField : processFields) {
                    appendQuery(" LEFT OUTER JOIN c.processFieldCaseValues ");
                    appendQuery("cf_");
                    appendQuery(processField.getId());
                    appendQuery(" WITH cf_");
                    appendQuery(processField.getId());
                    appendQuery(".processField.id = :cfId_");
                    appendQuery(processField.getId());
                    addParam("cfId_" + processField.getId(), processField.getId());
                }
            }
        }

        /**
         * Some associations (like case.assignee) can be NULL and if we sort by a column from those associations we must use OUTER JOIN
         * in order to get all the records (the ones with NULL also). However, if we don't sort by such columns, we don't need this join.
         * <br/><br/>
         * Example: in case of case.assignee, we cannot simply sort by c.assignee.name, we need to add <br/>
         * <code>LEFT OUTER JOIN c.assignee alias</code><br/>
         * and sort by alias.name
         *
         */
        protected void appendOuterJoinForOrder() {
            if ("assignee.username".equals(sortColumn)) {
                appendQuery(" LEFT OUTER JOIN c.assignee assignee");
            }
        }

        /**
         * Appends ORDER BY clause based on the sortColumn & sortDirection specified in the constructor, taking into account the joined process fields, if any
         */
        protected void appendOrderBy() {

            if (!StringUtils.isEmpty(sortColumn)) {
                if (sortColumn.startsWith("cf_")) {
                    int cfId = Integer.parseInt(sortColumn.substring(3));
                    boolean foundCustomColumn = false;
                    for (ProcessFieldModel processField : processFields) {
                        if (processField.getId() == cfId) {
                            sortColumn = sortColumn + "." + processField.getValueColumn(); // make it like 'cf_1.integerValue'
                            foundCustomColumn = true;
                            break;
                        }
                    }
                    if (!foundCustomColumn) {
                        sortColumn = null; // previous sort might have been remembered in the grid - cannot sort by non existing column
                    }
                } else if ("assignee.username".equals(sortColumn)) {
                    // do nothing, use that sort column as is (assignee is alias of OUTER JOINED case.assignee)
                } else {
                    sortColumn = "c." + sortColumn;
                }
            }

            if (!StringUtils.isEmpty(sortColumn)) {
                appendQuery(" ORDER BY ");
                appendQuery(sortColumn);
                if (!StringUtils.isEmpty(sortDirection)) {
                    appendQuery(" ");
                    appendQuery(sortDirection);
                }
            }
        }

        /**
         * Executes the query that's been created previously with appendCustomFieldsSelectAndJoin() (must be called with isCount = false) and other appendX() methods. Uses
         * {@link net.firejack.platform.core.model.registry.process.CaseModel#CASE_CUSTOM_FIELDS_TRANSFORMER} to transform the row selection defined in
         * {@link #appendCustomFieldsSelectAndJoin} into a {@link net.firejack.platform.core.model.registry.process.CaseModel} object, with
         * properties set into that CaseModel from other objects in the row.
         *
         * @return the list of Cases that the query + transformer return
         */
        protected List<CaseModel> queryForList() {
            if (isCount) {
                throw new IllegalArgumentException("Error in usage of " + getClass() + ". Cannot queryForList() with isCount=true");
            }

            List<Object[]> resultSet = getHibernateTemplate().execute(new HibernateCallback<List<Object[]>>() {
                public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                    Query query = session.createQuery(queryBuff.toString());
                    if (offset != null && offset > -1) {
                        query.setFirstResult(offset);
                    }
                    if (limit != null && limit > -1) {
                        query.setMaxResults(limit);
                    }
                    query.setProperties(queryParams);
                    query.setResultTransformer(CaseModel.CASE_CUSTOM_FIELDS_TRANSFORMER);
                    return query.list();
                }
            });

            //List<Object[]> resultSet = (List<Object[]>)findByQuery(offset, limit, queryBuff.toString(), queryParams, TaskModel.TASK_CUSTOM_FIELDS_TRANSFORMER);
            List<CaseModel> cases = new ArrayList<CaseModel>();
            for (Object[] data : resultSet) {
                CaseModel caseModel = (CaseModel) data[0];
                cases.add(caseModel);
            }

            //List<CaseModel> cases = findByQuery(offset, limit, queryBuff.toString(), queryParams, CaseModel.CASE_CUSTOM_FIELDS_TRANSFORMER);

            // for outer joined ProcessFieldCaseValues that don't exist for the given case, hibernate returns null
            // need to replace these nulls with objects that have at least the ProcessFieldModel set (in order to have correct column definitions in each row)
            if (processFields != null) {
                for (int i = 0; i < processFields.size(); i++) {
                    for (CaseModel processCase : cases) {
                        if (processCase.getProcessFieldCaseValues().get(i) == null) {
                            ProcessFieldCaseValue processFieldCaseValue = new ProcessFieldCaseValue();
                            processFieldCaseValue.setProcessField(processFields.get(i));
                            processCase.getProcessFieldCaseValues().set(i, processFieldCaseValue);
                        }
                    }
                }
            }

            return cases;
        }

        /**
         * Executes the query that's been created previously with appendCustomFieldsSelectAndJoin() (must be called with isCount = true) and other appendX() methods. Will
         * return only the count of records.
         *
         * @return the total number of records in DB that match the set params
         */
        protected long queryForCount() {
            if (!isCount) {
                throw new IllegalArgumentException("Error in usage of " + getClass() + ". Cannot queryForCount() with isCount=false");
            }

            return countByQuery(queryBuff.toString(), queryParams);
        }
    }

    private class QueryCreatorBySearchParams extends QueryCreatorForCustomFieldsJoin {
        
        private QueryCreatorBySearchParams(CaseSearchTermVO caseSearchTermVO, boolean isCount) {

            super(caseSearchTermVO, isCount, caseSearchTermVO.getProcessId());

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" WHERE c.id = c.id"); // useless condition, just to not have the check if we've already appended 'WHERE' in every other condition

            if (caseSearchTermVO.getCustomFields() != null) {
                for (CustomFieldsSearchVO customFieldsSearchVO : caseSearchTermVO.getCustomFields()) {
                    if (customFieldsSearchVO.getValue() != null) {
                        appendQuery(" AND cf_");
                        appendQuery(customFieldsSearchVO.getFieldId());
                        appendQuery(".");
                        appendQuery(customFieldsSearchVO.getValueColumn());
                        Object paramValue;
                        if (ProcessFieldType.STRING == ProcessFieldType.valueOf(customFieldsSearchVO.getValueType())) {
                            appendQuery(" LIKE :val_");
                            paramValue = "%" + customFieldsSearchVO.getValue() + '%';
                        } else {
                            appendQuery(" = :val_");
                            paramValue = customFieldsSearchVO.getValue();
                        }
                        appendQuery(customFieldsSearchVO.getFieldId());
                        addParam("val_" + customFieldsSearchVO.getFieldId(), paramValue);
                    }
                }
            }
            if (caseSearchTermVO.getProcessId() != null) {
                appendQuery(" AND c.process.id = :processId");
                addParam("processId", caseSearchTermVO.getProcessId());
            }
            if (caseSearchTermVO.getActive() != null) {
                appendQuery(" AND c.active = :active");
                addParam("active", caseSearchTermVO.getActive());
            }
            if (caseSearchTermVO.getActivityId() != null) {
                appendQuery(" AND EXISTS ( FROM TaskModel at WHERE at.case = c AND at.activity.id = :activityId AND at.active = true )");
                addParam("activityId", caseSearchTermVO.getActivityId());
            }
            if (caseSearchTermVO.getAssigneeId() != null) {
                appendQuery(" AND c.assignee.id = :assigneeId");
                addParam("assigneeId", caseSearchTermVO.getAssigneeId());
            }
            if (StringUtils.isNotBlank(caseSearchTermVO.getDescription())) {
                appendQuery(" AND c.description like :description");
                addParam("description", "%" + caseSearchTermVO.getDescription() + "%");
            }
            if (caseSearchTermVO.getStatusId() != null) {
                appendQuery(" AND c.status.id = :statusId");
                addParam("statusId", caseSearchTermVO.getStatusId());
            }
            if (caseSearchTermVO.getStartDate() != null) {
                appendQuery(" AND c.created > :startDate");
                addParam("startDate", caseSearchTermVO.getStartDate());
            }
            if (caseSearchTermVO.getEndDate() != null) {
                appendQuery(" AND c.created < :endDate");
                addParam("endDate", caseSearchTermVO.getEndDate());
            }
            if (caseSearchTermVO.getLookupPrefix() != null) {
                appendQuery(" AND c.process.lookup like :lookupPrefix");
                addParam("lookupPrefix", caseSearchTermVO.getLookupPrefix() + "%");
            }

            if (!isCount) {
                appendOrderBy();
            }
        }
    }

    /**
     * @see ICaseStore#findAllBySearchParams(CaseSearchTermVO, net.firejack.platform.core.model.SpecifiedIdsFilter)
     * @param caseSearchTermVO value object with search parameters
     * @param filter search filter
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseModel> findAllBySearchParams(CaseSearchTermVO caseSearchTermVO, SpecifiedIdsFilter<Long> filter) {

        QueryCreatorBySearchParams queryCreatorBySearchParams = new QueryCreatorBySearchParams(caseSearchTermVO, false);

        List<CaseModel> cases = queryCreatorBySearchParams.queryForList();

        for (CaseModel processCase : cases) {
            if (processCase.getAssignee() != null) {
                processCase.getAssignee().setUserRoles(null);
            }
        }
        return cases;
    }

    /**
     * @see ICaseStore#countAllBySearchParams(CaseSearchTermVO, net.firejack.platform.core.model.SpecifiedIdsFilter)
     * @param caseSearchTermVO value object with search parameters
     * @param filter search filter
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public long countAllBySearchParams(CaseSearchTermVO caseSearchTermVO, SpecifiedIdsFilter<Long> filter) {
        QueryCreatorBySearchParams queryCreatorBySearchParams = new QueryCreatorBySearchParams(caseSearchTermVO, true);

        return queryCreatorBySearchParams.queryForCount();
    }

    private class QueryCreatorByUserId extends QueryCreatorForCustomFieldsJoin {

        private QueryCreatorByUserId(Long userId, String lookupPrefix, Boolean active, Integer offset, Integer limit, String sortColumn, String sortDirection, boolean isCount) {

            super(offset, limit, sortColumn, sortDirection, isCount, null); // processId = null will get only global fields

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" WHERE (");
            appendQuery("    c.process.id IN (");
            appendQuery("        SELECT activity1.parent.id");
            appendQuery("        FROM ActorModel a1");
            appendQuery("            JOIN a1.activities activity1");
            appendQuery("            JOIN a1.userActors userActor");
            appendQuery("        WHERE userActor.user.id = :userId");
            appendQuery("        AND userActor.case IS NULL");
            appendQuery("    )");
            appendQuery("    OR");
            appendQuery("    c.process.id IN (");
            appendQuery("        SELECT activity2.parent.id");
            appendQuery("        FROM ActorModel a2");
            appendQuery("            JOIN a2.activities activity2");
            appendQuery("            JOIN a2.roles role");
            appendQuery("            JOIN role.userRoles userRoles");
            appendQuery("        WHERE userRoles.user.id = :userId");
            appendQuery("    )");
            appendQuery("    OR");
            appendQuery("    c.process.id IN (");
            appendQuery("        SELECT activity3.parent.id");
            appendQuery("        FROM ActorModel a3, UserModel u");
            appendQuery("            JOIN a3.activities activity3");
            appendQuery("            JOIN a3.groups grp");
            appendQuery("        WHERE u.id = :userId AND u.registryNode.id = grp.id AND u.registryNode.class = 'GRP'");
            appendQuery("    )");
            appendQuery("    OR");
            appendQuery("    c.id IN (");
            appendQuery("        SELECT u.case.id");
            appendQuery("        FROM UserActorModel u");
            appendQuery("        WHERE u.user.id = :userId");
            appendQuery("        AND u.case IS NOT NULL");
            appendQuery("    )");
            appendQuery(")");

            addParam("userId", userId);

            if (StringUtils.isNotBlank(lookupPrefix)) {
                appendQuery(" AND c.process.lookup like :lookupPrefix");
                addParam("lookupPrefix", lookupPrefix + "%");
            }
            if (active != null) {
                appendQuery(" AND c.active = :active");
                addParam("active", active);
            }

            if (!isCount) {
                appendOrderBy();
            }
        }
    }


    /**
     * Searches the cases by user ID
     *
     * @param userId        - user ID to search by
     * @param lookupPrefix  - prefix of the lookup
     * @param active        - flag showing whether the case is active
     * @param filter        - search filter
     * @param offset        - parameter specifying where to start the result list from
     * @param limit         - parameter specifying the number of the results
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return list of found cases
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseModel> findAllByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection) {
        QueryCreatorByUserId queryCreatorByUserId = new QueryCreatorByUserId(userId, lookupPrefix, active, offset, limit, sortColumn, sortDirection, false);

        return queryCreatorByUserId.queryForList();
    }

    /**
     * @param userId       - user ID to search by
     * @param lookupPrefix - prefix of the lookup
     * @param active       - flag showing whether the case is active
     * @param filter       - search filter
     * @return
     * @see ICaseStore#countAllByUserId(java.lang.Long, java.lang.String, java.lang.Boolean, net.firejack.platform.core.model.SpecifiedIdsFilter <java.lang.Long>)
     */
    @Override
    @Transactional(readOnly = true)
    public long countAllByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter<Long> filter) {
        QueryCreatorByUserId queryCreatorByUserId = new QueryCreatorByUserId(userId, lookupPrefix, active, null, null, null, null, true);

        return queryCreatorByUserId.queryForCount();
    }

    private class QueryCreatorBelongingToUserActor extends QueryCreatorForCustomFieldsJoin {

        private QueryCreatorBelongingToUserActor(Long userId, String lookupPrefix, Integer offset, Integer limit, String sortColumn, String sortDirection, boolean isCount) {

            super(offset, limit, sortColumn, sortDirection, isCount, null); // processId = null will get only global fields

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" JOIN t.activity.actor a");
            appendQuery(" WHERE (");
            appendQuery(" a.id IN (");
            appendQuery("        SELECT a1.id");
            appendQuery("        FROM ActorModel a1");
            appendQuery("            JOIN a1.userActors userActor");
            appendQuery("        WHERE userActor.user.id = :userId)");
            appendQuery(" OR");
            appendQuery("    a.id IN (");
            appendQuery("        SELECT a2.id");
            appendQuery("        FROM ActorModel a2");
            appendQuery("            JOIN a2.roles role");
            appendQuery("                JOIN role.userRoles userRoles");
            appendQuery("        WHERE userRoles.user.id = :userId)");
            appendQuery(" OR");
            appendQuery("    a.id IN (");
            appendQuery("        SELECT a3.id");
            appendQuery("        FROM ActorModel a3, UserModel u");
            appendQuery("            JOIN a3.groups grp");
            appendQuery("        WHERE u.id = :userId AND u.registryNode.id = grp.id AND u.registryNode.class = 'GRP')");
            appendQuery(" OR");
            appendQuery("    c.id IN (");
            appendQuery("        SELECT u.case.id");
            appendQuery("        FROM UserActorModel u");
            appendQuery("        WHERE u.user.id = :userId");
            appendQuery("        AND u.case IS NOT NULL");
            appendQuery("        AND u.actor = a)");
            appendQuery(") AND t.active = true");

            addParam("userId", userId);

            if (StringUtils.isNotBlank(lookupPrefix)) {
                appendQuery(" AND c.process.lookup like :lookupPrefix");
                addParam("lookupPrefix", lookupPrefix + "%");
            }

            if (!isCount) {
                appendOrderBy();
            }
        }
    }

    /**
     * @param userId        - ID of the user
     * @param lookupPrefix  - lookup prefix
     * @param filter        - IDs filter
     * @param offset        - item from which to start the results listing from
     * @param limit         - number of teh resulting items to be listed
     * @param sortColumn    - column to sort by
     * @param sortDirection - sotting direction
     * @return
     * @see ICaseStore#findBelongingToUserActor(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseModel> findBelongingToUserActor(Long userId, String lookupPrefix, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection) {

        QueryCreatorBelongingToUserActor queryCreatorBelongingToUserActor = new QueryCreatorBelongingToUserActor(userId, lookupPrefix, offset, limit, sortColumn, sortDirection, false);

        return queryCreatorBelongingToUserActor.queryForList();
    }

    /**
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return
     * @see ICaseStore#countBelongingToUserActor(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter)
     */
    @Override
    @Transactional(readOnly = true)
    public long countBelongingToUserActor(final Long userId, final String lookupPrefix, SpecifiedIdsFilter<Long> filter) {

        QueryCreatorBelongingToUserActor queryCreatorBelongingToUserActor = new QueryCreatorBelongingToUserActor(userId, lookupPrefix, null, null, null, null, true);

        return queryCreatorBelongingToUserActor.queryForCount();
    }


    private class QueryCreatorClosedCasesForCurrentUser extends QueryCreatorForCustomFieldsJoin {

        private QueryCreatorClosedCasesForCurrentUser(Long userId, String lookupPrefix, Integer offset, Integer limit, String sortColumn, String sortDirection, boolean isCount) {

            super(offset, limit, sortColumn, sortDirection, isCount, null); // processId = null will get only global fields

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" WHERE ");
            appendQuery(" c.active = false");
            appendQuery(" AND EXISTS (FROM CaseActionModel ca WHERE ca.case = c AND ca.type = :type AND ca.user.id = :userId) ");

            addParam("type", CaseActionType.PERFORM_ACTIVITY);
            addParam("userId", userId);

            if (StringUtils.isNotBlank(lookupPrefix)) {
                appendQuery(" AND c.process.lookup like :lookupPrefix");
                addParam("lookupPrefix", lookupPrefix + "%");
            }

            if (!isCount) {
                appendOrderBy();
            }
        }
    }

    /**
     * @param userId        - ID of the user
     * @param lookupPrefix  - lookup prefix
     * @param filter        - IDs filter
     * @param offset        - item from which to start the results listing from
     * @param limit         - number of teh resulting items to be listed
     * @param sortColumn    - column to sort by
     * @param sortDirection - sorting direction
     * @return
     * @see ICaseStore#findClosedCasesForCurrentUser(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseModel> findClosedCasesForCurrentUser(Long userId, String lookupPrefix, SpecifiedIdsFilter filter, Integer offset, Integer limit, String sortColumn, String sortDirection) {

        QueryCreatorClosedCasesForCurrentUser queryCreatorClosedCasesForCurrentUser = new QueryCreatorClosedCasesForCurrentUser(userId, lookupPrefix, offset, limit, sortColumn, sortDirection, false);

        return queryCreatorClosedCasesForCurrentUser.queryForList();

    }

    /**
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return
     * @see ICaseStore#countClosedCasesForCurrentUser(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter)
     */
    @Override
    public long countClosedCasesForCurrentUser(final Long userId, final String lookupPrefix, final SpecifiedIdsFilter filter) {

        QueryCreatorClosedCasesForCurrentUser queryCreatorClosedCasesForCurrentUser = new QueryCreatorClosedCasesForCurrentUser(userId, lookupPrefix, null, null, null, null, true);

        return queryCreatorClosedCasesForCurrentUser.queryForCount();
    }

    /**
     * @param processLookup - lookup of the process the case belongs to
     * @param entityId      - ID of the entity
     * @param entityType    - type of the entity
     * @return
     * @see ICaseStore#stop(java.lang.String, java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional
    public Long stop(String processLookup, Long entityId, String entityType) {
        CaseModel processCase = caseObjectStore.findCaseByProcessAndEntity(processLookup, entityId, entityType);
        if (processCase != null) {
            processCase.setActive(false);
            saveOrUpdate(processCase);
            return processCase.getId();
        }
        return null;
    }

    @Override
    @Transactional
    public void stop(Long caseId, Long explanationId, String comment) {
        CaseModel processCase = findById(caseId);
        
        Date currentDateTime = new Date();
        Long currentUserId = ContextManager.getUserInfoProvider().getId();
        UserModel currentUser = userStore.load(currentUserId);

        CaseActionModel caseAction = new CaseActionModel();
        caseAction.setPerformedOn(currentDateTime);
        caseAction.setType(CaseActionType.STOP);
        caseAction.setCase(processCase);
        caseAction.setUser(currentUser);
        caseAction.setCreated(currentDateTime);
        if (!StringUtils.isEmpty(comment)) {
            CaseNoteModel caseNote = new CaseNoteModel();
            caseNote.setProcessCase(processCase);
            caseNote.setText(comment);
            caseNote.setUser(currentUser);
            caseNote.setCreated(currentDateTime);
            caseNoteStore.saveOrUpdate(caseNote);
            caseAction.setCaseNote(caseNote);
        }
        if (explanationId != null) {
            caseAction.setCaseExplanation(caseExplanationStore.load(explanationId));
        }

        TaskModel activeTaskModel = findActiveTask(caseId);
        activeTaskModel.setActive(false);
        taskStore.saveOrUpdate(activeTaskModel);
        caseAction.setTaskModel(activeTaskModel);

        caseActionStore.saveOrUpdate(caseAction);

        processCase.setActive(false);
        saveOrUpdate(processCase);
    }

    /**
     * @param caseId - ID of the case
     * @return
     * @see ICaseStore#findActiveTask(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public TaskModel findActiveTask(Long caseId) {
        CaseModel processCase = findById(caseId);
        TaskModel activeTaskModel = null;
        for (TaskModel taskModel : processCase.getTaskModels()) {
            if (taskModel.getActive()) {
                activeTaskModel = taskModel;
            }
        }
        return activeTaskModel;
    }

    /**
     * @param entityTypeLookup - defines the type of the attached object
     * @param entityId         - ID of the attached object
     * @param filter           - IDs filter
     * @return
     * @see ICaseStore#findActive(java.lang.String, java.lang.Long, net.firejack.platform.core.model.SpecifiedIdsFilter <java.lang.Long>)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseModel> findActive(String entityTypeLookup, Long entityId, SpecifiedIdsFilter<Long> filter) {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("caseObjectModels", "caseObjectModels");

        List<Criterion> criterions = createCriterions(entityTypeLookup, entityId);
        List<CaseModel> cases = findAllWithFilter(null, null, criterions, aliases, filter, (Order) null);

        for (CaseModel processCase : cases) {
            if (processCase.getProcess() != null) {
                processCase.getProcess().setActivities(null);
                processCase.getProcess().setStatuses(null);
            }
            if (processCase.getAssignee() != null) {
                processCase.getAssignee().setUserRoles(null);
            }
        }
        return cases;
    }

    /**
     * @param caseId          - ID of the case to reset
     * @param comment         - text to be added to the newly created note (comment about case reset)
     * @param taskDescription - description for the newly created task
     * @see ICaseStore#reset(java.lang.Long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void reset(Long caseId, String comment, String taskDescription) throws UserNotInActorSetException, TaskNotActiveException {
        Date currentDateTime = new Date();
        Long currentUserId = ContextManager.getUserInfoProvider().getId();
        UserModel currentUser = userStore.findById(currentUserId);
        CaseModel processCase = findById(caseId);
        TaskModel activeTaskModel = findActiveTask(caseId);
        activeTaskModel.setActive(false);
        activeTaskModel.setCloseDate(currentDateTime);
        activeTaskModel.setUpdateDate(currentDateTime);
        taskStore.saveOrUpdate(activeTaskModel);
        int minOrder = Integer.MAX_VALUE;
        ActivityModel firstActivity = null;
        for (ActivityModel activity : processCase.getProcess().getActivities()) {
            if (activity.getSortPosition() < minOrder) {
                firstActivity = activity;
                minOrder = activity.getSortPosition();
            }
        }
        UserModel assignee = null;
        for (TaskModel taskModel : processCase.getTaskModels()) {
            if (taskModel.getActivity().getId().equals(firstActivity.getId())) {
                assignee = taskModel.getAssignee();
            }
        }
        TaskModel taskModel = new TaskModel();
        taskModel.setActive(true);
        taskModel.setActivity(firstActivity);
        taskModel.setAssignee(assignee);
        taskModel.setCase(processCase);
        taskModel.setCreated(currentDateTime);
        taskModel.setUpdateDate(currentDateTime);
        if (taskDescription != null) {
            taskModel.setDescription(taskDescription);
        } else {
            taskModel.setDescription(processCase.getDescription());
        }
        taskStore.saveOrUpdate(taskModel);

        CaseActionModel caseAction = new CaseActionModel();
        caseAction.setPerformedOn(currentDateTime);
        caseAction.setType(CaseActionType.RESET);
        caseAction.setCase(processCase);
        caseAction.setTaskModel(taskModel);
        caseAction.setUser(currentUser);
        caseAction.setCreated(currentDateTime);
        if (!StringUtils.isEmpty(comment)) {
            CaseNoteModel caseNote = new CaseNoteModel();
            caseNote.setProcessCase(processCase);
            caseNote.setTaskModel(taskModel);
            caseNote.setText(comment);
            caseNote.setUser(currentUser);
            caseNote.setCreated(currentDateTime);
            caseNoteStore.saveOrUpdate(caseNote);
            caseAction.setCaseNote(caseNote);
        }
        caseActionStore.saveOrUpdate(caseAction);
        processCase.setAssignee(assignee);
        processCase.setUpdateDate(currentDateTime);
        processCase.setStatus(firstActivity.getStatus());
        saveOrUpdate(processCase);
        if (firstActivity.getActivityType().equals(ActivityType.SYSTEM)) {
            taskStore.performIncludingFollowingSystemActivities(taskModel.getId(), assignee == null ? null : assignee.getId(), null, null, null);
        }
    }

    /**
     * Update case description
     *
     * @param caseId      case ID
     * @param description description
     */
    @Override
    @Transactional
    public void update(Long caseId, String description) {
        CaseModel processCase = findById(caseId);
        if (processCase != null) {
            processCase.setDescription(description);
            saveOrUpdate(processCase);
        }

        TaskModel activeTaskModel = findActiveTask(caseId);
        if (activeTaskModel != null) {
            activeTaskModel.setDescription(description);
            taskStore.saveOrUpdate(activeTaskModel);
        }
    }

    /**
     * @see ICaseStore#updateCaseCustomFields(java.lang.String, java.lang.String, java.lang.Long, java.util.List<ProcessCustomFieldVO>)
     * @param processLookup lookup of the process whose case is being updated
     * @param entityLookup lookup of the entity whose fields are being updated
     * @param entityId ID of the entity whose fields are being updated
     * @param customFields list of process custom fields
     */
    @Override
    @Transactional
    public void updateCaseCustomFields(String processLookup, String entityLookup, Long entityId, List<ProcessCustomFieldVO> customFields) {
        CaseModel processCase = caseObjectStore.findCaseByProcessAndEntity(processLookup, entityId, entityLookup);
        for (ProcessCustomFieldVO processCustomFieldVO : customFields) {
            String fieldLookup = processCustomFieldVO.getFieldLookup();
            Object value = processCustomFieldVO.getValue();
            processFieldStore.updateProcessFieldValue(processLookup, entityLookup, fieldLookup, value, processCase.getId());
        }
    }

    private List<Criterion> createCriterions(String entityTypeLookup, Long entityId) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (!StringUtils.isEmpty(entityTypeLookup)) {
            Criterion entityTypeLookupCriterion = Restrictions.eq("caseObjectModels.entityType", entityTypeLookup);
            criterions.add(entityTypeLookupCriterion);
        }

        if (entityId != null) {
            Criterion entityIdCriterion = Restrictions.eq("caseObjectModels.entityId", entityId);
            criterions.add(entityIdCriterion);
        }

        Criterion activeCriterion = Restrictions.eq("active", true);
        criterions.add(activeCriterion);

        return criterions;
    }

    /**
     * @param processLookup     - lookup of the process the case belongs to
     * @param assignee          - user assigned to the case
     * @param currentUser       - user currently logged in
     * @param caseObjectModels       - case object entities
     * @param userActorTeam     - user actor entities
     * @param caseDescription   - description of the case
     * @param allowNullAssignee - flag showing whether assignee is allowed to be set to null
     * @param customFields      - map of custom fields values having field lookups as keys
     * @return
     * @throws UserNotInActorSetException
     * @see ICaseStore#externalStart(java.lang.String, net.firejack.platform.core.model.user.UserModel, net.firejack.platform.core.model.user.UserModel, java.util.Collection, java.util.Map, java.lang.String, boolean, java.util.Map<java.lang.String,java.lang.Object>)
     */
    @Override
    @Transactional
    public Long externalStart(String processLookup, UserModel assignee, UserModel currentUser, Collection<CaseObjectModel> caseObjectModels, Map<Long, Set<Long>> userActorTeam, String caseDescription, boolean allowNullAssignee, Map<String, List<ProcessCustomFieldVO>> customFields) throws UserNotInActorSetException, TaskNotActiveException {

        List<UserActorModel> userActors;
        if (userActorTeam == null) {
            userActors = null;
        } else {
            userActors = new ArrayList<UserActorModel>();
            for (Map.Entry<Long, Set<Long>> entry : userActorTeam.entrySet()) {
                Long userId = entry.getKey();
                Collection<Long> actors = entry.getValue();
                UserModel user = userStore.findById(userId);
                if (user == null) {
                    throw new IllegalArgumentException("Failed to find an user for UserActorModel by specified userId = " + userId);
                }
                for (Long actorId : actors) {
                    ActorModel actor = actorStore.findById(actorId);
                    if (actor == null) {
                        throw new IllegalArgumentException("Failed to find an actor for UserActorModel by specified actorId = " + actorId);
                    }
                    UserActorModel userActor = new UserActorModel();
                    userActor.setUser(user);
                    userActor.setActor(actor);
                    userActors.add(userActor);
                }
            }
        }

        ProcessModel process = processStore.findByLookup(processLookup);

        List<ActivityModel> activities = process.getActivities();

        ActivityModel firstActivity = null;
        if (activities != null && !activities.isEmpty()) {
            firstActivity = activities.get(0);
        }

        if (firstActivity == null) {
            throw new BusinessFunctionException("Process (lookup = '" + processLookup + "') has no activities. Cannot start case.");
        }

        ActivityModel firstNonSystemActivity = null;
        for (ActivityModel activity : activities) {
            if (!activity.getActivityType().equals(ActivityType.SYSTEM)) {
                firstNonSystemActivity = activity;
                break;
            }
        }

        UserModel toBeAssigned = null;
        if (assignee != null && actorStore.isUserInActorSet(assignee.getId(), firstNonSystemActivity.getActor().getId())) {
            toBeAssigned = assignee;
        }

        if (toBeAssigned == null && !allowNullAssignee) {
            throw new UserNotInActorSetException("User with id " + (assignee != null ? assignee.getId() : null) + " not in actor set for activity '" + firstNonSystemActivity.getName() + "'. Cannot start case.");
        }

        Date currentDateTime = new Date();

        CaseModel processCase = new CaseModel();
        processCase.setProcess(process);
        if (caseDescription != null) {
            processCase.setDescription(caseDescription);
        } else {
            processCase.setDescription(process.getDescription());
        }
        processCase.setActive(true);
        processCase.setAssignee(toBeAssigned);
        processCase.setStartDate(currentDateTime);
        processCase.setUpdateDate(currentDateTime);

        List<StatusModel> statuses = process.getStatuses();
        StatusModel firstStatus = null;
        int minOrder = Integer.MAX_VALUE;
        for (StatusModel status : statuses) {
            if (status.getSortPosition() < minOrder) {
                firstStatus = status;
                minOrder = status.getSortPosition();
            }
        }
        processCase.setStatus(firstStatus);

        CaseActionModel caseAction = new CaseActionModel();
        caseAction.setCase(processCase);
        caseAction.setType(CaseActionType.START);
        caseAction.setPerformedOn(currentDateTime);
        caseAction.setUser(currentUser);

        saveOrUpdate(processCase);

        for (CaseObjectModel caseObjectModel : caseObjectModels) {
            UserModel creatorUser = caseObjectModel.getCreatedBy() == null ? null : userStore.findById(caseObjectModel.getCreatedBy().getId());
            UserModel updaterUser = caseObjectModel.getUpdatedBy() == null ? null : userStore.findById(caseObjectModel.getUpdatedBy().getId());
            TaskModel taskModel = caseObjectModel.getTask() == null ? null : taskStore.findById(caseObjectModel.getTask().getId());

            caseObjectModel.setCreatedBy(creatorUser);
            caseObjectModel.setUpdatedBy(updaterUser);
            caseObjectModel.setTask(taskModel);
        }

        for (CaseObjectModel caseObjectModel : caseObjectModels) {
            caseObjectModel.setCase(processCase);
            caseObjectModel.setStatus(firstStatus);
            caseObjectModel.setUpdateDate(currentDateTime);
            caseObjectStore.saveOrUpdate(caseObjectModel);
        }

        if (userActors != null) {
            for (UserActorModel userActor : userActors) {
                userActor.setCase(processCase);
            }
            userActorStore.saveOrUpdateAll(userActors);
        }

        TaskModel taskModel = new TaskModel();
        taskModel.setActive(true);
        taskModel.setUpdateDate(currentDateTime);
        taskModel.setCase(processCase);
        taskModel.setAssignee(toBeAssigned);
        taskModel.setActivity(firstActivity);
        taskModel.setDescription(processCase.getDescription());
        taskStore.saveOrUpdate(taskModel);
        if (firstActivity.getActivityType().equals(ActivityType.SYSTEM)) {
            taskStore.performIncludingFollowingSystemActivities(taskModel.getId(), toBeAssigned == null ? null : toBeAssigned.getId(), null, null, null);
        }
        caseAction.setTaskModel(taskModel);
        caseActionStore.saveOrUpdate(caseAction);
        processCase.setStatus(firstActivity.getStatus());

        for (String entityLookup : customFields.keySet()) {
            List<ProcessCustomFieldVO> fieldsPerEntity = customFields.get(entityLookup);
            for (ProcessCustomFieldVO processCustomFieldVO : fieldsPerEntity) {
                String fieldLookup = processCustomFieldVO.getFieldLookup();
                Object value = processCustomFieldVO.getValue();
                processFieldStore.updateProcessFieldValue(processLookup, entityLookup, fieldLookup, value, processCase.getId());
            }
        }

        return processCase.getId();
    }

    @Override
    @Transactional
    public CaseModel moveCaseToActivity(Long entityId, Long activityActionId, Long assigneeId, Long currentUserId, String comment) {
        CaseModel processCase;
        if (entityId == null || activityActionId == null || currentUserId == null) {
            processCase = null;
        } else {
            UserModel currentUser = userStore.findById(currentUserId);
            LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
            restrictions.add(Restrictions.idEq(activityActionId));
            Map<String, String> aliases = new HashMap<String, String>();
            aliases.put("activityFrom", "from");
            aliases.put("activityTo", "to");
            aliases.put("status", "status");
            List<ActivityActionModel> foundActions = activityActionStore.search(restrictions, aliases, null);
            ActivityActionModel activityAction = foundActions.get(0);
            UserModel assignee = assigneeId == null ? null : userStore.findById(assigneeId);

            restrictions.clear();
            restrictions.add(Restrictions.idEq(activityAction.getActivityFrom().getId()));
            aliases.clear();
            aliases.put("parent", "parent");
            List<ProcessModel> processList = activityStore.searchWithProjection(
                    restrictions, Projections.property("parent"), aliases, null);

            CaseObjectModel caseObjectModel;
            if (processList.isEmpty()) {
                processCase = null;
                caseObjectModel = null;
            } else {
                ProcessModel processModel = processList.get(0);
                EntityModel entityModel = getHibernateTemplate().get(EntityModel.class, processModel.getMain().getId());
                restrictions.clear();
                restrictions.add(Restrictions.eq("entityType", entityModel.getLookup()));
                restrictions.add(Restrictions.eq("entityId", entityId));
                restrictions.add(Restrictions.eq("case.process.id", processModel.getId()));
                aliases.clear();
                aliases.put("case", "case");
                List<CaseObjectModel> caseObjects = caseObjectStore.search(restrictions, aliases, null, false);
                caseObjectModel = caseObjects.isEmpty() ? null : caseObjects.get(0);
                processCase = caseObjectModel == null ? null : caseObjectModel.getCase();
            }

            if (processCase != null) {
                restrictions.clear();
                restrictions.add(Restrictions.and(
                        Restrictions.eq("case.id", processCase.getId()), Restrictions.eq("active", Boolean.TRUE)));
                List<TaskModel> currentActiveTasks = taskStore.search(restrictions, null);
                TaskModel oldActiveTaskModel;
                if (currentActiveTasks.isEmpty()) {
                    oldActiveTaskModel = null;
                } else {
                    oldActiveTaskModel = currentActiveTasks.get(0);
                    for (TaskModel activeTask : currentActiveTasks) {
                        activeTask.setActive(Boolean.FALSE);
                    }
                    taskStore.saveOrUpdateAll(currentActiveTasks);
                }

                StatusModel status = activityAction.getStatus();

                ActivityModel toActivity = activityAction.getActivityTo();

                boolean isNotFinalStep = !status.getName().equals(StatusModel.STATUS_FINISHED) &&
                        toActivity.getActivityOrder() != ActivityOrder.END;

                String taskDescription = StringUtils.isBlank(toActivity.getDescription()) ?
                        processCase.getDescription() : toActivity.getDescription();
                Date updateDate = new Date(System.currentTimeMillis());
                TaskModel nextTaskModel = new TaskModel();
                nextTaskModel.setDescription(taskDescription);
                nextTaskModel.setActivity(toActivity);
                nextTaskModel.setCase(processCase);
                nextTaskModel.setUpdateDate(updateDate);
                nextTaskModel.setAssignee(assignee);
                nextTaskModel.setActive(isNotFinalStep);
                taskStore.saveOrUpdate(nextTaskModel);
                processCase.setStatus(status);
                processCase.setActive(isNotFinalStep);
                saveOrUpdate(processCase);

                caseObjectModel.setTask(nextTaskModel);
                caseObjectModel.setStatus(status);
                caseObjectModel.setUpdateDate(updateDate);
                caseObjectModel.setUpdatedBy(currentUser);
                caseObjectStore.saveOrUpdate(caseObjectModel);

                if (oldActiveTaskModel != null) {
                    CaseActionModel caseAction = new CaseActionModel();
                    caseAction.setPerformedOn(updateDate);
                    caseAction.setType(CaseActionType.PERFORM_ACTIVITY);
                    caseAction.setCase(processCase);
                    caseAction.setUser(currentUser);
                    caseAction.setTaskModel(oldActiveTaskModel);
                    if (StringUtils.isNotBlank(comment)) {
                        CaseNoteModel caseNote = new CaseNoteModel();
                        caseNote.setProcessCase(processCase);
                        caseNote.setText(comment);
                        caseNote.setUser(currentUser);
                        caseNoteStore.saveOrUpdate(caseNote);
                        caseAction.setCaseNote(caseNote);
                    }
                    caseActionStore.saveOrUpdate(caseAction);
                }
            }
        }
        return processCase;
    }
}
