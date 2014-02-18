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
