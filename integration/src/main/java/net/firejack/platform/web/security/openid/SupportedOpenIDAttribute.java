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

package net.firejack.platform.web.security.openid;

import net.firejack.platform.core.utils.StringUtils;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.FetchRequest;

public enum SupportedOpenIDAttribute {

    USERNAME("nickname", "http://axschema.org/namePerson/friendly"),
    EMAIL("email", "http://axschema.org/contact/email"),
    FIRST_NAME("firstName", "http://axschema.org/namePerson/first"),
    MIDDLE_NAME("middleName", "http://axschema.org/namePerson/middle"),
    LAST_NAME("lastName", "http://axschema.org/namePerson/last");

    private String attributeName;
    private String attributeType;

    SupportedOpenIDAttribute(String attributeName, String attributeType) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
    }

    /**
     * @return
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @return
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @param fetchRequest
     * @throws org.openid4java.message.MessageException
     *
     */
    public void append(FetchRequest fetchRequest) throws MessageException {
        fetchRequest.addAttribute(getAttributeName(), getAttributeType(), true);
    }

    /**
     * @param attributeName
     * @return
     */
    public static SupportedOpenIDAttribute lookForSupportedAttribute(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            return null;
        }

        SupportedOpenIDAttribute[] openIDAttributes = values();
        for (SupportedOpenIDAttribute attribute : openIDAttributes) {
            if (attribute.getAttributeName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }
}