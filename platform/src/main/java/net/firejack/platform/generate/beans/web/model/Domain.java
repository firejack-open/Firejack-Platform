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

package net.firejack.platform.generate.beans.web.model;

import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.key.Key;

@Properties(subpackage = "domain")
public class Domain extends Model<Domain> {
    private Model model;

    /***/
    public Domain() {
    }

    /**
     * @param model
     */
    public Domain(Model model) {
        super(model);
        this.model = model;
    }

    /**
     * @return
     */
    public Model getModel() {
        return model;
    }

    /**
     * @param model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public Key getKey() {
        return model.getKey();
    }

    @Override
    public boolean isSingle() {
        return model.isSingle();
    }
}
