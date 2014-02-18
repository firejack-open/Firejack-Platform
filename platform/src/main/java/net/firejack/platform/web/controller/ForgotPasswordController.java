/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.web.controller.aop.SetAuthorizedUser;
import net.firejack.platform.web.controller.aop.SetInitData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(value = "/forgot-password")
public class ForgotPasswordController extends BaseController {

    /**
     * If application is not installed we have to redirect user to installation wizard
     *
     * @param model    spring util class
     * @param request  instance of Http Request
     * @param response instance of Http Response
     * @return the name of the view
     * @throws java.io.IOException method may throw IOException
     */
    @SetInitData
    @SetAuthorizedUser
    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        model.addAttribute("pageType", PageType.FORGOT_PASSWORD);
        return "forgot-password";
    }

}
