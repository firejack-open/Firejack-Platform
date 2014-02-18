package net.firejack.platform.core.store.version;

import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.UIDModel;
import net.firejack.platform.core.store.IStore;

import java.io.Serializable;

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

public interface IUIDStore<E extends UIDModel, ID extends Serializable> extends IStore<E, ID> {

    /**
     * @param id
     * @return
     */
    UID uidById(Long id);

    /**
     * @param uid
     * @return
     */
    UID uidById(String uid);

    /**
     * @param uidId
     * @return
     */
    E findByUIDId(Long uidId);

    /**
     * @param uid
     * @return
     */
    E findByUID(String uid);

    /**
     * @param uid
     * @return
     */
    E deleteByUID(String uid);

}
