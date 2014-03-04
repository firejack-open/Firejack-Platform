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

package net.firejack.platform.web.security.action;

import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.action.container.IActionContainer;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
class DefaultActionDetector implements IActionDetector {

    @Override
    public Action detectAction(ServletRequest request, EntityProtocol protocol) {
        OPFContext context = ContextManager.getContext();
        IActionContainer actionContainer = context.getActionContainer();
        List<Action> actionList = actionContainer.getActionList();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return ActionDetectorFactory.searchAction(protocol,
                WebUtils.getRequestPath(httpRequest), httpRequest.getMethod(), actionList);
    }

}