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