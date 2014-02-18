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

package net.firejack.aws.web.controller;

import net.firejack.aws.license.LicenseHelper;
import net.firejack.aws.util.MD5;
import net.firejack.aws.web.model.License;
import net.firejack.aws.web.model.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Scope("session")
@RequestMapping(value = {"/license"})
public class LicenseController {

    @Value("${password}")
    private String password;

    @RequestMapping(method = RequestMethod.GET)
    public String init() {
        return "license";
    }

    @ResponseBody
    @RequestMapping(value = "download", method = RequestMethod.POST)
    public Status license(@RequestBody License license) throws Exception {
        if (license.getAccessPassword() == null || license.getAccessPassword().trim().equals("")) {
            return new Status("Please provide a password", null);
        }

        if (MD5.getHash(password).equals(MD5.getHash(license.getAccessPassword()))) {
            return new Status("License key has been created", LicenseHelper.create(license).getName());
        } else {
            return new Status("The provided password is wrong", null);
        }
    }
}