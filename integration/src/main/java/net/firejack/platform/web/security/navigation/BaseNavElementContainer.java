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

import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;

import java.util.LinkedList;
import java.util.List;

/**
 * Base Navigation Element Container
 *
 * @see net.firejack.platform.web.security.navigation.INavElementContainer
 */
public abstract class BaseNavElementContainer implements INavElementContainer {

    private List<NavigationElement> navigationElements;

    /**
     *
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

    /**
     * If navigation elements already loaded then this method gets cached navigation elements. Otherwise retrieves navigation elements from store.
     *
     * @see INavElementContainer#getNavElementList()
     * @see BaseNavElementContainer#retrieveNavElements()
     */
    @Override
    public List<NavigationElement> getNavElementList() {
        if (navigationElements == null) {
            navigationElements = retrieveNavElements();
        }
        return navigationElements;
    }

    /**
     * Retrieve navigation element list
     *
     * @return retrieved navigation element list
     */
    protected abstract List<NavigationElement> retrieveNavElements();

}