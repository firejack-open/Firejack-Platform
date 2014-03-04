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

import net.firejack.platform.core.model.registry.process.CaseActionModel;
import net.firejack.platform.core.store.IStore;

import java.util.List;

/**
 * Interface provides access to case action data
 */
public interface ICaseActionStore extends IStore<CaseActionModel, Long> {

    /**
     * Finds all case actions by case
     * @param caseId - case ID to search by
     * @return list of case action entities
     */
    List<CaseActionModel> findAllByCase(Long caseId);

    /**
     * Saves or updates the case action entity
     * @param caseAction - case action to be saved/updated
     */
    void saveOrUpdate(CaseActionModel caseAction);

}