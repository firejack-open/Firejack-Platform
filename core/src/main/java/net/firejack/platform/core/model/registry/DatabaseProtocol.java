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

package net.firejack.platform.core.model.registry;


/**
 * Lists possible database protocols
 */
public enum DatabaseProtocol {

    JDBC;

    /**
     * Finds database protocol by its' name
     * @param name - database protocol name
     * @return database protocol
     */
    public static DatabaseProtocol findByName(String name) {
        DatabaseProtocol value = null;
        for (DatabaseProtocol e : values()) {
            if (e.name().equalsIgnoreCase(name)) {
                value = e;
                break;
            }
        }
        return value;
    }

}
