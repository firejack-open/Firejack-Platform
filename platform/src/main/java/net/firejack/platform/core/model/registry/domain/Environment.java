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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.core.model.registry.Entry;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;

import javax.xml.bind.annotation.*;
import java.util.List;

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
