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
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
