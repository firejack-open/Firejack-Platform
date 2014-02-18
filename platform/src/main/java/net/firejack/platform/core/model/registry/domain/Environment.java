package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.core.model.registry.Entry;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;

import javax.xml.bind.annotation.*;
import java.util.List;

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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Environment extends Entry {
    private SystemModel system;
    @XmlElement(name = "database")
    @XmlElementWrapper(name = "databases")
    private List<DatabaseModel> databases;
    @XmlElement(name = "server")
    @XmlElementWrapper(name = "servers")
    private List<ServerModel> servers;
    @XmlElement(name = "filestore")
    @XmlElementWrapper(name = "filestores")
    private List<FileStoreModel> filestores;

    /**
     * @return
     */
    public SystemModel getSystem() {
        return system;
    }

    /**
     * @param system
     */
    public void setSystem(SystemModel system) {
        this.system = system;
    }

    /**
     * @return
     */
    public List<DatabaseModel> getDatabases() {
        return databases;
    }

    /**
     * @param databases
     */
    public void setDatabases(List<DatabaseModel> databases) {
        this.databases = databases;
    }

    /**
     * @return
     */
    public List<ServerModel> getServers() {
        return servers;
    }

    /**
     * @param servers
     */
    public void setServers(List<ServerModel> servers) {
        this.servers = servers;
    }

    /**
     * @return
     */
    public List<FileStoreModel> getFilestores() {
        return filestores;
    }

    /**
     * @param filestores
     */
    public void setFilestores(List<FileStoreModel> filestores) {
        this.filestores = filestores;
    }
}
