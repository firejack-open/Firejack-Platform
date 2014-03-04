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

import net.firejack.platform.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SecuredRecordPath {

    private List<Long> ids;

    /***/
    public SecuredRecordPath() {
        this.ids = new ArrayList<Long>();
    }

    /**
     * @param ids
     */
    public SecuredRecordPath(List<Long> ids) {
        this.ids = ids;
    }

    /**
     * @return
     */
    public List<Long> getIds() {
        return ids;
    }

    /**
     * @param ids
     */
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    /**
     * @param id
     */
    public void addId(Long id) {
        if (this.ids == null) {
            this.ids = new ArrayList<Long>();
        }
        this.ids.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecuredRecordPath that = (SecuredRecordPath) o;

        String thisPath = StringUtils.join(this.ids.iterator(), ":");
        String thatPath = StringUtils.join(that.ids.iterator(), ":");

        return thisPath.equals(thatPath);
    }

    @Override
    public int hashCode() {
        return ids != null ? ids.hashCode() : 0;
    }

}
