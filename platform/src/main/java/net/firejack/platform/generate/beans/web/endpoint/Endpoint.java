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

package net.firejack.platform.generate.beans.web.endpoint;

import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.api.LocalService;

@Properties(subpackage = "endpoint", suffix = "Endpoint")
public class Endpoint extends Base {
    private String api;
    private LocalService local;
    private WebService webService;

    /**
     * @param service
     */
    public Endpoint(LocalService service) {
        super(service);
        this.local = service;
        setImports(service.getService().getImports());
    }

    public void generateWebService() {
        this.webService = new WebService(this);
    }

    public WebService getWebService() {
        return webService;
    }

    public LocalService getLocal() {
        return local;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
