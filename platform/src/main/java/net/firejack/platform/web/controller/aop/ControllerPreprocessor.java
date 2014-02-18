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

package net.firejack.platform.web.controller.aop;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@Order(4)
@Aspect
public class ControllerPreprocessor {

    private static final Logger logger = Logger.getLogger(ControllerPreprocessor.class);

	@Value("${platform.version.build.number}")
	private String buildNumber;

    private static String version;

	@Order(1)
	@Before(value = "@annotation(setInitData) && args(model,request,..)",
			argNames = "model,request,setInitData")
	private void setBaseUrl(Model model, HttpServletRequest request, SetInitData setInitData) {
        if (StringUtils.isNotBlank(version)) {
			model.addAttribute("version", version);
		} else if (!ConfigContainer.isAppInstalled()) {
            model.addAttribute("version", "Not Installed");
        }

        Config googleAnalyticsIdConfig = ConfigCacheManager.getInstance().getConfig(OpenFlameSecurityConstants.GOOGLE_ANALYTICS_CONFIG_LOOKUP);
        if (googleAnalyticsIdConfig != null) {
			model.addAttribute("googleAnalyticsId", googleAnalyticsIdConfig.getValue());
		}

		model.addAttribute("baseUrl", Env.FIREJACK_URL.getValue());
		model.addAttribute("host", OpenFlameConfig.DOMAIN_URL.getValue());
		model.addAttribute("pageUID", SecurityHelper.generateSecureId());
		model.addAttribute("buildNumber", buildNumber);
		model.addAttribute("loadTimestamp", new Date().getTime());

        String type = ServletRequestUtils.getStringParameter(request, "type", null);
        if (StringUtils.isNotBlank(type)) {
            Integer registryNodeId = ServletRequestUtils.getIntParameter(request, "id", 0);
            RegistryNodeType registryNodeType = RegistryNodeType.find(type);
            if (registryNodeType != null && registryNodeId > 0) {
                model.addAttribute("editEntityType", registryNodeType);
                model.addAttribute("editEntityId", registryNodeId);
            }
        }
	}

	@Order(2)
	@Before(value = "@annotation(setAuthorizedUser) && args(model,..)",
			argNames = "model,setAuthorizedUser")
	private void setAuthorizedUser(Model model, SetAuthorizedUser setAuthorizedUser) {
		model.addAttribute("isUserAuthenticated", ContextManager.isUserAuthenticated());
		if (ContextManager.isUserAuthenticated()) {
			IUserInfoProvider currentUser = ContextManager.getUserInfoProvider();
			model.addAttribute("currentUser", currentUser);

			UserPermission editDocumentationPermission = new UserPermission(OpenFlame.DOCUMENTATION_EDIT);
			boolean hasEditDocPermission = OPFContext.getContext().getPrincipal().checkUserPermission(editDocumentationPermission);
			model.addAttribute("canEditResource", hasEditDocPermission);
		}
	}

}
