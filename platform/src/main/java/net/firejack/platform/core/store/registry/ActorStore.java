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
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.core.store.process.IUserActorStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Class provides access to the data for the registry nodes of the actor type
 */
@SuppressWarnings("unused")
@Component("actorStore")
public class ActorStore extends RegistryNodeStore<ActorModel> implements IActorStore {

    @Autowired
    protected IUserActorStore userActorStore;

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Autowired
    @Qualifier("activityStore")
    private IActivityStore activityStore;

    @Autowired
    @Qualifier("configStore")
    private IConfigStore configStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(ActorModel.class);
    }

    /**
     * @param id actor id
     * @return actor
     * @see IActorStore#findById(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public ActorModel findById(final Long id) {
        ActorModel actor = super.findById(id);
        if (actor != null) {
            Hibernate.initialize(actor.getUserActors());
            if (actor.getUserActors() != null) {
                for (UserActorModel userActor : actor.getUserActors()) {
                    userActor.setProcess(null);
                    userActor.setActor(null);
                    if (userActor.getUser() != null) {
                        userActor.getUser().setUserRoles(null);
                    }
                }
            }
            Hibernate.initialize(actor.getRoles());
            if (actor.getRoles() != null) {
                for (RoleModel role : actor.getRoles()) {
                    role.setPermissions(null);
                }
            }
            Hibernate.initialize(actor.getGroups());
            if (actor.getGroups() != null) {
                for (GroupModel group : actor.getGroups()) {
                    group.setDirectory(null);
                }
            }
        }
        return actor;
    }

    /**
     *
     * @param term          - term to search by
     * @param processId     - ID of the process for the actor
     * @param baseLookup    - base lookup
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return found actors
     * @see IActorStore#findAllBySearchTerm(String, Long, String, String, String)
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<ActorModel> findAllBySearchTerm(
            final String term, final Long processId, String baseLookup, String sortColumn, String sortDirection) {
        Order order = null;
        if (sortColumn != null) {
            if (SortOrder.DESC.name().equalsIgnoreCase(sortDirection)) {
                order = Order.desc(sortColumn);
            } else {
                order = Order.asc(sortColumn);
            }
        }
        List<ActorModel> actors;
        if (processId == null) {
            List<Criterion> restrictions = new ArrayList<Criterion>();
            if (StringUtils.isNotBlank(term)) {
                restrictions.add(Restrictions.like("name", "%" + term + "%"));
            }
            if (StringUtils.isNotBlank(baseLookup)) {
                restrictions.add(Restrictions.like("lookup", baseLookup + "%"));
            }
            actors = findAllWithFilter(restrictions, null, order);
        } else {
            Criteria criteria = getSession().createCriteria(ActivityModel.class);
            criteria.add(Restrictions.eq("parent.id", processId));
            criteria.createAlias("actor", "actor");

            if (StringUtils.isNotBlank(term)) {
                criteria.add(Restrictions.like("actor.name", "%" + term + "%"));
            }
            if (StringUtils.isNotBlank(baseLookup)) {
                criteria.add(Restrictions.like("actor.lookup", baseLookup + '%'));
            }

            if (order != null) {
                criteria.addOrder(order);
            }
            criteria.setProjection(Projections.property("actor"));
            actors = (List<ActorModel>) criteria.list();
        }
        return actors;
    }

    /**
     * @param actor - actor to be persisted
     * @param isCreateAutoDescription indicates if method should create auto-description
     */
    @Override
    public void save(ActorModel actor, boolean isCreateAutoDescription) {
        boolean isNew = actor.getId() == null;

        List<UserActorModel> userActors = actor.getUserActors();

        List<UserActorModel> userActorsToAdd = new ArrayList<UserActorModel>();
        List<UserActorModel> userActorsToRemove = new ArrayList<UserActorModel>();
        Set<Long> userActorIds = new HashSet<Long>();

        if (userActors != null) {
            for (UserActorModel userActor : userActors) {

                if (userActor.getId() == null) {
                    userActorsToAdd.add(userActor);
                } else {
                    userActorIds.add(userActor.getId());
                }
            }
        }

        if (!isNew) {
            ActorModel oldActor = super.findById(actor.getId());
            if (oldActor.getUserActors() != null) {
                for (UserActorModel userActor : oldActor.getUserActors()) {
                    if (!userActorIds.contains(userActor.getId())) {
                        userActorsToRemove.add(userActor);
                    }
                }
            }
        }

        actor.setUserActors(null);

        if (isNew && (actor.getRoles() == null || actor.getRoles().isEmpty())) {
            ConfigModel config = new ConfigModel();
            config.setName("admin-role");
            config = configStore.findByExample(config);
            if (config == null) {
                throw new BusinessFunctionException("Config with name 'admin-role' has not found. Need to create it and assign it to Directory.");
            }

            RoleModel adminRole = roleStore.findByLookup(config.getValue());
            if (actor.getRoles() == null) {
                actor.setRoles(new ArrayList<RoleModel>());
            }
            actor.getRoles().add(adminRole);
        }

        super.save(actor, isCreateAutoDescription);

        for (UserActorModel userActor : userActorsToRemove) {
            userActorStore.deleteById(userActor.getId());
        }

        if (userActors != null) {
            for (UserActorModel userActor : userActors) {
                userActor.setActor(actor);
            }
        }

        if (userActorsToAdd != null) {
            for (UserActorModel userActorModel : userActorsToAdd) {
                UserModel userModel = userStore.findById(userActorModel.getUser().getId());
                userActorModel.setUser(userModel);
            }
            userActorStore.saveOrUpdateAll(userActorsToAdd);
        }

    }

    /**
     * @param lookupPrefix
     * @return
     * @see IRegistryNodeStore#findAllByLikeLookupPrefix(String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActorModel> findAllByLikeLookupPrefix(String lookupPrefix) {
        List<ActorModel> actors = super.findAllByLikeLookupPrefix(lookupPrefix);
        for (ActorModel actor : actors) {
            Hibernate.initialize(actor.getUserActors());
            Hibernate.initialize(actor.getGroups());
            Hibernate.initialize(actor.getRoles());
        }
        return actors;
    }

    /**
     * Deletes the actor
     *
     * @param actor
     */
    @Override
    @Transactional
    public void delete(ActorModel actor) {
        activityStore.deleteByActorId(actor.getId());
        super.delete(actor);
    }

    /**
     * @param caseId - ID of the case
     * @return
     * @see IActorStore#findCaseAssigneeList(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findCaseAssigneeList(Long caseId) {
        CaseModel processCase = getHibernateTemplate().get(CaseModel.class, caseId);
        if (processCase == null) {
            throw new BusinessFunctionException("Process case with id = " + caseId + " was not found.");
        }
        ProcessModel process = processCase.getProcess();
        List<ActivityModel> activities = process.getActivities();
        if (!Hibernate.isInitialized(activities)) {
            Hibernate.initialize(activities);
        }
        List<UserModel> assigneeList;
        if (activities == null || activities.isEmpty()) {
            assigneeList = null;
        } else {
            ActivityModel firstActivity = activities.get(0);
            assigneeList = userStore.findUsersBelongingToActor(firstActivity.getActor().getId());
        }
        return assigneeList;
    }

    /**
     * @param taskId - ID of the task
     * @param next   - flag showing whether the assignees should be listed for previous, current or next task
     * @return
     * @see IActorStore#findTaskAssigneeList(java.lang.Long, java.lang.Boolean)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findTaskAssigneeList(Long taskId, Boolean next) {
        TaskModel taskModel = getHibernateTemplate().get(TaskModel.class, taskId);
        if (taskModel == null) {
            throw new BusinessFunctionException("Process case task with id = " + taskId + " was not found.");
        }
        return getTaskAssigneeList(taskModel, next);
    }

    /**
     * @param caseId - ID of the case
     * @param next   - flag showing whether the assignees should be listed for previous, current or next task
     * @return
     * @see IActorStore#findCaseAssigneeList(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findCaseAssigneeList(Long caseId, Boolean next) {
        CaseModel processCase = getHibernateTemplate().get(CaseModel.class, caseId);
        if (processCase == null) {
            throw new BusinessFunctionException("Process case with id = " + caseId + " was not found.");
        }
        List<UserModel> assigneeList;
        if (processCase.getActive() == Boolean.TRUE) {
            if (!Hibernate.isInitialized(processCase.getTaskModels())) {
                Hibernate.initialize(processCase.getTaskModels());
            }
            TaskModel activeTaskModel = null;
            for (TaskModel taskModel : processCase.getTaskModels()) {
                if (taskModel.getActive()) {
                    activeTaskModel = taskModel;
                    break;
                }
            }
            if (activeTaskModel == null) {
                throw new BusinessFunctionException("Failed to find active task for case with id = " + caseId);
            } else {
                assigneeList = getTaskAssigneeList(activeTaskModel, next);
            }
        } else {
            assigneeList = new ArrayList<UserModel>();
            logger.warn("Trying to find assignee list for not active case.");
        }

        return assigneeList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findActivityAssigneeList(Long activityId) {
        List<UserModel> userModels = new ArrayList<UserModel>();
        ActivityModel activityModel = activityStore.findById(activityId);
        if (activityModel != null) {
            ActorModel actorModel = activityModel.getActor();
            if (actorModel != null) {
                userModels = userStore.findUsersBelongingToActor(actorModel.getId());
            }
        }
        return userModels;
    }

    /**
     * @param userId - ID of the user
     * @return list of actors that have assignment to the user with specified id
     * @see IActorStore#getActorsOfUser(long)
     */
    public List<ActorModel> getActorsOfUser(long userId) {
        return find(null, null, "Actor.findActorsOfUser", "userId", userId);
    }

    /**
     * @param userId  - ID of the user
     * @param actorId - ID of the actor
     * @return true if specified user is in actor set
     * @see IActorStore#isUserInActorSet(long, long)
     */
    public boolean isUserInActorSet(long userId, long actorId) {
        return count("Actor.isUserInActorSet", false, "userId", userId, "actorId", actorId) != 0;
    }

    @Override
    public boolean isUserInActorSetByActivity(Long userId, Long activityId) {
        boolean result;
        if (activityId == null || userId == null) {
            result = false;
        } else {
            ActivityModel activity = getHibernateTemplate().get(ActivityModel.class, activityId);
            result = activity != null &&
                    count("Actor.isUserInActorSet", false, "userId", userId, "actorId", activity.getActor().getId()) != 0;
        }
        return result;
    }

    /**
     * @see IActorStore#mergeForGenerator(
     *net.firejack.platform.core.model.registry.process.ActorModel, java.util.List, java.util.List, java.util.List)
     */
    @Override
    @Transactional
    public ActorModel mergeForGenerator(
            ActorModel actor, List<String> userLookupList,
            List<String> roleLookupList, List<String> groupLookupList) {
        if (actor != null) {
            ActorModel oldActor = findByUID(actor.getUid().getUid());
            if (oldActor != null) {
                if (!oldActor.getName().equals(actor.getName())) {
                    oldActor.setLookup(DiffUtils.lookup(oldActor.getPath(), actor.getName()));
                }
                oldActor.setName(actor.getName());
                oldActor.setDescription(actor.getDescription());
                oldActor.setDistributionEmail(actor.getDistributionEmail());

                List<UserModel> users = retrieveAssociatedElements(UserModel.class, userLookupList);
                List<UserActorModel> userActors;
                if (users == null || users.isEmpty()) {
                    userActors = null;
                } else {
                    userActors = new ArrayList<UserActorModel>();
                    for (UserModel user : users) {
                        UserActorModel userActor = new UserActorModel();
                        userActor.setActor(actor);
                        userActor.setUser(user);

                        userActors.add(userActor);
                    }
                }
                oldActor.setUserActors(userActors);
                List<RoleModel> roles = retrieveAssociatedElements(RoleModel.class, roleLookupList);
                oldActor.setRoles(roles);
                List<GroupModel> groups = retrieveAssociatedElements(GroupModel.class, groupLookupList);
                oldActor.setGroups(groups);
                save(oldActor, false);
            }
            actor = oldActor;
        }

        return actor;
    }

    @Override
    @Transactional
    public void saveForGenerator(ActorModel actor) {
        boolean isNew = actor.getId() == null;

        List<UserActorModel> userActors = actor.getUserActors();

        List<UserActorModel> userActorsToAdd = new ArrayList<UserActorModel>();
        List<UserActorModel> userActorsToRemove = new ArrayList<UserActorModel>();
        Set<Long> userActorIds = new HashSet<Long>();

        if (userActors != null) {
            for (UserActorModel userActor : userActors) {

                if (userActor.getId() == null) {
                    userActorsToAdd.add(userActor);
                } else {
                    userActorIds.add(userActor.getId());
                }
            }
        }

        if (!isNew) {
            ActorModel oldActor = super.findById(actor.getId());
            if (oldActor.getUserActors() != null) {
                for (UserActorModel userActor : oldActor.getUserActors()) {
                    if (!userActorIds.contains(userActor.getId())) {
                        userActorsToRemove.add(userActor);
                    }
                }
            }
        }

        actor.setUserActors(null);

        super.save(actor, false);

        for (UserActorModel userActor : userActorsToRemove) {
            userActorStore.deleteById(userActor.getId());
        }

        if (userActors != null) {
            for (UserActorModel userActor : userActors) {
                userActor.setActor(actor);
            }
        }

        if (!userActorsToAdd.isEmpty()) {
            for (UserActorModel userActorModel : userActorsToAdd) {
                UserModel userModel = userStore.findById(userActorModel.getUser().getId());
                userActorModel.setUser(userModel);
            }
            userActorStore.saveOrUpdateAll(userActorsToAdd);
        }
    }

    @Override
    @Transactional
    public void assignAdminRoleToActor(List<Long> actorIdList) {
        if (actorIdList != null && !actorIdList.isEmpty()) {
            Criteria criteria = getSession().createCriteria(getClazz());
            criteria.add(Restrictions.in("id", actorIdList));
            criteria.createAlias("roles", "roles");
            @SuppressWarnings("unchecked")
            List<ActorModel> storedActors = criteria.list();
            if (storedActors != null && !storedActors.isEmpty()) {
                ConfigModel config = new ConfigModel();
                config.setName("admin-role");
                config = configStore.findByExample(config);
                if (config == null) {
                    throw new BusinessFunctionException("Config with name 'admin-role' has not found. Need to create it and assign it to Directory.");
                }

                RoleModel adminRole = roleStore.findByLookup(config.getValue());
                if (adminRole == null) {
                    throw new BusinessFunctionException("Admin role has not found.");
                }
                for (ActorModel actor : storedActors) {
                    List<RoleModel> oldActorRoles = actor.getRoles();
                    boolean adminRoleNotAssigned = true;
                    if (oldActorRoles != null) {
                        for (RoleModel roleModel : oldActorRoles) {
                            if (adminRole.getId().equals(roleModel.getId())) {
                                adminRoleNotAssigned = false;
                                break;
                            }
                        }
                    }
                    if (adminRoleNotAssigned) {
                        List<RoleModel> newActorRoles = new ArrayList<RoleModel>();
                        if (oldActorRoles != null) {
                            newActorRoles.addAll(oldActorRoles);
                        }
                        actor.setRoles(newActorRoles);
                        getHibernateTemplate().saveOrUpdate(actor);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> retrieveAssociatedElements(Class<T> associationTargetClass, List<String> lookupList) {
        List<T> result;
        if (lookupList == null || lookupList.isEmpty()) {
            result = null;
        } else {
            Criteria criteria = getSession().createCriteria(associationTargetClass);
            criteria.add(Restrictions.in("lookup", lookupList));
            result = (List<T>) criteria.list();
        }
        return result;
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
     * Gets the list of the users assigned to the task
     *
     * @param taskModel - task the users are assigned to
     * @param next - flag showing whether the assignees should be listed for previous, current or next taskModel
     * @return list of assigned users
     */
    private List<UserModel> getTaskAssigneeList(TaskModel taskModel, Boolean next) {
        List<UserModel> assigneeList;
        if (next == null) {
            ActivityModel activity = taskModel.getActivity();
            assigneeList = userStore.findUsersBelongingToActor(activity.getActor().getId());
        } else {
            List<ActivityModel> activities = taskModel.getCase().getProcess().getActivities();
            if (!Hibernate.isInitialized(activities)) {
                Hibernate.initialize(activities);
            }
            if (activities == null || activities.isEmpty()) {
                assigneeList = null;
            } else {
                ActivityModel activity = taskModel.getActivity();
                ActivityModel requiredActivity = null;
                if (next) {
                    boolean activityPassed = false;
                    for (ActivityModel act : activities) {
                        if (activityPassed && !act.getActivityType().equals(ActivityType.SYSTEM)) {
                            requiredActivity = act;
                            break;
                        }
                        if (act.equals(activity)) {
                            activityPassed = true;
                        }
                    }
                } else {
                    ActivityModel previousActivity = null;
                    for (ActivityModel act : activities) {
                        if (act.equals(activity)) {
                            requiredActivity = previousActivity;
                            break;
                        }
                        if (!act.getActivityType().equals(ActivityType.SYSTEM)) {
                            previousActivity = act;
                        }
                    }
                }
                if (requiredActivity == null) {
                    logger.error("Required activity was not found.");
                    assigneeList = null;
                } else {
                    assigneeList = userStore.findUsersBelongingToActor(requiredActivity.getActor().getId());
                }
            }
        }
        return assigneeList;
    }

}