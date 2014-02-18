package net.firejack.platform.core.store.process;

import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.model.CaseActionType;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.exception.NoPreviousActivityException;
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
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SearchQuery;
import net.firejack.platform.core.utils.db.QueryCreator;
import net.firejack.platform.model.service.mail.ITaskMailService;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
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
 * Class provides access to task data
 */
@Component("taskStore")
@SuppressWarnings("unused")
public class TaskStore extends BaseStore<TaskModel, Long> implements ITaskStore {

    @Autowired
    private IActivityStore activityStore;
    @Autowired
    private IStatusStore statusStore;
    @Autowired
    private ICaseObjectStore caseObjectStore;
    @Autowired
    @Qualifier("userStore")
    protected IUserStore userStore;
    @Autowired
    protected ICaseStore caseStore;
    @Autowired
    protected ICaseNoteStore caseNoteStore;
    @Autowired
    protected ICaseExplanationStore caseExplanationStore;
    @Autowired
    protected ICaseActionStore caseActionStore;
    @Autowired
    protected IActorStore actorStore;
    @Autowired
    protected IProcessFieldStore processFieldStore;
    @Autowired
    @Qualifier("taskMailService")
    private ITaskMailService taskMailService;
    @Autowired
    private IActivityFieldStore activityFieldStore;


    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(TaskModel.class);
    }

    /**
     * Finds the task by ID
     *
     * @param id - ID of the task to search by
     * @return found task
     */
    @Override
    @Transactional(readOnly = true)
    public TaskModel findById(Long id) {
        TaskModel taskModel = super.findById(id);
        if (taskModel != null) {
            Hibernate.initialize(taskModel.getAssignee());
            if (taskModel.getAssignee() != null) {
                taskModel.getAssignee().setUserRoles(null);
            }
            Hibernate.initialize(taskModel.getActivity());
        }
        return taskModel;
    }

    /**
     * Helper class for making Task select & count queries that OUTER JOIN processFieldCaseValues.
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
         * are used in the other methods to create OUTER JOINs to processFieldCaseValues, ORDER BY clause and init of Task.case objects with ProcessFields in case
         * the field value doesn't exist (i.e. when OUTER JOIN returns null)
         *
         * @param paging offset, limit, sortColumn & sortDirection to use for the select object query (irrelevant when isCount = true)
         * @param isCount true to generate select count(*) query, false to generate normal select object query
         * @param processId the id of process for which the processFields will be retrieved
         */
        protected QueryCreatorForCustomFieldsJoin(AbstractPaginatedSortableSearchTermVO paging, boolean isCount, Long processId) {
            super(paging, isCount);

            List<String> additionalColumns = new ArrayList<String>();
            additionalColumns.add("t.activity.name");
            additionalColumns.add("t.case.process.name");
            additionalColumns.add("t.assignee.username");
            additionalColumns.add("t.case.status.name");
            setAdditionalSelectColumns(additionalColumns);

            this.processFields = processFieldStore.findByProcessIdPlusGlobal(processId);
        }

        /**
         * Make the query with as many outer joins to the processFieldCaseValues as there are process fields;
         * each join will be 1-1 because of the processField.id condition. Select task and all joined
         * process fields, if isCount==false or just count(*), if isCount==true: <br/>
         * <br/>
         * <code>
         *       SELECT t, cf_1, cf_2 <br/>
         *       FROM Task t <br/>
         *       INNER JOIN t.case c <br/>
         *       LEFT OUTER JOIN c.processFieldCaseValues cf_1 WITH cf_1.processField.id = :cfId_1 <br/>
         *       LEFT OUTER JOIN c.processFieldCaseValues cf_2 WITH cf_2.processField.id = :cfId_2 <br/>
         * </code>
         */
        protected void appendCustomFieldsSelectAndJoin() {

            if (isCount) {
                appendQuery("SELECT count(*)");
            } else {
                appendQuery("SELECT t");
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
            queryBuff.append(" FROM TaskModel t INNER JOIN t.case c");
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
                    sortColumn = "t." + sortColumn;
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
         * {@link net.firejack.platform.core.model.registry.process.TaskModel#TASK_CUSTOM_FIELDS_TRANSFORMER} to transform the row selection defined in
         * {@link #appendCustomFieldsSelectAndJoin} into a {@link net.firejack.platform.core.model.registry.process.TaskModel} object, with
         * properties set into that Task's case property from other objects in the row.
         *
         * @return the list of Tasks that the query + transformer return
         */
        protected List<TaskModel> queryForList() {
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
                    query.setResultTransformer(TaskModel.TASK_CUSTOM_FIELDS_TRANSFORMER);
                    return query.list();
                }
            });

            //List<Object[]> resultSet = (List<Object[]>)findByQuery(offset, limit, queryBuff.toString(), queryParams, TaskModel.TASK_CUSTOM_FIELDS_TRANSFORMER);
            List<TaskModel> taskModels = new ArrayList<TaskModel>();
            for (Object[] data : resultSet) {
                TaskModel taskModel = (TaskModel) data[0];
                taskModels.add(taskModel);
            }

            // for outer joined ProcessFieldCaseValues that don't exist for the given case, hibernate returns null
            // need to replace these nulls with objects that have at least the ProcessFieldModel set (in order to have correct column definitions in each row)
            if (processFields != null) {
                for (int i = 0; i < processFields.size(); i++) {
                    for (TaskModel taskModel : taskModels) {
                        if (taskModel.getCase().getProcessFieldCaseValues().get(i) == null) {
                            ProcessFieldCaseValue processFieldCaseValue = new ProcessFieldCaseValue();
                            processFieldCaseValue.setProcessField(processFields.get(i));
                            taskModel.getCase().getProcessFieldCaseValues().set(i, processFieldCaseValue);
                        }
                    }
                }
            }

            return taskModels;
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

        private QueryCreatorBySearchParams(TaskSearchTermVO taskSearchTermVO, boolean isCount) {

            super(taskSearchTermVO, isCount, taskSearchTermVO.getProcessId());

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" WHERE t.id = t.id"); // useless condition, just to not have the check if we've already appended 'WHERE' in every other condition

            if (taskSearchTermVO.getCustomFields() != null) {
                for (CustomFieldsSearchVO customFieldsSearchVO : taskSearchTermVO.getCustomFields()) {
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
            if (taskSearchTermVO.getProcessId() != null) {
                appendQuery(" AND c.process.id = :processId");
                addParam("processId", taskSearchTermVO.getProcessId());
            }
            if (taskSearchTermVO.getActive() != null) {
                appendQuery(" AND t.active = :active");
                addParam("active", taskSearchTermVO.getActive());
            }
            if (taskSearchTermVO.getActivityId() != null) {
                appendQuery(" AND t.activity.id = :activityId");
                addParam("activityId", taskSearchTermVO.getActivityId());
            }
            if (taskSearchTermVO.getAssigneeId() != null) {
                appendQuery(" AND t.assignee.id = :assigneeId");
                addParam("assigneeId", taskSearchTermVO.getAssigneeId());
            }
            if (StringUtils.isNotBlank(taskSearchTermVO.getDescription())) {
                appendQuery(" AND t.description like :description");
                addParam("description", "%" + taskSearchTermVO.getDescription() + "%");
            }
            if (taskSearchTermVO.getStatusId() != null) {
                appendQuery(" AND t.activity.status.id = :statusId");
                addParam("statusId", taskSearchTermVO.getStatusId());
            }
            if (taskSearchTermVO.getStartDate() != null) {
                appendQuery(" AND t.created > :startDate");
                addParam("startDate", taskSearchTermVO.getStartDate());
            }
            if (taskSearchTermVO.getEndDate() != null) {
                appendQuery(" AND t.created < :endDate");
                addParam("endDate", taskSearchTermVO.getEndDate());
            }
            if (taskSearchTermVO.getLookupPrefix() != null) {
                appendQuery(" AND t.case.process.lookup like :lookupPrefix");
                addParam("lookupPrefix", taskSearchTermVO.getLookupPrefix() + "%");
            }
            
            if (!isCount) {
                appendOrderBy();
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskModel> findAllBySearchParams(TaskSearchTermVO taskSearchTermVO, SpecifiedIdsFilter filter) {
        QueryCreatorBySearchParams queryCreatorBySearchParams = new QueryCreatorBySearchParams(taskSearchTermVO, false);
        return queryCreatorBySearchParams.queryForList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllBySearchParams(TaskSearchTermVO taskSearchTermVO, SpecifiedIdsFilter filter) {
        QueryCreatorBySearchParams queryCreatorBySearchParams = new QueryCreatorBySearchParams(taskSearchTermVO, true);
        return queryCreatorBySearchParams.queryForCount();       
    }

    private class QueryCreatorByUserId extends QueryCreatorForCustomFieldsJoin {

        private QueryCreatorByUserId(Long userId, String lookupPrefix, Boolean active, Integer offset, Integer limit, String sortColumn, String sortDirection, boolean isCount) {

            super(offset, limit, sortColumn, sortDirection, isCount, null);

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" WHERE (");
            appendQuery("    t.case.process.id IN (");
            appendQuery("        SELECT activity1.parent.id ");
            appendQuery("        FROM ActorModel a1 ");
            appendQuery("            JOIN a1.activities activity1 ");
            appendQuery("            JOIN a1.userActors userActor ");
            appendQuery("        WHERE userActor.user.id = :userId ");
            appendQuery("        AND userActor.case IS NULL) ");
            appendQuery("    OR ");
            appendQuery("    t.case.process.id IN (");
            appendQuery("        SELECT activity2.parent.id ");
            appendQuery("        FROM ActorModel a2 ");
            appendQuery("            JOIN a2.activities activity2 ");
            appendQuery("            JOIN a2.roles role ");
            appendQuery("                JOIN role.userRoles userRoles ");
            appendQuery("        WHERE userRoles.user.id = :userId) ");
            appendQuery("    OR ");
            appendQuery("    t.case.process.id IN (");
            appendQuery("        SELECT activity3.parent.id ");
            appendQuery("        FROM ActorModel a3, UserModel u ");
            appendQuery("            JOIN a3.activities activity3 ");
            appendQuery("            JOIN a3.groups grp ");
            appendQuery("        WHERE u.id = :userId AND u.registryNode.id = grp.id AND u.registryNode.class = 'GRP') ");
            appendQuery("    OR ");
            appendQuery("    t.case.id IN ( ");
            appendQuery("        SELECT u.case.id ");
            appendQuery("        FROM UserActorModel u ");
            appendQuery("        WHERE u.user.id = :userId ");
            appendQuery("        AND u.case IS NOT NULL) ");
            appendQuery(" ) ");

            addParam("userId", userId);

            if (StringUtils.isNotBlank(lookupPrefix)) {
                appendQuery(" AND t.case.process.lookup like :lookupPrefix");
                addParam("lookupPrefix", lookupPrefix + "%");
            }
            if (active != null) {
                appendQuery(" AND t.active = :active");
                addParam("active", active);
            }

            if (!isCount) {
                appendOrderBy();
            }
        }
    }

    /**
     * Finds the task by user ID
     *
     * @param userId        - ID of the user to search by
     * @param lookupPrefix  - lookup prefix
     * @param active        - flag showing whether the task is active
     * @param filter        - IDs filter
     * @param offset        - parameter specifying where to start the result list from
     * @param limit         - parameter specifying the number of the results
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return list of found tasks
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskModel> findAllByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection) {

        QueryCreatorByUserId queryCreatorByUserId = new QueryCreatorByUserId(userId, lookupPrefix, active, offset, limit, sortColumn, sortDirection, false);

        return queryCreatorByUserId.queryForList();

    }

    /**
     * @param userId       - ID of the user to search by
     * @param lookupPrefix - lookup prefix
     * @param active       - flag showing whether the task is active
     * @param filter       - IDs filter
     * @return
     * @see ITaskStore#countByUserId(java.lang.Long, java.lang.String, java.lang.Boolean, net.firejack.platform.core.model.SpecifiedIdsFilter)
     */
    @Override
    public long countByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter filter) {

        QueryCreatorByUserId queryCreatorByUserId = new QueryCreatorByUserId(userId, lookupPrefix, active, null, null, null, null, true);

        return queryCreatorByUserId.queryForCount();
        
    }

    private class QueryCreatorBelongingToUserActor extends QueryCreatorForCustomFieldsJoin {

        private QueryCreatorBelongingToUserActor(Long userId, String lookupPrefix, Integer offset, Integer limit, String sortColumn, String sortDirection, boolean isCount) {

            super(offset, limit, sortColumn, sortDirection, isCount, null); // processId = null will get only global fields

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" JOIN t.activity.actor a ");
            appendQuery(" WHERE (");
            appendQuery("    a.id IN (");
            appendQuery("        SELECT a1.id ");
            appendQuery("        FROM ActorModel a1 ");
            appendQuery("            JOIN a1.userActors userActor ");
            appendQuery("        WHERE userActor.user.id = :userId) ");
            appendQuery("    OR ");
            appendQuery("    a.id IN (");
            appendQuery("        SELECT a2.id ");
            appendQuery("        FROM ActorModel a2 ");
            appendQuery("            JOIN a2.roles role ");
            appendQuery("                JOIN role.userRoles userRoles ");
            appendQuery("        WHERE userRoles.user.id = :userId) ");
            appendQuery("    OR ");
            appendQuery("    a.id IN ( ");
            appendQuery("        SELECT a3.id ");
            appendQuery("        FROM ActorModel a3, UserModel u ");
            appendQuery("            JOIN a3.groups grp ");
            appendQuery("       WHERE u.id = :userId AND u.registryNode.id = grp.id AND u.registryNode.class = 'GRP') ");
            appendQuery("    OR ");
            appendQuery("    t.case.id IN ( ");
            appendQuery("        SELECT u.case.id ");
            appendQuery("        FROM UserActorModel u ");
            appendQuery("        WHERE u.user.id = :userId ");
            appendQuery("        AND u.case IS NOT NULL ");
            appendQuery("        AND u.actor = a) ");
            appendQuery(") AND t.active = true");

            addParam("userId", userId);

            if (StringUtils.isNotBlank(lookupPrefix)) {
                appendQuery(" AND t.case.process.lookup like :lookupPrefix");
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
     * @see ITaskStore#findBelongingToUserActor(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskModel> findBelongingToUserActor(Long userId, String lookupPrefix, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection) {

        QueryCreatorBelongingToUserActor queryCreatorBelongingToUserActor = new QueryCreatorBelongingToUserActor(userId, lookupPrefix, offset, limit, sortColumn, sortDirection, false);

        return queryCreatorBelongingToUserActor.queryForList();

    }

    /**
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return
     * @see ITaskStore#countBelongingToUserActor(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter)
     */
    @Override
    public long countBelongingToUserActor(final Long userId, final String lookupPrefix, SpecifiedIdsFilter filter) {

        QueryCreatorBelongingToUserActor queryCreatorBelongingToUserActor = new QueryCreatorBelongingToUserActor(userId, lookupPrefix, null, null, null, null, true);

        return queryCreatorBelongingToUserActor.queryForCount();

    }

    private class QueryCreatorClosedTasksForCurrentUser extends QueryCreatorForCustomFieldsJoin {

        private QueryCreatorClosedTasksForCurrentUser(Long userId, String lookupPrefix, Integer offset, Integer limit, String sortColumn, String sortDirection, boolean isCount) {

            super(offset, limit, sortColumn, sortDirection, isCount, null); // processId = null will get only global fields

            appendCustomFieldsSelectAndJoin();

            appendOuterJoinForOrder();

            appendQuery(" WHERE ");
            appendQuery(" t.active = false");
            appendQuery(" AND EXISTS (FROM CaseActionModel ca WHERE ca.taskModel = t AND ca.type = :type AND ca.user.id = :userId) ");

            addParam("type", CaseActionType.PERFORM_ACTIVITY);
            addParam("userId", userId);

            if (StringUtils.isNotBlank(lookupPrefix)) {
                appendQuery(" AND t.case.process.lookup like :lookupPrefix");
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
     * @param limit         - number of the resulting items to be listed
     * @param sortColumn    - column to sort by
     * @param sortDirection - sorting direction
     * @return
     * @see ITaskStore#findClosedTasksForCurrentUser(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskModel> findClosedTasksForCurrentUser(Long userId, String lookupPrefix, SpecifiedIdsFilter filter, Integer offset, Integer limit, String sortColumn, String sortDirection) {

        QueryCreatorClosedTasksForCurrentUser queryCreatorClosedTasksForCurrentUser = new QueryCreatorClosedTasksForCurrentUser(userId, lookupPrefix, offset, limit, sortColumn, sortDirection, false);

        return queryCreatorClosedTasksForCurrentUser.queryForList();

    }

    /**
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return
     * @see ITaskStore#countClosedTasksForCurrentUser(java.lang.Long, java.lang.String, net.firejack.platform.core.model.SpecifiedIdsFilter)
     */
    @Override
    public long countClosedTasksForCurrentUser(Long userId, String lookupPrefix, SpecifiedIdsFilter filter) {

        QueryCreatorClosedTasksForCurrentUser queryCreatorClosedTasksForCurrentUser = new QueryCreatorClosedTasksForCurrentUser(userId, lookupPrefix, null, null, null, null, true);

        return queryCreatorClosedTasksForCurrentUser.queryForCount();
        
    }

    /**
     * @param taskId        - ID of the task to be assigned
     * @param assigneeId    - ID of the user to assign to
     * @param explanationId - ID of the explanation of the assignment
     * @param noteText      - text of the note
     * @return
     * @throws UserNotInActorSetException
     * @throws TaskNotActiveException
     * @see ITaskStore#assign(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional
    public TaskModel assign(Long taskId, Long assigneeId, Long explanationId, String noteText) throws UserNotInActorSetException, TaskNotActiveException {
        Date currentDateTime = new Date();
        Long currentUserId = ContextManager.getUserInfoProvider().getId();
        UserModel currentUser = userStore.findById(currentUserId);

        TaskModel taskModel = findById(taskId);

        if (!taskModel.getActive()) {
            throw new TaskNotActiveException();
        }

        UserModel assignee = null;

        if (taskModel.getActivity() != null && taskModel.getActivity().getActor() != null) {
            if (actorStore.isUserInActorSet(assigneeId, taskModel.getActivity().getActor().getId())) {
                assignee = userStore.findById(assigneeId);
            }
        }

        if (assignee == null) {
            throw new UserNotInActorSetException("User with ID: " + assigneeId + " not in actor set for the task with ID: " + taskId);
        }

        taskModel.setAssignee(assignee);
        taskModel.setUpdateDate(currentDateTime);
        saveOrUpdate(taskModel);

        CaseModel processCase = taskModel.getCase();
        processCase.setAssignee(assignee);
        processCase.setUpdateDate(currentDateTime);
        caseStore.saveOrUpdate(processCase);

        CaseActionModel caseAction = new CaseActionModel();
        caseAction.setCase(processCase);
        caseAction.setTaskModel(taskModel);
        caseAction.setUser(assignee);
        caseAction.setPerformedOn(currentDateTime);
        caseAction.setType(CaseActionType.ASSIGNMENT);
        if (explanationId != null) {
            caseAction.setCaseExplanation(caseExplanationStore.findById(explanationId));
        }

        if (!StringUtils.isEmpty(noteText)) {
            CaseNoteModel caseNote = new CaseNoteModel();
            caseNote.setProcessCase(processCase);
            caseNote.setTaskModel(taskModel);
            caseNote.setText(noteText);
            caseNote.setUser(currentUser);
            caseNoteStore.saveOrUpdate(caseNote);

            caseAction.setCaseNote(caseNote);
        }

        caseActionStore.saveOrUpdate(caseAction);
        return taskModel;
    }

    /**
     * @param taskId          - ID of the task to be performed
     * @param assigneeId      - ID of the user to be assigned to the next task
     * @param explanationId   - ID of the explanation
     * @param noteText        - text of the note
     * @param taskDescription - description of the task
     * @return current task aster perform operation
     * @throws TaskNotActiveException
     * @throws UserNotInActorSetException
     * @see ITaskStore#perform(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public TaskModel perform(Long taskId, Long assigneeId, Long explanationId, String noteText, String taskDescription) throws TaskNotActiveException, UserNotInActorSetException {
        Long currentUserId = ContextManager.getUserInfoProvider().getId();
        UserModel currentUser = userStore.findById(currentUserId);

        Date currentDateTime = new Date();

        TaskModel taskModel = findById(taskId);

        if (!taskModel.getActive()) {
            throw new TaskNotActiveException();
        }

        if (!taskModel.getActivity().getActivityType().equals(ActivityType.SYSTEM) && !actorStore.isUserInActorSet(currentUserId, taskModel.getActivity().getActor().getId())) {
            throw new UserNotInActorSetException("Current user (ID: " + currentUserId + ") is not in the actor list for the task (ID: " + taskId + ").");
        }

        taskModel.setActive(false);
        taskModel.setCloseDate(currentDateTime);
        taskModel.setUpdateDate(currentDateTime);

        CaseModel processCase = taskModel.getCase();

        CaseActionModel caseAction = new CaseActionModel();
        caseAction.setCase(processCase);
        caseAction.setTaskModel(taskModel);
        caseAction.setUser(currentUser);
        caseAction.setPerformedOn(currentDateTime);
        caseAction.setType(CaseActionType.PERFORM_ACTIVITY);
        if (explanationId != null) {
            caseAction.setCaseExplanation(caseExplanationStore.findById(explanationId));
        }

        int nextActivityOrderPosition = taskModel.getActivity().getSortPosition() + 1;
        int maxSortPosition = taskModel.getActivity().getSortPosition();
        ActivityModel nextActivity = null;
        List<ActivityModel> activities = processCase.getProcess().getActivities();
        for (ActivityModel activity : activities) {
            if (activity.getSortPosition() == nextActivityOrderPosition) {
                nextActivity = activity;
            }
            if (maxSortPosition < activity.getSortPosition()) {
                maxSortPosition = activity.getSortPosition();
            }
        }

        TaskModel nextTaskModel = null;

        if (nextActivity != null) {
            UserModel assignee = null;
            if (assigneeId != null) {
                if (!actorStore.isUserInActorSet(assigneeId, nextActivity.getActor().getId())) {
                    throw new UserNotInActorSetException("Selected next assignee (ID: " + assigneeId + ") is not in the actor list for the activity (ID: " + nextActivity.getId() + ").");
                }
                if (!nextActivity.getActivityType().equals(ActivityType.SYSTEM) ||
                        (nextActivity.getActivityType().equals(ActivityType.SYSTEM) &&
                                nextActivityOrderPosition == maxSortPosition)) {
                    assignee = userStore.findById(assigneeId);
                }
            }

            nextTaskModel = new TaskModel();
            nextTaskModel.setActive(true);
            if (taskDescription != null) {
                nextTaskModel.setDescription(taskDescription);
            } else {
                nextTaskModel.setDescription(processCase.getDescription());
            }
            nextTaskModel.setActivity(nextActivity);
            nextTaskModel.setCase(processCase);
            nextTaskModel.setUpdateDate(currentDateTime);
            nextTaskModel.setAssignee(assignee);
            saveOrUpdate(nextTaskModel);

            processCase.setStatus(nextActivity.getStatus());

            processCase.setAssignee(assignee);
        } else {
            processCase.setCompleteDate(currentDateTime);
            processCase.setUpdateDate(currentDateTime);
            processCase.setActive(false);
        }

        if (!StringUtils.isEmpty(noteText)) {
            CaseNoteModel caseNote = new CaseNoteModel();
            caseNote.setProcessCase(processCase);
            caseNote.setTaskModel(nextTaskModel);
            caseNote.setText(noteText);
            caseNote.setUser(currentUser);
            caseNoteStore.saveOrUpdate(caseNote);

            caseAction.setCaseNote(caseNote);
        }

        saveOrUpdate(taskModel);

        caseStore.saveOrUpdate(processCase);

        caseActionStore.saveOrUpdate(caseAction);

        caseStore.saveOrUpdate(processCase);

        return nextTaskModel;
    }

    /**
     * @param taskId          - ID of the task to perform
     * @param assigneeId      - ID of the user who should be assigned to the next task
     * @param explanationId   - ID of the explanation for the task performing
     * @param noteText        - text of the note user has entered
     * @param taskDescription - description of the task
     * @return current task after performing the specified task
     * @throws TaskNotActiveException
     * @throws UserNotInActorSetException
     * @see ITaskStore#performIncludingFollowingSystemActivities(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public TaskModel performIncludingFollowingSystemActivities(Long taskId, Long assigneeId, Long explanationId, String noteText, String taskDescription) throws TaskNotActiveException, UserNotInActorSetException {
        TaskModel nextTaskModel = perform(taskId, assigneeId, explanationId, noteText, taskDescription);
        while (nextTaskModel != null && nextTaskModel.getActivity().getActivityType().equals(ActivityType.SYSTEM)) {
            nextTaskModel = perform(nextTaskModel.getId(), assigneeId, null, null, null);
        }
        return nextTaskModel;
    }

    /**
     * Rollbacks the task. If the task is of type SYSTEM then rollback all previous tasks until the non-SYSTEM task
     *
     * @param taskId - list of ID of the tasks to be performed
     * @param assigneeId - ID of the user to be assigned to the previous task
     * @param explanationId - ID of the explanation
     * @param noteText - text of the note
     * @param taskDescription - description of the task
     * @param checkCurrentActorSet check if current user is in actor set
     * @return previous task
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     * @throws TaskNotActiveException - thrown in case the task is inactive
     * @throws UserNotInActorSetException - thrown in case the user is not part of the actor set for the task
     */
    @Override
    @Transactional
    public TaskModel rollbackIncludingFollowingSystemActivity(
            Long taskId, Long assigneeId, Long explanationId, String noteText,
            String taskDescription, boolean checkCurrentActorSet)
            throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException {
        TaskModel prevTaskModel = rollback(taskId, assigneeId, explanationId, noteText, taskDescription, checkCurrentActorSet);
        while (prevTaskModel != null && prevTaskModel.getActivity().getActivityType().equals(ActivityType.SYSTEM)) {
            prevTaskModel = rollback(prevTaskModel.getId(), assigneeId, null, null, null, checkCurrentActorSet);
        }
        return prevTaskModel;
    }

    /**
     * Rollbacks tasks with id from taskIdList. If the task is of type SYSTEM
     * then rollback all previous tasks till the current task is non-SYSTEM task
     *
     * @param taskIdList - list of ID of the tasks to be performed
     * @param assigneeId - ID of the user to be assigned to the previous task
     * @param explanationId - ID of the explanation
     * @param noteText - text of the note
     * @param taskDescription - description of the task
     * @param checkCurrentActorSet check if current user is in actor set
     * @return map of tasks that we receive from rollback operation mapped by id of initial tasks from taskIdList.
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     * @throws TaskNotActiveException - thrown in case the task is inactive
     * @throws UserNotInActorSetException - thrown in case the user is not part of the actor set for the task
     */
    @Override
    @Transactional
    public Map<Long, TaskModel> rollbackIncludingFollowingSystemActivity(
            List<Long> taskIdList, Long assigneeId, Long explanationId, String noteText,
            String taskDescription, boolean checkCurrentActorSet)
            throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException {
        Map<Long, TaskModel> tasksAfterRollback = new HashMap<Long, TaskModel>();
        if (taskIdList != null) {
            for (Long taskId : taskIdList) {
                TaskModel currentTaskModelAfterRollback = rollbackIncludingFollowingSystemActivity(
                        taskId, assigneeId, explanationId, noteText, taskDescription, checkCurrentActorSet);
                tasksAfterRollback.put(taskId, currentTaskModelAfterRollback);
            }
        }
        return tasksAfterRollback;
    }

    @Override
    @Transactional
    public TaskModel rollback(Long taskId, Long assigneeId, Long explanationId,
                              String noteText, String taskDescription, Long activityId)
            throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException {
        Date currentDateTime = new Date();
        Long currentUserId = ContextManager.getUserInfoProvider().getId();
        UserModel currentUser = userStore.findById(currentUserId);

        TaskModel taskModel = findById(taskId);

        if (!taskModel.getActive()) {
            throw new TaskNotActiveException();
        }

        if (!actorStore.isUserInActorSet(currentUserId, taskModel.getActivity().getActor().getId())) {
            throw new UserNotInActorSetException("Current user (ID: " + currentUserId + ") is not in the actor list for the taskModel (ID: " + taskId + ").");
        }

        CaseModel processCase = taskModel.getCase();

        int previousActivityOrderPosition = taskModel.getActivity().getSortPosition() - 1;
        ActivityModel previousActivity = null;
        List<ActivityModel> activities = processCase.getProcess().getActivities();

        for (ActivityModel activity : activities) {
            /*if (activity.getSortPosition() > previousActivityOrderPosition) {
                break;
            }
            if (!activity.getActivityType().equals(ActivityType.SYSTEM)) {
                previousActivity = activity;
            }*/
            if (activity.getId().equals(activityId) &&
                    activity.getSortPosition() < taskModel.getActivity().getSortPosition()) {
                previousActivity = activity;
            }
        }

        if (previousActivity == null) {
            throw new NoPreviousActivityException();
        }

        UserModel assignee = null;

        if (assigneeId != null) {
            if (!actorStore.isUserInActorSet(assigneeId, previousActivity.getActor().getId())) {
                throw new UserNotInActorSetException("Selected next assignee (ID: " + assigneeId + ") is not in the actor list for the activity (ID: " + previousActivity.getId() + ").");
            }
            assignee = userStore.findById(assigneeId);
        } else {
            List<TaskModel> taskModels = previousActivity.getTaskModels();
            Long maxClosingDate = Long.MIN_VALUE;
            for (TaskModel t : taskModels) {
                if (t.getCase().getId().equals(processCase.getId())) {
                    if (t.getCloseDate() != null && t.getCloseDate().getTime() > maxClosingDate) {
                        assignee = t.getAssignee();
                        maxClosingDate = t.getCloseDate().getTime();
                    }
                }
            }
        }

        taskModel.setActive(false);
        taskModel.setCloseDate(currentDateTime);
        taskModel.setUpdateDate(currentDateTime);
        saveOrUpdate(taskModel);

        TaskModel previousTaskModel = new TaskModel();
        if (StringUtils.isBlank(taskDescription)) {
            previousTaskModel.setDescription(processCase.getDescription());
        } else {
            previousTaskModel.setDescription(taskDescription);
        }
        previousTaskModel.setActive(true);
        previousTaskModel.setActivity(previousActivity);
        previousTaskModel.setCase(processCase);
        previousTaskModel.setUpdateDate(currentDateTime);
        previousTaskModel.setAssignee(assignee);
        saveOrUpdate(previousTaskModel);

        processCase.setAssignee(assignee);
        processCase.setStatus(previousActivity.getStatus());
        caseStore.saveOrUpdate(processCase);

        CaseActionModel caseAction = new CaseActionModel();
        caseAction.setCase(processCase);
        caseAction.setTaskModel(previousTaskModel);
        caseAction.setUser(currentUser);
        caseAction.setPerformedOn(currentDateTime);
        caseAction.setType(CaseActionType.PREVIOUS_ACTIVITY);
        if (explanationId != null) {
            caseAction.setCaseExplanation(caseExplanationStore.findById(explanationId));
        }

        if (!StringUtils.isEmpty(noteText)) {
            CaseNoteModel caseNote = new CaseNoteModel();
            caseNote.setProcessCase(processCase);
            caseNote.setTaskModel(previousTaskModel);
            caseNote.setText(noteText);
            caseNote.setUser(currentUser);
            caseNoteStore.saveOrUpdate(caseNote);

            caseAction.setCaseNote(caseNote);
        }

        caseActionStore.saveOrUpdate(caseAction);

        return previousTaskModel;
    }

    /**
     * @param taskId - ID of the task to be performed
     * @param assigneeId - ID of the user to be assigned to the previous task
     * @param explanationId - ID of the explanation
     * @param noteText - text of the note
     * @param taskDescription - description of the task
     * @param checkCurrentActorSet check if current user is in actor set
     * @return previous task
     * @throws NoPreviousActivityException
     * @throws TaskNotActiveException
     * @throws UserNotInActorSetException
     * @see ITaskStore#rollback(Long, Long, Long, String, String, boolean)
     */
    @Override
    @Transactional
    public TaskModel rollback(Long taskId, Long assigneeId, Long explanationId, String noteText, String taskDescription, boolean checkCurrentActorSet) throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException {
        Date currentDateTime = new Date();
        Long currentUserId = ContextManager.getUserInfoProvider().getId();
        UserModel currentUser = userStore.findById(currentUserId);

        TaskModel taskModel = findById(taskId);

        if (!taskModel.getActive()) {
            throw new TaskNotActiveException();
        }

        if (checkCurrentActorSet) {
            if (!actorStore.isUserInActorSet(currentUserId, taskModel.getActivity().getActor().getId())) {
                throw new UserNotInActorSetException("Current user (ID: " + currentUserId + ") is not in the actor list for the taskModel (ID: " + taskId + ").");
            }
        }

        CaseModel processCase = taskModel.getCase();

        int previousActivityOrderPosition = taskModel.getActivity().getSortPosition() - 1;
        ActivityModel previousActivity = null;
        List<ActivityModel> activities = processCase.getProcess().getActivities();

        for (ActivityModel activity : activities) {
            if (activity.getSortPosition() > previousActivityOrderPosition) {
                break;
            }
            if (!activity.getActivityType().equals(ActivityType.SYSTEM)) {
                previousActivity = activity;
            }
        }

        if (previousActivity == null) {
            throw new NoPreviousActivityException();
        }

        UserModel assignee = null;

        if (assigneeId != null) {
            if (!actorStore.isUserInActorSet(assigneeId, previousActivity.getActor().getId())) {
                throw new UserNotInActorSetException("Selected next assignee (ID: " + assigneeId + ") is not in the actor list for the activity (ID: " + previousActivity.getId() + ").");
            }
            assignee = userStore.findById(assigneeId);
        } else {
            List<TaskModel> taskModels = previousActivity.getTaskModels();
            Long maxClosingDate = Long.MIN_VALUE;
            for (TaskModel t : taskModels) {
                if (t.getCase().getId().equals(processCase.getId())) {
                    if (t.getCloseDate() != null && t.getCloseDate().getTime() > maxClosingDate) {
                        assignee = t.getAssignee();
                        maxClosingDate = t.getCloseDate().getTime();
                    }
                }
            }
        }

        taskModel.setActive(false);
        taskModel.setCloseDate(currentDateTime);
        taskModel.setUpdateDate(currentDateTime);
        saveOrUpdate(taskModel);

        TaskModel previousTaskModel = new TaskModel();
        if (taskDescription != null) {
            previousTaskModel.setDescription(taskDescription);
        } else {
            previousTaskModel.setDescription(processCase.getDescription());
        }
        previousTaskModel.setActive(true);
        previousTaskModel.setActivity(previousActivity);
        previousTaskModel.setCase(processCase);
        previousTaskModel.setUpdateDate(currentDateTime);
        previousTaskModel.setAssignee(assignee);
        saveOrUpdate(previousTaskModel);

        processCase.setAssignee(assignee);
        processCase.setStatus(previousActivity.getStatus());
        caseStore.saveOrUpdate(processCase);

        CaseActionModel caseAction = new CaseActionModel();
        caseAction.setCase(processCase);
        caseAction.setTaskModel(previousTaskModel);
        caseAction.setUser(currentUser);
        caseAction.setPerformedOn(currentDateTime);
        caseAction.setType(CaseActionType.PREVIOUS_ACTIVITY);
        if (explanationId != null) {
            caseAction.setCaseExplanation(caseExplanationStore.findById(explanationId));
        }

        if (!StringUtils.isEmpty(noteText)) {
            CaseNoteModel caseNote = new CaseNoteModel();
            caseNote.setProcessCase(processCase);
            caseNote.setTaskModel(previousTaskModel);
            caseNote.setText(noteText);
            caseNote.setUser(currentUser);
            caseNoteStore.saveOrUpdate(caseNote);

            caseAction.setCaseNote(caseNote);
        }

        caseActionStore.saveOrUpdate(caseAction);

        return previousTaskModel;
    }

    @Override
    @Transactional
    public void saveOrUpdate(TaskModel taskModel) {
        boolean isNew = taskModel.getId() == null;
        super.saveOrUpdate(taskModel);
        if (isNew && taskModel.getAssignee() != null) { // if user doesn't belong to actor set assignee will be set to null
            taskMailService.sendEmailTaskIsCreated(taskModel);
        }
    }

    protected Criteria createCriteriaForFilter(Session session, SpecifiedIdsFilter filter) {
        Criteria criteria = super.createCriteriaForFilter(session, filter);
        criteria.createAlias("activity.parent", "process");
        criteria.createAlias("activity.status", "status");
        return criteria;
    }

    @Transactional(readOnly = true)
    public List<TaskModel> advancedSearch(String type, List<List<SearchQuery>> searchQueries, Paging paging) {
        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        parseAdvancedSearchRequest(searchQueries, criterions, aliases, paging);

        criterions = addCriteriaByType(type, criterions, aliases);

        return super.search(criterions, aliases, paging, true, true);
    }

    @Transactional(readOnly = true)
    public Integer advancedSearchCount(String type, List<List<SearchQuery>> searchQueries) {
        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        Map<String, String> aliases = new LinkedHashMap<String, String>();

        parseAdvancedSearchRequest(searchQueries, criterions, aliases, null);

        criterions = addCriteriaByType(type, criterions, aliases);

        return super.searchCount(criterions, aliases, true, true);
    }

    @Override
    @Transactional
    public TaskModel createWorkflowTask(ProcessModel processModel, EntityModel entityModel, Long recordId) {
        ActivityModel startActivityModel = null;
        List<ActivityModel> activityModels = activityStore.findByProcessId(processModel.getId(), null);
        for (ActivityModel activityModel : activityModels) {
            if (ActivityOrder.START.equals(activityModel.getActivityOrder())) {
                startActivityModel = activityModel;
                break;
            }
        }
        if (startActivityModel == null) {
            throw new BusinessFunctionException("Process has not Start Activity. Can't continue to create workflow.");
        }


        List<StatusModel> statuses = statusStore.findByProcessId(processModel.getId(), null);
        StatusModel startedStatus = null;
        for (StatusModel status : statuses) {
            if (StatusModel.STATUS_STARTED.equals(status.getName())) {
                startedStatus = status;
                break;
            }
        }

        Date currentDateTime = new Date();

        CaseModel caseModel = new CaseModel();
        caseModel.setProcess(processModel);
        caseModel.setDescription(processModel.getDescription());
        caseModel.setActive(true);
        caseModel.setStatus(startedStatus);
        caseModel.setStartDate(currentDateTime);
        caseModel.setUpdateDate(currentDateTime);
        caseStore.saveOrUpdate(caseModel);

        TaskModel taskModel = new TaskModel();
        taskModel.setActive(true);
        taskModel.setUpdateDate(currentDateTime);
        taskModel.setCase(caseModel);
        taskModel.setActivity(startActivityModel);
        taskModel.setDescription(caseModel.getDescription());
        saveOrUpdate(taskModel);

        Long currentUserId = ContextManager.getUserInfoProvider().getId();
        UserModel currentUser = userStore.findById(currentUserId);

        CaseObjectModel caseObjectModel = new CaseObjectModel();
        caseObjectModel.setCase(caseModel);
        caseObjectModel.setTask(taskModel);
        caseObjectModel.setStatus(startedStatus);
        caseObjectModel.setEntityId(recordId);
        caseObjectModel.setEntityType(entityModel.getLookup());
        caseObjectModel.setCreatedBy(currentUser);
        caseObjectModel.setUpdatedBy(currentUser);
        caseObjectModel.setUpdateDate(currentDateTime);
        caseObjectStore.saveOrUpdate(caseObjectModel);

        return taskModel;
    }

    @Override
    @Transactional
    public void deleteWorkflowTask(ProcessModel processModel, EntityModel entityModel, Long recordId) {
        CaseModel caseModel = caseObjectStore.findCaseByProcessAndEntity(processModel.getLookup(), recordId, entityModel.getLookup());
        if (caseModel != null) {
            caseStore.delete(caseModel);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TaskModel readWorkflowTask(final ProcessModel processModel, final EntityModel entityModel, final Long recordId) {
        final String processLookup = processModel.getLookup();
        final String entityLookup = entityModel.getLookup();

        CaseObjectModel caseObjectModel = getHibernateTemplate().execute(new HibernateCallback<CaseObjectModel>() {
            @Override
            public CaseObjectModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(CaseObjectModel.class);
                criteria = criteria.createAlias("task", "ts");
                criteria = criteria.createAlias("case", "cs");
                criteria = criteria.createAlias("cs.process", "proc");

                criteria.add(Restrictions.eq("ts.active", true));
                criteria.add(Restrictions.eq("proc.lookup", processLookup));
                criteria.add(Restrictions.eq("entityId", recordId));
                criteria.add(Restrictions.eq("entityType", entityLookup));

                criteria.setFetchMode("task", FetchMode.JOIN);
                criteria.setFetchMode("ts.activity", FetchMode.JOIN);
                criteria.setFetchMode("case", FetchMode.JOIN);
                criteria.setFetchMode("cs.process", FetchMode.JOIN);
                criteria.setFetchMode("cs.status", FetchMode.JOIN);
                return (CaseObjectModel) criteria.uniqueResult();
            }
        });

        TaskModel taskModel = null;
        if (caseObjectModel != null) {
            taskModel = caseObjectModel.getTask();
            List<ActivityFieldModel> activityFieldModels = activityFieldStore.findActivityFieldsByActivityId(taskModel.getActivity().getId());
            taskModel.getActivity().setFields(activityFieldModels);
        }
        return taskModel;
    }

    private LinkedList<Criterion> addCriteriaByType(String type, LinkedList<Criterion> criterions, Map<String, String> aliases) {
        LinkedList<Criterion> newCriterions = new LinkedList<Criterion>();
        if ("MY".equals(type)) {
            Long userId = OPFContext.getContext().getPrincipal().getUserInfoProvider().getId();
            for (Criterion criterion : criterions) {
                newCriterions.add(Restrictions.and(criterion, Restrictions.eq("assignee.id", userId)));
            }
        } else if ("TEAM".equals(type)) {
            Long userId = OPFContext.getContext().getPrincipal().getUserInfoProvider().getId();

            aliases.put("activity", "activity");
            aliases.put("activity.actor", "actor");

            DetachedCriteria actorSubCriteria = DetachedCriteria.forClass(ActorModel.class, "a")
                    .createAlias("a.userActors", "userActor")
                    .add(Restrictions.eq("userActor.user.id", userId))
                    .setProjection(Projections.property("a.id"));
            Criterion actorCriteria = Property.forName("actor.id").in(actorSubCriteria);

            for (Criterion criterion : criterions) {
                newCriterions.add(Restrictions.and(criterion, actorCriteria));
            }
        } else {
            newCriterions = criterions;
        }
        return newCriterions;
    }

}
