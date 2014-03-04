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

import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.model.registry.ProcessFieldType;
import net.firejack.platform.web.controller.aop.SetAuthorizedUser;
import net.firejack.platform.web.controller.aop.SetInitData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;


@Controller
@RequestMapping("/console/inbox")
public class InboxManagerController extends BaseController {

    @SetInitData
    @SetAuthorizedUser
    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model, ServletRequest request) {
        model.addAttribute("pageType", PageType.PROCESS);
        model.addAttribute("FIELD_TYPE_INTEGER", ProcessFieldType.INTEGER);
        model.addAttribute("FIELD_TYPE_STRING", ProcessFieldType.STRING);
        model.addAttribute("FIELD_TYPE_DATE", ProcessFieldType.DATE);
        model.addAttribute("FIELD_TYPE_BOOLEAN", ProcessFieldType.BOOLEAN);
        model.addAttribute("FIELD_TYPE_LONG", ProcessFieldType.LONG);
        model.addAttribute("FIELD_TYPE_DOUBLE", ProcessFieldType.DOUBLE);
        //disable inbox page for now.
//        return "inbox";
        return "in-box";
    }

}