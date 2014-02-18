/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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