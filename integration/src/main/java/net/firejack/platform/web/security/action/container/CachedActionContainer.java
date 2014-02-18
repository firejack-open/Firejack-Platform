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

package net.firejack.platform.web.security.action.container;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CachedActionContainer implements IActionContainer {

    private static final Logger logger = Logger.getLogger(CachedActionContainer.class);

    private static final String MESSAGE_PACKAGE_LOOKUP_WAS_NOT_INITIALIZED = "Package lookup was not initialized.";
    private static final String MESSAGE_PACKAGE_ACTIONS_IS_NULL_FROM_CACHE = "Package actions gotten from cache is null.";
    private static final String MESSAGE_PACKAGE_ACTIONS_IS_NULL_FROM_LOCAL_CACHE = "Package actions gotten from local cache is null.";

    @Override
    public List<Action> getAllowableActionList() {
        List<Action> allowableCachedActions = null;
        List<Action> actionElementList = getActionList();
        if (actionElementList != null) {
            OPFContext context = OPFContext.getContext();
            OpenFlamePrincipal openFlamePrincipal = context.getPrincipal();
            allowableCachedActions = new LinkedList<Action>();
            for (Action vo : actionElementList) {
                if (openFlamePrincipal.checkUserPermission(vo)) {
                    allowableCachedActions.add(vo);
                }
            }
        }

        return allowableCachedActions;
    }

    @Override
    public List<Action> getActionList() {
        if (!OpenFlameSecurityConstants.isClientContext() && !ConfigContainer.isAppInstalled()) {
            return Collections.emptyList();
        }
        String packageLookup = OpenFlameSecurityConstants.getPackageLookup();
        if (StringUtils.isBlank(packageLookup)) {
            throw new IllegalStateException(MESSAGE_PACKAGE_LOOKUP_WAS_NOT_INITIALIZED);
        }
        CacheManager cacheManager = CacheManager.getInstance();
        List<Action> packageActions = cacheManager.getActions(packageLookup);
        if (packageActions == null) {
            if (cacheManager.isLocal()) {
                ServiceResponse<Action> response = OPFEngine.RegistryService.readActionsFromCache(packageLookup);
                if (response.isSuccess()) {
                    packageActions = response.getData();
                    if (packageActions == null) {
                        logger.warn(MESSAGE_PACKAGE_ACTIONS_IS_NULL_FROM_LOCAL_CACHE);
                    } else {
                        cacheManager.setActions(packageLookup,  packageActions);
                    }
                } else {
                    logger.error("API Response: " + response.getMessage());
                }
            } else {
                logger.warn(MESSAGE_PACKAGE_ACTIONS_IS_NULL_FROM_CACHE);
            }
        }
        return packageActions;
    }

}