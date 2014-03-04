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

import com.thoughtworks.xstream.XStream;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class ProtocolMappings {

    private List<ProtocolMapping> protocolMappingList;

    /**
     * @return
     */
    public List<ProtocolMapping> getProtocolMappingList() {
        return protocolMappingList;
    }

    /**
     * @param protocolMappingList
     */
    public void setProtocolMappingList(List<ProtocolMapping> protocolMappingList) {
        this.protocolMappingList = protocolMappingList;
    }

    /**
     * @param source
     * @return
     */
    public static SortedSet<ProtocolMapping> processMappings(String source) {
        XStream xStream = new XStream();
        xStream.addImplicitCollection(ProtocolMappings.class, "protocolMappingList");
        xStream.alias("protocol-mappings", ProtocolMappings.class);
        xStream.alias("protocol", ProtocolMapping.class);
        xStream.useAttributeFor(ProtocolMapping.class, "protocol");
        xStream.aliasAttribute(ProtocolMapping.class, "protocol", "name");
        xStream.useAttributeFor(ProtocolMapping.class, "urlPrefix");
        xStream.aliasAttribute(ProtocolMapping.class, "urlPrefix", "prefix");
        ProtocolMappings mappings = (ProtocolMappings) xStream.fromXML(source);
        SortedSet<ProtocolMapping> mappingSet = new TreeSet<ProtocolMapping>();
        List<ProtocolMapping> mappingList = mappings.getProtocolMappingList();
        if (mappingList != null) {
            for (ProtocolMapping mapping : mappingList) {
                mapping.setUrlPrefix(mapping.getUrlPrefix() == null ?
                        null : mapping.getUrlPrefix().toLowerCase());
                mappingSet.add(mapping);
            }
        }
        return mappingSet;
    }
}