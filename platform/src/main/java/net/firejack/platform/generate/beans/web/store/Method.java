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

import java.util.TreeSet;

public class Method {
    protected MethodType type;
    protected TreeSet<Param> params;
    protected Param returnType;
    protected String description;

    /***/
    public Method() {
    }

    /**
     * @param type
     */
    public Method(MethodType type) {
        this.type = type;
    }

    /**
     * @param method
     */
    public Method(Method method) {
        this.type = method.getType();
        this.params = method.getParams();
        this.returnType = method.getReturnType();
        this.description = method.getDescription();
    }

    /**
     * @return
     */
    public MethodType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(MethodType type) {
        this.type = type;
    }

    /**
     * @return
     */
    public TreeSet<Param> getParams() {
        return params;
    }

    /**
     * @param param
     */
    public void addParam(Param param) {
        if (this.params == null) {
            this.params = new TreeSet<Param>();
        }
        this.params.add(param);
    }

    /**
     * @return
     */
    public int getNextOrder() {
        if (params != null) {
            return params.size() + 1;
        } else {
            return 0;
        }
    }

    /**
     * @return
     */
    public Param getReturnType() {
        return returnType;
    }

    /**
     * @param returnType
     */
    public void setReturnType(Param returnType) {
        this.returnType = returnType;
    }

    /**
     * @return
     */
    public boolean isRender() {
        return params != null || returnType != null;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
