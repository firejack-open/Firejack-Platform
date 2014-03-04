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

import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.model.registry.process.UserActorModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.IStore;

import java.util.List;

/**
 * Interface provides access to user actor data
 */
public interface IUserActorStore extends IStore<UserActorModel, Long> {

    /**
     * Searches the user actor entities by search parameters
     *
     * @param caseId        - ID of the case for the user actor
     * @param actorId       - ID of the actor
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return list of found user actor entities
     */
    List<UserActorModel> findAllBySearchParams(Long caseId, Long actorId, String sortColumn, String sortDirection);

    /**
     * Finds the next team member for the specified task
     *
     * @param taskId - ID of the task
     * @return user who is the next team member
     */
    UserModel findNextTeamMemberForTask(Long taskId);

    /**
     * Finds the next team member for the specified case
     *
     * @param caseId - ID of the case
     * @return user who is the next team member
     * @throws TaskNotActiveException - thrown in case there is no active task
     */
    UserModel findNextTeamMemberForCase(Long caseId) throws TaskNotActiveException;

    /**
     * Finds the previous team member for the specified task
     *
     * @param taskId - ID of the task
     * @return user who is the previous team member
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     */
    UserModel findPreviousTeamMemberForTask(Long taskId) throws NoPreviousActivityException;

    /**
     * Finds the previous team member for the specified case
     *
     * @param caseId - iD of the case
     * @return user who is the previous team member
     * @throws TaskNotActiveException      - thrown in case there is no active task
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     */
    UserModel findPreviousTeamMemberForCase(Long caseId) throws TaskNotActiveException, NoPreviousActivityException;

    /**
     * Checks if the user is in the actor set
     *
     * @param userId      - ID of the user to be checked on
     * @param actorLookup - lookup of the actor
     * @return true if the user is in the actor set, false otherwise
     */
    boolean checkIfUserIsActor(Long userId, String actorLookup);

    /**
     * Assigns the user to the actor set
     *
     * @param userId      - ID of the user to be assigned
     * @param actorLookup - actor lookup
     * @param version
     * @return true in case the operation was successful, false otherwise
     */
    boolean assignUserToActor(Long userId, String actorLookup, Integer version);

    /**
     * Assigns the user to the actor set
     *
     * @param caseId      - ID of the case
     * @param userId      - ID of the user to be assigned
     * @param actorLookup - actor lookup
     * @return true in case the operation was successful, false otherwise
     */
    boolean assignUserToActor(Long caseId, Long userId, String actorLookup);

}