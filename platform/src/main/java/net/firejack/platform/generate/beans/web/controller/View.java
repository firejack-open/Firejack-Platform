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

package net.firejack.platform.generate.beans.web.controller;

import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;

import java.io.File;
import java.util.List;

@Properties(subpackage = "views", extension = ".jsp")
public class View extends Base {
    private Model model;
    private List<String> paths;

    /***/
    public View() {
    }

    /**
     * @param model
     */
    public View(Model model) {
        super(model);
        this.name = getName().toLowerCase();
        this.model = model;
        setProjectPath("");
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * @param paths
     */
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getFilePosition() {
        String path = this.projectPath.replaceAll("\\.", "\\" + File.separator);
        path += File.separator + properties.subpackage().replaceAll("\\.", "\\" + File.separator);
        path += File.separator + this.classPath.replaceAll("\\.", "\\" + File.separator);
        return path;
    }
}
