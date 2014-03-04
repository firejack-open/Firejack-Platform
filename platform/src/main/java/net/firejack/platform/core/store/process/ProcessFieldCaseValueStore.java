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

import net.firejack.platform.core.model.registry.process.ProcessFieldCaseValue;
import net.firejack.platform.core.store.BaseStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class provides access to process fields values for cases
 */
@Component("processFieldCaseValueStore")
public class ProcessFieldCaseValueStore extends BaseStore<ProcessFieldCaseValue, Long> implements IProcessFieldCaseValueStore {

    /**
     * Generates and executes an HQL query for finding a value of a given field for a given case
     * @see IProcessFieldCaseValueStore#findByFieldAndCase(java.lang.Long, java.lang.Long)
     * @param fieldId - ID of the field whose value is being retrieved
     * @param caseId - ID of the case for which the value is being retrieved
     * @return process field value for the case
     */
    @Override
    @Transactional
    public ProcessFieldCaseValue findByFieldAndCase(Long fieldId, Long caseId) {
        String query = "FROM ProcessFieldCaseValue pfcv WHERE pfcv.case.id = :caseId AND pfcv.processField.id = :fieldId";
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("fieldId", fieldId);
        queryParams.put("caseId", caseId);
        List<ProcessFieldCaseValue> processFieldCaseValues = findByQuery(null, null, query, queryParams, null);
        if (processFieldCaseValues.isEmpty()) {
            return null;
        } 
        return processFieldCaseValues.get(0);
    }
    
}
