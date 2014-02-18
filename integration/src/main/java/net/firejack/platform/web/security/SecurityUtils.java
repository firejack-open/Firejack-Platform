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

package net.firejack.platform.web.security;

import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.action.container.IActionContainer;
import net.firejack.platform.web.security.action.container.IActionContainerFactory;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.navigation.INavElementContainer;
import net.firejack.platform.web.security.navigation.INavElementContainerFactory;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.permission.IPermissionContainerFactory;
import net.firejack.platform.web.security.resource.IResourceLocationContainer;
import net.firejack.platform.web.security.resource.IResourceLocationContainerFactory;
import net.firejack.platform.web.security.sr.ISecuredRecordInfoContainer;
import net.firejack.platform.web.security.sr.ISecuredRecordInfoContainerFactory;
import net.firejack.platform.web.security.sr.ISpecifiedIdsFilterContainer;
import net.firejack.platform.web.security.sr.ISpecifiedIdsFilterContainerFactory;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityUtils {

    private static final Logger logger = Logger.getLogger(SecurityUtils.class);
    private static IActionContainerFactory actionContainerFactory;
    private static INavElementContainerFactory navElementContainerFactory;
    private static IPermissionContainerFactory permissionContainerFactory;
    private static IResourceLocationContainerFactory resourceLocationContainerFactory;
    private static ISecuredRecordInfoContainerFactory securedRecordInfoContainerFactory;
    private static ISpecifiedIdsFilterContainerFactory specifiedIdsFilterContainerFactory;

    public static boolean matches(String requestUri, ResourceLocation maskedResource) {
        String pattern = maskedResource.getUrlPath();
        boolean urlMatches = false;
        switch (maskedResource.getWildcardStyle()) {
            case ANT:
                PathMatcher antStyleMatcher = new AntPathMatcher();
                urlMatches = antStyleMatcher.match(pattern, requestUri);
                break;
            case REGEXP:
                Pattern urlPattern = Pattern.compile(pattern);
                Matcher matcher = urlPattern.matcher(requestUri);
                urlMatches = matcher.matches();
        }
        return urlMatches;
    }

    public static IActionContainerFactory emptyActionContainerFactory() {
        if (actionContainerFactory == null) {
            actionContainerFactory = new IActionContainerFactory() {
                @Override
                public IActionContainer produceActionContainer() {
                    return null;
                }
            };
        }
        return actionContainerFactory;
    }

    public static INavElementContainerFactory emptyNavElementContainerFactory() {
        if (navElementContainerFactory == null) {
            navElementContainerFactory = new INavElementContainerFactory() {
                @Override
                public INavElementContainer produceNavElementContainer() {
                    return null;
                }
            };
        }
        return navElementContainerFactory;
    }

    public static IPermissionContainerFactory emptyPermissionContainerFactory() {
        if (permissionContainerFactory == null) {
            permissionContainerFactory = new IPermissionContainerFactory() {
                @Override
                public IPermissionContainer producePermissionContainer() {
                    return null;
                }
            };
        }
        return permissionContainerFactory;
    }

    public static IResourceLocationContainerFactory emptyResourceLocationContainerFactory() {
        if (resourceLocationContainerFactory == null) {
            resourceLocationContainerFactory = new IResourceLocationContainerFactory() {
                @Override
                public IResourceLocationContainer produceMaskedResourceContainer() {
                    return null;
                }
            };
        }
        return resourceLocationContainerFactory;
    }

    public static ISecuredRecordInfoContainerFactory emptySecuredRecordInfoContainerFactory() {
        if (securedRecordInfoContainerFactory == null) {
            securedRecordInfoContainerFactory = new ISecuredRecordInfoContainerFactory() {
                @Override
                public ISecuredRecordInfoContainer produceSecuredRecordContainer() {
                    return null;
                }
            };
        }
        return securedRecordInfoContainerFactory;
    }

    public static ISpecifiedIdsFilterContainerFactory emptySpecifiedIdsFilterContainerFactory() {
        if (specifiedIdsFilterContainerFactory == null) {
            specifiedIdsFilterContainerFactory = new ISpecifiedIdsFilterContainerFactory() {
                @Override
                public ISpecifiedIdsFilterContainer produceSpecifiedIdsFilter() {
                    return null;
                }
            };
        }
        return specifiedIdsFilterContainerFactory;
    }

    public static String encryptData(String data, String key) {
        String encryptedData;
        try {
            byte[] encryptedIp = KeyUtils.encrypt(data.getBytes(), key);

            StringBuilder buf = new StringBuilder(encryptedIp.length * 2);
            int i;
            for (i = 0; i < encryptedIp.length; i++) {
                if (((int) encryptedIp[i] & 0xff) < 0x10) {
                    buf.append("0");
                }
                buf.append(Long.toString((int) encryptedIp[i] & 0xff, 16));
            }
            encryptedData = buf.toString();
        } catch (Exception e) {
            logger.error("Failed to generate session token value.");
            throw new OpenFlameRuntimeException(e.getMessage(), e);
        }
        return encryptedData;
    }

    public static String decryptData(String cryptedData, String key) {
        String decryptedData;
        int len = cryptedData.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(cryptedData.charAt(i), 16) << 4)
                    + Character.digit(cryptedData.charAt(i + 1), 16));
        }
        try {
            byte[] decrypted = KeyUtils.decrypt(data, key);
            decryptedData = new String(decrypted);
        } catch (Exception e) {
            logger.error("Failed to decrypt cryptedData [" + cryptedData + "]. Reason: " + e.getMessage(), e);
            logger.error("Failed to decrypt session token value part.");
            throw new OpenFlameRuntimeException(e.getMessage(), e);
        }
        return decryptedData;
    }

    public static boolean tokenIsValid(String token, String ipAddress) {
        boolean isValid = true;
        if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(ipAddress)) {
            if (token.length() > 36) {
                String tokenBeginPart = token.substring(0, 8);
                String tokenEndPart = token.substring(36);
                String receivedIpInfo = decryptData(tokenEndPart, tokenBeginPart);
                isValid = receivedIpInfo.equals(ipAddress);
            }
        }
        return isValid;
    }

    public static String generateTokenValue(String ipAddress) {
        String token = UUID.randomUUID().toString();
        if (StringUtils.isNotBlank(ipAddress)) {
            String tokenPart = token.substring(0, 8);
            token += encryptData(ipAddress, tokenPart);
        }
        return token;
    }

    public static String getBrowserIpAddress(String sessionToken, HttpServletRequest request) {
        String clientIpAddress = request.getHeader(OpenFlameSecurityConstants.CLIENT_INFO_HEADER);
        if (StringUtils.isBlank(clientIpAddress)) {
            clientIpAddress = request.getRemoteAddr();
        } else {
            String key = sessionToken.substring(4, 12);
            clientIpAddress = SecurityUtils.decryptData(clientIpAddress, key);
        }
        return clientIpAddress;
    }

}