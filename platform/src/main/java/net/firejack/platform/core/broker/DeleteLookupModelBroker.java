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

package net.firejack.platform.core.broker;

import net.firejack.platform.core.broker.security.DeleteLookupModelSecurityHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.store.IStore;


public abstract class DeleteLookupModelBroker<L extends LookupModel> extends DeleteBroker<L> {

    private SecurityHandler handler;

    @Override
    protected SecurityHandler getSecurityHandler() {
        IStore<L, Long> store = getStore();
        if (handler == null && store != null) {
            handler = new DeleteLookupModelSecurityHandler<L>(store);
        }
        return handler;
    }

}