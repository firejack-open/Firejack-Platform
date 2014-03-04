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

package net.firejack.platform.web.security.filter.rules;

import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.web.security.filter.ISecurityFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompositeAuthorizationRule implements IAuthorizationRule {

    private List<IAuthorizationRule> ruleList;
    private static ThreadLocal<IAuthorizationRule> currentRuleHolder = new InheritableThreadLocal<IAuthorizationRule>();

    /**
     * Add authorization rule.
     * @param rule authorization rule to add
     */
    public void addRule(IAuthorizationRule rule) {
        getRuleList().add(rule);
    }

    @Override
    public boolean isRuleCase(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol) {
        for (IAuthorizationRule rule : getRuleList()) {
            if (rule.isRuleCase(securityFilter, httpRequest, protocol)) {
                currentRuleHolder.set(rule);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean authorizeAccess(
            ISecurityFilter securityFilter, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws IOException {
        IAuthorizationRule currentRule = currentRuleHolder.get();
        if (currentRule == null) {
            throw new IllegalStateException();
        }
        return currentRule.authorizeAccess(securityFilter, httpRequest, httpResponse);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void release() {
        IAuthorizationRule currentRule = currentRuleHolder.get();
        if (currentRule != null) {
            currentRule.release();
            currentRuleHolder.remove();
        }
    }

    private List<IAuthorizationRule> getRuleList() {
        if (ruleList == null) {
            ruleList = new ArrayList<IAuthorizationRule>();
        }
        return ruleList;
    }
}