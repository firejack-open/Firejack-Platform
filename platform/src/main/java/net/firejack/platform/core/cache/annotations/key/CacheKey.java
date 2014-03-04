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

package net.firejack.platform.core.cache.annotations.key;

import java.io.Serializable;


public class CacheKey<ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = -9165777721129385459L;
    private ID id;

    /***/
    public CacheKey() {
    }

    /**
     * @param id
     */
    public CacheKey(ID id) {
        this.id = id;
    }

    /**
     * @return
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheKey cacheKey = (CacheKey) o;

        if (id != null ? !id.equals(cacheKey.id) : cacheKey.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CacheKey");
        sb.append("{id=").append(id);
        sb.append('}');
        return sb.toString();
    }

}
