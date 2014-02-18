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

import com.thoughtworks.xstream.core.util.Base64Encoder;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.model.registry.resource.TextResourceVersionModel;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.controller.aop.SetAuthorizedUser;
import net.firejack.platform.web.controller.aop.SetInitData;
import net.firejack.platform.web.security.filter.message.FilterMessage;
import net.firejack.platform.web.security.filter.message.FilterMessageStock;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.GuestUser;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.session.UserSessionManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(value = {"/", "/home", "/console/home"})
public class HomeController extends BaseController {

    private static final Logger logger = Logger.getLogger(HomeController.class);

    @Autowired
    private ISystemStore systemStore;

    @Autowired
    @Qualifier("resourceStore")
    private IResourceStore<ResourceModel> resourceStore;

    @Autowired
    @Qualifier("resourceVersionStore")
    private IResourceVersionStore<TextResourceVersionModel> resourceVersionStore;

    /**
     * If application is not installed we have to redirect user to installation wizard
     *
     * @param model    spring util class
     * @param request  instance of Http Request
     * @param response instance of Http Response
     * @return the name of the view
     * @throws IOException method may throw IOException
     */
    @SetInitData
    @SetAuthorizedUser
    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        OpenFlamePrincipal principal = OPFContext.getContext().getPrincipal();
        String createPermissionLookup = DiffUtils.lookup(RegistryNodeType.USER.getEntityPath(), "create");
        List<String> permissions = new ArrayList<String>();
        permissions.add(createPermissionLookup);
        boolean showUserCreateButton = principal.checkUserPermission(permissions);
        model.addAttribute("showUserCreateButton", showUserCreateButton);
        if (request.getAttribute(OpenFlameSecurityConstants.OAUTH_ACTIVE_REQUEST_ATTRIBUTE) != null) {
            model.addAttribute("oauthActive", "true");
        }

        List<FilterMessage> filterMessageList = FilterMessageStock.getInstance().getFilterMessages();
        ObjectMapper mapper = new ObjectMapper();
	    String filterMessages = mapper.writeValueAsString(filterMessageList);
        model.addAttribute("filterMessages", filterMessages);

        model.addAttribute("welcomeMessage", "Not initialized yet ...");
        if (ConfigContainer.isAppInstalled()) {
            ResourceModel resource = resourceStore.findByLookup(OpenFlame.WELCOME_MESSAGE_RESOURCE);
            if (resource != null && resource.getType() == RegistryNodeType.TEXT_RESOURCE) {
                TextResourceVersionModel resourceVersion = resourceVersionStore.findLastVersionByResourceIdCulture(resource.getId(), Cultures.AMERICAN);
                if (resourceVersion != null) {
                    String welcomeMessage = resourceVersion.getText();
                    model.addAttribute("welcomeMessage", welcomeMessage);
                }
            }
            model.addAttribute("pageType", PageType.HOME);
            if (ContextManager.isUserAuthenticated()) {
                return "home";
            } else {
                ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();

                Config twitterEnableConfig = configCacheManager.getConfig(OpenFlameSecurityConstants.getPackageLookup() + ".twitter-enable");
                boolean twitterEnable = twitterEnableConfig != null && "true".equalsIgnoreCase(twitterEnableConfig.getValue());
                model.addAttribute("twitterLoginEnable", twitterEnable);

                Config facebookEnableConfig = configCacheManager.getConfig(OpenFlameSecurityConstants.getPackageLookup() + ".facebook-enable");
                boolean facebookEnable = facebookEnableConfig != null && "true".equalsIgnoreCase(facebookEnableConfig.getValue());
                model.addAttribute("facebookLoginEnable", facebookEnable);

                Config linkedinEnableConfig = configCacheManager.getConfig(OpenFlameSecurityConstants.getPackageLookup() + ".linkedin-enable");
                boolean linkedinEnable = linkedinEnableConfig != null && "true".equalsIgnoreCase(linkedinEnableConfig.getValue());
                model.addAttribute("linkedinLoginEnable", linkedinEnable);

                initKeyPair(model, request);
                return "login";
            }
        } else {
            UserSessionManager sessionManager = UserSessionManager.getInstance();
            final GuestUser guestUser = new GuestUser();
            final String token = sessionManager.openUserSession(guestUser);

            final AuthenticationToken authenticationToken = new AuthenticationToken();
            authenticationToken.setToken(token);
            authenticationToken.setUser(guestUser);

            final Cookie authenticationTokenCookie = new Cookie(
                    OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE,
                    token);
            authenticationTokenCookie.setPath("/");
            response.addCookie(authenticationTokenCookie);
            OPFContext.initContext(principal, token);
            response.sendRedirect("console/installation");
            return "installation";
        }
    }

    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String reset(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ConfigContainer.isAppInstalled()) {
            String serverName = request.getServerName();
            int port = request.getServerPort();
            String contextPath = request.getContextPath();

            SystemModel system = systemStore.findByLookup(OpenFlame.SYSTEM);

            system.setServerName(serverName);
            system.setPort(port);
            systemStore.save(system);

            Env.FIREJACK_URL.setValue(WebUtils.getNormalizedUrl(serverName, port, contextPath));

            ConfigContainer.setHost(serverName);
       	    ConfigContainer.setPort(port);

            response.sendRedirect("console/home");
        }
        return "installation";
    }

    private void initKeyPair(Model model, HttpServletRequest request) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            KeyPair pair = kpg.generateKeyPair();

            String publicKey = new Base64Encoder().encode(pair.getPublic().getEncoded());
            model.addAttribute("publicKey", publicKey.replaceAll("\n", ""));

            HttpSession session = request.getSession();
            session.setAttribute(OpenFlameSecurityConstants.PASSWORD_ENCRYPTED_PRIVATE_KEY, pair.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Can't generate private/public keys for sign on process.\n" + e.getMessage(), e);
        }
    }

    // Please don't remove it.

    /**
     * @return
     */
    @ModelAttribute("defaultLogin")
    public String getDefaultLogin() {
        return getDebugMode() ? "admin" : "";
    }

    /**
     * @return
     */
    @ModelAttribute("defaultPassword")
    public String getDefaultPassword() {
        return getDebugMode() ? "111111" : "";
    }

}