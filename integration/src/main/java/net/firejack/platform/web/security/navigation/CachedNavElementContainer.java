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

package net.firejack.platform.web.security.navigation;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.site.domain.NavigationElement;
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

public class CachedNavElementContainer implements INavElementContainer {

    private static final String MESSAGE_PACKAGE_LOOKUP_WAS_NOT_INITIALIZED = "Package lookup was not initialized.";
    private static final String MESSAGE_PACKAGE_NE_LIST_IS_NULL_FROM_CACHE = "Package navigation elements gotten from cache is null.";
    private static final String MESSAGE_PACKAGE_NE_LIST_IS_NULL_FROM_LOCAL_CACHE = "Package navigation elements gotten from local cache is null.";

    private static final Logger logger = Logger.getLogger(CachedNavElementContainer.class);

    private String packageLookup;

    /**
     * @param packageLookup
     */
    public CachedNavElementContainer(String packageLookup) {
        if (StringUtils.isBlank(packageLookup)) {
            throw new IllegalArgumentException(MESSAGE_PACKAGE_LOOKUP_WAS_NOT_INITIALIZED);
        }
        this.packageLookup = packageLookup;
    }

    /**
     * @see INavElementContainer#getAllowedNavElementList()
     */
    @Override
    public List<NavigationElement> getAllowedNavElementList() {
        List<NavigationElement> navElementList = getNavElementList();
        List<NavigationElement> allowedNavigationElements = null;
        if (navElementList != null) {
            OPFContext context = OPFContext.getContext();
            OpenFlamePrincipal openFlamePrincipal = context.getPrincipal();
            allowedNavigationElements = new LinkedList<NavigationElement>();
            for (NavigationElement vo : navElementList) {
                if (openFlamePrincipal.checkUserPermission(vo)) {
                    allowedNavigationElements.add(vo);
                }
            }
        }
        return allowedNavigationElements;
    }

    @Override
    public List<NavigationElement> getNavElementList() {
        if (!OpenFlameSecurityConstants.isClientContext() && !ConfigContainer.isAppInstalled()) {
            return Collections.emptyList();
        }
        return retrieveNavElementListByPackage(this.packageLookup);
    }

    @Override
    public List<NavigationElement> getNavElementListByPackage(String packageLookup) {
        return retrieveNavElementListByPackage(packageLookup);
    }

    private List<NavigationElement> retrieveNavElementListByPackage(String packageLookup) {
        CacheManager cacheManager = CacheManager.getInstance();
        List<NavigationElement> navigationList = cacheManager.getNavigationList(packageLookup);
        if (navigationList == null) {
            if (cacheManager.isLocal()) {
                ServiceResponse<NavigationElement> response =
                        OPFEngine.SiteService.readCachedNavigationElements(packageLookup);
                if (response.isSuccess()) {
                    navigationList = response.getData();
                    if (navigationList == null) {
                        logger.warn(MESSAGE_PACKAGE_NE_LIST_IS_NULL_FROM_LOCAL_CACHE);
                    } else {
                        cacheManager.setNavigationList(packageLookup, navigationList);
                    }
                } else {
                    logger.error("API Response: " + response.getMessage());
                }
            } else {
                logger.warn(MESSAGE_PACKAGE_NE_LIST_IS_NULL_FROM_CACHE);
            }
        }
        return navigationList;
    }

}