/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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