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

package net.firejack.platform.model.wsdl.bean;


import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Wsdl {
    private Class endpoint;
    private Class service;
    @XmlElement(name = "service")
   	@XmlElementWrapper(name = "services")
    private List<Service> services;

    public Wsdl() {
    }

    public Wsdl(Class endpoint, Class service) {
        this.endpoint = endpoint;
        this.service = service;
    }

    public Class getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Class endpoint) {
        this.endpoint = endpoint;
    }

    public Class getService() {
        return service;
    }

    public void setService(Class service) {
        this.service = service;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void addService(Service service) {
        if (services == null)
            services = new ArrayList<Service>();
        services.add(service);
    }
}
