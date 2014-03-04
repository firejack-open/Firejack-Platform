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
