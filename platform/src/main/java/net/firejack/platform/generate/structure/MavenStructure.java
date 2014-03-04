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

package net.firejack.platform.generate.structure;

import net.firejack.platform.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class MavenStructure implements Structure {
    private String name;
    private File project;
    private File ipad;
    private File src;
    private File resource;
    private File profile;
    private File webapp;
    private File js;
    private File webInf;
    private File lib;
    private File build;
    private File sql;

    /**
     * @param project
     * @param name
     */
    public MavenStructure(File project, String name) {
        this.name = name;
        this.project = project;
        this.profile = new File(project, "profile");
        this.ipad = FileUtils.create(project, "ipad", name);
        this.resource = FileUtils.create(project, "src", "main", "resources");
        this.src = FileUtils.create(project, "src", "main", "java");
        this.webapp = FileUtils.create(project, "src", "main", "webapp");
        this.js = new File(this.webapp, "js");
        this.webInf = FileUtils.create(webapp, "WEB-INF");
        this.lib = FileUtils.create(webInf, "lib");
        this.build = FileUtils.create(project, "build");
        this.sql = FileUtils.create(project, "db");
    }

    public String getName() {
        return name;
    }

    public File getProject() {
        return project;
    }

    public File getIpad() {
        return ipad;
    }

    public File getResource() {
        return resource;
    }

    public File getSrc() {
        return src;
    }

    public File getWebapp() {
        return webapp;
    }

    @Override
    public File getJS() {
        return js;
    }

    public File getWebInf() {
        return webInf;
    }

    public File getLib() {
        return lib;
    }

    public File getProfile() {
        return profile;
    }

    public File getBuild() {
        return build;
    }

    public File getDbSql() {
        return sql;
    }

    @Override
    public void clean() {
        try {
            if (project != null) {
                FileUtils.deleteDirectory(project);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MavenStructure");
        sb.append("{project=").append(project);
        sb.append('}');
        return sb.toString();
    }
}
