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

package net.firejack.platform.api.registry.model;


public enum PackageFileType {

    PACKAGE_XML("xml", "package.xml"),

    CONTENT_XML("cxml", "content.cxml"),

    APP_WAR("war", "ROOT.war"),

    RESOURCE_ZIP("zip", "resource.zip"),

    PACKAGE_SOURCE("src.zip", null),

    IPAD_SOURCE("ipad.src.zip", null),

    PACKAGE_OFR("ofr", null),

    PACKAGE_UPGRADE("uxml", "upgrade.uxml"),

    CONTENT_ARCHIVE("ofcr", null);

    private String extension;
    private String ofrFileName;

    PackageFileType(String extension, String ofrFileName) {
        this.extension = extension;
        this.ofrFileName = ofrFileName;
    }

    /**
     * @return
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return
     */
    public String getDotExtension() {
        return "." + extension;
    }

    /**
     * @return
     */
    public String getOfrFileName() {
        return ofrFileName;
    }

    /**
     * @param extension
     * @return
     */
    public static PackageFileType findByExtension(String extension) {
        PackageFileType value = null;
        for (PackageFileType e : values()) {
            if (e.getExtension().equalsIgnoreCase(extension)) {
                value = e;
                break;
            }
        }
        return value;
    }

}
