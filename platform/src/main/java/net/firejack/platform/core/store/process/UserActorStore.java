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

import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.core.utils.StringUtils;
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
 * Class provides access to user actor data
 */
@Component("userActorStore")
public class UserActorStore extends BaseStore<UserActorModel, Long> implements IUserActorStore {

    @Autowired
    protected ICaseStore caseStore;

    @Autowired
    protected ITaskStore taskStore;

    @Autowired
    @Qualifier("userStore")
    protected IUserStore userStore;

    @Autowired
    protected IActorStore actorStore;

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(UserActorModel.class);
    }

    /**
     * @param caseId        - ID of the case for the user actor
     * @param actorId       - ID of the actor
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return returns all found user actors
     * @see IUserActorStore#findAllBySearchParams(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserActorModel> findAllBySearchParams(Long caseId, Long actorId, String sortColumn, String sortDirection) {
        List<Criterion> criterions = createCriterions(caseId, actorId);
        Order order = null;
        if (sortColumn != null) {
            if (SortOrder.DESC.name().equalsIgnoreCase(sortDirection)) {
                order = Order.desc(sortColumn);
            } else {
                order = Order.asc(sortColumn);
            }
        }

        List<UserActorModel> userActors;
        if (order != null) {
            userActors = findAllWithFilter(criterions, null, order);
        } else {
            userActors = findAllWithFilter(criterions, null);
        }

        return userActors;
    }

    /**
     * @param taskId - ID of the task
     * @return next team member for the task
     * @see IUserActorStore#findNextTeamMemberForTask(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public UserModel findNextTeamMemberForTask(Long taskId) {
        TaskModel taskModel = taskStore.findById(taskId);
        int nextActivityOrderPosition = taskModel.getActivity().getSortPosition() + 1;
        ActivityModel nextActivity = null;
        CaseModel processCase = taskModel.getCase();
        List<ActivityModel> activities = processCase.getProcess().getActivities();
        for (ActivityModel activity : activities) {
            if (activity.getSortPosition() == nextActivityOrderPosition) {
                nextActivity = activity;
            }
        }
        UserModel user = null;
        if (nextActivity != null && nextActivity.getActor() != null) {
            Long actorId = nextActivity.getActor().getId();
            List<UserActorModel> userActors = findAllBySearchParams(processCase.getId(), actorId, "id", "ASC");
            if (!userActors.isEmpty()) {
                user = userActors.get(0).getUser();
            }
        }
        return user;
    }

    /**
     * @param caseId - ID of the case
     * @return next team member for the case
     * @throws TaskNotActiveException
     * @see IUserActorStore#findNextTeamMemberForCase(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public UserModel findNextTeamMemberForCase(Long caseId) throws TaskNotActiveException {
        CaseModel processCase = caseStore.findByIdNoNulls(caseId);
        List<TaskModel> taskModels = processCase.getTaskModels();
        Long taskId = null;
        for (TaskModel taskModel : taskModels) {
            if (taskModel.getActive()) {
                taskId = taskModel.getId();
            }
        }
        if (taskId == null) {
            throw new TaskNotActiveException();
        }
        return findNextTeamMemberForTask(taskId);
    }

    /**
     * @param taskId - ID of the task
     * @return previous team member for the task
     * @throws NoPreviousActivityException
     * @see IUserActorStore#findPreviousTeamMemberForTask(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public UserModel findPreviousTeamMemberForTask(Long taskId) throws NoPreviousActivityException {
        TaskModel taskModel = taskStore.findById(taskId);
        int previousActivityOrderPosition = taskModel.getActivity().getSortPosition() - 1;
        ActivityModel previousActivity = null;
        CaseModel processCase = taskModel.getCase();
        List<ActivityModel> activities = processCase.getProcess().getActivities();
        for (ActivityModel activity : activities) {
            if (activity.getSortPosition() == previousActivityOrderPosition) {
                previousActivity = activity;
            }
        }
        if (previousActivity == null) {
            throw new NoPreviousActivityException();
        }
        UserModel user = null;
        if (previousActivity.getActor() != null) {
            Long actorId = previousActivity.getActor().getId();
            List<UserActorModel> userActors = findAllBySearchParams(processCase.getId(), actorId, "id", "ASC");
            if (!userActors.isEmpty()) {
                user = userActors.get(0).getUser();
            }
        }
        return user;
    }

    /**
     * @param caseId - iD of the case
     * @return previous team member for the task
     * @throws TaskNotActiveException
     * @throws NoPreviousActivityException
     * @see IUserActorStore#findPreviousTeamMemberForCase(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public UserModel findPreviousTeamMemberForCase(Long caseId) throws TaskNotActiveException, NoPreviousActivityException {
        CaseModel processCase = caseStore.findByIdNoNulls(caseId);
        List<TaskModel> taskModels = processCase.getTaskModels();
        Long taskId = null;
        for (TaskModel taskModel : taskModels) {
            if (taskModel.getActive()) {
                taskId = taskModel.getId();
            }
        }
        if (taskId == null) {
            throw new TaskNotActiveException();
        }
        return findPreviousTeamMemberForTask(taskId);
    }

    /**
     * @param userId      - ID of the user to be checked on
     * @param actorLookup - lookup of the actor
     * @return true if user is an actor, otherwise returns false
     * @see IUserActorStore#checkIfUserIsActor(java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkIfUserIsActor(Long userId, String actorLookup) {
        int count = count("UserActorModel.findIdListByUserAndByActorLookup",
                true, "userId", userId, "actorLookup", actorLookup);
        return count > 0;
    }

    /**
     * @param userId      - ID of the user to be assigned
     * @param actorLookup - actor lookup
     * @param version version?
     * @return returns true if user successfully assigned to the actor. Otherwise returns false.
     * @see IUserActorStore#assignUserToActor(Long, String, Integer)
     */
    @Override
    @Transactional
    public boolean assignUserToActor(Long userId, String actorLookup, Integer version) {
        return assignUserToActor(null, userId, actorLookup);
    }

    /**
     * @param caseId      - ID of the case
     * @param userId      - ID of the user to be assigned
     * @param actorLookup - actor lookup
     * @return return s true if user assigned to actor sucessfully. Otherwise returns false
     * @see IUserActorStore#assignUserToActor(Long, String, Integer)
     */
    @Override
    @Transactional
    public boolean assignUserToActor(Long caseId, Long userId, String actorLookup) {
        boolean result = false;
        if (StringUtils.isNotBlank(actorLookup) && userId != null) {
            ActorModel actor = actorStore.findByLookup(actorLookup);
            if (actor != null) {
                if (!Hibernate.isInitialized(actor.getUserActors())) {
                    Hibernate.initialize(actor.getUserActors());
                }
                List<UserActorModel> userActors = actor.getUserActors();
                boolean notFound = true;
                if (userActors != null) {
                    for (UserActorModel userActor : userActors) {
                        if (userId.equals(userActor.getUser().getId())) {
                            notFound = false;
                            break;
                        }
                    }
                }
                if (notFound) {
                    UserModel user = userStore.findById(userId);
                    CaseModel processCase = caseId == null ? null : caseStore.findById(caseId);
                    UserActorModel userActor = new UserActorModel();
                    userActor.setActor(actor);
                    userActor.setUser(user);
                    userActor.setCase(processCase);
                    saveOrUpdate(userActor);
                }
                result = true;
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void saveOrUpdateAll(List<UserActorModel> userActors) {
        if (userActors != null && ! userActors.isEmpty()) {
            Set<Long> userIdSet = new HashSet<Long>();
            Set<Long> actorIdSet = new HashSet<Long>();
            Set<Long> processIdSet = new HashSet<Long>();
            Set<Long> caseIdSet = new HashSet<Long>();
            for (UserActorModel userActor : userActors) {
                checkModelConsistence(userActor.getUser());
                checkModelConsistence(userActor.getActor());
                //checkModelConsistence(userActor.getCase());

                userIdSet.add(userActor.getUser().getId());
                actorIdSet.add(userActor.getActor().getId());
                if (userActor.getProcess() != null && userActor.getProcess().getId() != null) {
                    processIdSet.add(userActor.getProcess().getId());
                }
                if (userActor.getCase() != null && userActor.getCase().getId() != null) {
                    caseIdSet.add(userActor.getCase().getId());
                }
            }
            Map<Long, UserModel> users = loadRecords(userIdSet, UserModel.class);
            Map<Long, ActorModel> actors = loadRecords(actorIdSet, ActorModel.class);
            Map<Long, ProcessModel> processes = processIdSet.isEmpty() ?
                    null : loadRecords(processIdSet, ProcessModel.class);
            Map<Long, CaseModel> cases;
            if (caseIdSet.isEmpty()) {
                cases = null;
            } else {
                cases = loadRecords(caseIdSet, CaseModel.class);
            }
            for (UserActorModel userActor : userActors) {
                userActor.setUser(users.get(userActor.getUser().getId()));
                userActor.setActor(actors.get(userActor.getActor().getId()));
                if (userActor.getProcess() != null && processes != null ) {
                    userActor.setProcess(processes.get(userActor.getProcess().getId()));
                }
                if (userActor.getProcess() != null && userActor.getProcess().getId() != null && cases != null) {
                    userActor.setCase(cases.get(userActor.getCase().getId()));
                }
            }

            super.saveOrUpdateAll(userActors);
        }
    }

    private void checkModelConsistence(BaseEntityModel model) {
        if (model == null || model.getId() == null) {
            throw new IllegalArgumentException("Object to save has inconsistent attributes.");
        }
    }

    private <T extends BaseEntityModel> Map<Long, T> loadRecords(Collection<Long> idList, Class<T> modelClass) {
        Map<Long, T> map;
        if (modelClass == null || idList == null || idList.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            Criteria criteria = getSession().createCriteria(modelClass);
            if (idList.size() == 1) {
                criteria.add(Restrictions.eq("id", idList.iterator().next()));
            } else {
                criteria.add(Restrictions.in("id", idList));
            }
            @SuppressWarnings("unchecked")
            List<T> modelList = (List<T>) criteria.list();
            if (modelList == null || modelList.isEmpty() || idList.size() != modelList.size()) {
                throw new OpenFlameRuntimeException("Object of type [" + modelClass.getName() + "] doesn't exist in database.");
            } else {
                map = new HashMap<Long, T>();
                for (T model : modelList) {
                    map.put(model.getId(), model);
                }
            }
        }
        return map;
    }

    /**
     * Creates criterions for the search
     *
     * @param caseId  - ID of the case
     * @param actorId - ID of the actor
     * @return list of the criterions
     */
    private List<Criterion> createCriterions(Long caseId, Long actorId) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (caseId != null) {
            Criterion caseCriterion = Restrictions.eq("case.id", caseId);
            criterions.add(caseCriterion);
        }

        if (actorId != null) {
            Criterion actorCriterion = Restrictions.eq("actor.id", actorId);
            criterions.add(actorCriterion);
        }

        return criterions;
    }

}
