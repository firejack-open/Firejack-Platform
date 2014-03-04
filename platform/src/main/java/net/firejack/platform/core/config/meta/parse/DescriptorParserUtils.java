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