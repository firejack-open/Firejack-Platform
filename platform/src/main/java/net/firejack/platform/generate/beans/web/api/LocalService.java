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
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.store.Method;

import java.util.List;

@Properties(suffix = "ServiceLocal")
public class LocalService extends Service {
    private Service service;
    private ProxyService proxy;

    public LocalService(Model model) {
        super(StringUtils.capitalize(model.getPrefix()) + model.getServiceName());

        this.projectPath = model.getProjectPath();
        this.classPath = model.getClassPath();
        this.serviceName = StringUtils.capitalize(model.getPrefix()) + model.getServiceName();
        this.normalize = model.getNormalize();
        setDescription(model.getDescription());
    }

    public LocalService(Service service, List<Method> methods) {
        this.service = service;
        addMethods(methods);
        setImports(service.getImports());

        this.name = service.getServiceName();
        this.normalize = service.getNormalize();
        this.projectPath = service.getProjectPath();
        this.classPath = service.getClassPath();
        this.serviceName = service.getServiceName();
        setDescription(service.getDescription());
    }

    public Service getService() {
        return service;
    }

    public void createService() {
        service = new Service(this, getMethods());
    }

    public ProxyService getProxy() {
        return proxy;
    }

    public void createProxy() {
        this.proxy = new ProxyService(service, getMethods());
    }
}
