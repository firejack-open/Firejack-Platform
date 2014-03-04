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

import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.Interface;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;

import java.util.ArrayList;
import java.util.List;

@Properties(subpackage = "store", prefix = "Basic", suffix = "Store")
public class Store extends Base {
    private Model model;

    private Interface Interface;
    private List<Method> methods;

    /***/
    public Store() {
    }

    /**
     * @param model
     */
    public Store(Model model) {
        super(model);
        this.model = model;
        addImport(model.getKey());
        addImport(model);
    }

    public Model getModel() {
        return model;
    }

    /**
     * @return
     */
    public Interface getInterface() {
        return Interface;
    }

    /***/
    public void createInterface() {
        Interface = new StoreInterface(this, methods);
    }

    /**
     * @return
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * @param method
     */
    public void addMethods(Method method) {
        if (this.methods == null) {
            this.methods = new ArrayList<Method>();
        }

        this.methods.add(method);
    }

    /**
     * @param type
     * @return
     */
    public Method find(MethodType type) {
        if (methods != null) {
            for (Method method : methods) {
                if (method.getType().equals(type)) {
                    return method;
                }
            }
        }
        return null;
    }
}
