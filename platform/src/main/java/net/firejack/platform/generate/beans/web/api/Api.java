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

package net.firejack.platform.generate.beans.web.api;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.endpoint.Endpoint;
import net.firejack.platform.generate.beans.web.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Properties(suffix = "API")
public class Api extends Base {

    private String url;
    private List<LocalService> locals;
    private List<Endpoint> endpoints;
    private Collection<Model> models;

    public Api(String url, String path, String name) {
        this.name = StringUtils.capitalize(name);
        this.url = url;
        this.projectPath = path;
        this.classPath = "";
    }

    public List<LocalService> getLocals() {
        return locals;
    }

    public void addService(LocalService local) {
        if (locals == null) {
            locals = new ArrayList<LocalService>();
        }
        this.locals.add(local);
        addImport(local.getService());
        addImport(local.getProxy());
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void addEndpoint(Endpoint endpoint) {
        if (endpoints == null) {
            endpoints = new ArrayList();
        }
        this.endpoints.add(endpoint);
    }

    public String getUrl() {
        return url;
    }

    public Collection<Model> getModels() {
        return models;
    }

    public void setModels(Collection<Model> models) {
        this.models = models;
    }

    public String getFilePosition() {
        return this.projectPath.replaceAll("\\.", "\\" + File.separator);
    }
}
