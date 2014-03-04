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
