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

package net.firejack.platform.web.security.spring.openid;

import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import org.openid4java.consumer.ConsumerException;
import org.springframework.security.openid.OpenID4JavaConsumer;
import org.springframework.security.openid.OpenIDAttribute;

import java.util.Arrays;


public class AttributeAwareOpenIDConsumer extends OpenID4JavaConsumer {

    public static final OpenIDAttribute USERNAME = toOpenIDAttribute(SupportedOpenIDAttribute.USERNAME);
    public static final OpenIDAttribute EMAIL = toOpenIDAttribute(SupportedOpenIDAttribute.EMAIL);
    public static final OpenIDAttribute FIRST_NAME = toOpenIDAttribute(SupportedOpenIDAttribute.FIRST_NAME);
    public static final OpenIDAttribute MIDDLE_NAME = toOpenIDAttribute(SupportedOpenIDAttribute.MIDDLE_NAME);
    public static final OpenIDAttribute LAST_NAME = toOpenIDAttribute(SupportedOpenIDAttribute.LAST_NAME);

    /**
     * @throws org.openid4java.consumer.ConsumerException
     *
     */
    public AttributeAwareOpenIDConsumer() throws ConsumerException {
        super(Arrays.asList(USERNAME, EMAIL, FIRST_NAME, LAST_NAME, MIDDLE_NAME));
    }

    /**
     * @param supportedAttribute
     * @return
     */
    public static OpenIDAttribute toOpenIDAttribute(SupportedOpenIDAttribute supportedAttribute) {
        OpenIDAttribute openIDAttribute = new OpenIDAttribute(
                supportedAttribute.getAttributeName(), supportedAttribute.getAttributeType());
        openIDAttribute.setRequired(true);
        return openIDAttribute;
    }
}
