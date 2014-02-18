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

import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.user.UserModel;

import java.util.List;

/**
 * Interface provides access to the data for the registry nodes of the actor type
 */
public interface IActorStore extends IRegistryNodeStore<ActorModel> {

    /**
     * Finds the actor by ID
     * @param aLong - ID of the actor
     * @return found actor
     */
    ActorModel findById(Long aLong);

    /**
     * Searches the actors by search term
     *
     * @param term - term to search by
     * @param processId - ID of the process for the actor
     * @param baseLookup - base lookup
     * @param sortColumn - column to sort the results by
     * @param sortDirection - sorting direction   @return list of found actors
     */
    List<ActorModel> findAllBySearchTerm(String term, Long processId, String baseLookup, String sortColumn, String sortDirection);

    /**
     * Saves the actor
     * @param actor - actor to be persisted
     */
    void save(ActorModel actor);

    /**
     * Lists the users assigned to the specified case
     * @param caseId - ID of the case
     * @return list of assigned users
     */
    List<UserModel> findCaseAssigneeList(Long caseId);

    /**
     * Lists the users assigned to the specified task
     * @param taskId - ID of the task
     * @param next - flag showing whether the assignees should be listed for previous, current or next task
     * @return list of assigned users
     */
    List<UserModel> findTaskAssigneeList(Long taskId, Boolean next);

    /**
     * Lists the users assigned to the specified case
     * @param caseId - ID of the case
     * @param next - flag showing whether the assignees should be listed for previous, current or next task
     * @return list of assigned users
     */
    List<UserModel> findCaseAssigneeList(Long caseId, Boolean next);

    List<UserModel> findActivityAssigneeList(Long activityId);

    /**
     * List the actors the use belongs to
     * @param userId - ID of the user
     * @return list of actors
     */
    List<ActorModel> getActorsOfUser(long userId);

    /**
     * Checks if the user is in the actor set
     * @param userId - ID of the user
     * @param actorId - ID of the actor
     * @return true if the user is in the actor set, false otherwise
     */
    boolean isUserInActorSet(long userId, long actorId);

    /**
     * Checks if the user is in the actor set
     * @param userId - ID of the user
     * @param activityId - ID of the actor
     * @return true if the user is in the actor set, false otherwise
     */
    boolean isUserInActorSetByActivity(Long userId, Long activityId);

    /**
     * Merges updated actor
     * @param actor actor to merge
     * @param usersLookupList lookup list of users assigned to the actor
     * @param roleLookupList lookup list of roles assigned to the actor
     * @param groupLookupList lookup list of groups assigned to the actor
     * @return merged actor
     */
    ActorModel mergeForGenerator(ActorModel actor, List<String> usersLookupList,
                            List<String> roleLookupList, List<String> groupLookupList);

    void assignAdminRoleToActor(List<Long> actorIdList);

}