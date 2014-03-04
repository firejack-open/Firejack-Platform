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

package net.firejack.platform.web.security.resource;

import java.util.List;

public class CachedResourceLocationContainerFactory implements IResourceLocationContainerFactory {

    private String packageLookup;
    private List<IResourceLocationContainerRule> rules;

    /**
     * @param packageLookup package lookup
     */
    public CachedResourceLocationContainerFactory(String packageLookup) {
        this.packageLookup = packageLookup;
    }

    public void setRules(List<IResourceLocationContainerRule> rules) {
        this.rules = rules;
    }

    @Override
    public IResourceLocationContainer produceMaskedResourceContainer() {
        CachedResourceLocationContainer resourceLocationContainer = new CachedResourceLocationContainer(packageLookup);
        resourceLocationContainer.setRules(rules);
        return resourceLocationContainer;
    }

}