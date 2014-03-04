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

import net.firejack.platform.generate.beans.Interface;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;

import java.util.List;

@Properties(subpackage = "store", suffix = "Store")
public class StoreInterface extends Interface {

    private Model model;

    /**
     * @param store
     * @param methods
     */
    public StoreInterface(Store store, List<Method> methods) {
        super(store.getModel(), methods);
        this.model = store.getModel();
    }

    public Model getModel() {
        return model;
    }
}
