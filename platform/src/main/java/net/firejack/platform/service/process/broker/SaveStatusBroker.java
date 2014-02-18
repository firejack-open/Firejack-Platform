/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Status;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.store.process.IStatusStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Class encapsulates the functionality of persisting a status
 */
public abstract class SaveStatusBroker extends SaveBroker<StatusModel, Status, Status> {

    @Autowired
    @Qualifier("statusStore")
    private IStatusStore statusStore;

    /**
     * Converts a status data transfer object to a status entity
     * @param status status data transfer object
     * @return status entity
     */
    @Override
    protected StatusModel convertToEntity(Status status) {
        return factory.convertFrom(StatusModel.class, status);
    }

    /**
     * Converts a status entity to a status data transfer object
     * @param entity status entity
     * @return status data transfer object
     */
    @Override
    protected Status convertToModel(StatusModel entity) {
        return factory.convertTo(Status.class, entity);
    }

    /**
     * Invokes data access layer in order to save a status
     *
     * @param statusModel status entity
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected void save(StatusModel statusModel) throws Exception {
        statusStore.save(statusModel);
    }

}
