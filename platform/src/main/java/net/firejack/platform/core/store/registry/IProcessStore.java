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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.api.process.domain.ActivityType;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.process.*;

import java.util.List;
import java.util.Map;

/**
 * Interface provides access to the data for the registry nodes of the process type
 */
public interface IProcessStore extends IRegistryNodeStore<ProcessModel> {

    /**
     * Finds the process by ID
     *
     * @param aLong - ID of the process
     * @return found process
     */
    ProcessModel findById(Long aLong);

    /**
     * Saves the process
     *
     * @param process - process to be persisted
     */
    void save(ProcessModel process);

    /**
     * Searches the processes by the search term
     *
     * @param term          - term to be searched by
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return list of found processes
     */
    List<ProcessModel> findAllBySearchTerm(String term, String sortColumn, String sortDirection);

    /**
     * This methods returns list of found processes based on term parameter and specified type of first activity
     * @param term word to use in search
     * @param startingActivityType type of first activity of found processes
     * @param sortColumn column to use on sorting
     * @param sortDirection sort direction
     * @param filter id filter
     * @return list of found processes
     */
    List<ProcessModel> findAllBySearchTermAndActivityType(
            String term, ActivityType startingActivityType, String sortColumn,
            String sortDirection, SpecifiedIdsFilter<Long> filter);

    /**
     * Searches the processes by the search term
     *
     * @param term          - term to be searched by
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return list of found processes
     */
    List<ProcessModel> findAllBySearchTerm(String term, String sortColumn, String sortDirection, SpecifiedIdsFilter<Long> filter);

    /**
     * Searches the processes by user ID
     *
     * @param userId - ID of the user
     * @param filter - IDs filter
     * @return list of found processes
     */
    List<ProcessModel> findAllByUserId(Long userId, SpecifiedIdsFilter<Long> filter);

    /**
     * Searches the processes by user ID
     *
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return list of found processes
     */
    List<ProcessModel> findAllByUserId(Long userId, String lookupPrefix, SpecifiedIdsFilter<Long> filter);

    /**
     * Deletes the process
     *
     * @param process - process to be deleted
     */
    void delete(ProcessModel process);


    /**
     * @param process
     * @return
     */
    ProcessModel mergeForGenerator(ProcessModel process);

    ProcessModel createWorkflow(ProcessModel processModel, Map<String, StatusModel> statuses,
                                Map<ActivityModel, List<ActivityActionModel>> activityActions, Map<String, ActorModel> actors);

}