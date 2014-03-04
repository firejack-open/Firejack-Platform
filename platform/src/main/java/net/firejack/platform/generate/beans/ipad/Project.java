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

package net.firejack.platform.generate.beans.ipad;


import java.util.List;

public class Project implements iPad {
    private String name;
    private String project;
    private List<FileReference> entities;
    private List<FileReference> controllers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<FileReference> getEntities() {
        return entities;
    }

    public void setEntities(List<FileReference> entities) {
        this.entities = entities;
    }

    public List<FileReference> getControllers() {
        return controllers;
    }

    public void setControllers(List<FileReference> controllers) {
        this.controllers = controllers;
    }

    @Override
    public String getFilePosition() {
        return "${name}.xcodeproj";
    }
}
