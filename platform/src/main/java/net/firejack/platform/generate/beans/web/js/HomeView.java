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

package net.firejack.platform.generate.beans.web.js;

import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.annotation.Properties;

import java.util.Collection;

@Properties(subpackage = "view", suffix = "View", extension = ".js")
public class HomeView extends EntityView {
    private Collection<DomainView> domains;

    public HomeView(String projectPath, String name, Collection<DomainView> domains) {
        this.projectPath = projectPath;
        this.name = StringUtils.capitalize(name);
        this.normalize = name;
        this.classPath = "";
        this.domains = domains;
        this.lookup = DiffUtils.lookup(projectPath, name);
    }

    public Collection<DomainView> getDomains() {
        return domains;
    }
}
