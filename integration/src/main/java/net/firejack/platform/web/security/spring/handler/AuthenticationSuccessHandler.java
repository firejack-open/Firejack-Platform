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

package net.firejack.platform.web.security.spring.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private List<ISuccessfulAuthenticationHandler> onSuccessActionList;

    /**
     * @param onSuccessActionList
     */
    public void setOnSuccessActionList(List<ISuccessfulAuthenticationHandler> onSuccessActionList) {
        this.onSuccessActionList = onSuccessActionList;
    }

    /**
     * @return
     */
    public List<ISuccessfulAuthenticationHandler> getOnSuccessActionList() {
        if (onSuccessActionList == null) {
            onSuccessActionList = new ArrayList<ISuccessfulAuthenticationHandler>();
        }
        return onSuccessActionList;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        for (ISuccessfulAuthenticationHandler onSuccessAction : getOnSuccessActionList()) {
            onSuccessAction.onSuccessfulAuthentication(authentication);
        }

        handle(request, response, authentication);
        clearAuthenticationAttributes(request);

//        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
//
//        Writer out = responseWrapper.getWriter();
//
//        String targetUrl = determineTargetUrl( request, response );
//        out.write("{success:true, targetUrl : \'" + targetUrl + "\'}");
//        out.close();
    }

}