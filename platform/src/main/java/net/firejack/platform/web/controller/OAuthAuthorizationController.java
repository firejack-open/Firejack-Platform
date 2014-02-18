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

import net.firejack.platform.core.utils.Env;
import net.firejack.platform.web.security.oauth.provider.OAuthAuthorizationHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/oauth_authorization_form")
public class OAuthAuthorizationController {

    /**
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String authorize(Model model) {
        model.addAttribute("authorizationUrl", Env.FIREJACK_URL.getValue() +
                OAuthAuthorizationHandler.DEFAULT_AUTHORIZATION_HANDLER_URL);
        return "oauth_authorize";
    }
}