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

package net.firejack.platform.model.upgrader;


import net.firejack.platform.core.utils.VersionUtils;

public class Version implements Comparable<Version> {

    private Integer fromVersion;
    private Integer toVersion;
    private String scriptPath;

    /**
     * @param fromVersion
     */
    public Version(Integer fromVersion) {
        this.fromVersion = fromVersion;
    }

    /**
     * @param fromVersion
     * @param scriptPath
     */
    public Version(Integer fromVersion, String scriptPath) {
        this.fromVersion = fromVersion;
        this.scriptPath = scriptPath;
    }

    /**
     * @param fromVersion
     * @param toVersion
     * @param scriptPath
     */
    public Version(Integer fromVersion, Integer toVersion, String scriptPath) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.scriptPath = scriptPath;
    }

    /**
     * @return
     */
    public Integer getFromVersion() {
        return fromVersion;
    }

    /**
     * @return
     */
    public Integer getToVersion() {
        return toVersion;
    }

    /**
     * @return
     */
    public String getScriptPath() {
        return scriptPath;
    }

    @Override
    public int hashCode() {
        int result = fromVersion != null ? fromVersion.hashCode() : 0;
        result = 31 * result + (toVersion != null ? toVersion.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (!fromVersion.equals(version.fromVersion)) return false;
        if (!toVersion.equals(version.toVersion)) return false;

        return true;
    }

    @Override
    public String toString() {
        return VersionUtils.convertToVersion(fromVersion) + "_" + VersionUtils.convertToVersion(toVersion);
    }

    public int compareTo(Version other) {
        return this.fromVersion.compareTo(other.fromVersion);
    }

}
