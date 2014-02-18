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

package net.firejack.platform.web.security.sr;

import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CachedSecuredRecordInfoContainer implements ISecuredRecordInfoContainer {

    private ISecuredRecordDataLoader securedRecordDataLoader;

    /**
     * @return
     */
    public ISecuredRecordDataLoader getSecuredRecordDataLoader() {
        if (securedRecordDataLoader == null) {
            securedRecordDataLoader = populateSecuredRecordDataLoader();
        }
        return securedRecordDataLoader;
    }

    @Override
    public List<SecuredRecordNode> loadSecuredRecords() {
        if (!OpenFlameSecurityConstants.isClientContext() && !ConfigContainer.isAppInstalled()) {
            return Collections.emptyList();
        }
        Map<Long, SecuredRecordNode> srMap = getSecuredRecordDataLoader().loadSecuredRecords();
        return srMap == null ? new ArrayList<SecuredRecordNode>() : new ArrayList<SecuredRecordNode>(srMap.values());
    }

    protected ISecuredRecordDataLoader populateSecuredRecordDataLoader() {
        return new CachedSecuredRecordDataLoader();
    }

}