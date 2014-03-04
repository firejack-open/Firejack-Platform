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

import net.firejack.platform.core.model.registry.process.CaseExplanationModel;
import net.firejack.platform.core.store.version.IUIDStore;

import java.util.List;

/**
 * Interface provides access to case explanation data
 */
public interface ICaseExplanationStore extends IUIDStore<CaseExplanationModel, Long> {

    /**
     * Finds case explanation entities by search term
     * @param processId - ID of the process explanations belong to
     * @param term - the term to search by
     * @param sortColumn - column to sort by
     * @param sortDirection - sorting direction
     * @return list of case explanations
     */
    List<CaseExplanationModel> findProcessExplanationsBySearchTerm(Long processId, String term, String sortColumn, String sortDirection);

    /**
     * Deletes case explanation entity by process IF
     * @param processId - process ID
     */
    void deleteByProcessId(Long processId);

}
