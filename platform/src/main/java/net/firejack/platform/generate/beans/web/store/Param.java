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

package net.firejack.platform.generate.beans.web.store;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.key.Key;

public class Param<T extends Param> implements Comparable<T> {
    private String name;
    private FieldType type;
    private boolean key;
    private Base domain;

    /***/
    public Param() {
    }

    /**
     * @param name
     * @param type
     */
    public Param(String name, FieldType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @param name
     * @param type
     * @param domain
     */
    public Param(String name, FieldType type, Model domain) {
        this.name = name;
        this.type = type;
        this.domain = domain;
    }

    public Param(String name, Key key) {
        this.name = name;
        this.type = key.getType();
        this.key = true;
        if (key.isComposite()) {
            this.domain = (Base) key;
        }
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public FieldType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     * @param type
     * @return
     */
    public boolean isType(FieldType type) {
        return this.type.equals(type);
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public Base getDomain() {
        return domain;
    }

    public void setDomain(Base domain) {
        this.domain = domain;
    }

    /**
     * @return
     */
    public String getParamName() {
        if (isType(FieldType.OBJECT) || isType(FieldType.LIST)) {
            return getDomain().getName();
        } else {
            return getType().getClassName();
        }
    }

    @Override
    public int compareTo(T param) {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Param)) return false;

        Param param = (Param) o;

        return !(name != null ? !name.equals(param.name) : param.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
