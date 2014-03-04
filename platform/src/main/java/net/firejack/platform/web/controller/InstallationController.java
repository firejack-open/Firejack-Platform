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

import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.web.controller.aop.SetAuthorizedUser;
import net.firejack.platform.web.controller.aop.SetInitData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/console/installation")
public class InstallationController extends BaseController {

    /**
     * @param model
     * @param request
     * @param response
     * @return
     */
    @SetInitData
    @SetAuthorizedUser
    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model, HttpServletRequest request, HttpServletResponse response) {
        if (!ConfigContainer.isAppInstalled()) {
//            String installationStep = OpenFlameConfig.CURRENT_INSTALLATION_STEP.getValue();
//            if (StringUtils.isBlank(installationStep)) {
//                installationStep = ConfigContainer.SYSTEM_SETUP_STEP;
//            }
//            String installationStatus = OpenFlameConfig.CURRENT_INSTALLATION_STATUS.getValue();
//            if (StringUtils.isBlank(installationStatus)) {
//                installationStatus = ConfigContainer.START_STATUS;
//            }
//            model.addAttribute("installationStep", installationStep);
//            model.addAttribute("installationStatus", installationStatus);
        } else {
            //   We shouldn't appear at this place because AbsSecurityFilter has to catch request to installation url after app installed
            return "redirect:home";
        }
        return "installation";
    }

}
