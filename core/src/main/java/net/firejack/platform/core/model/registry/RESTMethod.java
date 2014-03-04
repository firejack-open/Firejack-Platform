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


public enum RESTMethod {

    CREATE(HTTPMethod.POST),

    READ(HTTPMethod.GET),

    READ_ALL(HTTPMethod.GET, "read-all"),

    UPDATE(HTTPMethod.PUT),

    DELETE(HTTPMethod.DELETE),

    SEARCH(HTTPMethod.GET),

    ADVANCED_SEARCH(HTTPMethod.GET, "advanced-search");

    private HTTPMethod method;
    private String actionName;

    RESTMethod(HTTPMethod method) {
        this.method = method;
        this.actionName = name().toLowerCase();
    }

    RESTMethod(HTTPMethod method, String actionName) {
        this.method = method;
        this.actionName = actionName;
    }

    /**
     * @return
     */
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    /**
     * @return
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param name
     * @return
     */
    public static RESTMethod findByName(String name) {
        RESTMethod value = null;
        for (RESTMethod e : values()) {
            if (e.name().equals(name)) {
                value = e;
                break;
            }
        }
        return value;
    }

}
