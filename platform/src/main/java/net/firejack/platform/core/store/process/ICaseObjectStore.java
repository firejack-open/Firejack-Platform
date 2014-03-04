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

import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.CaseObjectModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.utils.Tuple;

import java.util.List;

/**
 * Interface provides access to case object data
 */
public interface ICaseObjectStore extends IStore<CaseObjectModel, Long> {

    /**
     * Finds the case by process and entity
     * @param processLookup - lookup of the process to search by
     * @param entityId - ID of the entity
     * @param entityType - type of the entity
     * @return found case
     */
    CaseModel findCaseByProcessAndEntity(String processLookup, Long entityId, String entityType);

    /**
     * Finds the case by process and entity
     * @param processLookup - lookup of the process to search by
     * @param entityId - ID of the entity
     * @param entityType - type of the entity
     * @return list of found cases and objects IDs
     */
    List<Tuple<Long, CaseModel>> findCaseByProcessAndEntity(String processLookup, List<Long> entityId, String entityType);

    /**
     * Finds case object entities by assignee
     * @param assigneeId - ID of the user assigned to the case
     * @param entityType - type of the entity
     * @param statusId - ID of the case status
     * @return list of found case object entities
     */
    List<CaseObjectModel> findByAssignee(Long assigneeId, String entityType, Long statusId);

    CaseObjectModel findByTask(Long taskId);

}
