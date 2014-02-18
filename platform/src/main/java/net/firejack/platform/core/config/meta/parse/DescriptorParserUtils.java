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

package net.firejack.platform.core.config.meta.parse;


import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.construct.PackageDescriptor;
import net.firejack.platform.core.config.meta.exception.ParseException;
import net.firejack.platform.core.config.meta.parse.xml.IPackageXmlProcessor;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DescriptorParserUtils {

    private static final Pattern PACKAGE_PLACEHOLDER_VALUE = Pattern.compile("\\$\\{([\\w\\.]+)\\}");
	private static final String PASSWORD_KEY = "app.admin.password";

    private DescriptorParserUtils() {
    }

    /**
     * @param xml
     * @param xmlProcessor
     * @return
     * @throws net.firejack.platform.core.config.meta.exception.ParseException
     *
     */
    public static IPackageDescriptor convertDescriptor(String xml, IPackageXmlProcessor xmlProcessor) throws ParseException {
        xml = replaceDynamicProperties(xml);
        return xmlProcessor.packageFromXml(xml, PackageDescriptor.class);
    }

    private static String replaceDynamicProperties(String xml) {
        Matcher m = PACKAGE_PLACEHOLDER_VALUE.matcher(xml);
        while (m.find()) {
            String placeholder = m.group();
            String placeholderName = m.group(1);
            String value = ConfigContainer.get(placeholderName);
	        if(value != null && placeholderName.equals(PASSWORD_KEY)){
		        value = SecurityHelper.hash(value);
	        }
            if (StringUtils.isNotBlank(value)) {
                xml = xml.replace(placeholder, value);
//            } else {
//                String message = "Can't find properties value for the placeholder: '" + placeholderName + "'";
//                logger.error(message);
//                throw new ParseException(message);
            }
        }
        return xml;
    }

}