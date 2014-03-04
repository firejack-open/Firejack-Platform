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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.utils.StringUtils;


public class Reference {

    private String refName;
    private String refPath;
    private String constraintName;

    public Reference() {
    }

    public Reference(String refName, String refPath) {
        this.refName = refName;
        this.refPath = refPath;
    }

    /**
     * @return
     */
    public String getRefName() {
        return refName;
    }

    /**
     * @param refName
     */
    public void setRefName(String refName) {
        this.refName = refName;
    }

    /**
     * @return
     */
    public String getRefPath() {
        return refPath;
    }

    /**
     * @param refPath
     */
    public void setRefPath(String refPath) {
        this.refPath = refPath;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;

        Reference reference = (Reference) o;

        return StringUtils.equals(refName, reference.refName) &&
                StringUtils.equals(refPath, reference.refPath) &&
                StringUtils.equals(constraintName, reference.constraintName);

    }

    @Override
    public int hashCode() {
        int result = refName != null ? refName.hashCode() : 0;
        result = 31 * result + (refPath != null ? refPath.hashCode() : 0);
        result = 31 * result + (constraintName != null ? constraintName.hashCode() : 0);
        return result;
    }
}