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

import net.firejack.platform.core.model.registry.process.ActivityFieldModel;
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

@Component("activityFieldStore")
@SuppressWarnings("unused")
public class ActivityFieldStore extends BaseStore<ActivityFieldModel, Long> implements IActivityFieldStore {

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(ActivityFieldModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityFieldModel> findActivityFieldsByActivityId(Long activityId) {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("activity", "actv");
        aliases.put("field", "fld");
        aliases.put("relationship", "rsp");
        aliases.put("rsp.sourceEntity", "se");
        aliases.put("rsp.targetEntity", "te");

        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        criterions.add(Restrictions.eq("actv.id", activityId));

        return search(criterions, aliases, null, false, true);
    }

}
