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

package net.firejack.platform.core.config.meta.parse;


public final class DescriptorParserFactory {

    private static DescriptorParserFactory INSTANCE;

    private DescriptorParserFactory() {
    }

    /**
     * @param type
     * @return
     */
    public <T> IPackageDescriptorParser<T> buildMetaDataParser(DescriptorParserType type) {
	    if (type == null) {
		    throw new IllegalArgumentException("type should not be null.");
	    }

	    if (type == DescriptorParserType.XML_STREAM) {
		    return (IPackageDescriptorParser<T>) new XMLStreamDescriptorParser();
	    } else if (type == DescriptorParserType.XML_FILE) {
		    return (IPackageDescriptorParser<T>) new XMLFileDescriptorParser();
	    } else if (type == DescriptorParserType.DATABASE) {
		    return (IPackageDescriptorParser<T>) new XmlFromExistantDbDescriptorParser();
	    } else if (type == DescriptorParserType.XML_STRING) {
		    return (IPackageDescriptorParser<T>) new XmlStringDescriptorParser();
	    }
	    throw new UnsupportedOperationException();
    }

	/**
     * @return
     */
    public static DescriptorParserFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DescriptorParserFactory();
        }
        return INSTANCE;
    }

}