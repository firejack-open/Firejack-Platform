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
import net.firejack.platform.core.store.lookup.LookupStore;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("activityStore")
public class ActivityStore extends LookupStore<ActivityModel, Long> implements IActivityStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(ActivityModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityModel findById(Long id, boolean isActor, boolean isParent) {
        ActivityModel activity = super.findById(id);
        if (activity != null) {
            if (!isParent) {
                activity.setParent(null);
            }
            if (!isActor) {
                activity.setActor(null);
            }
        }
        return activity;
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityModel findWithFieldsById(Long id, SpecifiedIdsFilter filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("id", id));

        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("fields", "flds");
        aliases.put("flds.field", "fld");
        aliases.put("flds.relationship", "rsp");
        aliases.put("rsp.targetEntity", "ten");

        return findByCriteria(criterions, aliases, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityModel> findByProcessId(Long processId, SpecifiedIdsFilter filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("parent.id", processId));

        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("fields", "flds");
        aliases.put("flds.field", "fld");
        aliases.put("flds.relationship", "rsp");
        aliases.put("rsp.targetEntity", "ten");

        return findAllWithFilter(criterions, aliases, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityModel> findByStatusId(Long statusId, SpecifiedIdsFilter filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion statusCriterion = Restrictions.eq("status.id", statusId);
        criterions.add(statusCriterion);
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @Transactional
    public void deleteByActorId(Long actorId) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion idCriterion = Restrictions.eq("actor.id", actorId);
        criterions.add(idCriterion);
        List<ActivityModel> activities = findAllWithFilter(criterions, null);
        for (ActivityModel activity : activities) {
            delete(activity);
        }
    }

    @Override
    @Transactional
    public void deleteByProcessId(Long processId) {
        List<ActivityModel> activities = findByProcessId(processId, null);
        for (ActivityModel activity : activities) {
            delete(activity);
        }
    }
}
