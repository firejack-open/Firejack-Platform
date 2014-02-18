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