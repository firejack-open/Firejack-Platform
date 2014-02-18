/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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