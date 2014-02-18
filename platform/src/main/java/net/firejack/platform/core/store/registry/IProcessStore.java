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