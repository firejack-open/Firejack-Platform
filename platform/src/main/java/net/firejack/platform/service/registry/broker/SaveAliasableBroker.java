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

package net.firejack.platform.service.registry.broker;

import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.store.registry.ISystemStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class SaveAliasableBroker<E extends RegistryNodeModel, RQDTO extends AbstractDTO, RSDTO extends AbstractDTO>
        extends SaveBroker<E, RQDTO, RSDTO> {

    @Autowired
    @Qualifier("systemStore")
    private ISystemStore systemStore;

    protected void validate(RQDTO inputObject, E entity) {
        RegistryNodeModel mainServerModel = entity.getMain();
        if (mainServerModel != null) {
            throw new BusinessFunctionException("Can't save alias " + entity.getClass().getSimpleName() + ".");
        }
        RegistryNodeModel mainSystemModel = systemStore.findById(entity.getParent().getId());
        if (mainSystemModel.getMain() != null ) {
            throw new BusinessFunctionException("Can't save " + entity.getClass().getSimpleName() + " for Alias System.");
        }

    }

}
