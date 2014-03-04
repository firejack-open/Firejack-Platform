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

package net.firejack.platform.web.security.protocol;

import net.firejack.platform.core.model.registry.EntityProtocol;


public class ProtocolMapping implements Comparable<ProtocolMapping> {

    private EntityProtocol protocol;
    private String urlPrefix;

    /**
     * @return
     */
    public EntityProtocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     */
    public void setProtocol(EntityProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return
     */
    public String getUrlPrefix() {
        return urlPrefix;
    }

    /**
     * @param urlPrefix
     */
    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Override
    public int compareTo(ProtocolMapping mapping) {
        int result;
        if (mapping == null) {
            result = 1;
        } else if (mapping == this) {
            result = 0;
        } else if (getUrlPrefix() != null) {
            result = -getUrlPrefix().compareTo(mapping.getUrlPrefix());
        } else {
            result = 0;
        }
        return result;
    }
}