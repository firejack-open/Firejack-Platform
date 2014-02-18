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
