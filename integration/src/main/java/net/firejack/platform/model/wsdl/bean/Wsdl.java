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
