package net.firejack.platform.service.registry.broker.system;

import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.core.broker.ReadAllBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

@TrackDetails
@Component("readSystemListBroker")
public class ReadSystemListBroker extends ReadAllBroker<SystemModel, System> {

	@Autowired
	private ISystemStore store;

	@Override
	protected ILookupStore<SystemModel, Long> getStore() {
		return store;
	}

    protected List<SystemModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        Boolean aliases = (Boolean) request.getData().get("aliases");
        if (aliases != null) {
            List<Criterion> criterions = new ArrayList<Criterion>();
            if (aliases) {
                criterions.add(Restrictions.isNotNull("main"));
            } else {
                criterions.add(Restrictions.isNull("main"));
            }
            return getStore().findAllWithFilter(criterions, getFilter());
        } else {
            return getStore().findAllWithFilter(getFilter());
        }
    }
}
