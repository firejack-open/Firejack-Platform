/**
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
