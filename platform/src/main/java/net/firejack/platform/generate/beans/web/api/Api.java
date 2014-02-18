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
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.endpoint.Endpoint;
import net.firejack.platform.generate.beans.web.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Properties(suffix = "API")
public class Api extends Base {

    private String url;
    private List<LocalService> locals;
    private List<Endpoint> endpoints;
    private Collection<Model> models;

    public Api(String url, String path, String name) {
        this.name = StringUtils.capitalize(name);
        this.url = url;
        this.projectPath = path;
        this.classPath = "";
    }

    public List<LocalService> getLocals() {
        return locals;
    }

    public void addService(LocalService local) {
        if (locals == null) {
            locals = new ArrayList<LocalService>();
        }
        this.locals.add(local);
        addImport(local.getService());
        addImport(local.getProxy());
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void addEndpoint(Endpoint endpoint) {
        if (endpoints == null) {
            endpoints = new ArrayList();
        }
        this.endpoints.add(endpoint);
    }

    public String getUrl() {
        return url;
    }

    public Collection<Model> getModels() {
        return models;
    }

    public void setModels(Collection<Model> models) {
        this.models = models;
    }

    public String getFilePosition() {
        return this.projectPath.replaceAll("\\.", "\\" + File.separator);
    }
}
