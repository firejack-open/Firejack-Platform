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

package net.firejack.platform.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "opf_lookup", uniqueConstraints = @UniqueConstraint(name = "uk_hash", columnNames = "hash"))
public class Lookup extends BaseEntityModel {
    private static final long serialVersionUID = -8439075688214700674L;
    private String hash;

    /***/
    public Lookup() {
    }

    /**
     * @param hash
     */
    public Lookup(String hash) {
        this.hash = hash;
    }

    /**
     * @return
     */
    @Column(columnDefinition = "TINY_TEXT", nullable = false)
    public String getHash() {
        return hash;
    }

    /**
     * @param hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }
}
