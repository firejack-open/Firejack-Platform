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

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.store.lookup.LookupStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Class provides access to status data
 */
@Component("statusStore")
public class StatusStore extends LookupStore<StatusModel, Long> implements IStatusStore {

    @Autowired
    @Qualifier("activityStore")
    private IActivityStore activityStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(StatusModel.class);
    }

    /**
     * @param processId - ID of the process to search by
     * @param filter    - IDs filter
     * @return
     * @see IStatusStore#findByProcessId(java.lang.Long, net.firejack.platform.core.model.SpecifiedIdsFilter)
     */
    @Override
    @Transactional(readOnly = true)
    public List<StatusModel> findByProcessId(Long processId, SpecifiedIdsFilter filter) {
        return findAllByParentIdWithFilter(processId, filter);
    }

    /**
     * @param processId - ID of the process to delete by
     * @see IStatusStore#deleteByProcessId(java.lang.Long)
     */
    @Override
    @Transactional
    public void deleteByProcessId(Long processId) {
        List<StatusModel> statuses = findByProcessId(processId, null);
        for (StatusModel status : statuses) {
            delete(status);
        }
    }

    /**
     * Deletes the status
     *
     * @param status
     */
    @Override
    @Transactional
    public void delete(StatusModel status) {
        List<ActivityModel> activities = activityStore.findByStatusId(status.getId(), null);
        for (ActivityModel activity : activities) {
            activityStore.delete(activity);
        }
        super.delete(status);
    }

}
