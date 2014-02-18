package net.firejack.platform.model.wsdl.bean;
/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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


import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Service {
    @XmlAttribute
    public String name;
    @XmlTransient
    public Wsdl wsdl;
    @XmlElement(name = "parameter")
   	@XmlElementWrapper(name = "parameters")
    public List<Parameter> parameters;
    @XmlElement(name = "return")
    public Parameter _return;

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Wsdl getWsdl() {
        return wsdl;
    }

    public void setWsdl(Wsdl wsdl) {
        this.wsdl = wsdl;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Parameter parameter){
        if (parameters == null)
            parameters = new ArrayList<Parameter>();
        parameters.add(parameter);
    }

    public Parameter getReturn() {
        return _return;
    }

    public void setReturn(Parameter _return) {
        this._return = _return;
    }
}
