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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.domain.ActionParameterModel;
import net.firejack.platform.core.store.lookup.LookupStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ActionParameterStore extends LookupStore<ActionParameterModel, Long> implements IActionParameterStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(ActionParameterModel.class);
    }

    @Override
    @Transactional
    public void saveOrUpdate(ActionParameterModel parameter) {
        parameter.setChildCount(0);
        super.saveOrUpdate(parameter);
    }

    @Override
    @Transactional
    public void saveOrUpdateAll(List<ActionParameterModel> parameters) {
        for (ActionParameterModel parameter : parameters) {
            parameter.setChildCount(0);
        }
        super.saveOrUpdateAll(parameters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionParameterModel> findAllByActionId(Long actionId) {
        return find(null, null, "ActionParameterStore.findAllByActionId",
                "actionId", actionId);
    }

    @Override
    @Transactional
    public void deleteByActionId(Long actionId) {
        List<ActionParameterModel> parameters = findAllByActionId(actionId);
        for (ActionParameterModel parameter : parameters) {
            delete(parameter);
        }
    }

}
