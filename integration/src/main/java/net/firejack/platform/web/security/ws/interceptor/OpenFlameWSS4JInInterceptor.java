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

package net.firejack.platform.web.security.ws.interceptor;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.model.context.ContextContainerDelegate;
import net.firejack.platform.web.security.ws.WSContextDataHolder;
import net.firejack.platform.web.security.ws.authorization.OpenFlameWSSecurityContext;
import net.firejack.platform.web.security.ws.callback.PasswordCallbackHandler;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityEngineResult;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpenFlameWSS4JInInterceptor extends WSS4JInInterceptor {

    private static final Logger logger = Logger.getLogger(OpenFlameWSS4JInInterceptor.class);

    public OpenFlameWSS4JInInterceptor() {
        Map<String ,Object> map = new HashMap<String, Object>();

        map.put("action","UsernameToken");
        map.put("passwordType","PasswordText");
        map.put("passwordCallbackRef",new PasswordCallbackHandler());

        setProperties(map);
    }

    @Override
    public void handleMessage(SoapMessage msg) throws Fault {
        WSContextDataHolder.cleanup();
        ContextContainerDelegate contextContainerDelegate = ContextContainerDelegate.getInstance();
        contextContainerDelegate.initBusinessContextForGuest();
        HttpServletRequest httpRequest = (HttpServletRequest) msg.get(AbstractHTTPDestination.HTTP_REQUEST);
        WSContextDataHolder.setClientIpAddress(httpRequest.getRemoteAddr());
        String requestPath = WebUtils.getRequestPath(httpRequest);
        logger.info("Current request path is: " + requestPath);
        WSContextDataHolder.setRequestPath(requestPath);
        try {
            super.handleMessage(msg);
        } catch (Fault fault) {
            logger.error("Failed to handle SOAP message.");
            WSContextDataHolder.cleanup();
            throw fault;
        }
    }

    @Override
    protected SecurityContext createSecurityContext(Principal p) {
        return new OpenFlameWSSecurityContext();
    }

    @Override
    protected WSSecurityEngine getSecurityEngine(boolean utWithCallbacks) {
        WSSecurityEngine securityEngine = super.getSecurityEngine(utWithCallbacks);
        if (securityEngine == null) {
            securityEngine = new WSSecurityEngine();
        }
        WSSConfig wssConfig = securityEngine.getWssConfig();
        if (wssConfig == null) {
            wssConfig = WSSConfig.getNewInstance();
            securityEngine.setWssConfig(wssConfig);
        }
        wssConfig.setValidator(WSSecurityEngine.USERNAME_TOKEN, OpenFlameUsernameTokenValidator.class);
        return securityEngine;
    }

    //
    protected boolean checkReceiverResultsAnyOrder(List<WSSecurityEngineResult> wsResult, List<Integer> actions) {
        boolean overriddenBehaviour = checkReceiverResultsAnyOrderInternal(wsResult, actions);
        if (!overriddenBehaviour) {
            overriddenBehaviour = super.checkReceiverResultsAnyOrder(wsResult, actions);
        }
        return overriddenBehaviour;
    }

    @SuppressWarnings("unused")
    protected boolean checkReceiverResultsAnyOrderInternal(
            List<WSSecurityEngineResult> wsResult, List<Integer> actions) {
        return StringUtils.isNotBlank(WSContextDataHolder.getCurrentToken());
    }

}