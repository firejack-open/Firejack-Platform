package net.firejack.platform.generate.beans.web.endpoint;

import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.api.LocalService;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
