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


import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private Class type;
    @XmlAttribute
    private boolean bean;
    @XmlAttribute
    private boolean list;
    @XmlAttribute
    private boolean holder;
    @XmlAttribute
    private WebParam.Mode mode;

    public Parameter() {
    }

    public Parameter(String name, Class type, boolean bean, boolean list, boolean holder, WebParam.Mode mode) {
        this.name = name;
        this.type = type;
        this.bean = bean;
        this.list = list;
        this.holder = holder;
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public boolean isBean() {
        return bean;
    }

    public void setBean(boolean bean) {
        this.bean = bean;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public boolean isHolder() {
        return holder;
    }

    public void setHolder(boolean holder) {
        this.holder = holder;
    }

    public WebParam.Mode getMode() {
        return mode;
    }

    public void setMode(WebParam.Mode mode) {
        this.mode = mode;
    }
}
