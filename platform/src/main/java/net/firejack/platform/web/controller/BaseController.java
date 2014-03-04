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

package net.firejack.platform.web.controller;

import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.utils.WebUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseController {

    @Value("${debug.mode}")
    private boolean debugMode;

    @Resource(name = "registryAllowTypes")
    private Map<RegistryNodeType, List<RegistryNodeType>> registryAllowTypeMap;

    /**
     * @return
     */
    @ModelAttribute("debugMode")
    public boolean getDebugMode() {
        return debugMode;
    }

    @ModelAttribute("allowTypes")
    public String getAllowTypes() throws IOException {
        Map<String, ArrayList<String>> allowTypes = new HashMap<String, ArrayList<String>>();
        for (Map.Entry<RegistryNodeType, List<RegistryNodeType>> entry : registryAllowTypeMap.entrySet()) {
            RegistryNodeType registryNodeType = entry.getKey();
            List<RegistryNodeType> registryAllowTypes = entry.getValue();
            ArrayList<String> registryNodeTypes = new ArrayList<String>();
            for (RegistryNodeType registryAllowType : registryAllowTypes) {
                registryNodeTypes.add(registryAllowType.name());
            }
            allowTypes.put(registryNodeType.name(), registryNodeTypes);
        }
//        RegistryAllowTypeVO registryAllowTypeVO = new RegistryAllowTypeVO(allowTypes);
        return WebUtils.serializeObjectToJSON(allowTypes);
    }

}
