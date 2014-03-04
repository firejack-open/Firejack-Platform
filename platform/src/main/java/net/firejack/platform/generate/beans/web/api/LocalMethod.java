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

package net.firejack.platform.generate.beans.web.api;

import net.firejack.platform.generate.beans.web.broker.Broker;
import net.firejack.platform.generate.beans.web.store.Method;

public class LocalMethod extends Method {

    private Broker broker;
    private String name;
    private String soapPath;
    private String path;

    /**
     * @param broker
     */
    public LocalMethod(Broker broker) {
        this.broker = broker;
        this.type = broker.getType();
    }

    /**
     * @return
     */
    public Broker getBroker() {
        return broker;
    }

    /**
     * @param broker
     */
    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getSoapPath() {
        return soapPath;
    }

    /**
     * @param soapPath
     */
    public void setSoapPath(String soapPath) {
        this.soapPath = soapPath;
    }

    /**
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
