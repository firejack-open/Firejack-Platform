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

package net.firejack.platform.core.broker;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNodePath;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.action.ActionDetectorFactory;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.sr.CachedSecuredRecordDataLoader;
import net.firejack.platform.web.security.sr.ISecuredRecordDataLoader;

import java.util.List;
import java.util.Map;

public abstract class FilteredListBroker<M extends LookupModel, RSDTO extends AbstractDTO, RQDTO extends AbstractDTO>
        extends ListBroker<M, RSDTO, RQDTO> {

    protected abstract ILookupStore<M, Long> getStore();

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

    public SpecifiedIdsFilter<Long> getFilter() {
        return OPFContext.getContext().findSpecifiedIdsFilterByType();
    }

    /**
     * @param type
     * @return
     */
    public SpecifiedIdsFilter getFilter(String type) {
        return OPFContext.getContext().findSpecifiedIdsFilterByType(type);
    }

	/**
	 *
	 * @param parentSecuredRecordId
	 * @param type
	 * @return
	 */
    public SpecifiedIdsFilter getFilter(Long parentSecuredRecordId, String type) {
        SpecifiedIdsFilter<Long> filter;
        OPFContext context = OPFContext.getContext();
        if (context.getPrincipal().isGuestPrincipal()) {
            filter = null;
        } else if (parentSecuredRecordId == null) {
            filter = context.findSpecifiedIdsFilterByType(type);
        } else {
            Map<Long, SecuredRecordNode> srMap = getSecuredRecordDataLoader().loadSecuredRecords();
            SecuredRecordNode parentSecuredRecord = srMap.get(parentSecuredRecordId);
            filter = null;
            if (parentSecuredRecord != null) {
                Long userId = context.getPrincipal().getUserInfoProvider().getId();
                Map<Long, List<UserPermission>> srPermissions =
                        getSecuredRecordDataLoader().loadSecuredRecordContextPermissions(userId);
                if (srPermissions != null) {
                    if (hasReadPermissionsForType(parentSecuredRecordId, type, srPermissions)) {
                        filter = new SpecifiedIdsFilter<Long>();
                        filter.setAll(Boolean.TRUE);
                    } else if (parentSecuredRecord.getNodePaths() != null) {
                        for (SecuredRecordNodePath srPath : parentSecuredRecord.getNodePaths()) {
                            boolean found = false;
                            if (srPath.getPathEntries() != null) {
                                for (Long srId : srPath.getPathEntries()) {
                                    if (hasReadPermissionsForType(srId, type, srPermissions)) {
                                        filter = new SpecifiedIdsFilter<Long>();
                                        filter.setAll(Boolean.TRUE);
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                    }
                }
            }
            if (filter == null) {
                filter = (SpecifiedIdsFilter<Long>) context.findSpecifiedIdsFilterByType(type);
            }
        }

        return filter;
    }

    protected ISecuredRecordDataLoader populateSecuredRecordDataLoader() {
        return new CachedSecuredRecordDataLoader();
    }

    private boolean hasReadPermissionsForType(Long securedRecordId, String type, Map<Long, List<UserPermission>> srPermissions) {
        boolean result = false;
        if (srPermissions != null) {
            List<UserPermission> permissions = srPermissions.get(securedRecordId);
            if (permissions != null) {
                for (UserPermission permission : permissions) {
                    String entityType = null;
                    if (ActionDetectorFactory.isReadPermission(permission)) {
                        entityType = ActionDetectorFactory.getReadPermissionType(permission);
                    } else if (ActionDetectorFactory.isReadAllPermission(permission)) {
                        entityType = ActionDetectorFactory.getReadAllPermissionType(permission.getPermission());
                    }
                    if (StringUtils.isNotBlank(entityType) && entityType.equalsIgnoreCase(type)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    protected String getSuccessMessage() {
        return "List has been found by id.";
    }

}