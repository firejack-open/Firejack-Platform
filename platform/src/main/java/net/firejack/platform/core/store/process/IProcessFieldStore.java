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

import net.firejack.platform.core.model.registry.process.ProcessFieldModel;
import net.firejack.platform.core.store.version.IUIDStore;

import java.util.List;


/**
 * Interface provides access to ProcessField data
 */
public interface IProcessFieldStore extends IUIDStore<ProcessFieldModel, Long> {

    /**
     * Finds a process field by process ID.
     * If the ID is null, returns global process fields.
     * @param processId - ID of the process for which the fields are being retrieved
     * @return list of process fields
     */
    List<ProcessFieldModel> findByProcessIdPlusGlobal(Long processId);

    /**
     * Updates value of a process field
     * @param processLookup lookup of the process whose field value is being modified
     * @param entityLookup lookup of the entity which has the field
     * @param fieldLookup lookup of the field whose value is being modified
     * @param value new field value
     * @param caseId ID of the case for which the value is being modified
     */
    void updateProcessFieldValue(String processLookup, String entityLookup, String fieldLookup, Object value, Long caseId);

}