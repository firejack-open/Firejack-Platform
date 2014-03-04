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