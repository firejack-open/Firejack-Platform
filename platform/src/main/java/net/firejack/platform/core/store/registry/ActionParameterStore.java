/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
