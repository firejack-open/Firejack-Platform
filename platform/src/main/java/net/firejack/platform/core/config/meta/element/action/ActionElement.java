/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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