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

import net.firejack.platform.core.model.registry.process.ActivityActionModel;
import net.firejack.platform.core.store.BaseStore;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("activityActionStore")
public class ActivityActionStore extends BaseStore<ActivityActionModel, Long> implements IActivityActionStore {

    @PostConstruct
    public void init() {
        setClazz(ActivityActionModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityActionModel> findActionsFromActivity(Long activityId) {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("activityFrom", "from");
        aliases.put("activityTo", "to");

        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        criterions.add(Restrictions.eq("from.id", activityId));

        return search(criterions, aliases, null);
    }

}
