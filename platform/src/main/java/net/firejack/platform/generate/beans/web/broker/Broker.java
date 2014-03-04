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

package net.firejack.platform.generate.beans.web.broker;

import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.MethodType;
import net.firejack.platform.generate.beans.web.store.Param;
import net.firejack.platform.generate.tools.Utils;

import java.util.ArrayList;
import java.util.List;

@Properties(subpackage = "broker", suffix = "Broker")
public class Broker extends Base {
    private String path;
    private HTTPMethod httpMethod;
    private boolean wsdl;
    private String wsdlMethod;

    private MethodType type;
    private Model request;
    private Model response;
    private Method method;
    private List<Param> params = new ArrayList<Param>();

    /**
     * @param model
     * @param type
     */
    public Broker(Model model, MethodType type) {
        this(model, type.name());
        this.type = type;
    }

    /**
     * @param response
     * @param name
     */
    public Broker(Model response, String name) {
        super(response);

        this.name = Utils.brokerName(name, response.getSimpleName());
        this.path = Utils.fieldFormatting(getName());
        this.response = response;
        addImport(response);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return
     */
    public boolean isStub() {
        return type == null;
    }

    /**
     * @return
     */
    public MethodType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(MethodType type) {
        this.type = type;
    }

    /**
     * @param type
     * @return
     */
    public boolean isType(MethodType type) {
        return this.type.equals(type);
    }

    public Model getRequest() {
        return request;
    }

    public void setRequest(Model request) {
        this.request = request;
    }

    public Model getResponse() {
        return response;
    }

    public void setResponse(Model response) {
        this.response = response;
    }

    /**
     * @return
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * @return
     */
    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * @param httpMethod
     */
    public void setHttpMethod(HTTPMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public boolean isWsdl() {
        return wsdl;
    }

    public void setWsdl(boolean wsdl) {
        this.wsdl = wsdl;
    }

    public String getWsdlMethod() {
        return wsdlMethod;
    }

    public void setWsdlMethod(String wsdlMethod) {
        this.wsdlMethod = wsdlMethod;
    }

    /**
     * @return
     */
    public List<Param> getParams() {
        return params;
    }

    /**
     * @param param
     */
    public void addParam(Param param) {
        this.params.add(param);
    }
}
