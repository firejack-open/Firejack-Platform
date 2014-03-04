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

import java.util.ArrayList;
import java.util.List;

@Properties(subpackage = "view", suffix = "View", extension = ".js")
public class DomainView extends EntityView {
    private List<ViewModel> models;

    public DomainView(String projectPath, String name, String path) {
        this.projectPath = projectPath;
        this.classPath = path;
        this.name = StringUtils.capitalize(name);
        this.normalize = name;
        if (StringUtils.isNotBlank(path)) {
            projectPath += ('.' + path);
        }
        this.lookup = DiffUtils.lookup(projectPath, name);
    }

    public List<ViewModel> getModels() {
        return models;
    }

    public void addModel(ViewModel model) {
        if (models == null) {
            models = new ArrayList<ViewModel>();
        }
        this.models.add(model);
    }
}
