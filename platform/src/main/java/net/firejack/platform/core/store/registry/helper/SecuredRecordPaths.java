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

package net.firejack.platform.core.store.registry.helper;

import java.util.LinkedHashSet;
import java.util.Set;

public class SecuredRecordPaths {

    private Set<SecuredRecordPath> paths;

    /***/
    public SecuredRecordPaths() {
        this.paths = new LinkedHashSet<SecuredRecordPath>();
    }

    /**
     * @param paths
     */
    public SecuredRecordPaths(Set<SecuredRecordPath> paths) {
        this.paths = paths;
    }

    /**
     * @return
     */
    public Set<SecuredRecordPath> getPaths() {
        return paths;
    }

    /**
     * @param paths
     */
    public void setPaths(Set<SecuredRecordPath> paths) {
        this.paths = paths;
    }

    /**
     * @param path
     */
    public void addPath(SecuredRecordPath path) {
        if (this.paths == null) {
            this.paths = new LinkedHashSet<SecuredRecordPath>();
        }
        this.paths.add(path);
    }

    /**
     * @param path
     */
    public void removePath(SecuredRecordPath path) {
        if (this.paths != null) {
            this.paths.remove(path);
        }
    }

}
