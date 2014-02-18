/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.security.extension.sr;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.sr.CachedSecuredRecordDataLoader;

import java.util.List;
import java.util.Map;

@Deprecated
public class ConsoleSecuredRecordDataLoader extends CachedSecuredRecordDataLoader {

    @Override
    protected Map<Long, List<UserPermission>> loadSRContextPermissionsForLocalUse() {
        OPFContext context = OPFContext.getContext();
        OpenFlamePrincipal principal = context.getPrincipal();
        return principal.isGuestPrincipal() ? null :
                CacheManager.getInstance().getSecuredRecordContextPermissions(
                        principal.getUserInfoProvider().getId());
    }

    @Override
    protected Map<Long, SecuredRecordNode> loadSecuredRecordsForLocalUse() {
        return CacheManager.getInstance().getSecuredRecords();
    }

}