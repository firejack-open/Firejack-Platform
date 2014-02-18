/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.model.config.servlet.preprocessor;

import com.thoughtworks.xstream.core.util.Base64Encoder;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Component
public class EncryptPasswordProcessor implements IGatewayPreProcessor {

    private static final Logger logger = Logger.getLogger(EncryptPasswordProcessor.class);

    @Override
    public void execute(Map<String, String> map, HttpServletRequest request, HttpServletResponse response, NavigationElement currentNavigationElement) {
        if (currentNavigationElement != null && currentNavigationElement.getPageUrl().equals(getLoginPageUrl())) {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                KeyPair pair = kpg.generateKeyPair();

                String publicKey = new Base64Encoder().encode(pair.getPublic().getEncoded());
                map.put("publicKey", "'" + publicKey.replaceAll("\n", "") + "'");

                HttpSession session = request.getSession();
                session.setAttribute(OpenFlameSecurityConstants.PASSWORD_ENCRYPTED_PRIVATE_KEY, pair.getPrivate());
            } catch (NoSuchAlgorithmException e) {
                logger.error("Can't generate private/public keys for sign on process.\n" + e.getMessage(), e);
            }
        }
    }

    public String getLoginPageUrl() {
        return "/login";
    }

}
