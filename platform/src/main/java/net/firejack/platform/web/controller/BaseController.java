/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
