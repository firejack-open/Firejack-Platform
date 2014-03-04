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

package net.firejack.platform.generate.beans;

import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public abstract class Interface extends Base {
    private List<Method> methods;

    protected Interface() {
    }

    protected Interface(String name) {
        this.name = name;
    }

    protected Interface(Base base, List<Method> methods) {
        super(base);
        this.serviceName = base.getServiceName();
        this.methods = methods;

        if (methods != null) {
            for (Method method : methods) {
                TreeSet<Param> params = method.getParams();
                Param returnType = method.getReturnType();
                if (returnType != null) {
                    addImport(returnType.getDomain());
                }
                if (params != null) {
                    for (Param param : params) {
                        addImport(param.getDomain());
                    }
                }
            }
        }
    }

    /**
     * @return
     */
    public List<Method> getMethods() {
        return methods;
    }

    public void addMethods(List<Method> methods) {
        if (this.methods == null)
            this.methods = new ArrayList<Method>();
        this.methods.addAll(methods);
    }

    public void addMethod(Method method) {
        if (this.methods == null)
            this.methods = new ArrayList<Method>();
        this.methods.add(method);
    }
}
