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

import net.firejack.platform.api.process.domain.ActivityType;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.UIDModel;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.core.store.process.ICaseExplanationStore;
import net.firejack.platform.core.store.process.IProcessFieldStore;
import net.firejack.platform.core.store.process.IStatusStore;
import net.firejack.platform.core.utils.CollectionUtils;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Class provides access to the data for the registry nodes of the process type
 */
@SuppressWarnings("unused")
@Component("processStore")
public class ProcessStore extends RegistryNodeStore<ProcessModel> implements IProcessStore {

    @Autowired
    @Qualifier("activityStore")
    private IActivityStore activityStore;

    @Autowired
    @Qualifier("statusStore")
    private IStatusStore statusStore;

    @Autowired
    @Qualifier("caseExplanationStore")
    private ICaseExplanationStore caseExplanationStore;

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Autowired
    @Qualifier("processFieldStore")
    private IProcessFieldStore processFieldStore;

    @PostConstruct
    public void init() {
        setClazz(ProcessModel.class);
    }

    /**
     * @param id - ID of the process
     * @return found process
     * @see net.firejack.platform.core.store.registry.IProcessStore#findById(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModel findById(Long id) {
        ProcessModel process = super.findById(id);
        if (process != null) {
            Hibernate.initialize(process.getActivities());
            Hibernate.initialize(process.getStatuses());
            Hibernate.initialize(process.getCaseExplanations());
            List<ProcessFieldModel> fieldList = processFieldStore.findByProcessIdPlusGlobal(id);
            process.setProcessFields(fieldList);
        }
        return process;
    }

    /**
     * @param process - process to be persisted
     * @param isCreateAutoDescription - should this method create auto-description or not
     * @see IProcessStore#save(net.firejack.platform.core.model.registry.process.ProcessModel)
     */
    @Override
    @Transactional
    public void save(ProcessModel process, boolean isCreateAutoDescription) {
        boolean isNew = process.getId() == null;
        ProcessModel oldProcess = null;
        if (!isNew) {
            oldProcess = findById(process.getId());
        }

        List<StatusModel> statuses = process.getStatuses();

        List<StatusModel> statusesToAdd = new ArrayList<StatusModel>();
        List<StatusModel> statusesToUpdate = new ArrayList<StatusModel>();
        List<StatusModel> statusesToRemove = new ArrayList<StatusModel>();
        Set<Long> statusIds = new HashSet<Long>();

        if (statuses != null) {
            for (StatusModel status : statuses) {
                statusIds.add(status.getId());
                status.setChildCount(0);
                if (status.getId() == null) {
                    statusesToAdd.add(status);
                } else {
                    statusesToUpdate.add(status);
                }
            }
        }

        if (!isNew && oldProcess != null && oldProcess.getStatuses() != null) {
            for (StatusModel status : oldProcess.getStatuses()) {
                if (!statusIds.contains(status.getId())) {
                    statusesToRemove.add(status);
                }
            }
        }

        process.setStatuses(null);

        List<ActivityModel> activities = process.getActivities();

        List<ActivityModel> activitiesToAdd = new ArrayList<ActivityModel>();
        List<ActivityModel> activitiesToUpdate = new ArrayList<ActivityModel>();
        List<ActivityModel> activitiesToRemove = new ArrayList<ActivityModel>();
        Set<Long> activityIds = new HashSet<Long>();

        if (activities != null) {
            for (ActivityModel activity : activities) {
                activity.setChildCount(0);
                if (activity.getId() == null) {
                    activitiesToAdd.add(activity);
                } else {
                    activityIds.add(activity.getId());
                    activitiesToUpdate.add(activity);
                }
            }
        }

        if (!isNew && oldProcess != null && oldProcess.getActivities() != null) {
            for (ActivityModel activity : oldProcess.getActivities()) {
                if (!activityIds.contains(activity.getId())) {
                    activitiesToRemove.add(activity);
                }
            }
        }

        process.setActivities(null);

        List<CaseExplanationModel> caseExplanations = process.getCaseExplanations();

        List<CaseExplanationModel> caseExplanationsToAdd = new ArrayList<CaseExplanationModel>();
        List<CaseExplanationModel> caseExplanationsToUpdate = new ArrayList<CaseExplanationModel>();
        List<CaseExplanationModel> caseExplanationsToRemove = new ArrayList<CaseExplanationModel>();
        Set<Long> caseExplanationIds = new HashSet<Long>();

        if (caseExplanations != null) {
            for (CaseExplanationModel caseExplanation : caseExplanations) {
                caseExplanationIds.add(caseExplanation.getId());
                if (caseExplanation.getId() == null) {
                    caseExplanationsToAdd.add(caseExplanation);
                } else {
                    caseExplanationsToUpdate.add(caseExplanation);
                }
            }
        }

        if (!isNew && oldProcess != null && oldProcess.getCaseExplanations() != null) {
            for (CaseExplanationModel caseExplanation : oldProcess.getCaseExplanations()) {
                if (!caseExplanationIds.contains(caseExplanation.getId())) {
                    caseExplanationsToRemove.add(caseExplanation);
                }
            }
        }

        process.setCaseExplanations(null);


        List<ProcessFieldModel> processFields = process.getProcessFields();

        List<ProcessFieldModel> processFieldsToAdd = new ArrayList<ProcessFieldModel>();
        List<ProcessFieldModel> processFieldsToUpdate = new ArrayList<ProcessFieldModel>();
        List<ProcessFieldModel> processFieldsToRemove = new ArrayList<ProcessFieldModel>();
        Set<Long> processFieldIds = new HashSet<Long>();

        if (processFields != null) {
            for (ProcessFieldModel processField : processFields) {
                processFieldIds.add(processField.getId());
                if (processField.getId() == null) {
                    processFieldsToAdd.add(processField);
                } else {
                    processFieldsToUpdate.add(processField);
                }
            }
        }

        if (!isNew && oldProcess != null && oldProcess.getProcessFields() != null) {
            for (ProcessFieldModel processField : oldProcess.getProcessFields()) {
                if (!processFieldIds.contains(processField.getId())) {
                    processFieldsToRemove.add(processField);
                }
            }
        }

        process.setProcessFields(null);

        // need to clear the cache because of global fields which appear in all processes (normally, cache would be cleared only for this process)
        getCacheService().removeAll();
        super.save(process, isCreateAutoDescription);

        if (statuses != null) {
            for (StatusModel status : statuses) {
                status.setParent(process);
            }

            for (StatusModel status : statusesToAdd) {
                statusStore.save(status);
            }

            for (StatusModel status : statusesToUpdate) {
                statusStore.merge(status);
            }
        }

        for (ActivityModel activity : activitiesToRemove) {
            activityStore.deleteById(activity.getId());
        }

        if (activities != null && statuses != null) {
            for (ActivityModel activity : activities) {
                activity.setParent(process);
                StatusModel status = activity.getStatus();
                if (status.getId() < 0) {
                    int orderPosition = -status.getId().intValue();
                    for (StatusModel storedStatus : statuses) {
                        if (storedStatus.getSortPosition().equals(orderPosition)) {
                            activity.setStatus(storedStatus);
                        }
                    }
                }
            }

            for (ActivityModel activity : activitiesToAdd) {
                if (activity.getStatus() != null) {
                    StatusModel status = statusStore.findById(activity.getStatus().getId());
                    status.setChildCount(0);
                    activity.setStatus(status);
                }
                activityStore.save(activity);
            }

            for (ActivityModel activity : activitiesToUpdate) {
                if (activity.getStatus() != null) {
                    StatusModel status = statusStore.findById(activity.getStatus().getId());
                    status.setChildCount(0);
                    activity.setStatus(status);
                }

                activityStore.merge(activity);
            }
        }

        for (StatusModel status : statusesToRemove) {
            statusStore.deleteById(status.getId());
        }

        for (ProcessFieldModel processField : processFieldsToRemove) {
            processFieldStore.deleteById(processField.getId());
        }

        if (processFields != null) {
            for (ProcessFieldModel processField : processFields) {
                if (!processField.getGlobal()) { // global fields should have processId = NULL
                    processField.setProcess(process);
                }
            }

            for (ProcessFieldModel processField : processFieldsToAdd) {
                processFieldStore.saveOrUpdate(processField);
            }

            for (ProcessFieldModel processField : processFieldsToUpdate) {
                processFieldStore.merge(processField);
            }
        }


        for (CaseExplanationModel caseExplanation : caseExplanationsToRemove) {
            caseExplanationStore.deleteById(caseExplanation.getId());
        }

        if (caseExplanations != null) {
            for (CaseExplanationModel caseExplanation : caseExplanations) {
                caseExplanation.setProcess(process);
            }

            for (CaseExplanationModel caseExplanation : caseExplanationsToAdd) {
                caseExplanationStore.saveOrUpdate(caseExplanation);
            }

            for (CaseExplanationModel caseExplanation : caseExplanationsToUpdate) {
                caseExplanationStore.merge(caseExplanation);
            }
        }
    }

    /**
     * @param lookup lookup
     * @return found process
     * @see net.firejack.platform.core.store.lookup.ILookupStore#findByLookup(String)
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModel findByLookup(String lookup) {
        ProcessModel process = super.findByLookup(lookup);
        Hibernate.initialize(process.getStatuses());
        Hibernate.initialize(process.getActivities());
        return process;
    }

    /**
     * @param term          - term to be searched by
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return found processes
     * @see IProcessStore#findAllBySearchTerm(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProcessModel> findAllBySearchTerm(String term, String sortColumn, String sortDirection) {
        return findAllBySearchTerm(term, sortColumn, sortDirection, null);
    }

    /**
     * @param term          - term to be searched by
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return found processes
     * @see IProcessStore#findAllBySearchTerm(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProcessModel> findAllBySearchTerm(
            String term, String sortColumn, String sortDirection, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = createCriterions(term);
        return findAllBySearchTermAndActivityType(term, null, sortColumn, sortDirection, filter);
    }

    /**
     * This methods returns list of found processes based on term parameter and specified type of first activity
     * @param term word to use in search
     * @param startingActivityType type of first activity of found processes
     * @param sortColumn column to use on sorting
     * @param sortDirection sort direction
     * @param filter id filter
     * @return list of found processes
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProcessModel> findAllBySearchTermAndActivityType(
            String term, ActivityType startingActivityType, String sortColumn,
            String sortDirection, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = createCriterions(term);
        Order order = null;
        if (sortColumn != null) {
            if (SortOrder.DESC.name().equalsIgnoreCase(sortDirection)) {
                order = Order.desc(sortColumn);
            } else {
                order = Order.asc(sortColumn);
            }
        }

        List<ProcessModel> processes = findAllWithFilter(criterions, filter, order);
        if (startingActivityType != null) {
            List<ProcessModel> filteredProcesses = new ArrayList<ProcessModel>();
            for (ProcessModel process : processes) {
                List<ActivityModel> activities = process.getActivities();
                if (activities != null && !activities.isEmpty() &&
                        activities.get(0).getActivityType() == startingActivityType) {
                    filteredProcesses.add(process);
                }
            }
            processes = filteredProcesses;
        }
        for (ProcessModel process : processes) {
            process.setStatuses(null);
            process.setActivities(null);
        }

        return processes;
    }

    /**
     * @param userId - ID of the user
     * @param filter - IDs filter
     * @return found processes
     * @see IProcessStore#findAllByUserId(java.lang.Long, net.firejack.platform.core.model.SpecifiedIdsFilter<java.lang.Long>)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProcessModel> findAllByUserId(Long userId, SpecifiedIdsFilter<Long> filter) {
        return findAllByUserId(userId, null, filter);
    }

    /**
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return found processes
     * @see IProcessStore#findAllByUserId(java.lang.Long, net.firejack.platform.core.model.SpecifiedIdsFilter<java.lang.Long>)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProcessModel> findAllByUserId(Long userId, String lookupPrefix, SpecifiedIdsFilter<Long> filter) {
        return StringUtils.isBlank(lookupPrefix) ?
                find(null, null, "Process.findAllByUserId", "userId", userId) :
                find(null, null, "Process.findAllByUserIdAndLookupPrefix", "userId", userId, "lookupPrefix", lookupPrefix + "%");
    }

    /**
     * Creates criterions for the search
     *
     * @param term - term to search by
     * @return list of criterions
     */
    private List<Criterion> createCriterions(String term) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        if (!StringUtils.isEmpty(term)) {
            Criterion nameCriterion = Restrictions.like("name", "%" + term + "%");
            criterions.add(nameCriterion);
        }
        return criterions;
    }

    /**
     * @param lookupPrefix lookup prefix
     * @return found processes
     * @see IRegistryNodeStore#findAllByLikeLookupPrefix(String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProcessModel> findAllByLikeLookupPrefix(String lookupPrefix) {
        List<ProcessModel> processes = super.findAllByLikeLookupPrefix(lookupPrefix);
        for (ProcessModel process : processes) {
            Hibernate.initialize(process.getActivities());
            Hibernate.initialize(process.getStatuses());
            Hibernate.initialize(process.getCaseExplanations());
            if (process.getProcessFields() != null) {
                Hibernate.initialize(process.getProcessFields());
                for (ProcessFieldModel processField : process.getProcessFields()) {
                    Hibernate.initialize(processField.getRegistryNodeType());
                    Hibernate.initialize(processField.getRegistryNodeType().getUid());
                    Hibernate.initialize(processField.getField());
                    Hibernate.initialize(processField.getField().getUid());
                }
            }
        }
        return processes;
    }

    /**
     * @param process - process to be deleted
     * @see IProcessStore#delete(net.firejack.platform.core.model.registry.process.ProcessModel)
     */
    @Override
    @Transactional
    public void delete(ProcessModel process) {
        activityStore.deleteByProcessId(process.getId());
        statusStore.deleteByProcessId(process.getId());
        caseExplanationStore.deleteByProcessId(process.getId());
        super.delete(process);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessModel findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        ProcessModel process = (ProcessModel) criteria.uniqueResult();
        if (process != null) {
            if (CollectionUtils.isEmpty(process.getActivities())) {
                List<ActivityModel> activities = activityStore.findByProcessId(process.getId(), null);
                process.setActivities(activities);
            } else {
                Hibernate.initialize(process.getActivities());
                for (ActivityModel activity : process.getActivities()) {
                    Hibernate.initialize(activity);
                }
            }
            if (CollectionUtils.isEmpty(process.getCaseExplanations())) {
                List<CaseExplanationModel> explanations =
                        caseExplanationStore.findProcessExplanationsBySearchTerm(
                                process.getId(), null, null, null);
                process.setCaseExplanations(explanations);
            } else {
                Hibernate.initialize(process.getCaseExplanations());
                for (CaseExplanationModel caseExplanation : process.getCaseExplanations()) {
                    Hibernate.initialize(caseExplanation);
                }
            }
            if (CollectionUtils.isEmpty(process.getStatuses())) {
                List<StatusModel> statuses = statusStore.findByProcessId(process.getId(), null);
                process.setStatuses(statuses);
            } else {
                Hibernate.initialize(process.getStatuses());
                for (StatusModel status : process.getStatuses()) {
                    Hibernate.initialize(status);
                }
            }
            if (CollectionUtils.isEmpty(process.getProcessFields())) {
                List<ProcessFieldModel> processFields =
                        processFieldStore.findByProcessIdPlusGlobal(process.getId());
                process.setProcessFields(processFields);
            } else {
                Hibernate.initialize(process.getProcessFields());
                for (ProcessFieldModel processField : process.getProcessFields()) {
                    Hibernate.initialize(processField);
                    Hibernate.initialize(processField.getRegistryNodeType());
                    Hibernate.initialize(processField.getRegistryNodeType().getUid());
                }
            }
        }
        return process;
    }

    @Override
    @Transactional
    public ProcessModel mergeForGenerator(ProcessModel process) {
        ProcessModel oldProcess = findByUID(process.getUid().getUid());
        if (oldProcess != null) {
            Map<String, StatusModel> newStatuses =
                    getElementsByUidMap(process.getStatuses());
            Map<String, StatusModel> oldStatuses =
                    getElementsByUidMap(oldProcess.getStatuses());

            List<StatusModel> statusesToAdd = new ArrayList<StatusModel>();
            List<StatusModel> statusesToUpdate = new ArrayList<StatusModel>();
            List<StatusModel> statusesToRemove = new ArrayList<StatusModel>();

            for (Map.Entry<String, StatusModel> statusEntry : newStatuses.entrySet()) {
                String statusUID = statusEntry.getKey();
                StatusModel statusFromNewSet = statusEntry.getValue();
                statusFromNewSet.setChildCount(0);
                if (oldStatuses.containsKey(statusUID)) {
                    statusesToUpdate.add(statusFromNewSet);
                } else {
                    statusesToAdd.add(statusFromNewSet);
                }
            }

            for (Map.Entry<String, StatusModel> statusEntry : oldStatuses.entrySet()) {
                String statusUID = statusEntry.getKey();
                StatusModel statusFromOldSet = statusEntry.getValue();
                if (!newStatuses.containsKey(statusUID)) {
                    statusesToRemove.add(statusFromOldSet);
                }
            }

            oldProcess.setStatuses(null);

            Map<String, ActivityModel> newActivities =
                    getElementsByUidMap(process.getActivities());
            Map<String, ActivityModel> oldActivities =
                    getElementsByUidMap(oldProcess.getActivities());

            List<ActivityModel> activitiesToAdd = new ArrayList<ActivityModel>();
            List<ActivityModel> activitiesToUpdate = new ArrayList<ActivityModel>();
            List<ActivityModel> activitiesToRemove = new ArrayList<ActivityModel>();

            for (Map.Entry<String, ActivityModel> activityEntry : newActivities.entrySet()) {
                String activityUID = activityEntry.getKey();
                ActivityModel activityFromNewSet = activityEntry.getValue();
                activityFromNewSet.setChildCount(0);
                if (oldActivities.containsKey(activityUID)) {
                    activitiesToUpdate.add(activityFromNewSet);
                } else {
                    activitiesToAdd.add(activityFromNewSet);
                }
            }

            for (Map.Entry<String, ActivityModel> activityEntry : oldActivities.entrySet()) {
                String activityUID = activityEntry.getKey();
                ActivityModel activityFromOldSet = activityEntry.getValue();
                if (!newActivities.containsKey(activityUID)) {
                    activitiesToRemove.add(activityFromOldSet);
                }
            }

            oldProcess.setActivities(null);

            Map<String, CaseExplanationModel> newExplanations =
                    getElementsByUidMap(process.getCaseExplanations());
            Map<String, CaseExplanationModel> oldExplanations =
                    getElementsByUidMap(oldProcess.getCaseExplanations());

            List<CaseExplanationModel> caseExplanationsToAdd = new ArrayList<CaseExplanationModel>();
            List<CaseExplanationModel> caseExplanationsToUpdate = new ArrayList<CaseExplanationModel>();
            List<CaseExplanationModel> caseExplanationsToRemove = new ArrayList<CaseExplanationModel>();

            for (Map.Entry<String, CaseExplanationModel> caseExplanationEntry : newExplanations.entrySet()) {
                String explanationUID = caseExplanationEntry.getKey();
                CaseExplanationModel explanationFromNewSet = caseExplanationEntry.getValue();
                if (oldExplanations.containsKey(explanationFromNewSet.getUid().getUid())) {
                    caseExplanationsToUpdate.add(explanationFromNewSet);
                } else {
                    caseExplanationsToAdd.add(explanationFromNewSet);
                }
            }

            for (Map.Entry<String, CaseExplanationModel> caseExplanationEntry : oldExplanations.entrySet()) {
                String explanationUID = caseExplanationEntry.getKey();
                CaseExplanationModel explanationFromOldSet = caseExplanationEntry.getValue();
                if (!newExplanations.containsKey(explanationUID)) {
                    caseExplanationsToRemove.add(explanationFromOldSet);
                }
            }

            oldProcess.setCaseExplanations(null);

            Map<String, ProcessFieldModel> newProcessFields =
                    getElementsByUidMap(process.getProcessFields());
            Map<String, ProcessFieldModel> oldProcessFields =
                    getElementsByUidMap(oldProcess.getProcessFields());
            List<ProcessFieldModel> processFieldsToAdd = new ArrayList<ProcessFieldModel>();
            List<ProcessFieldModel> processFieldsToUpdate = new ArrayList<ProcessFieldModel>();
            List<ProcessFieldModel> processFieldsToRemove = new ArrayList<ProcessFieldModel>();

            for (Map.Entry<String, ProcessFieldModel> processFieldEntry : newProcessFields.entrySet()) {
                String processFieldUID = processFieldEntry.getKey();
                ProcessFieldModel fieldFromNewSet = processFieldEntry.getValue();
                if (oldProcessFields.containsKey(fieldFromNewSet.getUid().getUid())) {
                    processFieldsToUpdate.add(fieldFromNewSet);
                } else {
                    processFieldsToAdd.add(fieldFromNewSet);
                }
            }

            for (Map.Entry<String, ProcessFieldModel> processFieldEntry : oldProcessFields.entrySet()) {
                String processFieldUID = processFieldEntry.getKey();
                ProcessFieldModel fieldFromOldSet = processFieldEntry.getValue();
                if (!newProcessFields.containsKey(processFieldUID)) {
                    processFieldsToRemove.add(fieldFromOldSet);
                }
            }

            oldProcess.setProcessFields(null);

            if (!oldProcess.getName().equals(process.getName())) {
                oldProcess.setName(process.getName());
                oldProcess.setLookup(DiffUtils.lookup(oldProcess.getPath(), process.getName()));
            }
            oldProcess.setDescription(process.getDescription());

            super.save(oldProcess, false);

            /////////////////////////////////////////////////////////////////////
            for (StatusModel status : statusesToRemove) {
                statusStore.deleteById(status.getId());
            }

            for (StatusModel status : statusesToAdd) {
                status.setParent(oldProcess);
                statusStore.save(status);
            }

            for (StatusModel newStatus : statusesToUpdate) {
                StatusModel oldStatus = oldStatuses.get(newStatus.getUid().getUid());
                oldStatus.setSortPosition(newStatus.getSortPosition());
                oldStatus.setName(newStatus.getName());
                oldStatus.setPath(oldProcess.getLookup());
                oldStatus.setParent(oldProcess);
                oldStatus.setLookup(DiffUtils.lookup(oldStatus.getPath(), oldStatus.getName()));
                oldStatus.setDescription(newStatus.getDescription());
                statusStore.save(oldStatus);
            }
            /////////////////////////////////////////////////////////////////////

            for (ActivityModel activity : activitiesToRemove) {
                activityStore.deleteById(activity.getId());
            }

            for (ActivityModel activity : activitiesToAdd) {
                activity.setParent(oldProcess);
                if (activity.getStatus() != null) {
                    StatusModel status = statusStore.findByUID(activity.getStatus().getUid().getUid());
                    activity.setStatus(status);
                }
                activityStore.save(activity);
            }

            for (ActivityModel activity : activitiesToUpdate) {
                StatusModel status = statusStore.findByUID(activity.getStatus().getUid().getUid());
                ActivityModel oldActivity = oldActivities.get(activity.getUid().getUid());
                oldActivity.setStatus(status);
                oldActivity.setNotify(activity.getNotify());
                oldActivity.setActivityType(activity.getActivityType());
                oldActivity.setSortPosition(activity.getSortPosition());

                ActorModel actor = actorStore.findById(activity.getActor().getId());
                oldActivity.setActor(actor);

                oldActivity.setParent(oldProcess);
                oldActivity.setName(activity.getName());
                oldActivity.setPath(activity.getPath());
                oldActivity.setLookup(activity.getLookup());

                activityStore.save(oldActivity);
            }

            /////////////////////////////////////////////////////////////////////
            for (CaseExplanationModel caseExplanation : caseExplanationsToRemove) {
                caseExplanationStore.deleteById(caseExplanation.getId());
            }

            for (CaseExplanationModel caseExplanation : caseExplanationsToAdd) {
                caseExplanation.setProcess(oldProcess);
                caseExplanationStore.saveOrUpdate(caseExplanation);
            }

            for (CaseExplanationModel caseExplanation : caseExplanationsToUpdate) {
                CaseExplanationModel oldExplanation = oldExplanations.get(caseExplanation.getUid().getUid());
                oldExplanation.setProcess(oldProcess);
                oldExplanation.setLongDescription(caseExplanation.getLongDescription());
                oldExplanation.setShortDescription(caseExplanation.getShortDescription());

                caseExplanationStore.saveOrUpdate(oldExplanation);
            }

            /////////////////////////////////////////////////////////////////////
            for (ProcessFieldModel processField : processFieldsToRemove) {
                processFieldStore.deleteById(processField.getId());
            }

            for (ProcessFieldModel processField : processFieldsToAdd) {
                processField.setProcess(oldProcess);
                processFieldStore.saveOrUpdate(processField);
            }

            for (ProcessFieldModel processField : processFieldsToUpdate) {
                ProcessFieldModel oldProcessField = oldProcessFields.get(processField.getUid().getUid());
                oldProcessField.setProcess(oldProcess);
                oldProcessField.setField(processField.getField());
                oldProcessField.setRegistryNodeType(processField.getRegistryNodeType());
                oldProcessField.setFormat(processField.getFormat());
                oldProcessField.setName(processField.getName());
                oldProcessField.setGlobal(processField.getGlobal());
                oldProcessField.setOrderPosition(processField.getOrderPosition());
                oldProcessField.setValueType(processField.getValueType());

                processFieldStore.saveOrUpdate(oldProcessField);
            }
        }
        return oldProcess;
    }

    @Override
    @Transactional
    public ProcessModel createWorkflow(
            ProcessModel workflow, Map<String, StatusModel> statuses,
            Map<ActivityModel, List<ActivityActionModel>> activityActions, Map<String, ActorModel> actors) {
        if (workflow == null || statuses == null || statuses.isEmpty() || activityActions == null || activityActions.isEmpty()) {
            throw new IllegalArgumentException("Wrong workflow creation parameters.");
        }
        super.save(workflow, true);
        for (StatusModel status : statuses.values())  {
            status.setParent(workflow);
            statusStore.save(status);
        }
        for (ActorModel actorModel : actors.values()) {
            actorModel.setParent(workflow.getParent());
            actorStore.save(actorModel);
        }

        Map<String, ActivityModel> activityByName = new HashMap<String, ActivityModel>();
        for (ActivityModel activityModel : activityActions.keySet()) {
            ActorModel activityActor = activityModel.getActor();
            if (activityActor.getId() == null) {
                activityActor = actors.get(activityActor.getName());
            }
            activityModel.setActor(activityActor);
            activityModel.setParent(workflow);
            activityStore.save(activityModel);
            activityByName.put(activityModel.getName(), activityModel);
        }

        for (Map.Entry<ActivityModel, List<ActivityActionModel>> entry : activityActions.entrySet()) {
            ActivityModel fromActivity = activityByName.get(entry.getKey().getName());
            List<ActivityActionModel> activityActionModels = entry.getValue();
            for (ActivityActionModel activityAction : activityActionModels) {
                activityAction.setActivityFrom(fromActivity);
                ActivityModel toActivity = activityByName.get(activityAction.getActivityTo().getName());
                activityAction.setActivityTo(toActivity);
                StatusModel statusModel = statuses.get(activityAction.getStatus().getName());
                activityAction.setStatus(statusModel);
                getHibernateTemplate().saveOrUpdate(activityAction);
            }
        }

        return findById(workflow.getId());
    }

    private <M extends UIDModel> Map<String, M> getElementsByUidMap(List<M> modelList) {
        Map<String, M> elementsByUid = new HashMap<String, M>();
        if (modelList != null) {
            for (M m : modelList) {
                elementsByUid.put(m.getUid().getUid(), m);
            }
        }
        return elementsByUid;
    }

}