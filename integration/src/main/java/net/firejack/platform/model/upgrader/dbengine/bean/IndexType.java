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

package net.firejack.platform.model.upgrader.dbengine.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "index-type")
@XmlEnum
public enum IndexType {

    @XmlEnumValue("INDEX")
    INDEX("INDEX", "INDEX"),

    @XmlEnumValue("PRIMARY_KEY")
    PRIMARY_KEY("PRIMARY KEY", "PRIMARY KEY"),

    @XmlEnumValue("UNIQUE")
    UNIQUE("UNIQUE", "INDEX");

    private String addKey;
    private String dropKey;

    IndexType(String addKey, String dropKey) {
        this.addKey = addKey;
        this.dropKey = dropKey;
    }

    /**
     * @return
     */
    public String getAddKey() {
        return addKey;
    }

    /**
     * @return
     */
    public String getDropKey() {
        return dropKey;
    }

}
