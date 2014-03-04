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

package net.firejack.platform.core.config.meta.element.action;

import net.firejack.platform.core.config.meta.element.BaseNavigableRegistryNodeElement;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.model.registry.domain.ActionModel;

import java.util.List;


public class ActionElement extends BaseNavigableRegistryNodeElement<ActionModel> {

    private HTTPMethod method;
    private String soapUrlPath;
    private String soapMethod;
    private String inputVOEntityLookup;
    private String outputVOEntityLookup;
    private List<ActionParameterElement> parameters;

    /**
     * @return
     */
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    /**
     * @return
     */
    public String getSoapUrlPath() {
        return soapUrlPath;
    }

    /**
     * @param soapUrlPath
     */
    public void setSoapUrlPath(String soapUrlPath) {
        this.soapUrlPath = soapUrlPath;
    }

    /**
     * @return
     */
    public String getSoapMethod() {
        return soapMethod;
    }

    /**
     * @param soapMethod
     */
    public void setSoapMethod(String soapMethod) {
        this.soapMethod = soapMethod;
    }

    /**
     * @return
     */
    public String getInputVOEntityLookup() {
        return inputVOEntityLookup;
    }

    /**
     * @param inputVOEntityLookup
     */
    public void setInputVOEntityLookup(String inputVOEntityLookup) {
        this.inputVOEntityLookup = inputVOEntityLookup;
    }

    /**
     * @return
     */
    public String getOutputVOEntityLookup() {
        return outputVOEntityLookup;
    }

    /**
     * @param outputVOEntityLookup
     */
    public void setOutputVOEntityLookup(String outputVOEntityLookup) {
        this.outputVOEntityLookup = outputVOEntityLookup;
    }

    /**
     * @return
     */
    public List<ActionParameterElement> getParameters() {
        return parameters;
    }

    /**
     * @param parameters
     */
    public void setParameters(List<ActionParameterElement> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Class<ActionModel> getEntityClass() {
        return ActionModel.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionElement)) return false;
        if (!super.equals(o)) return false;

        ActionElement actionElement = (ActionElement) o;

        return method == actionElement.method;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }
}