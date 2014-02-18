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
