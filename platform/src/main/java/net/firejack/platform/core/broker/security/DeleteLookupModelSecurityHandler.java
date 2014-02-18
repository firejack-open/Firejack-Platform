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

package net.firejack.platform.core.broker.security;

import net.firejack.platform.api.authority.CommonSecurityHandler;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;

public class DeleteLookupModelSecurityHandler<L extends LookupModel> extends CommonSecurityHandler {

    private IStore<L, Long> store;

    public DeleteLookupModelSecurityHandler(IStore<L, Long> store) {
        if (store == null) {
            Logger.getLogger(this.getClass()).warn("Specified store is null.");
        }
        this.store = store;
    }

    @Override
    protected String getItemPath(Action currentAction, AbstractDTO requestData) {
        String path = null;
        if (requestData instanceof SimpleIdentifier) {
            SimpleIdentifier simpleIdentifier = (SimpleIdentifier) requestData;
            Object identifier = simpleIdentifier.getIdentifier();
            if (identifier != null) {
                Long id = null;
                String lookup = null;
                if (identifier instanceof Long) {
                    id = (Long) identifier;
                } else if (identifier instanceof String) {
                    lookup = (String) identifier;
                }
                if (id == null) {
                    if (lookup != null) {
                        path = StringUtils.getPackageLookup(lookup);
                    }
                } else {
                    L foundItem = this.store.findById(id);
                    if (foundItem != null) {
                        path = foundItem.getPath();
                    }
                }
            }
        }
        return path;
    }

}